package message;

public class Bitfield extends Message{
	public Bitfield(byte[] bitFieldBuffer){
		super.msg_type = Type.BIT_FIELD;
		super.msg_payload = bitFieldBuffer;
	}
}
