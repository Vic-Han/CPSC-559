package LoadBalancer;

import java.io.DataOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import Utilities.codes;
import Utilities.LeaderChangeNotification;
import Utilities.ServicePorts;

public class LeaderNotifier {

    private final int managementPort = ServicePorts.MANAGEMENT_PORT; //management port for control messages

    public LeaderNotifier(){
    }

    public void notifyServersOfNewLeader(String newLeaderAddress, List<String> serverAddresses)
    {
        String [] newLeaderAddressChunks = newLeaderAddress.split(":");
        String newLeaderAddressIP = newLeaderAddressChunks[0];
        LeaderChangeNotification newLeaderMessage = new LeaderChangeNotification(LeaderChangeNotification.MessageType.LEADER_CHANGE_NOTIFICATION, newLeaderAddressIP);
        for(String serverAddress : serverAddresses)
        {
            if(!serverAddress.equals(newLeaderAddress)) //exclude new leader 
            {
                try {
                    //notifyServerOfNewLeader(serverAddress, newLeaderAddress);
                    sendNewLeaderNotification(serverAddress, newLeaderMessage);
                }catch(Exception e)
                {
                    System.err.println("Failed to notify server " + serverAddress + " of the new leader: " + e.getMessage());
                    // TODO: Implement appropriate error handling, such as logging or retrying
                }
            }
        }
    }

    private void sendNewLeaderNotification(String serverAddress, LeaderChangeNotification newLeaderMessage) throws Exception //caught by calling method notifyServersOfNewLeader
    {
        String[] parts = serverAddress.split(":");
        String host = parts[0];

        try (Socket socket = new Socket(host, managementPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(newLeaderMessage);
        }
    }

    public void notifyInitialLeaderState(String serverAddress) throws Exception
    {
        String[] parts = serverAddress.split(":");
        String host = parts[0]; 

        LeaderChangeNotification setInitialLeaderStateMessage = new LeaderChangeNotification(LeaderChangeNotification.MessageType.SET_LEADER_STATE_NOTIFICATION, host);

        try (Socket socket = new Socket(host, managementPort);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())){
                out.writeObject(setInitialLeaderStateMessage);
            }
    }

}
