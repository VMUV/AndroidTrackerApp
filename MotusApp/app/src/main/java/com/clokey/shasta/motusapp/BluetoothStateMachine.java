package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.preference.Preference;
import android.util.Log;

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
    private Timer mTimer;
    private OutputStream mOutputStream;
    private BluetoothStates currentState;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothSocket mBluetoothSocket;
    private byte[] outgoingMessage = new byte[22];
    private final String SERVER_UUID;
    private final String SERVER_NAME;


    public BluetoothStateMachine(String serverName, String serverUuid)
    {
        isStreaming = false;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = serverUuid;
        SERVER_NAME = serverName;
        mTimer = new Timer(true);
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
        try
        {
            // SERVER_UUID is the app's UUID string, also used by the client code.
            mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
            Log.v("BTSM.waitForConnection", "Server Socker:" + mBluetoothServerSocket.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's listen() method failed", e);
        }

        try
        {
            Log.v("BTSM.waitForConnection", "looking for clients");
            mBluetoothSocket = mBluetoothServerSocket.accept();
            if (mBluetoothServerSocket != null)
            {
                Log.v("BTSM.waitForConnection", "client found, moving to stream data state");
                currentState = BluetoothStates.streamData;
                mBluetoothServerSocket.close();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Interrupt method failed", e);
        }
    }

    private void streamRotationalData()
    {
        if (!isStreaming)
        {
            try
            {
                mOutputStream = mBluetoothSocket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            try
            {
                mTimer.scheduleAtFixedRate(new SendMessage(),0,25);
                isStreaming = true;
            }
            catch (Exception consumed)
            {
                /* Allow thread to exit */
                //Todo maybe notify the main activity if the thread exits in this way
            }
        }
    }

    private void stayConnectedAndStandby() {/*This does nothing but keep the thread in the standby state.*/}

    class SendMessage extends TimerTask
    {
        public void run()
        {
            try
            {
                RotationalDataStorage.dataQueue.GetStreamable(outgoingMessage);
                mOutputStream.write(outgoingMessage);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);
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
