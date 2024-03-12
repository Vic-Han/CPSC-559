## Client saves changes
- Automatically called when client closes gui
- Client can force save as well to make intermediate changes
- See file and database replication

## Database Replication
- Servers handle every database transaction other than the login and register
- This inlcudes file creation and deletion, as well as sharing and unsharing
- to push the transaction, the server must send all the arguments to the load balancer as well as the corresponding code for the sql transaction
- The load balancer will be listening on a special thread that will attempt to execute each transaction
- After any attempt, the load balancer will broadcast the arguments and code for the query, or broadcast an error message (with the arguments) so that the server that initiated can repsond to the client
- The servers should listen on a separate thread

## File Replication
- Servers have access to each others IP and port
- The sender will initiate an server-server upload request(different from client-server)
- The reciever will send the appropriate repsonse code to accept the request if it can
- See file transfer for details on file transfer
- Once the server is done replicating to every server it will inform the load balancer of a new file being created
- See database replication for more


## Server restores another node
- New server creates all the tables
- New server asks the load balancer for active node
- Load balancer finds an all active nodes and gives the IPs and ports back
- New server picks one node to ask
- New server sends a restore message
- Old server first sends the sqlite file, as the protocol already exists
- Old server begins transfering files
- In addition to the file transfer protocol defined in maincases.md the old server must tell the new server the path of the file
- New server should ack each file completion
- After all files have been acked, the old server should send a Done message
- New server informs the load balancer it is ready to start accepting clients
- Load balancer updates it's own list and broadcasts new server to all old servers
- All old servers ack and update their list


