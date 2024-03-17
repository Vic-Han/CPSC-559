package LoadBalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.net.*;
import Utilities.*;


public class LoadBalancer {
    private List<String> serverAddresses; //private list of serverAddresses
    private AtomicInteger currentIndex = new AtomicInteger(0); //atomic for thread safety 




    //Basic constructor to ensure that we get all the server addresses here. This is now being initialized by LoadBalancerInit (which also initializes the HealthChecks to run at a set interval to ensure servers are up)
    public LoadBalancer(List<String> initialServerAddresses)
    {
        serverAddresses.addAll(initialServerAddresses); 
    }

    public synchronized String getNextServer(){

        if(serverAddresses.isEmpty())
        {
            throw new IllegalStateException("No servers are available.");
        }
        int index = currentIndex.getAndUpdate(i -> (i + 1) % serverAddresses.size()); 
        return serverAddresses.get(index);
    }

    //synchronized as we can't have more than 1 thread trying to access the global var at the same time or we get RACE CONDITIONS
    public synchronized void removeFailedServer(String serverAddress)
    {
        if(serverAddresses.contains(serverAddress))
        {
            serverAddresses.remove(serverAddress);
            System.out.println("Server removed due to failure: " + serverAddress);
        }
        else
        {
            System.out.println("Attempted to remove a server that was not in the list: " + serverAddress);
        }

        //can also re-sync list if shared (but I don't think we share this so we should be good)
    }

    //synchronized as we can't have more than 1 thread trying to access the global var at the same time or we get RACE CONDITIONS
    public synchronized void addRecoveredServer(String serverAddress)
    {
        //perform a check to ensure that this server isn't already in the serverlist so that our round robin algorithm actually works properly 
        if (!serverAddresses.contains(serverAddress))
        {
            serverAddresses.add(serverAddress);
            System.out.println("Server recovered and added: " + serverAddress);
        }
        else
        {
            //Do we want to log anything? 
            System.out.println("Server already in the list: " + serverAddress); 
        }
    } 
}
