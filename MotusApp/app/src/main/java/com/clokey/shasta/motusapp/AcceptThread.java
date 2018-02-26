package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Shasta on 2/24/2018.
 */

public class AcceptThread extends Thread
{
    private BluetoothServerSocket mmServerSocket;
    private BluetoothAdapter mmAdapter;
    private final byte[] SERVER_UUID;
    private final String SERVER_NAME;

    public AcceptThread(String serverName, byte[] serverUuid)
    {
        this.mmAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = serverUuid;
        SERVER_NAME = serverName;

        try
        {
            // SERVER_UUID is the app's UUID string, also used by the client code.
            mmServerSocket = mmAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.nameUUIDFromBytes(SERVER_UUID));
            Log.v("AcceptThread constuct", mmServerSocket.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
    }

    public void run()
    {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    Log.v("AcceptThread.run", "looking for clients");
                    socket = mmServerSocket.accept();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null)
                {
                    BluetoothUtils.startBTTransmission(socket);
                    Log.v("AcceptThread.run", "client connected, messages sending");
                    try
                    {
                        mmServerSocket.close();
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Could not close the connect socket", e);
                    }
                    break;
                }
            }
        }
        catch (Exception e) {Log.e(TAG, "Interrupt method failed", e);}
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel()
    {
        try
        {
            interrupt();
            mmServerSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the connect socket", e);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Could not interrupt the thread", e);
        }
    }
}
