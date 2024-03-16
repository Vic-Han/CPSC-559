package LoadBalancer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthCheckScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private LoadBalancer loadBalancer; 

    //Constructor to ensure we get the active instance of the LoadBalancer to ensure we are correctly performing health checks via LoadBalancer functions
    public HealthCheckScheduler(LoadBalancer loadBalancer)
    {
        this.loadBalancer = loadBalancer; 
    }

    public void startHealthCheck(List<String> serverAddresses){
        serverAddresses.forEach(serverAddress -> {
            ServerHealthCheck healthCheck = new ServerHealthCheck(serverAddress, loadBalancer);
            //Currently set to run every 30 seconds (supposedly 10 to 30 is the best range so we may need to lower this if we find synchronization isn't happening properly)
            scheduler.scheduleAtFixedRate(healthCheck, 0, 30, TimeUnit.SECONDS); 
        });

    }

}
