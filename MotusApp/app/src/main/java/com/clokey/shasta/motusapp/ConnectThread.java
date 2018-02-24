package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Shasta on 2/24/2018.
 */

public class ConnectThread extends Thread
{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mmAdapter;
    private final byte[] SERVER_UUID;

    public ConnectThread(BluetoothDevice device, byte[] myUuid)
    {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.mmAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = myUuid;

        try
        {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.nameUUIDFromBytes(SERVER_UUID));
            Log.v("ConnectThread constuct", tmp.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run()
    {
        // Cancel discovery because it otherwise slows down the connection.
        mmAdapter.cancelDiscovery();

        try
        {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.v("ConnectThread.run", "connection attempted");
            mmSocket.connect();
            Log.v("ConnectThread.run", "connection made");
        }
        catch (IOException connectException)
        {
            // Unable to connect; close the socket and return.
            try
            {
                mmSocket.close();
            }
            catch (IOException closeException)
            {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //TODO uncomment this and implement message manager static class
        // MessageManager.manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel()
    {
        try
        {
            mmSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}