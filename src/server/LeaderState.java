package server;

public class LeaderState {
    private static volatile String currentLeaderAddress;

    public static synchronized String getLeaderAddress()
    {
        return currentLeaderAddress;
    }

    public static synchronized void setLeaderAddress(String newLeaderAddress)
    {
        currentLeaderAddress = newLeaderAddress;
    }

}
