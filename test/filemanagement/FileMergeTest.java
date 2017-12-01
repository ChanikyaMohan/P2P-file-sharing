package filemanagement;

import java.io.File;

import org.junit.Test;

import filemanagement.FileMerge;

public class FileMergeTest {

	@Test
	public void testMergeFiles() {
		FileMerge filem =  new FileMerge(1001, 1280,"20MB.zip");
		//filem.outFileName = "madhav.jpg";
		//filem.outFile = new File(filem.outFileName);
		//filem.init();
		filem.mergeFiles();

	}

}