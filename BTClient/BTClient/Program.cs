using System;
using System.Net.Sockets;
using System.Threading;
using InTheHand.Net;
using InTheHand.Net.Sockets;
using InTheHand.Net.Bluetooth;

namespace BTClient
{
    class Program
    {
        static BluetoothClient bc;
        static BluetoothDeviceInfo di;
        static NetworkStream streamIn;
        static Guid service = new Guid("{7A51FDC2-FDDF-4c9b-AFFC-98BCD91BF93B}");
        static BTStates state = BTStates.start_radio;

        static void LaunchRadio()
        {
            BluetoothRadio radio = BluetoothRadio.PrimaryRadio;
            if (radio == null)
            {
                state = BTStates.start_radio;
                return;
            }
            else if (radio.Mode == RadioMode.PowerOff)
                BluetoothRadio.PrimaryRadio.Mode = RadioMode.Connectable;

            bc = new BluetoothClient();
            state = BTStates.find_connected_devices;
        }

        static void LookForConnectedDevices()
        {
            bool correctUUID = false;
            BluetoothDeviceInfo[] info = bc.DiscoverDevices();
            foreach (BluetoothDeviceInfo deviceInfo in info)
            {
                for (int i = 0; i < deviceInfo.InstalledServices.Length; i++)
                {
                    if (deviceInfo.InstalledServices[i] == service)
                    {
                        di = deviceInfo;
                        correctUUID = true;
                    }
                }
            }

            if (correctUUID)
                state = BTStates.connect_to_service;
        }

        static void ConnectToService()
        {
            try
            {
                bc.Connect(new BluetoothEndPoint((BluetoothAddress)di.DeviceAddress, service));
                state = BTStates.connected_to_service;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                state = BTStates.find_connected_devices;
            }
        }

        static void RunBTStateMachine()
        {
            switch (state)
            {
                case BTStates.start_radio:
                    {
                        Console.WriteLine("Starting radio..");
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
                        Console.WriteLine("Found " + di.DeviceName + " connecting to service "
                            + service.ToString());
                        ConnectToService();
                    }
                    break;
                case BTStates.connected_to_service:
                    {
                        Console.WriteLine("Connected to service " + service.ToString());
                        try
                        {
                            streamIn = bc.GetStream();
                            streamIn.ReadTimeout = 25;
                            state = BTStates.read_stream;
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine(e.Message);
                        }
                    }
                    break;
                case BTStates.read_stream:
                    {
                        byte[] data = new byte[256];
                        int numBytes = 0;

                        try
                        {
                            numBytes = streamIn.Read(data, 0, 256);
                            if (numBytes > 0)
                            {
                                Console.WriteLine("Got " + numBytes + " bytes!");
                                Console.Write("{");
                                for (int i = 0; i < numBytes - 1; i++)
                                    Console.Write(data[i] + ",");
                                Console.Write(data[numBytes - 1]);
                                Console.Write("}");
                                Console.WriteLine();
                            }
                            else
                            {
                                Console.WriteLine("Timeout");
                            }
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine(e.Message);
                        }
                    }
                    break;
                case BTStates.hardware_fault:
                    {

                    }
                    break;
            }
        }

        static void Main(string[] args)
        {
            while (true)
            {
                Thread.Sleep(2);
                RunBTStateMachine();
            }
        }
    }

    enum BTStates
    {
        start_radio = 0,
        find_connected_devices,
        connect_to_service,
        connected_to_service,
        read_stream,
        hardware_fault
    }
}
