package server;


//This class can be used for RequestVote and for AppendEntries(i.e., the heartbeat)
public class RPCMessage {

    public enum MessageType{
        PREVIOUS_LEADER, //maybe for propagating back if the original leader comes back online? not sure if we need
        NEW_LEADER //handle new leader notifications
    }

    private final MessageType messageType;
    private final String leaderAddress;
    private final String previousLeaderAddress;

    //constructor
    public RPCMessage(MessageType messageType, String leaderAddress, String previousLeaderAddress)
    {
        this.messageType = messageType; 
        this.leaderAddress = leaderAddress; //can be null for non new-leader messages if needed
        this.previousLeaderAddress = previousLeaderAddress;
    }

    public RPCMessage(MessageType messageType, String leaderAddress)
    {
        this.messageType = messageType; 
        this.leaderAddress = leaderAddress; //can be null for non new-leader messages if needed
        this.previousLeaderAddress = ""; 
    }

    public MessageType getMessageType()
    {
        return this.messageType; 
    }


    public String getLeaderAddress()
    {
        return leaderAddress;
    }



}
