package handler;
import java.nio.ByteBuffer;
import java.util.BitSet;

import init.Peer;
import filemanagement.FileSplit;
import handler.PeerHandler;
import message.*;
import message.Message.Type;

public class MessageHandler {
	private int remotepeerID;
	private int selfpeerID;
	private FileSplit fmgr;
	public PeerHandler phandler;

	public MessageHandler(int self,int remote, FileSplit fmgr) {
		this.selfpeerID = self;
		this.fmgr = fmgr;
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
				// send request message for a piece
				System.out.println("Received unchoke message");
				selfpeer.Unchoke();
				if (required.isEmpty()){
					// send Not interested message
					this.phandler.insertChokedPeer(remotepeer);
					return new NotInterested();
				} else {
					// send reuqest message of random piece index
					int index = required.nextSetBit(0);
					System.out.println("Required Index Request: "+index);
					selfpeer.setAvailablePartsIndex(index);
					byte[] b = ByteBuffer.allocate(4).putInt(index).array();
					return new Request(b);
				}
			case CHOKE:
				// do nothing for now
				selfpeer.Choke();
				System.out.println("Received choke message");
				break;
			case INTERESTED:
				// should add remotepeer in the list of preferred peers
				System.out.println("Received interested message addding to preferred");
				this.phandler.addPreferredPeer(remotepeerID);
				return null;
				//break;
			case NOT_INTERESTED:
				// should remove remotepeer in the list of preferred peers
				this.phandler.removePreferredPeer(remotepeerID);
				return null;
				//break;
			case HAVE:
				Have h = (Have)msg;
				int index = h.getpieceIndex();
				if (required.get(index)){
					return new Interested(); //interesting piece
				} else {
					return new NotInterested(); //already have the piece
				}
			case BIT_FIELD:
				Bitfield bf = (Bitfield)msg;
				 System.out.println("Bitfield recieved :"+ bf.getBitSet());
				if (selfpeer.getRequiredPart(bf.getBitSet()).isEmpty()){

					return new NotInterested();
				} else {
					remotepeer.setparts( bf.getBitSet());
					return new Interested();
				}

				//break;
			case REQUEST:
				Request r = (Request)msg;
				 System.out.println("Request Received for index :"+r.getRequestIndex());
				return new Piece(r.msg_payload,fmgr.getBytefromtheIndex(r.getRequestIndex()));
				//break;
			case PIECE:
				Piece p = (Piece)msg;
				fmgr.savePiece(p.getpieceIndex(),p.getPieceContent());
				int inx = required.nextSetBit(0);
				if (!selfpeer.ischoke() && inx >=0){
					selfpeer.setAvailablePartsIndex(inx);
					byte[] b = ByteBuffer.allocate(4).putInt(inx).array();
					return new Request(b);
				}
				break;
			case HANDSHAKE:
				//after handshake get the bitfield of selfpeer and send it to remote
				System.out.println("Sending bitfield of parts: "+selfpeer.availableParts);
				MESSAGE = new Bitfield(selfpeer.availableParts);
				System.out.println("array to bitset"+BitSet.valueOf(MESSAGE.msg_payload));
				break;
			default: System.out.println("Illeagal Type of message recieved");
		}

		return MESSAGE;
	}

}
