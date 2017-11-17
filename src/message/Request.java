package message;

public class Request extends Message{
	public Request(byte[] requestingPieceIndexField){
		super.msg_type = Type.REQUEST;
		super.msg_payload = requestingPieceIndexField;
		super.msg_length += super.msg_payload.length;
	}
	public Request(){
		super.msg_type = Type.REQUEST;
	}
	public int getRequestIndex(){
		int x = java.nio.ByteBuffer.wrap(super.msg_payload).getInt();
		return x;
	}
}
