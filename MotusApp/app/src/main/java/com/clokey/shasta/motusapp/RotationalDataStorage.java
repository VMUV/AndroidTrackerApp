package com.clokey.shasta.motusapp;

/**
 * Created by Shasta on 3/6/2018.
 */

public class RotationalDataStorage
{
    private static byte[] ping = {0}, pong = {-1};
    private static boolean pingPongState = true;

    public static void setRotationalData(byte[] data)
    {
        if (pingPongState == true)
        {
            ping = data;
            pingPongState = false;
        }
        else
        {
            pong = data;
            pingPongState = true;
        }
    }

    public static byte[] getRotationalData()
    {
        if (getPingPongState() == false)
            return ping;
        else
            return pong;
    }

    public static boolean getPingPongState()
    {
        return pingPongState;
    }
}
