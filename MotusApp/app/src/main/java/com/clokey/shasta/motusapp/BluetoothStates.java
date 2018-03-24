package com.clokey.shasta.motusapp;

/**
 * Created by Shasta on 3/23/2018.
 */

public enum BluetoothStates
{
    waitForClient(0),
    streamData(1),
    connectedStandby(2);

    private int stateValue;

    private BluetoothStates(int value)
    {
        stateValue = value;
    }

    public int getStateValue() { return stateValue; }
}
