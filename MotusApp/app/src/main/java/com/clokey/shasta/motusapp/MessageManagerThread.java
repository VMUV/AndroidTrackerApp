package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothSocket;
import android.util.Log;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import comms.protocol.java.DataPacket;
import comms.protocol.java.DataQueue;

/**
 * Created by Shasta on 2/26/2018.
 */
public class MessageManagerThread extends Thread
{
    private static final String TAG = "MY_APP_DEBUG_TAG";

    private Timer mmTimer;
    private final BluetoothSocket mmSocket;
    private final OutputStream mmOutStream;
    private byte[] outgoingMessage = new byte[22];

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

    class SendMessage extends TimerTask
    {
        public void run()
        {   try
            {
                RotationalDataStorage.dataQueue.GetStreamable(outgoingMessage);
                mmOutStream.write(outgoingMessage);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel()
    {
        try
        {
            mmTimer.cancel();
            mmSocket.close();
            interrupt();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}