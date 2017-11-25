package iostream;
import java.util.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

import init.LogConfig;
import message.Handshake;
import message.Message;
import message.Message.Type;

public class IOStreamReader extends DataInputStream implements ObjectInput{
	private boolean hanshk_recvd =false;
	public IOStreamReader(InputStream in) throws IOException, SecurityException {
		super(in);
		// TODO Auto-generated constructor stub
	}


	public Message readInstanceOf() throws ClassNotFoundException, IOException{
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
			msg.read(this,b.length-1);
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


	@Override
	public Object readObject() throws ClassNotFoundException, IOException {
		if (!hanshk_recvd){
			byte [] b = new byte[available()] ;
			//readfully(b,0,)
			read(b);
			//reset();
			//System.out.println("b ="+b.toString()+" b.length = "+b.length);
			//System.out.println(b.toString());
			if(b.length >= 32)
			{
				String s = new String (b, 0, 18);
				//LogConfig.getLogRecord().debugLog("received byte"+s);
				//LogConfig.getLogRecord().debugLog("b ="+Arrays.toString(b)+" b.length = "+b.length);
				if(s.equals("P2PFILESHARINGPROJ"))
				{
					hanshk_recvd = true;
					return new Handshake(byteArrayToInt(b, 28));
				}

			}
		} else {
			int len = readInt();
			//int len = fromByteArray(b);
			Message msg;
			//System.out.println("b ="+Arrays.toString(b)+" b.length = "+b.length);
			//Type type = Message.Type.getTypeFromCode(b[4]);
			//LogConfig.getLogRecord().debugLog("len ="+len);

			msg = Message.getMessage(Message.Type.getTypeFromCode(readByte()), len);
			//Passing message length - type of 1 byte
			msg.read(this, len-1);
			return msg;

		}
		return null;
	}

	int fromByteArray(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}




}
