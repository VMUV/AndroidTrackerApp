using System.Threading;

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
                    Thread.Sleep(25);
            }

            _isRunning = false;
        }
    }
}
