Initial connection:

The client has access to the load balancers IP and port#
The client should ask the load balancer for an available server
After recieving the request, the load balancer will ask(in a round robin format) the next server to see if it is currently connected with a client. 
The server may handle several connections to several clients in the future.
Server must repsond with either a yes or no. 
See failure.md for what to do if the server does not respond.
The load balancer will continue to ask each server until it recieves a yes.
Once the load balancer finds an available server, it will send the IP/port of the server as well as an authentication code to the client.
The client will handshake(send to server, server acks, client acks) using the auth code and setup a connection.
From here the client can start asking for services such as login and file transfer.


Registration:

Login:

Client File upload:

Client File Download:

Client shares file they own with another user

Client unshares file they own with another user

Client deletes file




