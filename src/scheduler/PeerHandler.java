package scheduler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import init.Initialization;
import init.Peer;


public class PeerHandler implements Initialization {

	private HashMap<Integer, Peer> _peerTable; //to get peer object based on peer ID;
	private HashSet<Integer> _preferredPeers; //maintain set of preferred peers
	private HashSet<Integer> _unChokedPeers;

	public PeerHandler(){
		_peerTable = new HashMap<Integer, Peer>();
		_preferredPeers = new HashSet<Integer>();
		_unChokedPeers = new HashSet<Integer>();

	}

	@Override
	public void init() {
		//todo some intialization


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

	public List<Integer> getunChokedPeers(){
		List<Integer> list = new ArrayList<Integer>(_unChokedPeers);
		return list;
	}
	
	public boolean isChoked(int peerId){
		return (!_unChokedPeers.contains(peerId));
	}

	public void insertChokedPeer(Peer p){
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
		_peerTable.clear();
		_preferredPeers.clear();
		_unChokedPeers.clear();

	}

}
