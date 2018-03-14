package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.clokey.shasta.motusapp.MainActivity.mHandler;

/**
 * Created by Shasta on 2/26/2018.
 */
public class MessageManagerThread extends Thread
{
    private static final String TAG = "MY_APP_DEBUG_TAG";
    Timer mmTimer;

    // Defines several constants used when transmitting messages between the service and the UI.
    private interface MessageConstants
    {
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    private final BluetoothSocket mmSocket;
    private final OutputStream mmOutStream;
    private byte[] outgoingMessage;

    public MessageManagerThread(BluetoothSocket socket)
    {
        mmTimer = new Timer(true);
        mmSocket = socket;
        OutputStream tmpOut = null;
        try
        {
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        mmOutStream = tmpOut;
    }

    public void run()
    {
        try
        {
            mmTimer.scheduleAtFixedRate(new SendMessage(),0,25);
        }
        catch (Exception consumed)
        {
            /* Allow thread to exit */
            //Todo maybe notify the main activity if the thread exits in this way
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel()
    {
        try
        {
            mmTimer.cancel();
            interrupt();
            mmSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    class SendMessage extends TimerTask
    {
        public void run()
        {   try
            {
                outgoingMessage = new byte[]{1, 2, 3}; //DataStorage.getRotationalData()); //get array of bytes from DataStorage static class and send it over the BT socket
                mmOutStream.write(outgoingMessage);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, outgoingMessage);
                writtenMsg.sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }
    }
}