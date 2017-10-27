package handler;

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
	private LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();
	private PeerHandler phandler;

	public SocketConnectionHandler(int peerId,Socket socket, PeerHandler p){
			this.socket = socket;
			this.peerId = peerId;
			this.remotepeerId = -1;
			this.isunchoked = false;
			this.state = ConnectionState.initiated;
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
	public SocketConnectionHandler(int peerId,int remotePeer, Socket socket, PeerHandler p){
		this(peerId, socket, p);
		this.remotepeerId = remotePeer;

	}

	@Override
	public void run() {
		new Thread() {

            @Override
            public void run() {
            	Message msg = null;
            	// listen to incoming message
            	while(!socket.isClosed()){
					while ((msg = msgQueue.poll()) != null) {
					    System.out.println("Sending message....");
					    SendMessage(msg);
					}
            	}
            }
		}.start();

		if (socket == null)
			System.out.println("socket is null ");
		if(!socket.isClosed()){
			Message message =null;
			System.out.println("sending handshake from "+this.peerId);
			send(new Handshake(this.peerId));
			//send(new Interested());
			try{
				while(!socket.isClosed())
				{
					//receive the message sent from the client
					try {
						message = (Message)in.readObject();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						socket.isClosed();
						state = ConnectionState.close;
					}

					//show the message to the user
					if (message == null)
						continue;
					if (message instanceof Handshake){
						Handshake h = (Handshake) message;
						System.out.println("Handshake Message Received peerid: "+h.peerID);
						if (this.remotepeerId == -1 || this.remotepeerId == h.peerID){
							state = ConnectionState.connected;
							this.remotepeerId = h.peerID;
							System.out.println("Connected to peer : "+h.peerID);
							if (phandler.ConnectionTable.get(this.remotepeerId)!=null){
								//do something with the old connection
							}
							phandler.ConnectionTable.put(this.remotepeerId, this); //add the new socket connection for the remot peer
						}
					} else {
						System.out.println("other message received"+message.msg_type);
					}

					/*if (message.equals("P2PFILESHARINGPROJ")  && state != ConnectionState.connected){
						//msgQueue.add(new Handshake());
						state = ConnectionState.connected;

						System.out.println("Connected ");
					}*/
					//Capitalize all letters in the message
					//String MESSAGE = message.toUpperCase();
					//send MESSAGE back to the client
					//sendMessage(MESSAGE);
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
					classnot.printStackTrace();
					socket.isClosed();
					state = ConnectionState.close;
			}


		} else {
			System.out.println("Connection is closed");
			state = ConnectionState.disconnected;
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	public void send(Message msg){
		msgQueue.add(msg);
	}

	//send a message to the output stream
		public void SendMessage(Message message)
		{

			try{
				out.writeObject(message);
				out.flush();
				System.out.println("Send message: " + message + " to Client " + peerId);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}



}
