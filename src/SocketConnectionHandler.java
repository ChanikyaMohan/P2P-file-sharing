
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.Handshake;
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
	public ConnectionState state = ConnectionState.initiated;
	//private String message; //change to message class
	public int remotepeerId;
	private   ObjectInputStream in;	//stream read from the socket
	private boolean isunchoked;
	private ObjectOutputStream out;    //stream write to the socket
	private LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();

	public SocketConnectionHandler(int peerId,Socket socket){
			this.socket = socket;
			this.peerId = peerId;
			this.remotepeerId = -1;
			this.isunchoked = false;

			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				out = null;
				in = null;
				e.printStackTrace();
			}


	}
	public SocketConnectionHandler(int peerId,int remotePeer, Socket socket){
		this(peerId, socket);
		this.remotepeerId = remotePeer;

	}

	@Override
	public void run() {
		new Thread() {

            @Override
            public void run() {
            	Message msg = null;
            	// listen to incoming message
            	while(true){
					while ((msg = msgQueue.poll()) != null) {
					    System.out.println("Sending message");
					    SendMessage(msg);
					}
            	}
            }
		}.start();

		if (socket == null)
			System.out.println("socket is null ");
		if(!socket.isClosed()){
			String message =null;
			System.out.println("sending handshake from "+this.peerId);
			if (this.remotepeerId != -1)
				msgQueue.add(new Handshake());
			try{
				while(!socket.isClosed())
				{
					//receive the message sent from the client
					try {
						message = (String)in.readObject();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						socket.isClosed();
						state = ConnectionState.disconnected;
					}
					//show the message to the user
					if (message == null)
						continue;
					System.out.println("Receive message: " + message + " from client " + peerId);

					if (message.equals("P2PFILESHARINGPROJ")  && state != ConnectionState.connected){
						//msgQueue.add(new Handshake());
						state = ConnectionState.connected;

						System.out.println("Connected ");
					}
					//Capitalize all letters in the message
					//String MESSAGE = message.toUpperCase();
					//send MESSAGE back to the client
					//sendMessage(MESSAGE);
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
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
				out.writeObject(message.msg);
				out.flush();
				System.out.println("Send message: " + message.msg + " to Client " + peerId);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}



}
