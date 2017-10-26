package message;

import java.util.BitSet;

public class Bitfield extends Message{
	public Bitfield(BitSet bits){
		super.msg_type = Type.BIT_FIELD;
		super.msg_payload = bits.toByteArray();
	}
	public BitSet getBitSet(){
		return BitSet.valueOf(super.msg_payload);
	}
}
