package com.clokey.shasta.motusapp;

import comms.protocol.java.DataQueue;

public class SynchronizedDataQueue {
    private static DataQueue dataQueue = new DataQueue(2048);

    public static synchronized boolean HasData() {
        return !dataQueue.IsEmpty();
    }

    public static synchronized void SetData(byte[] data) {
        dataQueue.ParseStreamable(data, data.length);
    }

    public static synchronized byte[] GetData() {
        byte[] tmp = new byte[16535];
        int numBytes = dataQueue.GetStreamable(tmp);
        byte[] rtn = new byte[numBytes];
        System.arraycopy(tmp, 0, rtn, 0, numBytes);
        return rtn;
    }
}
