import static org.junit.Assert.*;

import org.junit.Test;


public class peerProcessServerTest {

	@Test
	public void testPeerProcess() {
		//fail("Not yet implemented");
		peerProcess p1 = new peerProcess(1001);
		p1.init();
		Thread t1 = new Thread(p1);
        t1.start();
		p1.start();
		//sleep(5);
		/*try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
		/*peerProcess p2 = new peerProcess(1002);
		p2.init();
		p2.ConnectPeers();*/
		

		//Thread t2 = new Thread(p1);
        //t2.start();


	}

	@Test
	public void testInit() {
		//peerProcess p = new peerProcess(1);
		//p.init();
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
	}

}
