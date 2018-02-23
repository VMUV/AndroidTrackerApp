import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.clokey.shasta.motusapp.BluetoothUtils;

import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.easymock.EasyMock.expect;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Shasta on 2/23/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothDevice.class, BluetoothAdapter.class, Activity.class })
public class BluetoothUtilsTests
{
    private static final String DEVICE_NAME = "MocDeviceName";
    private static final String DEVICE_MAC = "MocDeviceMac";

    private BluetoothAdapter mockAdapter;

    private Set<BluetoothDevice> createMockDeviceSet()
    {
        Set<BluetoothDevice> btDeviceSet = new LinkedHashSet<BluetoothDevice>();

        btDeviceSet.add(createMockDevice(DEVICE_NAME, DEVICE_MAC));
        btDeviceSet.add(createMockDevice("Not a real device", "Not a real Mac"));

        return btDeviceSet;
    }

    private BluetoothDevice createMockDevice(String deviceName, String deviceMac)
    {
        BluetoothDevice btDevice = PowerMockito.mock(BluetoothDevice.class);

        PowerMockito.when(btDevice.getName()).thenReturn(deviceName);
        PowerMockito.when(btDevice.getAddress()).thenReturn(deviceMac);

        return btDevice;
    }

    @Test
    public void initializeBTSetAdapterSuccess() throws Exception
    {
        //TODO figure out how to mock the bluetooth system and have it work
        //Create the mock adapter and attach a set of mock devices to it
        mockAdapter = createMock(BluetoothAdapter.class);
        Set<BluetoothDevice> mockDeviceSet = createMockDeviceSet();
        expect(mockAdapter.getBondedDevices()).andReturn(mockDeviceSet);

        //Mock BluetoothAdapter's static method to return our mock adapter
        mockStatic(BluetoothAdapter.class);
        expect(BluetoothAdapter.getDefaultAdapter()).andReturn(mockAdapter);

        //Get ready for testing
        replay(mockAdapter);
        replay(BluetoothAdapter.class);

        //boolean successfulInit = Whitebox.invokeMethod(BluetoothUtils, "initialize");
        boolean successfulInit = BluetoothUtils.initializeBT();

        assertEquals(successfulInit, true);
    }
}
