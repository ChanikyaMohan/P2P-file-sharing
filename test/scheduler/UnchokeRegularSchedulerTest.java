package scheduler;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import init.CommonConfig;
import init.PeerInfoConfig;

import org.junit.Test;

public class UnchokeRegularSchedulerTest {

	@Test
	public void testRegularScheduler() throws InterruptedException {
		//fail("Not yet implemented");
		CommonConfig config = new CommonConfig();
		config.init();
		PeerInfoConfig pconfig = new PeerInfoConfig();
		pconfig.init();
		PeerHandler p = PeerHandler.getInstance();
		p.init(pconfig.peersList);
		p.addPreferredPeerList(p.getPeersList());
		p.reload();
		UnchokeRegularScheduler sTask = new UnchokeRegularScheduler(config.opsUnchokeIntvl, config.unchokeIntvl, config.nofNeighbour, p );
		int i = 0;
		sTask.run(); while(i <10){
			TimeUnit.SECONDS.sleep(6);
			System.out.print("List of new unchoke peers:  ");
			for (int k: p.getunChokedPeers()){
				System.out.print(k+", ");
			}
			System.out.println();
			i++;
		}
	}


	@Test
	public void testUnchokeRegularScheduler() {
		CommonConfig config = new CommonConfig();
		config.init();
		PeerInfoConfig pconfig = new PeerInfoConfig();
		pconfig.init();
		PeerHandler p = PeerHandler.getInstance();
		p.init(pconfig.peersList);
		p.addPreferredPeerList(p.getPeersList());
		p.reload();
		UnchokeSchedulerTask utask = new UnchokeSchedulerTask(p, config.nofNeighbour);
		utask.run();

	}

}