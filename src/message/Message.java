package message;

public class Message {

	/* Enum to handle type of the message
	 * each enum type value is type casted to byte to limit it's size to one byte
	 * getCode() - returns the code of given type object
	 * getType() - return the type of given code
	 */
	public enum Type{
		CHOKE ((byte) 0),
	    UNCHOKE ((byte) 1),
	    INTERESTED ((byte) 2),
	    NOT_INTERESTED ((byte) 3),
	    HAVE ((byte) 4),
	    BIT_FIELD ((byte) 5),
	    REQUEST ((byte) 6),
	    PIECE ((byte) 7);

		final byte code;

		Type(byte code){
			this.code = code;
		}

		public byte getCode(){
			return this.code;
		}

		public Type getType(byte code){
			Type[] all_types = Type.values();
			Type t = null;
			for(int i=0;i<all_types.length;i++){
				Type type = all_types[i];
				if(type.code == code){
					t = type;
					break;
				}
			}
			return t;
		}
	}

	public int msg_length;
	public Type msg_type = null;
	public byte[] msg_payload = null;

	//why this?
	public String msg= null;

	//for msgs without payload
	Message(Type type){
		this.msg_type = type;
	}

	//for msgs with payload
	Message(Type type, byte[] payload){
		if(payload == null){
			this.msg_length = 0;
		} else {
			this.msg_length = payload.length;
		}
		this.msg_type = type;
		this.msg_payload = payload;
	}

	public int getLength(){
		return this.msg_length;
	}

	public Type getType(){
		return this.msg_type;
	}

	public byte[] getPayload(){
		return this.msg_payload;
	}

}
