package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Piece extends Message{
	public Piece(byte[] pieceIndex, byte[] pieceContent){
		super.msg_type = Type.PIECE;
		super.msg_payload = concatinateByteArrays(pieceIndex,pieceContent);
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

}
