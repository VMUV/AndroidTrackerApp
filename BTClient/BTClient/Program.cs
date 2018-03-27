using System.Threading;

namespace BTClient
{
    class Program
    {
        static void Main(string[] args)
        {
            BTClient client = null;
            while (true)
            {
                Thread.Sleep(2);
                if (client == null)
                    client = new BTClient();
                BTStates state = client.RunBTStateMachine();
                if (state == BTStates.disconnected)
                {
                    client = null;
                }
            }
        }
    }
}
