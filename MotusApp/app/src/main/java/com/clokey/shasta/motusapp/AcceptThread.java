package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Shasta on 2/24/2018.
 */

public class AcceptThread extends Thread
{
    private BluetoothServerSocket mmServerSocket;
    private BluetoothAdapter mmAdapter;
    private BluetoothSocket mmSocket;
    private final String SERVER_UUID;
    private final String SERVER_NAME;

    public AcceptThread(String serverName, String serverUuid)
    {
        this.mmAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVER_UUID = serverUuid;
        SERVER_NAME = serverName;

        try
        {
            // SERVER_UUID is the app's UUID string, also used by the client code.
            mmServerSocket = mmAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
            Log.v("AcceptThread construct", mmServerSocket.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
    }

    public void run()
    {
        try
        {
            Log.v("AcceptThread.run", "looking for clients");
            mmSocket = mmServerSocket.accept();
            Log.v("AcceptThread.run", "client found, launching message thread");
            BluetoothUtils.startBTStream(mmSocket);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Interrupt method failed", e);
        }
    }
}
