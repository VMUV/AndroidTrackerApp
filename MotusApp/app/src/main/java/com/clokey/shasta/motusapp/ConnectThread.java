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
    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mmAdapter;
    private final byte[] SERVER_UUID;

    public ConnectThread(BluetoothDevice device, byte[] serverUuid)
    {
        mmDevice = device;
        this.mmAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = serverUuid;

        try
        {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(UUID.nameUUIDFromBytes(SERVER_UUID));
            Log.v("ConnectThread constuct", mmSocket.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's create() method failed", e);
        }
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
            Log.v("ConnectThread.run", "connection timed out");
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