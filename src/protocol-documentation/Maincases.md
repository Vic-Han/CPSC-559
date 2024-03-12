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

## File transfer
- The sender will need to send several packets based on the size of the file. 
- The sender will first send the file name and size as a string and int repsectively. 
- The reciever may need to figure out where the store the file. The reciver needs to ack back
- The reciever should also expect another message within a certain time in order to detect failure
- Once the sender recieves the ack, it will send a byte array(4kb) one at a time
- For each binary packet, the reciever must send an ack back to the sender
- The sender should wait until the ack has been recieved to send the next packet
- Once the sender has sent the final packet and recieved the ack, the client should send a FILE TRANSFER DONE code
- The reciever should ack back in order to avoid a timeout resend from the sender

## Client File upload:

- The client will send the upload request special code
- The server will ack the request
- See file transfer for the rest

## Client File Download:
- The client will send the download request code 
- The server first needs to check if the file exists, send an error if it doesn't
- The server should ack the request and immediately start transfering
- See file transfer for the rest

## Client shares file they own with another user
- The client will send the share file code
- The server will send a response code letting the client know they can attempt the operation
- The client will send their own user id, the filename/id, and the user they are attempting to share with
- The server will verify that the user that they are sharing with and the file exist, send the corresponding error otherwise
- The server needs to send the transaction to the master and wait until it can execute it's own query see replication.md for more
- The server will send the success code back and the client can display it

## Client unshares file they own with another user
- The client will send the share file code
- The server will send a response code letting the client know they can attempt the operation
- The client will send their own user id, the filename/id, and the user they are attempting to unshare with
- The server will verify that the user that they are unsharing with and the file exist, send the corresponding error otherwise
- The server needs to send the transaction to the master and wait until it can execute it's own query see replication.md for more
- The server will send the success code back and the client can display it

## Client deletes file
- The client will send the deleted file code
- The server will send a response code letting the client know they can attempt the operation
- The client will send their own user id, and the filename/id
- The server will verify that the file exists, send the corresponding error otherwise
- The server needs to send the transaction to the master and wait until it can execute it's own query see replication.md for more
- The server will send the success code back once it removed the file from it's local file system and the client can display it



