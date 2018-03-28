using System.Threading;
using Comms_Protocol_CSharp;
using System;

namespace BTClient
{
    class BTWorker
    {
        private bool _isRunning = false;

        public bool IsRunning
        {
            get { return _isRunning; }
        }

        public void Run()
        {
            Thread thread = new Thread(new ThreadStart(WorkerThread));
            thread.Start();
        }

        private void WorkerThread()
        {
            _isRunning = true;

            BTClient client = new BTClient();
            BTStates state;
            while (true)
            {
                state = client.RunBTStateMachine();
                if (state == BTStates.disconnected)
                {
                    client.RunBTStateMachine();
                    Thread.Sleep(5000);
                    break;
                }
                else
                {
                    while (!client.dataQueue.IsEmpty())
                    {
                        RotationVectorRawDataPacket packet = new RotationVectorRawDataPacket(client.dataQueue.Get());
                        RotationVector_Quat quat = packet.GetQuat();
                        Console.WriteLine("w:" + quat.w + "\tx:" + quat.x +
                            "\ty:" + quat.y + "\tz:" + quat.z);
                    }
                    Thread.Sleep(10);
                }
            }

            _isRunning = false;
        }
    }
}
