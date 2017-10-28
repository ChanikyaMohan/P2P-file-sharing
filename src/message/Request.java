package message;

public class Request extends Message{
	public Request(byte[] requestingPieceIndexField){
		super.msg_type = Type.REQUEST;
		super.msg_payload = requestingPieceIndexField;
	}
	public int getRequestIndex(){
		int x = java.nio.ByteBuffer.wrap(super.msg_payload).getInt();
		return x;
	}
}
