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
                        float[] quats = packet.DeSerialize();
                        Console.WriteLine("w: " + quats[0] + "x: " + quats[1] +
                            "y: " + quats[2] + "z: " + quats[3]);
                    }
                    Thread.Sleep(10);
                }
            }

            _isRunning = false;
        }
    }
}
