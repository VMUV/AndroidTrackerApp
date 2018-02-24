package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Shasta on 2/23/2018.
 */

public class BluetoothUtils
{
    private static ArrayList<PairedDevice> pairedDevicesContainer = new ArrayList<>();

    private static boolean isBluetoothSupported = true;

    private static boolean isInitialized = false;

    private static BluetoothAdapter mBluetoothAdapter;

    private static ConnectThread connectThread;

    private static final byte[] SERVER_UUID = {6,9,6,9,6,9};

    public static boolean initializeBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isInitialized = true;
        if (mBluetoothAdapter == null)
        {
            isBluetoothSupported = false;
        }
        else
        {
            isBluetoothSupported = true;
        }
        return isBluetoothSupported;
    }

    public static boolean isIsBluetoothSupported()
    {
        return isBluetoothSupported;
    }

    public static void updatePairedDevices()
    {
        if (isBTEnabled())
        {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            pairedDevicesContainer.clear();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    pairedDevicesContainer.add(new PairedDevice(deviceName, deviceHardwareAddress));
                }
            }
        }
    }

    public static ArrayList<PairedDevice> getPairedDevices()
    {
        return pairedDevicesContainer;
    }

    public static boolean isBTEnabled()
    {
        if (isBluetoothSupported && isInitialized)
            return mBluetoothAdapter.isEnabled();
        else
            return false;
    }

    public static void startBTConnection(String macAddress)
    {

        connectThread = new ConnectThread(mBluetoothAdapter.getRemoteDevice(macAddress),SERVER_UUID);
        connectThread.start();
        Log.v("startBTConnection", "Thread Started");
    }




}
