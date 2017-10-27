import message.*;

public class MessageHandler {

	public MessageHandler() {}

	public Message handleRequest(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.msg_type){
			case UNCHOKE:

				break;
			case CHOKE:

				break;
			case INTERESTED:
				break;
			case NOT_INTERESTED:
				break;
			case HAVE:
				break;
			case BIT_FIELD:
				break;
			case REQUEST:
				break;
			case PIECE:
				break;
			case HANDSHAKE:
				break;
			default: System.out.println("Illeagal Type of message recieved");
		}
		return null;
	}

}
