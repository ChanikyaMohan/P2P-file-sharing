package scheduler;

import handler.PeerHandler;

import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class OptimisticUnchokeSchedulerTask extends TimerTask{

	public PeerHandler pHandle;
	private Random _rand;
	public OptimisticUnchokeSchedulerTask(PeerHandler pHandle){
		this.pHandle = pHandle;
		this._rand = new Random();
	}
	@Override
	public void run() {
		// find the optimistic uchoke peer randomly
		List<Integer> chokelist = this.pHandle.getChokedPeers();
		if (chokelist.size()>0){
			int randompeer = _rand.nextInt(chokelist.size());
			int optpeer = chokelist.get(randompeer);
			this.pHandle.getPeer(optpeer).OptunChoke();
			this.pHandle.getPeer(this.pHandle.getOptunChokedPeer()).OptChoke();
			this.pHandle.addOptunChokedPeer(optpeer);
			System.out.println("Optimistic Unchoked peer"+optpeer);
		}

	}

}
