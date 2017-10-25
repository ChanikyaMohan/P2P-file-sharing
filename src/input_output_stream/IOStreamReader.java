package input_output_stream;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import message.Handshake;
import message.Message;
import message.Message.Type;

public class IOStreamReader extends ObjectInputStream{
	
	public IOStreamReader(InputStream in) throws IOException, SecurityException {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	
	public Message readInstanceOf() throws ClassNotFoundException, IOException{		
		//Reading the first 4 bytes of the array which includes length of Message Type. 
		//Hence subtracting 1 from it to get the length of payload.
		int length = readInt()-1;
		Message msg;
		msg = Message.getMessage(Message.Type.getTypeFromCode(readByte()), length);
		msg.read(this);
		return msg;
	}
	
	
}
