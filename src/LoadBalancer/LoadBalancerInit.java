package LoadBalancer;

import java.util.Arrays;
import java.util.List;

public class LoadBalancerInit {

    public static void main(String[] args)
    {
        int loadBalancerPort = 1969; 

        //Need to have the server address list available for this
        List<String> serverAddresses = Arrays.asList(
            "127.0.0.1:1971", 
            "127.0.0.1:1972",
            "127.0.0.1:1973"
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
        //start server on a separate thread 
        new Thread(() -> {
            LoadBalancerServer server = new LoadBalancerServer(loadBalancer, port);
            server.start();
        }).start();
    }

}
