## Initial connection:

- The client has access to the load balancers IP and port#
- The client should ask the load balancer for an available server
- After recieving the request, the load balancer will ask(in a round robin format) the next server to see if it is currently connected with a client. 
- The server may handle several connections to several clients in the future.
- Server must repsond with either a yes or no. 
- See Failure.md for what to do if the server does not respond.
- The load balancer will continue to ask each server until it recieves a yes.
- Once the load balancer finds an available server, it will send the IP/port of the server as well as an authentication code to the client.
- The client will handshake(send to server, server acks, client acks) using the auth code and setup a connection.
- From here the client can start asking for services such as login and file transfer.


## Registration:

- The client will send a message to the server with the registration request special code
- The server will ack the request
- The client will send
- A username as a string
- A password as a string(for now)
- The server will check to see if the user already exists in the database
- The server will respond with either the newly created user ID or an error code
- See Synchronization.md for the broadcast protocol
- The client will cache the userID to use in later requests

## Login:

- The client will send a message to the server with the login request special code
- The server will ack the request
- The client will send
- A username as a string
- A password as a string(for now)
- The server will check to see if the user exists in the database
- The server will respond with either the existing user ID or an error code
- See Synchronization.md for the broadcast protocol
- The client will cache the userID to use in later requests

## Client File upload:

- The client will send the upload request special code
- The server will ack the request
- The client will send

## Client File Download:

## Client shares file they own with another user

## Client unshares file they own with another user

## Client deletes file




