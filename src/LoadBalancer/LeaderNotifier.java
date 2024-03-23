package LoadBalancer;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import Utilities.codes;

public class LeaderNotifier {

    private final int managementPort = 1984; //management port for control messages

    public LeaderNotifier(){
    }

    public void notifyServersOfNewLeader(String newLeaderAddress, List<String> serverAddresses)
    {
        RPCMessage newLeaderMessage = new RPCMessage(RPCMessage.MessageType.NEW_LEADER, 0, -1, newLeaderAddress);
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

    private void sendNewLeaderNotification(String serverAddress, RPCMessage newLeaderMessage) throws Exception //caught by calling method notifyServersOfNewLeader
    {
        String[] parts = serverAddress.split(":");
        String host = parts[0];

        try (Socket socket = new Socket(host, managementPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(newLeaderMessage);
        }
    }

    private void notifyServerOfNewLeader(String serverAddress, String newLeaderAddress) throws Exception //caught by calling method notifyServersOfNewLeader
    {
        String[] parts = serverAddress.split(":");
        String host = parts[0];
        try(Socket socket = new Socket(host, managementPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())){
                out.writeByte(codes.NEWLEADERNOTIFICATION);
                out.writeUTF(newLeaderAddress);
                
            }
    }

}
