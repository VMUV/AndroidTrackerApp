using System.Threading;

namespace BTClient
{
    class Program
    {
        static void Main(string[] args)
        {
            BTWorker btWorker = new BTWorker();
            while (true)
            {
                if (!btWorker.IsRunning)
                    btWorker.Run();

                Thread.Sleep(1000);
            }
        }
    }
}
