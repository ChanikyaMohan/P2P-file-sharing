package init;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Peer {
	public int port;
	public String host;
	public int id;
	public boolean isFile;
	public boolean remotechoke;
	private AtomicInteger _downloadrate;
	private boolean _isChoke; //not use
	private boolean _isOptunchoke; //not use
	public BitSet availableParts;
	public int rate;
	public int noOfParts;
	public Peer(int id, String host, int port, boolean isfile){
		this.id = id;
		this.host = host;
		this.port = port;
		this.isFile = isfile;
		this._downloadrate = new AtomicInteger(0);
		this.availableParts = new BitSet();
		this.Choke();
		this.OptChoke();
		this.noOfParts = Integer.MAX_VALUE;
		this.remotechoke = true;
	}
	public int get_downloadrate() {
		this.rate =  _downloadrate.getAndSet(0);
		return this.rate;
	}



	public void setparts(BitSet b){
		availableParts.or(b);
		if(availableParts.cardinality() == this.noOfParts){
			isFile =  true;
		} else {
			isFile = false;
		}

	}


	public void setsaveparts(BitSet b){
		if(b.cardinality() == this.noOfParts){
			isFile =  true;
		} else {
			isFile = false;
		}
		availableParts = b;
	}

	public void settotalParts(int parts){
		//setAvailablePartsIndex(index);
		this.noOfParts = parts;
		this.availableParts = new BitSet(this.noOfParts);
	}

	public void setAvailablePartsIndex(int index){
		if (index > availableParts.size())
			return;
		availableParts.set(index);
		LogConfig.getLogRecord().debugLog("available parts size= "+this.noOfParts+" cardinallity="+availableParts.cardinality());
		if(availableParts.cardinality() == this.noOfParts){
			isFile =  true;
		} else {
			isFile = false;
		}
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
		this.rate = this._downloadrate.addAndGet(bytelenght);
		return this.rate;
	}
	public void Choke() {
		this._isChoke = true;
	}
	public void Unchoke() {
		this._isChoke = false;
	}

	public boolean isRemoteChoke() {
		return this.remotechoke;
	}

	public void RemoteChoke() {
		this.remotechoke = true;
	}
	public void RemoteUnchoke() {
		this.remotechoke = false;
	}
	public void OptChoke() {
		this._isOptunchoke = false;
		this._isChoke = true;
	}
	public void OptunChoke() {
		this._isOptunchoke = true;
		this._isChoke = false;
	}
	public boolean ischoke(){
		return this._isChoke;
	}

}
