package com.clokey.shasta.motusapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Shasta on 3/6/2018.
 */

public class RotationalDataStorageTest
{

    private boolean initialPingPongState;
    private final byte[] firstPayload = {1,2,3};
    private final byte[] secondPayload = {3,2,1};
    private byte[] dataFromBuffer;

    @Test
    public void setRotationalData_checkThatBufferPingPongs()
    {
        givenInitialPingPongStateIsSet();
        whenIWriteToTheBuffer();
        thenTheDataBufferPingPongs();
    }

    @Test
    public void getRotationalData_checkThatReadFollowsPingPong()
    {
        givenRotationalDataIsSet(firstPayload);
        whenIReadFromTheBuffer();
        thenIGetTheExpectedPayload(firstPayload);
        givenRotationalDataIsSet(secondPayload);
        whenIReadFromTheBuffer();
        thenIGetTheExpectedPayload(secondPayload);
    }

    private void givenRotationalDataIsSet(byte[] payload)
    {
        RotationalDataStorage.setRotationalData(payload);
    }

    private void whenIReadFromTheBuffer()
    {
        dataFromBuffer = RotationalDataStorage.getRotationalData();
    }

    private void thenIGetTheExpectedPayload(byte[] expectedPayload)
    {
        assertArrayEquals(expectedPayload, dataFromBuffer);
    }

    private void givenInitialPingPongStateIsSet()
    {
        initialPingPongState = RotationalDataStorage.getPingPongState();
    }

    private void whenIWriteToTheBuffer()
    {
        RotationalDataStorage.setRotationalData(new byte[]{1,2,3});
    }

    private void thenTheDataBufferPingPongs()
    {
        assertNotEquals(initialPingPongState, RotationalDataStorage.getPingPongState());
    }
}
