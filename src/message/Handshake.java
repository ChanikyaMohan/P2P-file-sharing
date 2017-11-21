package message;

import iostream.IOStreamWriter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import init.LogConfig;

public class Handshake extends  Message{
		public String handhsake_header;
		public byte[] zero_bits = new byte[10];
		public int peerID;

		public Handshake(int peer_id){
			this.handhsake_header = "P2PFILESHARINGPROJ";
			this.peerID = peer_id;

			super.msg_type = Type.HANDSHAKE;
			super.msg_length = 32; //18 byte header + 10 byte zero bits + 4 byte peerID + 1 (Type of message)

			super.msg_payload = new byte[32];
			System.arraycopy(this.handhsake_header.getBytes(),0,super.msg_payload,0,18);
			System.arraycopy(convertIntToBytes(this.peerID),0,super.msg_payload,28,4);
		}

		private byte[] convertIntToBytes(int peerIDint) {
			// TODO Auto-generated method stub
			byte[] bytes = ByteBuffer.allocate(4).putInt(peerIDint).array();
			return bytes;
		}

		public int getpeerID(){
			return this.peerID;
		}

		@Override
		public void write (IOStreamWriter out) throws IOException
		{
			LogConfig.getLogRecord().debugLog("writing handshake message");
			if (msg_payload!=null && msg_payload.length>0)
				out.write(msg_payload, 0, msg_payload.length);

			LogConfig.getLogRecord().debugLog("done writing");
		}
}
