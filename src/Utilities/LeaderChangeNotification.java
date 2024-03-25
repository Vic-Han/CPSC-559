package Utilities;

import java.io.Serializable;

//This class can be used for RequestVote and for AppendEntries(i.e., the heartbeat)
public class LeaderChangeNotification implements Serializable {
    private static final long servialVersionUID = 1L; //ensure compatability over different JVM versions 

    public enum MessageType{
        //PREVIOUS_LEADER, //maybe for propagating back if the original leader comes back online? not sure if we need
        LEADER_CHANGE_NOTIFICATION, //handle new leader notifications
        SET_LEADER_STATE_NOTIFICATION
    }

    private final String newLeaderAddress;
    private final MessageType messageType;

    public LeaderChangeNotification(MessageType messageType, String leaderAddress)
    {
        this.messageType = messageType; 
        this.newLeaderAddress = leaderAddress; //can be null for non new-leader messages if needed
    }

    public MessageType getMessageType()
    {
        return this.messageType; 
    }


    public String getLeaderAddress()
    {
        return newLeaderAddress;
    }



}
