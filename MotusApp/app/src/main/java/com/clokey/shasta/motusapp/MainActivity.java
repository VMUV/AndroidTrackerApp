package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity
{

    //Done make a "pairedDevice" class with parameters(String deviceName, boolean isAvailable, boolean isConnected)
    //ToDo make a bluetooth static class that handles all data protocol activities
        //Done add a function that polls the paired items list and returns a list of "paired devices" objects
        //ToDo add a function that connects to a to a device on the "paired devices list"
        //Done add a static arrayList of pairedDevices in the bluetooth utils class that holds all the paired devices
        //ToDo add a function that disconnects from the current bluetooth connected device
        //ToDo add a static object that stores the bluetooth device that is currently connected
        //ToDo add a function that sends a message to the currently connected bluetooth device
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


    static boolean isBluetoothSupported = true;

    private final int REQUEST_ENABLE_BT = 1;

    private PairedDevicesAdapter mPairedDevicesAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPairedDevicesAdapter = new PairedDevicesAdapter(this, new ArrayList<PairedDevice>());

        ListView pairedDeviceList = findViewById(R.id.paired_device_list);

        pairedDeviceList.setAdapter(mPairedDevicesAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            isBluetoothSupported = false;
        }

        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
        else
        {

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            ArrayList<PairedDevice> pairedDevicesContainer = new ArrayList<>();

            if (pairedDevices.size() > 0)
            {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices)
                {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    pairedDevicesContainer.add(new PairedDevice(deviceName, deviceHardwareAddress));
                }
            }

            mPairedDevicesAdapter.addAll(pairedDevicesContainer);
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT)
        {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED)
            {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null)
                {
                    isBluetoothSupported = false;
                }

                if (!mBluetoothAdapter.isEnabled())
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                }

            }
            else if (resultCode == RESULT_OK)
            {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                ArrayList<PairedDevice> pairedDevicesContainer = new ArrayList<>();

                if (pairedDevices.size() > 0)
                {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices)
                    {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        pairedDevicesContainer.add(new PairedDevice(deviceName,deviceHardwareAddress));
                    }
                }
                mPairedDevicesAdapter.addAll(pairedDevicesContainer);
            }
        }
    }
}


