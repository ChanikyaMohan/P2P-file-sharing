import static org.junit.Assert.*;

import org.junit.Test;


public class peerProcessClientTest {

	@Test
	public void testPeerProcess() {
		
		peerProcess p2 = new peerProcess(1002);
		p2.init();
		p2.start();
		/*while(true){

		}*/

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
