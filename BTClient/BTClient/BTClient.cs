using System;
using System.Net.Sockets;
using System.Threading;
using InTheHand.Net;
using InTheHand.Net.Sockets;
using InTheHand.Net.Bluetooth;

namespace BTClient
{
    class BTClient
    {
        private BluetoothClient client;
        private BluetoothDeviceInfo[] devices;
        private NetworkStream streamIn;
        private Guid service = new Guid("{7A51FDC2-FDDF-4c9b-AFFC-98BCD91BF93B}");
        private BTStates state = BTStates.start_radio;
        private int deviceIndex = 0;
        private byte[] streamData = new byte[2056];
        private int timeOutCounter = 0;

        private void LaunchRadio()
        {
            BluetoothRadio radio = BluetoothRadio.PrimaryRadio;
            if (radio == null)
            {
                state = BTStates.start_radio;
                return;
            }
            else if (radio.Mode == RadioMode.PowerOff)
                BluetoothRadio.PrimaryRadio.Mode = RadioMode.Connectable;

            client = new BluetoothClient();
            state = BTStates.find_connected_devices;
        }

        private void LookForConnectedDevices()
        {
            try
            {
                devices = client.DiscoverDevices();
                if (deviceIndex >= devices.Length)
                    deviceIndex = 0;
                state = BTStates.connect_to_service;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.GetType() + ": " + e.Message);
                deviceIndex = 0;
            }
        }

        private void ConnectToService()
        {
            try
            {
                BluetoothDeviceInfo info = devices[deviceIndex++];
                client.Connect(new BluetoothEndPoint((BluetoothAddress)info.DeviceAddress, service));
                state = BTStates.connected_to_service;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.GetType() + ": " + e.Message);
                state = BTStates.find_connected_devices;
            }
        }

        private void InitStream()
        {
            try
            {
                streamIn = client.GetStream();
                state = BTStates.read_stream;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.GetType() + ": " + e.Message);
            }
        }

        private void ReadStream()
        {
            try
            {
                streamIn.ReadTimeout = 100;

                if (streamIn.DataAvailable)
                {
                    timeOutCounter = 0;
                    int numBytes = streamIn.Read(streamData, 0, streamData.Length);

                    Console.WriteLine("Got " + numBytes + " bytes:");
                    if (numBytes > 0)
                    {
                        Console.Write("{");
                        for (int i = 0; i < numBytes - 1; i++)
                            Console.Write(streamData[i] + ",");
                        Console.Write(streamData[numBytes - 1]);
                        Console.Write("}");
                        Console.WriteLine();
                    }
                }
                else if (!client.Connected)
                {
                    timeOutCounter = 5000;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.GetType() + ": " + e.Message);
                state = BTStates.read_stream;
                timeOutCounter = 5000;
            }

            if (timeOutCounter >= 5000)
            {
                state = BTStates.disconnected;
            }
        }

        public BTStates RunBTStateMachine()
        {
            switch (state)
            {
                case BTStates.start_radio:
                    {
                        LaunchRadio();
                    }
                    break;
                case BTStates.find_connected_devices:
                    {
                        Console.WriteLine("Searching for connected devices..");
                        LookForConnectedDevices();
                    }
                    break;
                case BTStates.connect_to_service:
                    {
                        Console.WriteLine("Found " + devices.Length + " devices");
                        if (devices.Length > 0)
                            Console.Write("Attempting to connect to " + devices[deviceIndex].DeviceName +
                                " with service " + service.ToString());
                        ConnectToService();
                    }
                    break;
                case BTStates.connected_to_service:
                    {
                        Console.WriteLine("Connected to service " + service.ToString());
                        InitStream();
                    }
                    break;
                case BTStates.read_stream:
                    {
                        ReadStream();
                    }
                    break;
                case BTStates.disconnected:
                    {
                        streamIn.Dispose();
                        client.Close();
                        client.Dispose();
                        devices = null;
                    }
                    break;
            }

            return state;
        }
    }
    public enum BTStates
    {
        start_radio = 0,
        find_connected_devices,
        connect_to_service,
        connected_to_service,
        read_stream,
        disconnected
    }
}
