import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class peerProcessServerTest {

	@Test
	public void testPeerProcess() throws InterruptedException {
		//fail("Not yet implemented");
		peerProcess p1 = new peerProcess(1001);
		p1.init();
		Thread t1 = new Thread(p1);
        t1.start();
		p1.start();
		int i = 0;
		while(!p1.isterminate){
			TimeUnit.SECONDS.sleep(10);
			System.out.println("waiting... i ="+i);
			i++;
		}
		System.out.println("process terminated 1001");
		//p1.isterminate = true;


	}



}
