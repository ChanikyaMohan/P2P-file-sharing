package init;

import java.util.concurrent.atomic.AtomicInteger;

public class Peer {
	public int port;
	public String host;
	public int id;
	public boolean isFile;
	private AtomicInteger _downloadrate;
	private boolean _isChoke;
	private boolean _isOptunchoke;
	public Peer(int id, String host, int port, boolean isfile){
		this.id = id;
		this.host = host;
		this.port = port;
		this.isFile = isfile;
		this._downloadrate = new AtomicInteger(0);
		this.Choke();
		this.OptChoke();
	}
	public int get_downloadrate() {
		return _downloadrate.getAndSet(0);
	}
	public int set_downloadrate(int bytelenght) {
		return this._downloadrate.addAndGet(bytelenght);
	}
	public void Choke() {
		this._isChoke = true;
	}
	public void Unchoke() {
		this._isChoke = false;
	}
	public void OptChoke() {
		this._isOptunchoke = false;
	}
	public void OptunChoke() {
		this._isOptunchoke = true;
	}

}
