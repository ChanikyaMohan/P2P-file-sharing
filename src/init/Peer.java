package init;

public class Peer {
	public int port;
	public String host;
	public int id;
	public boolean isFile;
	public Peer(int id, String host, int port, boolean isfile){
		this.id = id;
		this.host = host;
		this.port = port;
		this.isFile = isfile;

	}
}
