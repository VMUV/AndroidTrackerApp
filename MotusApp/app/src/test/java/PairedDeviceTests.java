import com.clokey.shasta.motusapp.PairedDevice;

import static org.junit.Assert.*;

import org.junit.Test;

public class PairedDeviceTests
{
    private final String deviceName = "deviceName", macAddress = "macAddress";

    @Test
    public void testGetDeviceName()
    {
        PairedDevice testDevice = new PairedDevice(deviceName, macAddress);

        assertEquals(testDevice.getDeviceName(), deviceName);
    }

    @Test
    public void testGetMacAddress()
    {
        PairedDevice testDevice = new PairedDevice(deviceName, macAddress);

        assertEquals(testDevice.getMacAddress(), macAddress);
    }
}
