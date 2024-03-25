package server;

public class ServerState {
    private static ServerState instance = new ServerState();

    // Enum to define possible states
    public enum State {
        FOLLOWER,
        LEADER
    }

    // Variable to hold the current state
    private State currentState = State.FOLLOWER; // Default state

    // Private constructor to prevent instantiation
    private ServerState() {}

    // Method to get the singleton instance
    public static ServerState getInstance() {
        return instance;
    }

    // Method to get the current state
    public synchronized State getState() {
        return currentState;
    }

    // Method to set the current state
    public synchronized void setState(State newState) {
        currentState = newState;
        System.out.println("Server state changed to: " + newState);
        // Consider notifying other components of the state change if necessary
    }
}