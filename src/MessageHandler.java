import java.nio.ByteBuffer;
import java.util.BitSet;

import init.Peer;
import handler.PeerHandler;
import message.*;

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
		Peer remotepeer = this.phandler.getPeer(remotepeerID);
		Peer selfpeer = this.phandler.getPeer(selfpeerID);
		switch(msg.msg_type){
			case UNCHOKE:
				// send request message for a piece
				BitSet required = selfpeer.getRequiredPart(remotepeer.availableParts);
				if (required.cardinality() <=0){
					// send Not interested message
					return new NotInterested();
				} else {
					// send reuqest message of random piece index
					int index = required.nextSetBit(0);
					selfpeer.setAvailablePartsIndex(index);
					byte[] b = ByteBuffer.allocate(4).putInt(index).array();
					return new Request(b);
				}
			case CHOKE:
				// do nothing for now
				System.out.println("Received choke message");
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
				break;
			default: System.out.println("Illeagal Type of message recieved");
		}
		return null;
	}

}
