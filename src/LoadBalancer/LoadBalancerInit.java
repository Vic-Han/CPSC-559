package LoadBalancer;

import java.util.Arrays;
import java.util.List;

public class LoadBalancerInit {

    public static void main(String[] args)
    {
        int loadBalancerPort = 2001; 

        //Need to have the server address list available for this
        List<String> serverAddresses = Arrays.asList(
            "162.157.110.232:1972",
                 "104.205.0.115:1972"
            //"SOME IP ADDRESS:1972", 
            //"ANOTHER UNIQUE IP :1972"
        );

        
        //init load balancer object with server addresses list 
        LoadBalancer loadBalancer = new LoadBalancer(serverAddresses);
        //start the load balancer server 
        startLoadBalancerServer(loadBalancer, loadBalancerPort);

        //Init and start health check scheduler 
        HealthCheckScheduler healthCheckScheduler = new HealthCheckScheduler(loadBalancer); 
        healthCheckScheduler.startHealthCheck(serverAddresses);

        //TODO: Implement this to also start intercommunicaiton services 

    }

    private static void startLoadBalancerServer(LoadBalancer loadBalancer, int port)
    {
        //start LoadBalancer server on a separate thread 
        new Thread(() -> {
            LoadBalancerServer server = new LoadBalancerServer(loadBalancer, port);
            server.start();
        }).start();
    }

}
