package message;

import iostream.IOStreamReader;
import iostream.IOStreamWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import init.LogConfig;

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
	    PIECE ((byte) 7),
	    HANDSHAKE ((byte) 8);

		final byte code;

		Type(byte code){
			this.code = code;
		}

		public byte getCode(){
			return this.code;
		}

		public static Type getTypeFromCode(byte code){
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

	//default length is 1 because all messages have a type field irrespective of whether they have payload or not
	public int msg_length = 1;
	public Type msg_type = null;
	public byte[] msg_payload = null;


	//why this?
	public String msg= null;

	//default constructor
	public Message(){}

	//for msgs without payload
	public Message(Type type){
		this.msg_type = type;
	}

	//for msgs with payload
	public Message(Type type, byte[] payload){
		//msg_length will be 1 when there is no payload
		// will be payload.length + 1 when there is payload
		//the additional 1 byte is of variable type
		if(payload == null){
			this.msg_length = 1;
		} else {
			this.msg_length = payload.length+1;
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

	//Method to return a message object according to the message type
	public static Message getMessage (Type type, int length) throws ClassNotFoundException
	{
		if(type == Type.CHOKE)
			return new Choke();
		else if(type == Type.BIT_FIELD)
			return new Bitfield(new BitSet());
		else if(type == Type.HAVE)
			return new Have();
		else if(type == Type.INTERESTED)
			return new Interested();
		else if(type == Type.NOT_INTERESTED)
			return new NotInterested();
		else if(type == Type.PIECE)
			return new Piece();
		else if(type == Type.REQUEST)
			return new Request();
		else if(type == Type.UNCHOKE)
			return new Unchoke();
		else
		{
			LogConfig.getLogRecord().debugLog("Message Type not found");
			throw new ClassNotFoundException("Message Type not found");
		}

	}

	//Method to read from OubjectInputStream
	public void read (IOStreamReader ioStreamReader, int length) throws IOException
	{
		if (length>0){
			msg_payload = new byte[length];
	        if (msg_payload!=null && msg_payload.length>0)
	        	ioStreamReader.readFully(msg_payload, 0, length);
	           // System.arraycopy(buf, pos, msg_payload, 0, length);
	        else
	        {
	        	LogConfig.getLogRecord().debugLog("Payload is empty");
	        	throw new IOException("Payload is empty");
	        }
		}
    }

	//Method to write to ObjectOutputStream
	public void write (IOStreamWriter ioStreamWriter) throws IOException
	{
		LogConfig.getLogRecord().debugLog("writing message");
		byte buf [] = ByteBuffer.allocate(4+msg_length).putInt(msg_length).array();
		buf[4] = msg_type.getCode();
		//LogConfig.getLogRecord().debugLog("byte output buffer ="+Arrays.toString(buf));
		if (msg_payload!=null && msg_payload.length>0) {
			//LogConfig.getLogRecord().debugLog("sending msg_payload ="+Arrays.toString(msg_payload)+" msg_payload.length = "+msg_payload.length);
			System.arraycopy(msg_payload, 0, buf, 5, msg_payload.length);
		}
		//LogConfig.getLogRecord().debugLog("output buffer ="+Arrays.toString(buf));
		ioStreamWriter.write(buf);
		//byte []buf = new byte[4+1+msg_payload.length];
		//ioStreamWriter.writeByte(msg_length);
		//writeintgeter(ioStreamWriter, msg_length);
		//ioStreamWriter.wr
		/*ioStreamWriter.writeByte(msg_type.getCode());
		if (msg_payload!=null && msg_payload.length>0) {
			System.out.println("sending msg_payload ="+Arrays.toString(msg_payload)+" msg_payload.length = "+msg_payload.length);
			ioStreamWriter.write(msg_payload, 0, msg_payload.length);
		}*/
	}
	private void writeintgeter(IOStreamWriter ioStreamWriter, int v) throws IOException{
		byte arr[] = ByteBuffer.allocate(4).putInt(v).array();
		//LogConfig.getLogRecord().debugLog("int array  ="+Arrays.toString(arr));
		ioStreamWriter.write(arr, 0, 4);
		//for (byte b : arr){
		//	ioStreamWriter.writeByte(b);
		//}
	}
}
