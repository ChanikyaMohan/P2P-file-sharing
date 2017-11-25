package handler;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import util.AtomicBitSet;
import init.CommonConfig;
import init.Initialization;
import init.Peer;
import init.PeerInfoConfig;


public class PeerHandler implements Initialization {

	private HashMap<Integer, Peer> _peerTable; //to get peer object based on peer ID;
	public HashMap<Integer, SocketConnectionHandler> ConnectionTable; //peer id to SocketConnectionHandlertable ;
	private Set<Integer> _preferredPeers; //maintain set of preferred peers
	private Set<Integer> _unChokedPeers; //maintain list of unchoked peers
	private int OptunChokePeer;
	public static PeerInfoConfig pconfig;
	private static PeerHandler pHandler;
	//private BitSet _requiredParts;

	private PeerHandler(){
		_peerTable = new HashMap<Integer, Peer>();
		_preferredPeers = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
		_unChokedPeers = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
		ConnectionTable = new HashMap<Integer, SocketConnectionHandler>();
		this.OptunChokePeer = -1;

	}


	public static PeerHandler getInstance(){
        if(pHandler == null){
        	pHandler = new PeerHandler();
        	pconfig = new PeerInfoConfig();
    		pconfig.init();
    		pHandler.init(pconfig.peersList);

        }
        return pHandler;
    }

	/*public BitSet getRequiredParts(){
		return this._requiredParts;
	}


	public void setRequiredParts(BitSet set){
		this._requiredParts.and(set);
	}*/

	public void init(List<Peer>list) {
		//todo some intialization
		CommonConfig config = new CommonConfig();
		config.init();
		double inpFileLength = (double) config.fileSize;
		double splitPieceSize = (double) config.peiceSize;
		int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);
		//this.availableParts = new BitSet(nofSplits);

		for (Peer p : list){
			p.availableParts =  new BitSet(nofSplits);
			_peerTable.put(p.id, p);
			ConnectionTable.put(p.id, null);
		}
		//this._requiredParts = new AtomicBitSet(5);// put the length as the # of file parts

	}

	public List<Integer> getPeersList() {
		//todo some intialization
		List<Integer> list = new ArrayList<Integer>(_peerTable.keySet());
		return list;
	}

	public void addPreferredPeerList(List<Integer>list) {
		//todo some intialization
		for (int p : list){
			addPreferredPeer(p);
		}

	}

	public void insertPeer(Peer p ){
		_peerTable.put(p.id, p);

	}

	public Peer getPeer(int peerId){
		return _peerTable.get(peerId);
	}

	public boolean isPreferred(int peerId){
		return (_preferredPeers.contains(peerId));
	}

	public void insertPreferredPeer(Peer p){
		_preferredPeers.add(p.id);
	}

	public void addPreferredPeer(int p){
		_preferredPeers.add(p);
	}

	public void removePreferredPeer(int p){
		_preferredPeers.remove(p);
	}

	public List<Integer> getPreferredPeers(){
		List<Integer> list = new ArrayList<Integer>(_preferredPeers);
		return list;
	}


	public boolean isunChoked(int peerId){
		return (_unChokedPeers.contains(peerId));
	}

	public void insertunChokedPeer(Peer p){
		_unChokedPeers.add(p.id);
	}


	public void addunChokedPeer(int peerId){
		//Peer p = _peerTable.get(peerId);
		_peerTable.get(peerId).Unchoke();
		_unChokedPeers.add(peerId);
	}

	public void resetandaddunChokePeers(HashSet<Integer> list){
		for(int id : getPreferredPeers()){

			if (list.contains(id) ||  getOptunChokedPeer() == id){
				_peerTable.get(id).Unchoke();
			} else {
				_peerTable.get(id).Choke();
			}
		}
		this._unChokedPeers.clear();
		this._unChokedPeers.addAll(list);

	}


	public void addOptunChokedPeer(int peerId){
		//Peer p = _peerTable.get(peerId);
		_peerTable.get(peerId).OptunChoke();
		this.OptunChokePeer = peerId;
		_unChokedPeers.add(peerId);
	}

	public int getOptunChokedPeer(){
		return this.OptunChokePeer;

	}

	public List<Integer> getunChokedPeers(){
		List<Integer> list = new ArrayList<Integer>(_unChokedPeers);
		return list;
	}


	public boolean isChoked(int peerId){
		return (!_unChokedPeers.contains(peerId));
	}

	public void insertChokedPeer(Peer p){
		p.Choke();
		_unChokedPeers.remove(p.id);
	}

	public List<Integer> getChokedPeers(){
		List<Integer> list = new ArrayList<Integer>();
		for (int p: _preferredPeers){
			if (isChoked(p)){
				list.add(p);
			}
		}
		return list;
	}

	public void removePreferredPeer(Peer p){
		_preferredPeers.remove(p.id);
	}


	@Override
	public void reload() {
		_unChokedPeers.clear();

	}


	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
