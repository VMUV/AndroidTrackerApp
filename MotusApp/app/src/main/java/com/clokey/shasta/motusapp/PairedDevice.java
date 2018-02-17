package com.clokey.shasta.motusapp;

/**
 * Created by Shasta on 2/14/2018.
 */

public class PairedDevice
{
    private String deviceName;
    private boolean isAvailable, isConnected;

    public PairedDevice(String deviceName, boolean isAvailable, boolean isConnected)
    {
        this.deviceName = deviceName;
        this.isAvailable = isAvailable;
        this.isConnected = isConnected;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public boolean isAvailable()
    {
        return isAvailable;
    }

    public void setAvailable(boolean available)
    {
        isAvailable = available;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void setConnected(boolean connected)
    {
        isConnected = connected;
    }
}
