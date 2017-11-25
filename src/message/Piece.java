package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import init.LogConfig;
import iostream.IOStreamReader;

public class Piece extends Message{
	public Piece(byte[] pieceIndex, byte[] pieceContent){
		super.msg_type = Type.PIECE;
		concatinateByteArrays(pieceIndex,pieceContent);
		super.msg_length += super.msg_payload.length;
	}

	public Piece(){
		super.msg_type = Type.PIECE;
	}

	private void concatinateByteArrays(byte[] pieceIndex, byte[] pieceContent) {
		// TODO Auto-generated method stub
		//ByteArrayOutputStream stream = new ByteArrayOutputStream( );
		super.msg_payload  = new byte [4+pieceContent.length];
		System.arraycopy(pieceIndex, 0, super.msg_payload, 0, pieceIndex.length);
		System.arraycopy(pieceContent, 0, super.msg_payload, pieceIndex.length, pieceContent.length);
		//LogConfig.getLogRecord().debugLog(" after pieceContent lenth = "+ super.msg_payload.length);
		/*try {
			System.out.println("before  pieceContent lenth = "+ pieceContent.length);
			stream.write(pieceIndex);
			stream.write(pieceContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" after pieceContent lenth = "+ stream.toByteArray().length);
		return stream.toByteArray();*/
	}

	public int getpieceIndex(){
		byte[] pieceIndex = new byte[4];
		for(int i=0;i<4;i++){
			pieceIndex[i] = super.msg_payload[i];
		}
		ByteBuffer buf = ByteBuffer.wrap(pieceIndex);
		return buf.getInt();
	}

	public byte[] getPieceContent(){
		byte[] pieceContent = new byte[super.msg_payload.length - 4]; // excluding msg_type length and pieceIndex length
		System.arraycopy(super.msg_payload, 4, pieceContent, 0, pieceContent.length);
		/*for(int i=4;i<super.msg_payload.length;i++){
			pieceContent[i] = super.msg_payload[i];
		}*/
		//LogConfig.getLogRecord().debugLog("piececontentlen = "+pieceContent.length);
		return pieceContent;
	}

	@Override
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
}
