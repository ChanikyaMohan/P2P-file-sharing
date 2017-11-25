package handler;

import filemanagement.FileManager;
import filemanagement.FileSplit;
import init.LogConfig;
import iostream.IOStreamReader;
import iostream.IOStreamWriter;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.Handshake;
import message.Interested;
import message.Message;

/***
 *
 *
 *
 * Take care of a single socket connection between client and server
 *
 */

public class SocketConnectionHandler implements Runnable{
	//HashMap<Integer, Integer> peertable = new HashMap<Integer, Integer>();
	int peerId;
	boolean isconnection = false;
	final public Socket socket;
	public ConnectionState state;
	//private String message; //change to message class
	public int remotepeerId;
	private   IOStreamReader in;	//stream read from the socket
	private boolean isunchoked;
	private IOStreamWriter out;    //stream write to the socket
	public LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();
	private PeerHandler phandler;
	private MessageHandler msgHandler;
	private FileManager fmgr;

	public SocketConnectionHandler(int peerId,Socket socket, PeerHandler p,FileManager f ){
			this.socket = socket;
			this.peerId = peerId;
			this.remotepeerId = -1;
			this.isunchoked = false;
			this.state = ConnectionState.initiated;
			this.msgHandler = null;
			this.fmgr = f;
			phandler = p;

			try {
				out = new IOStreamWriter(socket.getOutputStream());
				out.flush();
				in = new IOStreamReader(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				out = null;
				in = null;
				e.printStackTrace();
			}


	}
	public SocketConnectionHandler(int peerId,int remotePeer, Socket socket, PeerHandler p, FileManager fmgr){
		this(peerId, socket, p, fmgr);
		this.remotepeerId = remotePeer;

	}

	@Override
	public void run() {
		new Thread() {

            @Override
            public void run() {
            	Message msg = null;
            	// listen to incoming message
            	while(!socket.isClosed() || state != ConnectionState.close){
            		try{
            			msg = msgQueue.poll();
						if (msg!= null) {
							//LogConfig.getLogRecord().debugLog("Sending message....");
						    SendMessage(msg);
						}
            		} catch(Exception e){
            			LogConfig.getLogRecord().debugLog("error here"+e);
            		}
            	}
            }
		}.start();

		if (socket == null)
			LogConfig.getLogRecord().debugLog("socket is null ");
		if(!socket.isClosed()){
			Message message =null;
			//LogConfig.getLogRecord().debugLog("sending handshake from "+this.peerId);
			send(new Handshake(this.peerId));
			//send(new Interested());
			try{
				while(!socket.isClosed() || state != ConnectionState.close)
				{
					//receive the message sent from the client
					try {
						message = (Message)in.readObject();

						if (message == null)
							continue;
						if (message instanceof Handshake){
							Handshake h = (Handshake) message;
							//LogConfig.getLogRecord().debugLog("Handshake Message Received peerid: "+h.peerID);
							if (this.remotepeerId == -1 || this.remotepeerId == h.peerID){
								state = ConnectionState.connected;
								this.remotepeerId = h.peerID;
								LogConfig.getLogRecord().isConnected(this.remotepeerId);
								//LogConfig.getLogRecord().debugLog("Connected to peer : "+h.peerID);
								if (phandler.ConnectionTable.get(this.remotepeerId)!=null){
									//do something with the old connection
								}
								phandler.ConnectionTable.put(this.remotepeerId, this); //add the new socket connection for the remot peer
								msgHandler = new MessageHandler(this.peerId,this.remotepeerId, this.fmgr);
							}
						}
						if (msgHandler != null) {
							//LogConfig.getLogRecord().debugLog(" Message received from peer("+this.remotepeerId+")"+message.msg_type);
							send(msgHandler.handleRequest(message));
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						socket.isClosed();
						state = ConnectionState.close;
					}

				}
			}
			catch(ClassNotFoundException classnot){
					//LogConfig.getLogRecord().debugLog("Data received in unknown format");
					//classnot.printStackTrace();
					socket.isClosed();
					state = ConnectionState.close;
			}


		} else {
			LogConfig.getLogRecord().debugLog("Connection is closed");
			state = ConnectionState.disconnected;
			try {
				socket.close();
				state = ConnectionState.close;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}


	}

	public void send(Message msg){
		if (msg == null)
			return;
		msgQueue.add(msg);
	}

	//send a message to the output stream
	public void SendMessage(Message message)
	{
		if (message == null)
			return;
		try{
			out.writeObject(message);
			out.flush();
			//LogConfig.getLogRecord().debugLog("Send message from("+this.peerId+"): " + message + " to Client " + this.remotepeerId);
		}
		catch(IOException ioException){
			//ioException.printStackTrace();
		}
	}

	public void terminate(){
		state = ConnectionState.close;
	}



}
