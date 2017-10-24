package message;

public class Have extends Message{
	public Have(byte[] pieceIndexField){
		super.msg_type = Type.HAVE;
		super.msg_payload = pieceIndexField;
	}
}
