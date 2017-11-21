package filemanagement;

import org.junit.Test;

import filemanagement.FileMerge;

public class FileMergeTest {

	@Test
	public void testMergeFiles() {
		FileMerge filem =  new FileMerge("mergedfile.jpg");
		filem.init();
		filem.mergeFiles();

	}

}