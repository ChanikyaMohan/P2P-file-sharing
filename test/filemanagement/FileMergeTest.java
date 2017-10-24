package filemanagement;

import org.junit.Test;

import filemanagement.FileMerge;

public class FileMergeTest {

	@Test
	public void testMergeFiles() {
		FileMerge filem =  new FileMerge("mergedfile.pdf");
		filem.initProperties();
		System.out.println("Config:"+filem.cfg);
		filem.mergeFiles();

	}

}