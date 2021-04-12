Compilation
To compile the application you need to go to the scripts folder and run ./compile.sh

Running
First, you need to go to scripts folder and run ./rmi to start the rmiregistry

Then, you can run as many peers as you want and to do so you can run
./peer.sh <version> <peer_id> <svc_access_point> <mc_addr> <mc_port> <mdb_addr> <mdb_port> <mdr_addr> <mdr_port>
version - Version of the protocol
peer_id - peer ID
svc_access_point - peer access point
mc_addr - IP address of the MC channel
mc_port - Port of the MC channel
mdb_addr - IP address of the MDB channel
mdb_port - Port of the MDB channel
mdr_addr - IP address of the MDR channel
mdr_port - Port of the MDR channel

To run the Application Test, you can run ./test <peer_ap> BACKUP|RESTORE|DELETE|RECLAIM|STATE [<opnd_1>] [<opnd_2>]
peer_ap - Access Point
opnd_1 - Is either the path name of the file to backup/restore/delete, for the respective 3 subprotocols, or, in the case of RECLAIM the maximum amount of disk space (in KByte) that the service can use to store the chunks.
opnd_2 - This operand is an integer that specifies the desired replication degree and applies only to the backup protocol

We also have some tests scripts prepared to test de application.
For that you can run two peers: ./peerexample1.sh and ./peerexample2.sh
Then you can run any of the following scripts: ./testbackup.sh or ./testdelete.sh or ./testreclaim.sh or ./testrestore.sh or ./teststate.sh

To terminate the application press ctrl-c in every peer process

peer.sh 1.0 1 1 230.0.0.0 8234 230.0.0.1 8234 230.0.0.2 8234
peer.sh 1.0 2 2 230.0.0.0 8234 230.0.0.1 8234 230.0.0.2 8234
peer.sh 1.0 3 3 230.0.0.0 8234 230.0.0.1 8234 230.0.0.2 8234
peer.sh 1.0 4 4 230.0.0.0 8234 230.0.0.1 8234 230.0.0.2 8234

test.sh 1 BACKUP ../../files/files/peer-35/runnel_thino.png 1
test.sh 2 BACKUP ../../files/files/peer-35/spooky_month.gif 2

test.sh 1 DELETE ../../files/files/peer-35/runnel_thino.png
test.sh 2 DELETE ../../files/files/peer-35/spooky_month.gif

test.sh 2 RECLAIM 1234
