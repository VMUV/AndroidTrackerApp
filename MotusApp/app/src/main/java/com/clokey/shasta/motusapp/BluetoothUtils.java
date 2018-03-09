package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

    private static AcceptThread acceptThread;

    private static MessageManagerThread messageManagerThread;

    private static final String SERVER_UUID = "7A51FDC2-FDDF-4c9b-AFFC-98BCD91BF93B";

    private static final String SERVER_NAME = "MOTUS_TRACKER_APP";

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

    public static void startBTConnection()
    {
        acceptThread = new AcceptThread(SERVER_NAME, SERVER_UUID);
        acceptThread.start();
        Log.v("startBTConnection", "accept thread started");
    }

    public static void startBTTransmission(BluetoothSocket btSocket)
    {
        if (messageManagerThread == null)
        {
            messageManagerThread = new MessageManagerThread(btSocket);
            messageManagerThread.start();
        }
        else
        {
            messageManagerThread.cancel();
            messageManagerThread = new MessageManagerThread(btSocket);
            messageManagerThread.start();
        }
    }

    public static void stopBTTransmission()
    {
        if (messageManagerThread != null)
            messageManagerThread.cancel();
    }





}
