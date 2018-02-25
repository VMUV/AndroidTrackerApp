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
        while (true)
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
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                //TODO uncomment this and implement message manager static class
                // MessageManager.manageMyConnectedSocket(mmSocket);
                Log.v("AcceptThread.run", "client connected");
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

    // Closes the connect socket and causes the thread to finish.
    public void cancel()
    {
        try
        {
            mmServerSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
