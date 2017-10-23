package filemanagement;

import java.io.*;

import init.CommonConfig;

/* FileMerge takes name of the file to while all the split files should be merged
 * initProperties() - takes output file name and create cfg object
 * mergeFiles() - looks into splitParts folder reads each file and writes into output file
 */
public class FileMerge {
	public static File outFile = null;
	public static String outFileName = null;
	public static CommonConfig cfg = null;

	public FileMerge(String oFName){
		outFileName = oFName;
		outFile = new File(oFName);
	}

	/* here cfg object can be used to check the size of combined file
	 * whether it is equal to all split files combined
	 * whether the size of each piece is equal to given value or not etc
	 */

	public static void initProperties(){
		//create a config object with the output file name
		//initialize the properties
		cfg = new CommonConfig(outFileName);
		cfg.init();
	}

	public static void mergeFiles(){
	    try {
	    	FileOutputStream outFileStream = new FileOutputStream(outFile);
	    	FileInputStream inpFile = null;

	    	//name of folder containing all the split files
	    	//listFiles() will return all the files in the folder
	    	File splitFilesFolder = new File("splitParts");
	    	File[] listOfFiles = splitFilesFolder.listFiles();

	    	//read each file in the folder and write the buffer contents to outputstream
	        for (File SplitFile : listOfFiles) {
	        	inpFile = new FileInputStream(SplitFile);
	        	int fileSize = (int) SplitFile.length();
	        	byte[] buffer = new byte[fileSize];
	            int lengthRead = inpFile.read(buffer, 0, fileSize);
	            if(lengthRead > 0){
	            	outFileStream.write(buffer);
	            	outFileStream.flush();
	            }
	            inpFile.close();
	        }
	        outFileStream.close();
	    } catch (Exception e) {
	       System.out.println("Error occured while merging file");
	    }
	}
}
