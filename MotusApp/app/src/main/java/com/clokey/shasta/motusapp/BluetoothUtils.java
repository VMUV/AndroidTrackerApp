package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Shasta on 2/23/2018.
 */

public class BluetoothUtils
{
    private static boolean isBluetoothSupported = true;

    private static boolean isInitialized = false;

    private static BluetoothAdapter mBluetoothAdapter;

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

    public static ArrayList<PairedDevice> getPairedDevices()
    {
        if (isBTEnabled())
        {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            ArrayList<PairedDevice> pairedDevicesContainer = new ArrayList<>();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    pairedDevicesContainer.add(new PairedDevice(deviceName, deviceHardwareAddress));
                }
            }
            return pairedDevicesContainer;
        }
        else
            return null;
    }

    public static boolean isBTEnabled()
    {
        if (isBluetoothSupported && isInitialized)
            return mBluetoothAdapter.isEnabled();
        else
            return false;
    }


}
