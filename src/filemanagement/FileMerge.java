package filemanagement;

import java.io.*;
import java.util.Arrays;

import init.CommonConfig;
import init.Initialization;
import init.LogConfig;

/* FileMerge takes name of the file to while all the split files should be merged
 * initProperties() - takes output file name and create cfg object
 * mergeFiles() - looks into splitParts folder reads each file and writes into output file
 */
public class FileMerge implements Initialization{
	public File outFile = null;
	public String outFileName = null;
	public CommonConfig cfg = null;

	public FileMerge(String oFName){
		outFileName = oFName;
		outFile = new File(oFName);
	}

	/* here cfg object can be used to check the size of combined file
	 * whether it is equal to all split files combined
	 * whether the size of each piece is equal to given value or not etc
	 */

	@Override
	public void init() {
		// TODO Auto-generated method stub
		//create a config object with the output file name
		//initialize the properties
		cfg = new CommonConfig();
		cfg.init();
	}

	public void mergeFiles(){
	    try {
	    	FileOutputStream outFileStream = new FileOutputStream(outFile);
	    	FileInputStream inpFile = null;

	    	//name of folder containing all the split files
	    	//listFiles() will return all the files in the folder
	    	String splitDirectoryPath = System.getProperty("user.dir")+"/peer_1001";
	    	//File splitFilesFolder = new File(splitDirectoryPath);
	    	///File[] listOfFiles = splitFilesFolder.listFiles();
	    	//int numberOfFiles = listOfFiles.length;

	    	double inpFileLength = (double) cfg.fileSize;
			double splitPieceSize = (double) cfg.peiceSize;
			int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);
	    	//read each file in the folder and write the buffer contents to output stream
	        for (int i=0;i<nofSplits;i++) {
	        	LogConfig.getLogRecord().debugLog("Printing file : "+i+"_madhav.dat");
	        	//System.out.println("Printing file : "+i+"_madhav.dat");
	        	File f = new File(splitDirectoryPath+"/"+i+"_madhav.dat");
	        	inpFile = new FileInputStream(f);
	        	int fileSize = (int) f.length();
	        	byte[] buffer = new byte[fileSize];
	            int lengthRead = inpFile.read(buffer, 0, fileSize);
	            if(lengthRead > 0){
	            	outFileStream.write(buffer);
//	            	outFileStream.flush();
	            }
	            inpFile.close();
	        }
	        outFileStream.close();
	    } catch (Exception e) {
	    	LogConfig.getLogRecord().debugLog("Error occured while merging file");
	        //System.out.println("Error occured while merging file");
	    }
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}
}
