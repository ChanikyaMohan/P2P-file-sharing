package message;

import java.nio.ByteBuffer;

public class Handshake extends  Message{
		public String handhsake_header;
		public byte[] zero_bits = new byte[10];
		public int peerID;

		public Handshake(int peer_id){
			this.handhsake_header = "P2PFILESHARINGPROJ";
			this.peerID = peer_id;

			super.msg_type = Type.HANDSHAKE;
			super.msg_length = 33; //18 byte header + 10 byte zero bits + 4 byte peerID + 1 (Type of message)

			super.msg_payload = new byte[32];
			System.arraycopy(this.handhsake_header.getBytes(),0,super.msg_payload,0,18);
			System.arraycopy(convertIntToBytes(this.peerID),0,super.msg_payload,28,32);
		}

		private byte[] convertIntToBytes(int peerIDint) {
			// TODO Auto-generated method stub
			byte[] bytes = ByteBuffer.allocate(4).putInt(peerIDint).array();
			return bytes;
		}

}
