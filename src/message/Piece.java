package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Piece extends Message{
	public Piece(byte[] pieceIndex, byte[] pieceContent){
		super.msg_type = Type.PIECE;
		super.msg_payload = concatinateByteArrays(pieceIndex,pieceContent);
		super.msg_length += super.msg_payload.length;
	}

	private byte[] concatinateByteArrays(byte[] pieceIndex, byte[] pieceContent) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream = new ByteArrayOutputStream( );
		try {
			stream.write(pieceIndex);
			stream.write(pieceContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stream.toByteArray();
	}

	public int getpieceIndex(){
		byte[] pieceIndex = new byte[4];
		for(int i=0;i<4;i++){
			pieceIndex[i] = super.msg_payload[i];
		}
		ByteBuffer buf = ByteBuffer.wrap(pieceIndex);
		return buf.getInt();
	}

	public byte[] getPieceContent(){
		byte[] pieceContent = new byte[super.msg_length - 5]; // excluding msg_type length and pieceIndex length
		for(int i=4;i<super.msg_payload.length;i++){
			pieceContent[i] = super.msg_payload[i];
		}
		return pieceContent;
	}
}
