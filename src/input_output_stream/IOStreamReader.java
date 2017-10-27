package input_output_stream;
import java.util.*;
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
		byte [] b = (byte []) readObject();
		if(b.length >= 32)
		{
			String s = new String (b, 0, 17);
			if(s.equals("P2PFILESHARINGPROJ"))
			{
				return new Handshake(byteArrayToInt(b, 28));
			}
		}
		if(b.length > 4)
		{
			int length = byteArrayToInt(b,4);
			Message msg;
			//Type type = Message.Type.getTypeFromCode(b[4]);
			
			msg = Message.getMessage(Message.Type.getTypeFromCode(b[4]), length);
			//Passing message length - type of 1 byte
			msg.read(b,5,b.length-1);
			return msg;
		}
		return null;
	}	
		
    public static int byteArrayToInt(byte[] b, int offset) {

        int value = 0;

        for (int i = 0; i < 4; i++) {

            int shift = (4 - 1 - i) * 8;

            value += (b[i + offset] & 0x000000FF) << shift;

        }

        return value;

    }
	
	
}
