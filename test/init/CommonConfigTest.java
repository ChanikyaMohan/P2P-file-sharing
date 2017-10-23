package init;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommonConfigTest {

	@Test
	public void testInit() {
		CommonConfig c = new CommonConfig("Common.cfg");
		c.init();
		c.reload();
	}

}
