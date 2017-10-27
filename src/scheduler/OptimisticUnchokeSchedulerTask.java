package scheduler;

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
			this.pHandle.addOptunChokedPeer(chokelist.get(randompeer));
		}


	}

}
