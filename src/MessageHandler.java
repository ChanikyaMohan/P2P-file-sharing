import java.util.BitSet;

import init.Peer;
import handler.PeerHandler;
import message.*;
import message.Message.Type;

public class MessageHandler {
	private int remotepeerID;
	private int selfpeerID;
	public PeerHandler phandler;

	public MessageHandler(int self,int remote) {
		this.selfpeerID = self;
		this.remotepeerID =remote;
		this.phandler = PeerHandler.getInstance();
	}

	public Message handleRequest(Message msg) {
		// TODO Auto-generated method stub
		Message MESSAGE = null;
		Peer remotepeer = this.phandler.getPeer(remotepeerID);
		Peer selfpeer = this.phandler.getPeer(selfpeerID);

		BitSet required = selfpeer.getRequiredPart(remotepeer.availableParts);

		switch(msg.msg_type){
			case UNCHOKE:

				break;
			case CHOKE:

				break;
			case INTERESTED:
				break;
			case NOT_INTERESTED:
				break;
			case HAVE:
				break;
			case BIT_FIELD:
				break;
			case REQUEST:
				break;
			case PIECE:
				break;
			case HANDSHAKE:
				//after handshake get the bitfield of selfpeer and send it to remote
				MESSAGE = new Message(Type.BIT_FIELD,selfpeer.availableParts.toByteArray());
				break;
			default: System.out.println("Illeagal Type of message recieved");
		}

		return MESSAGE;
	}

}
