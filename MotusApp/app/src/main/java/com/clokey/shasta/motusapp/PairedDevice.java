package com.clokey.shasta.motusapp;

/**
 * Created by Shasta on 2/14/2018.
 */

public class PairedDevice
{
    private String deviceName, macAddress;

    public PairedDevice(String deviceName, String macAddress)
    {
        this.deviceName = deviceName;
        this.macAddress = macAddress;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public String getMacAddress()
    {
        return macAddress;
    }


}
