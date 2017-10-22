import init.Initialization;
import init.Peer;
import init.PeerInfoConfig;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class peerProcess implements Runnable, Initialization{
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	int peerPort;
	int peerId;
	boolean hasFile;
	boolean isterminate = false;
	String peerHostAddress;
	List<Peer>peers = new ArrayList<Peer>();
	PeerInfoConfig pconfig;
	List<SocketConnectionHandler> activeConnections = new ArrayList<SocketConnectionHandler>();


	public peerProcess(int peerId ) {
		this.peerId = peerId;
	}

	@Override
	public void init() {
		PeerInfoConfig pconfig = new PeerInfoConfig("PeerInfo.cfg");
		pconfig.init();
		for (Peer peer : pconfig.peersList){
			if (peer.id == peerId){
				this.peerPort = peer.port;
				this.peerHostAddress = peer.host;
				this.hasFile = peer.isFile;
				break;
			}
			peers.add(peer);
		}

	}

	@Override
	public void run(){
		// TODO Auto-generated method stub
		System.out.println("Start server on port "+ this.peerPort);
    	ServerSocket listener = null;
		try {
			listener = new ServerSocket(this.peerPort);
			System.out.println("server running on port "+this.peerPort);
			SocketConnectionHandler connection = null;
    		while(true) {
				try {
						connection  = new SocketConnectionHandler(this.peerId, listener.accept());
		        		System.out.println("Client "  + this.peerId + " is connected!");
		        		if (connection != null){
			        		activeConnections.add(connection);
			        		startConnection(connection);
			        	}
	    		} catch (Exception e) {

	            }
    		}
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        		try {
					listener.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}

	}

	public void ConnectPeers() {
		for (Peer peer: peers){
			try{
				//create a socket to connect to the server
				System.out.println("Requesting socket Host= "+ peer.host+"and port= "+peer.port);
				requestSocket = new Socket(peer.host, peer.port);
				SocketConnectionHandler connection  = new SocketConnectionHandler(this.peerId, requestSocket);
				System.out.println("Connected to "+peer.host+" in port "+ peer.port);
				activeConnections.add(connection);
				startConnection(connection);

			}
			catch (ConnectException e) {
	    			System.err.println("Connection refused. You need to initiate a server first.");
			} catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public void startConnection(SocketConnectionHandler connection){
		for (SocketConnectionHandler con : activeConnections){
			if (con == connection && con.state == ConnectionState.initiated){
				con.state = ConnectionState.connecting;
				Thread t = new Thread(con);
		        t.start();
			}
		}
		
	}
	
	public void start(){
		try{
			ConnectPeers() ;
				while(!isterminate){
			}
		} catch(Exception e){
			System.err.println("error: "+e);
		}
	}

	//main method
	/*public static void main(String args[])
	{
		peerProcess p = new peerProcess(1002);
		p.init();
		p.ConnectPeers();
	}*/


	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}


}
