package message;

public class Have extends Message{
	public Have(byte[] pieceIndexField){
		super.msg_type = Type.HAVE;
		super.msg_payload = pieceIndexField;
	}
	public int getpieceIndex(){
		int x = java.nio.ByteBuffer.wrap(super.msg_payload).getInt();
		return x;
	}
}
