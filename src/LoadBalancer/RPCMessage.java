package LoadBalancer;


//This class can be used for RequestVote and for AppendEntries(i.e., the heartbeat)
public class RPCMessage {

    public enum MessageType{
        REQUEST_VOTE,
        APPEND_ENTRIES, //for heartbeats
        NEW_LEADER //handle new leader notifications
    }

    private final MessageType messageType;
    private final int term; 
    private final int candidateID; 
    private final String leaderAddress;

    //constructor
    public RPCMessage(MessageType messageType, int term, int candidateID, String leaderAddress)
    {
        this.messageType = messageType; 
        this.term = term; 
        this.candidateID = candidateID;
        this.leaderAddress = leaderAddress; //can be null for non new-leader messages if needed
    }

    public MessageType getMessageType()
    {
        return this.messageType; 
    }

    public int getTerm()
    {
        return this.term; 
    }

    public int getCandidateID()
    {
        return this.candidateID;
    }

    public String getLeaderAddress()
    {
        return leaderAddress;
    }

}
