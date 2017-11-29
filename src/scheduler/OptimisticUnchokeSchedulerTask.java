package scheduler;

import handler.PeerHandler;
import handler.SocketConnectionHandler;
import init.LogConfig;
import init.Peer;

import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import message.Unchoke;

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
			Peer oldoptunchoked = this.pHandle.getPeer(this.pHandle.getOptunChokedPeer());
			if (oldoptunchoked ==null || oldoptunchoked.id != optpeer){
				if (oldoptunchoked!= null)
					this.pHandle.getPeer(oldoptunchoked.id).OptChoke();
				SocketConnectionHandler sc = this.pHandle.ConnectionTable.get(optpeer);
				if (sc!=null)
					sc.send(new Unchoke()); //if the new peer is the new unchoked peer send unchoke message
				this.pHandle.addOptunChokedPeer(optpeer);
			}
			LogConfig.getLogRecord().changeOfOptimisticallyUnchokedNeighbors(optpeer);
			LogConfig.getLogRecord().debugLog("Optimistic Unchoked peer"+optpeer);
		}

	}

}
