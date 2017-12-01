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
           //System.out.println("wrong arguments");
           return;
        }
        final int peerId = Integer.parseInt(args[0]);
        peerProcess p1 = new peerProcess(peerId);
		p1.init();
		Thread t1 = new Thread(p1);
        t1.start();
		p1.start();
		int i = 0;
		while(!p1.isterminate){
			TimeUnit.SECONDS.sleep(10);
			//System.out.println("waiting... i ="+i);
			i++;
		}
		//System.out.println("process terminated "+peerId);
		System.exit(0);
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
		boolean seq = true;
		for (Peer peer : pconfig.peersList){
			this.pHandler.getPeer(peer.id).settotalParts(this.fmgr.noOfSplits);
			if (peer.id == peerId){
				this.peerPort = peer.port;
				this.peerHostAddress = peer.host;
				this.hasFile = peer.isFile;
				/*if (this.hasFile){
					this.fmgr.splitFile();
				}*/
				this.pHandler.getPeer(peerId).setparts(this.fmgr.getCurrentAvailableParts());
				seq = false;
			}
			if (seq)
				peers.add(peer);
		}


		UnchokeRegularScheduler sTask = new UnchokeRegularScheduler(this.peerId, config.opsUnchokeIntvl, config.unchokeIntvl, config.nofNeighbour, this.pHandler );
		sTask.run();

	}

	@Override
	public void run(){
		// TODO Auto-generated method stub
		System.out.println("Start server on port "+ this.peerPort);
    	ServerSocket listener = null;
		try {
			listener = new ServerSocket(this.peerPort);
			LogConfig.getLogRecord().debugLog("server running on port "+this.peerPort);
			SocketConnectionHandler connection = null;
			while(!isterminate) {
				try {
						connection  = new SocketConnectionHandler(this.peerId, listener.accept(), pHandler, this.fmgr);
						LogConfig.getLogRecord().debugLog("Client "  + this.peerId + " is connected!");
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
				LogConfig.getLogRecord().debugLog("Requesting socket Host= "+ peer.host+"and port= "+peer.port);
				requestSocket = new Socket(peer.host, peer.port);
				SocketConnectionHandler connection  = new SocketConnectionHandler(this.peerId, peer.id,requestSocket, pHandler, this.fmgr);
				LogConfig.getLogRecord().debugLog("Connected to "+peer.host+" in port "+ peer.port);
				LogConfig.getLogRecord().connectTo(peer.id);
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
			//HashSet<Integer> activepeers = new HashSet<Integer>();
			//activepeers.addAll(this.pHandler.getPeersList());
			while(!isterminate){
				int count = 0;
				for (int id : this.pHandler.getPeersList()) {
				  //  Integer id = i.next();
					LogConfig.getLogRecord().debugLog("Peerd Id:"+id+", isfile= "+this.pHandler.getPeer(id).isFile);
				    if (this.pHandler.getPeer(id).isFile){
						//SocketConnectionHandler con = this.pHandler.ConnectionTable.get(id);
						//if (con!= null && con.msgQueue.size() <=0)
							//con.terminate();
							count++;
						//i.remove();
					}
				}
				LogConfig.getLogRecord().debugLog("Peer list size :"+this.pHandler.getPeersList().size()+", count= "+count);
				if (this.pHandler.getPeersList().size()==count)
					isterminate = true;
				TimeUnit.SECONDS.sleep(10); //fix this
				/*for (int id: activepeers){
					if (this.pHandler.getPeer(id).isFile && this.hasFile){
						SocketConnectionHandler con = this.pHandler.ConnectionTable.get(id);
						if (con!= null)
							con.terminate();
						activepeers.remove(id);
					}
					if (activepeers.size()<=0)
						isterminate = true;
				}*/
				/*for (SocketConnectionHandler con : activeConnections){
					if (con.state ==ConnectionState.close){
						//isterminate= true;
					}

				}*/
			}
		} catch(Exception e){
			System.err.println("error here: "+e);
		}
		terminateall();

		fmgr.deletedparts();
	}

	public void terminateall(){
		for (SocketConnectionHandler con : activeConnections){
			if (con !=null){
				con.terminate();
			}
		}
		isterminate= true;
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
