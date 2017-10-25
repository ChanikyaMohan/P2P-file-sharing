package filemanagement;

import static org.junit.Assert.*;

import org.junit.Test;

import filemanagement.FileSplit;

public class FileSplitTest {

	@Test
	public void pickUpConfigProperties() {
		FileSplit filesp =  new FileSplit();
		filesp.init();
		assertNotNull("config is null",filesp.cfg);
		System.out.println("Config:"+filesp.cfg);
		filesp.splitFile();
	}

}
