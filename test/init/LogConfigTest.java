package init;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogConfigTest {
	LogConfig logger=LogConfig.getLogRecord();
	@Test
	public void testSetLoggerForPeer() {

		//logger.setLoggerForPeer(1001);

		//fail("Not yet implemented");
	}

	@Test
	public void testDebugLog() {
		logger.debugLog("Testing Debug Log");
		//fail("Not yet implemented");
	}

	@Test
	public void testChoked() {
		logger.choked(1002);
		//fail("Not yet implemented");
	}

}
