package init;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Peer {
	public int port;
	public String host;
	public int id;
	public boolean isFile;
	private AtomicInteger _downloadrate;
	private boolean _isChoke; //not use
	private boolean _isOptunchoke; //not use
	public BitSet availableParts;
	public int rate;
	public Peer(int id, String host, int port, boolean isfile){
		this.id = id;
		this.host = host;
		this.port = port;
		this.isFile = isfile;
		this._downloadrate = new AtomicInteger(0);
		this.availableParts = new BitSet();
		this.Choke();
		this.OptChoke();
	}
	public int get_downloadrate() {
		this.rate =  _downloadrate.getAndSet(0);
		return this.rate;
	}

	public void setparts(BitSet b){
		availableParts.or(b);
	}

	public void setAvailablePartsIndex(int index){
		if (index > availableParts.size())
			return;
		availableParts.set(index);
	}

	public BitSet getRequiredPart(BitSet b){
		BitSet r = new BitSet(availableParts.size()) ;
		r.or(availableParts);
		r.flip(0, r.size());
		r.and(b); // should flip the available bits and do and with the bitfield received
		return r;
	}
	public int get_rate() {
		return this.rate;
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
