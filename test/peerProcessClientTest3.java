import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class peerProcessClientTest3 {

	@Test
	public void testPeerProcess() throws InterruptedException {

		peerProcess p2 = new peerProcess(1004);
		p2.init();
		Thread t1 = new Thread(p2);
        t1.start();
		p2.start();
		int i =0;
		while(!p2.isterminate){
			TimeUnit.SECONDS.sleep(10);
			System.out.println("waiting... i ="+i);
			i++;
		}
		System.out.println("process terminated 1002");


	}

	/*@Test
	public void testInit() {
		//peerProcess p = new peerProcess(1);
		//p.init();

		peerProcess p2 = new peerProcess(1002);
		p2.init();
	}

	@Test
	public void testRun() {
		//fail("Not yet implemented");
	}

	@Test
	public void testConnectPeers() {
		//fail("Not yet implemented");

	}

	@Test
	public void testReload() {
		//fail("Not yet implemented");
	}*/

}
