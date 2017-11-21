import filemanagement.FileManager;
import filemanagement.FileSplit;
import handler.PeerHandler;
import handler.ConnectionState;
import handler.SocketConnectionHandler;
import init.CommonConfig;
import init.Initialization;
import init.LogConfig;
import init.Peer;
import init.PeerInfoConfig;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import message.Have;
import scheduler.UnchokeRegularScheduler;


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
	PeerHandler pHandler;
	FileManager fmgr;
	List<SocketConnectionHandler> activeConnections = Collections.synchronizedList(new ArrayList<SocketConnectionHandler>());

	public static void main (String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InterruptedException {
        if (args.length != 1) {
           System.out.println("wrong arguments");
           return;
        }
        final int peerId = Integer.parseInt(args[0]);
        peerProcess p1 = new peerProcess(peerId);
		p1.init();
		Thread t1 = new Thread(p1);
        t1.start();
		p1.start();
		int i = 0;
		while(i< 5){
			TimeUnit.SECONDS.sleep(10);
			System.out.println("waiting... i ="+i);
			i++;
		}
		p1.isterminate = true;
	}
	public peerProcess(int peerId ) {
		this.peerId = peerId;
	}

	@Override
	public void init() {
		CommonConfig config = new CommonConfig();
		config.init();
		PeerInfoConfig pconfig = new PeerInfoConfig();
		pconfig.init();
		pHandler = PeerHandler.getInstance();
		this.fmgr = new FileManager(this.peerId,pconfig, config);
		this.fmgr.init();
		LogConfig.getLogRecord().setLoggerForPeer(this.peerId);
		for (Peer peer : pconfig.peersList){
			if (peer.id == peerId){
				this.peerPort = peer.port;
				this.peerHostAddress = peer.host;
				this.hasFile = peer.isFile;
				/*if (this.hasFile){
					this.fmgr.splitFile();
				}*/
				this.pHandler.getPeer(peerId).setparts(this.fmgr.getCurrentAvailableParts());
				break;
			}
			peers.add(peer);
		}


		UnchokeRegularScheduler sTask = new UnchokeRegularScheduler(config.opsUnchokeIntvl, config.unchokeIntvl, config.nofNeighbour, this.pHandler );
		sTask.run();

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
						connection  = new SocketConnectionHandler(this.peerId, listener.accept(), pHandler, this.fmgr);
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
				SocketConnectionHandler connection  = new SocketConnectionHandler(this.peerId, peer.id,requestSocket, pHandler, this.fmgr);
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
					for (SocketConnectionHandler con : activeConnections){
						if (con.state ==ConnectionState.close)
							isterminate= true;
							//System.out.println("terminating server");
					}
			}
		} catch(Exception e){
			System.err.println("error: "+e);
		}
		terminate();
	}

	public void terminate(){
		for (SocketConnectionHandler con : activeConnections){
			if (con !=null){
				con.terminate();
			}
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
