package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

import comms.protocol.java.DataPacket;

/**
 * Created by Shasta on 2/23/2018.
 */

public class BluetoothUtils
{
    private static boolean isBluetoothSupported = true;
    private static boolean isInitialized = false;
    private static BluetoothAdapter mBluetoothAdapter;
    private static AcceptThread acceptThread;
    private static MessageManagerThread messageManagerThread;
    private static final String SERVER_UUID = "7A51FDC2-FDDF-4c9b-AFFC-98BCD91BF93B";
    private static final String SERVER_NAME = "MOTUS_TRACKER_APP";

    private static BluetoothStateMachine mBluetoothStateMachine;

    public static void initializeBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isInitialized = true;
        if (mBluetoothAdapter == null)
            isBluetoothSupported = false;
        else
        {
            isBluetoothSupported = true;
            mBluetoothStateMachine = new BluetoothStateMachine(SERVER_NAME, SERVER_UUID);
        }
    }

    public static boolean isIsBluetoothSupported()
    {
        return isBluetoothSupported;
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

    public static void startBTStream(BluetoothSocket btSocket)
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

    public static void runBTSM()
    {
        mBluetoothStateMachine.start();
    }

    public static void changeBTSMState(BluetoothStates nextState)
    {
        mBluetoothStateMachine.changeState(nextState);
    }

    public static void stopBTSM()
    {
        mBluetoothStateMachine.cancel();
    }
}
