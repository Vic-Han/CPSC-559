## Client Closes gui

## Database Replication

## File Replication

## Server restores another node
- New server creates all the tables
- New server asks the load balancer for active node
- Load balancer finds an all active nodes and gives the IPs and ports back
- New server picks one node to ask
- New server sends a restore message
- Old server first sends all of the user table
- New server acks each packet(1kb TBD)
- Old server sends all of file table
- New server acks each packet(1kb TBD)
- Old server sends all of shared table(1)
- New server acks each packet(1kb TBD)
- Old server begins transfering files
- In addition to the file transfer protocol defined in maincases.md the old server must tell the new server the path of the file
- New server should ack each file completion
- After all files have been acked, the old server should send a Done message
- New server informs the load balancer it is ready to start accepting clients
- Load balancer updates it's own list and broadcasts new server to all old servers
- All old servers ack and update their list
