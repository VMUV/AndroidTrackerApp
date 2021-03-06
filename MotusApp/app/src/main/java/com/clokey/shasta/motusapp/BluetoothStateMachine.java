package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Shasta on 3/23/2018.
 */

public class BluetoothStateMachine extends Thread
{
    private boolean isStreaming;
    private boolean overrideToStandby;
    private Timer mTimer;
    private OutputStream mOutputStream;
    private BluetoothStates currentState;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothSocket mBluetoothSocket;
    private Message mStateChangeHandlerMessage;
    private Bundle mMessageBundle;
    private final String SERVER_UUID;
    private final String SERVER_NAME;


    public BluetoothStateMachine(String serverName, String serverUuid)
    {
        isStreaming = false;
        overrideToStandby = false;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = serverUuid;
        SERVER_NAME = serverName;
        currentState = BluetoothStates.waitForClient;
    }

    @Override
    public void run()
    {
        while(!interrupted())
        {
            switch (currentState.getStateValue())
            {
                case 0:
                {
                    waitForConnection();
                }
                break;
                case 1:
                {
                    streamRotationalData();
                }
                break;
                case 2:
                {
                    stayConnectedAndStandby();
                }
                break;
                default:
                {
                    changeState(BluetoothStates.waitForClient);
                }
                break;
            }
        }
    }

    private void waitForConnection()
    {
        mStateChangeHandlerMessage = new Message();
        mStateChangeHandlerMessage.arg1 = getCurrentState().getStateValue();
        MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
        try
        {
            // SERVER_UUID is the app's UUID string, also used by the client code.
            mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
            Log.v("BTSM.waitForConnection", "Server Socker:" + mBluetoothServerSocket.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's listen() method failed", e);
            mStateChangeHandlerMessage = new Message();
            mStateChangeHandlerMessage.arg1 = 101;
            MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
            cancel();
        }

        try
        {
            Log.v("BTSM.waitForConnection", "looking for clients");
            mBluetoothSocket = mBluetoothServerSocket.accept();
            if (mBluetoothServerSocket != null)
            {
                Log.v("BTSM.waitForConnection", "client found, moving to stream data state");
                if (overrideToStandby)
                {
                    currentState = BluetoothStates.connectedStandby;
                    mStateChangeHandlerMessage = new Message();
                    mStateChangeHandlerMessage.arg1 = BluetoothStates.connectedStandby.getStateValue();
                    MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
                }
                else
                {
                    currentState = BluetoothStates.streamData;
                    mStateChangeHandlerMessage = new Message();
                    mStateChangeHandlerMessage.arg1 = BluetoothStates.streamData.getStateValue();
                    MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
                }
                mBluetoothServerSocket.close();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Socket's accept() method failed", e);
            mStateChangeHandlerMessage = new Message();
            mStateChangeHandlerMessage.arg1 = 101;
            MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
            cancel();

        }
    }

    private void streamRotationalData()
    {
        if (!isStreaming)
        {
            overrideToStandby = false;
            isStreaming = true;
            try
            {
                mOutputStream = mBluetoothSocket.getOutputStream();
                Log.v("BTSM.streamRotData", "successfully got output stream");
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when creating output stream", e);
                mStateChangeHandlerMessage = new Message();
                mStateChangeHandlerMessage.arg1 = 101;
                MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
                cancel();
            }

            try
            {
                mTimer = new Timer(true);
                mTimer.scheduleAtFixedRate(new SendMessage(),0,25);
                Log.v("BTSM.streamRotData", "Started Data Stream. isStreaming");
            }
            catch (Exception e)
            {
                /* Allow thread to exit */
                Log.v("BTSM.streamRotData", "Failed to schedule data stream: " + e.toString());
                currentState = BluetoothStates.connectedStandby;
                mStateChangeHandlerMessage = new Message();
                mStateChangeHandlerMessage.arg1 = BluetoothStates.connectedStandby.getStateValue();
                MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);

            }
        }
    }

    private void stayConnectedAndStandby()
    {
        overrideToStandby = false;
    }

    class SendMessage extends TimerTask
    {
        public void run()
        {
            try
            {
                if (RotationalDataStorage.HasData()) {
                    byte[] data = RotationalDataStorage.GetData();
                    mOutputStream.write(data);
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);
                mStateChangeHandlerMessage = new Message();
                mStateChangeHandlerMessage.arg1 = 101;
                MainActivity.mBluetoothMessageHandler.sendMessage(mStateChangeHandlerMessage);
                cancel();

            }
        }
    }

    public void changeState(BluetoothStates nextState)
    {
        switch (currentState.getStateValue())
        {
            case 1:
            {
                if (nextState.getStateValue() == 2)
                {
                    mTimer.cancel();
                    currentState = nextState;
                    isStreaming = false;
                }
                else if (nextState.getStateValue() == 0)
                {
                    mTimer.cancel();
                    try {mBluetoothSocket.close();}
                    catch (IOException e) { }
                    currentState = nextState;
                    isStreaming = false;
                }
            }
            break;
            case 2:
            {
                currentState = nextState;
            }
            break;
            default:
            break;
        }
    }

    public BluetoothStates getCurrentState()
    {
        return currentState;
    }

    /**Should only be called when attempting to make `waitForConnection` state fall into `connectedStandby`*/
    public void setOverrideToStandby(boolean standbyOrStream)
    {
        overrideToStandby = standbyOrStream;
    }

    public void cancel()
    {
        switch (currentState.getStateValue())
        {
            case 0:
            {
                try {mBluetoothServerSocket.close();}
                catch (Exception e) {}
                try {mBluetoothSocket.close();}
                catch (Exception e) {}
                interrupt();
            }
            break;
            case 1:
            {
                mTimer.cancel();
                try {mBluetoothSocket.close();}
                catch (Exception e) {}
                interrupt();
            }
            break;
            case 2:
            {
                interrupt();
            }
            break;
            default:
            {
                interrupt();
            }
            break;
        }
    }
}
