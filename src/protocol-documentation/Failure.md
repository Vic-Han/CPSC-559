## Server Initiates transfer with another server


## Server dies in the middle of transaction
- The client should have all their unsaved changes cached - all the write transactions they have made
- The client will ask the load balancer for a new server
- The load balancer will respond with another active replicas location


## Client disconnects unexpectedly



## Load balancer is unable to connect with server.
- Load balancer sends a server any type of message eg. client redirect
- Server does not ack back within 3(TBD) attempts
- Load balancer removes the server from list of active nodes
- Load balancer broadcasts a dead node message to all servers
- Servers who recieve this message should remove the dead servers from their list of peers and ack back

