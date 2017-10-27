package scheduler;

import init.Peer;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;

public class UnchokeSchedulerTask extends TimerTask {

	public PeerHandler pHandle;
	public int preferredN;
	private Random _rand;
	Queue<Peer> peerMinQueue;
	public UnchokeSchedulerTask(PeerHandler pHandle, int k){
		this.pHandle = pHandle;
		this._rand = new Random();
		this.preferredN = k;
		this.peerMinQueue = new PriorityQueue<>(downloadrateComparator);
	}


	@Override
	public void run() {
		// find the K max downloadable peers
		List<Integer> peers = this.pHandle.getPreferredPeers();
		for (int i=0; i < peers.size();i++){
			Peer p = this.pHandle.getPeer(peers.get(i));
			if (this.peerMinQueue.size() >= this.preferredN){
				Peer top = this.peerMinQueue.peek();
				if (top.rate == p.rate){
					if (this._rand.nextBoolean())
						this.peerMinQueue.offer(p);
				} else if (top.rate < p.rate){
					this.peerMinQueue.poll();
					this.peerMinQueue.offer(p);
				}
			} else {
				this.peerMinQueue.offer(p);
			}
		}
		this.pHandle.reload(); //clear unchoked list
		for (Peer p :peerMinQueue){
			this.pHandle.addunChokedPeer(p.id);
		}
		if (this.pHandle.getOptunChokedPeer() > 0){
			this.pHandle.addunChokedPeer(this.pHandle.getOptunChokedPeer()); //add the unchoked peer into the list
		}


	}

	public static Comparator<Peer> downloadrateComparator = new Comparator<Peer>(){

			@Override
			public int compare(Peer p1, Peer p2) {
	            return (int) (p1.rate - p2.rate);
	        }
	};

}
