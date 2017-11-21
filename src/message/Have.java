package message;

import java.nio.ByteBuffer;

public class Have extends Message{
	public Have(byte[] pieceIndexField){
		super.msg_type = Type.HAVE;
		super.msg_payload = pieceIndexField;
		super.msg_length += super.msg_payload.length;
	}
	public Have(){
		super.msg_type = Type.HAVE;
	}

	public Have(int index){
		super.msg_type = Type.HAVE;
		super.msg_payload =  ByteBuffer.allocate(4).putInt(index).array();
		super.msg_length += super.msg_payload.length;
	}
	public int getpieceIndex(){
		int x = java.nio.ByteBuffer.wrap(super.msg_payload).getInt();
		return x;
	}
}
