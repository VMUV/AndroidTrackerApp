package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity
{

    //Done make a "pairedDevice" class with parameters(String deviceName, boolean isAvailable, boolean isConnected)
    //ToDo make a bluetooth static class that handles all data protocol activities
        //Done add a function that polls the paired items list and returns a list of "paired devices" objects
        //Done add a function that connects to a to a device on the "paired devices list"
        //Done add a static arrayList of pairedDevices in the bluetooth utils class that holds all the paired devices
        //ToDo add a function that disconnects from the current bluetooth connected device
    //ToDo make a Static class that handles all background work for data transmission
        //ToDo give the class an object of the Thread class(or one of its children)
        //ToDo thread should check if bluetooth device is connected
            //ToDo if so, it should start sending data over to the other device on a specified port
            //ToDo if not, it should terminate the thread and notify the main thread that the transmission was unsuccessful
        //ToDo class should have a function to terminate the transmission and end the thread
    //ToDo when the app opens, check bluetooth connectivity and ask the user to verify that the current connected device is the host computer
    //ToDo add a button listener to the motus image icon on the main screen of the app
        //ToDo when the button is clicked, start a separate thread which is built to send data to the connected bluetooth device
        //ToDo if the connection is successfully transmitting, show a "transmitting data" animation at the center of the motus
        //ToDo if the transmission is unsuccessful, show a "transmission failed" toast notification
        //ToDo if the button is clicked again, tell the static background task management class to terminate the thread

    private final int REQUEST_ENABLE_BT = 1;

    private PairedDevicesAdapter mPairedDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPairedDevicesAdapter = new PairedDevicesAdapter(this, new ArrayList<PairedDevice>());

        ListView pairedDeviceList = findViewById(R.id.paired_device_list);

        pairedDeviceList.setAdapter(mPairedDevicesAdapter);

        BluetoothUtils.initializeBT();


        if (BluetoothUtils.isIsBluetoothSupported())
        {
            if (!BluetoothUtils.isBTEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
            {
                BluetoothUtils.updatePairedDevices();
                if (BluetoothUtils.getPairedDevices() != null)
                    mPairedDevicesAdapter.addAll(BluetoothUtils.getPairedDevices());
            }
        }

        //make the on click listener to handle what happens when each item in the list is clicked
        pairedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                PairedDevice deviceToLoad = BluetoothUtils.getPairedDevices().get(i);
                BluetoothUtils.startBTConnection(deviceToLoad.getMacAddress());
            }
        });
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT)
        {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED)
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else if (resultCode == RESULT_OK)
            {
                BluetoothUtils.updatePairedDevices();
                if (BluetoothUtils.getPairedDevices() != null)
                    mPairedDevicesAdapter.addAll(BluetoothUtils.getPairedDevices());
            }
        }
    }

}


