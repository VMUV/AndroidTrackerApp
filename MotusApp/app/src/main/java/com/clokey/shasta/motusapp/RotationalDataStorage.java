package com.clokey.shasta.motusapp;

import comms.protocol.java.DataQueue;

/**
 * Created by Shasta on 3/6/2018.
 */

public class RotationalDataStorage
{
    private static DataQueue dataQueue = new DataQueue();

    public static synchronized boolean HasData()
    {
        return !dataQueue.IsEmpty();
    }

    public static synchronized void SetData(byte[] data)
    {
        dataQueue.ParseStreamable(data, data.length);
    }

    public static synchronized byte[] GetData()
    {
        byte[] tmp = new byte[2048];
        int numBytes = dataQueue.GetStreamable(tmp);
        byte[] rtn = new byte[numBytes];
        System.arraycopy(tmp, 0, rtn, 0, numBytes);
        return rtn;
    }
}
