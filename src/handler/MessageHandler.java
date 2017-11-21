package handler;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import init.LogConfig;
import init.Peer;
import filemanagement.FileManager;
import filemanagement.FileSplit;
import handler.PeerHandler;
import message.*;
import message.Message.Type;

public class MessageHandler {
	private int remotepeerID;
	private int selfpeerID;
	private FileManager fmgr;
	public PeerHandler phandler;

	public MessageHandler(int self,int remote, FileManager fmgr) {
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
				LogConfig.getLogRecord().debugLog("Received unchoke message");
				selfpeer.Unchoke();
				LogConfig.getLogRecord().unchoked(remotepeerID);
				if (required.isEmpty()){
					// send Not interested message
					this.phandler.insertChokedPeer(remotepeer);
					return new NotInterested();
				} else {
					// send reuqest message of random piece index
					int index = pickRandomIndex(required);
					LogConfig.getLogRecord().debugLog("Required Index Request: "+index);
					selfpeer.setAvailablePartsIndex(index);
					byte[] b = ByteBuffer.allocate(4).putInt(index).array();
					return new Request(b);
				}
			case CHOKE:
				// do nothing for now
				selfpeer.Choke();
				LogConfig.getLogRecord().choked(remotepeerID);
				LogConfig.getLogRecord().debugLog("Received choke message");
				break;
			case INTERESTED:
				// should add remotepeer in the list of preferred peers
				LogConfig.getLogRecord().debugLog("Received interested message addding to preferred");
				LogConfig.getLogRecord().receivedInterested(remotepeerID);
				this.phandler.addPreferredPeer(remotepeerID);
				LogConfig.getLogRecord().changeOfPrefereedNeighbors(this.phandler.getPreferredPeers());
				return null;
				//break;
			case NOT_INTERESTED:
				// should remove remotepeer in the list of preferred peers
				LogConfig.getLogRecord().receivedNotInterested(remotepeerID);
				this.phandler.removePreferredPeer(remotepeerID);
				LogConfig.getLogRecord().changeOfPrefereedNeighbors(this.phandler.getPreferredPeers());
				return null;
				//break;
			case HAVE:
				Have h = (Have)msg;
				int index = h.getpieceIndex();
				LogConfig.getLogRecord().receivedHave(remotepeerID, index);
				remotepeer.setAvailablePartsIndex(index);
				if (selfpeer.isFile && fmgr.checkIfAllPiecesAreReceived()){
					SocketConnectionHandler con = this.phandler.ConnectionTable.get(this.selfpeerID);
					if (con!= null)
						con.terminate();
				}
				/*if (required.get(index)){
					return new Interested(); //interesting piece
				} else {
					return new NotInterested(); //already have the piece
				}*/
				break;
			case BIT_FIELD:
				Bitfield bf = (Bitfield)msg;
				LogConfig.getLogRecord().debugLog("Bitfield recieved :"+ bf.getBitSet());
				if (selfpeer.getRequiredPart(bf.getBitSet()).isEmpty()){

					return new NotInterested();
				} else {
					remotepeer.setparts( bf.getBitSet());
					return new Interested();
				}

				//break;
			case REQUEST:
				Request r = (Request)msg;
				LogConfig.getLogRecord().debugLog("Request Received for index :"+r.getRequestIndex());
				return new Piece(r.msg_payload,fmgr.getBytefromtheIndex(r.getRequestIndex()));
				//break;
			case PIECE:
				Piece p = (Piece)msg;
				int i = p.getpieceIndex();
				fmgr.savePiece(i,p.getPieceContent());
				LogConfig.getLogRecord().pieceDownloaded(selfpeerID, i, this.phandler.getPeer(selfpeerID).availableParts.cardinality());
				sendHave(i);
				remotepeer.set_downloadrate(p.getPieceContent().length);
				int inx = pickRandomIndex(required);
				if (!selfpeer.ischoke() && inx >=0){
					selfpeer.setAvailablePartsIndex(inx);
					byte[] b = ByteBuffer.allocate(4).putInt(inx).array();
					return new Request(b);
				}
				break;
			case HANDSHAKE:
				//after handshake get the bitfield of selfpeer and send it to remote
				LogConfig.getLogRecord().debugLog("Sending bitfield of parts: "+selfpeer.availableParts);
				MESSAGE = new Bitfield(selfpeer.availableParts);
				LogConfig.getLogRecord().debugLog("array to bitset"+BitSet.valueOf(MESSAGE.msg_payload));
				break;
			default: LogConfig.getLogRecord().debugLog("Illeagal Type of message recieved");
		}

		return MESSAGE;
	}

	public int pickRandomIndex(BitSet required){
		List<Integer> indexes = new ArrayList<Integer>();
		Random randomGenerator = new Random();
		for (int i = required.nextSetBit(0); i != -1; i = required.nextSetBit(i + 1)) {
		    indexes.add(i);
		}
		if (indexes.size()> 0){
		int index = randomGenerator.nextInt(indexes.size());
			return indexes.get(index);
		} else
			return -1;
	}

	public void sendHave(int index){
		//List<SocketConnectionHandler> cons = this.phandler.ConnectionTable.values();
		for (int  key :  this.phandler.ConnectionTable.keySet()){
			if (this.selfpeerID != key){
				SocketConnectionHandler con = this.phandler.ConnectionTable.get(key);
				if (con !=null){
					//con.terminate();
					con.send(new Have(index));
				}
			}
		}
	}

}
