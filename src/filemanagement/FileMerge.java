package filemanagement;

import java.io.*;

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
	private CommonConfig cfg;
	private int noOfSplits;
	private int peerId;

	public FileMerge(int peerId,int noOfSplits, String outputFileName){
		this.peerId = peerId;
		this.noOfSplits = noOfSplits;
		this.outFileName = outputFileName;
		outFile = new File(System.getProperty("user.dir")+"/peer_"+peerId+"/"+outFileName);
		//outFile = new File(this.outFileName);
	}

	/* here cfg object can be used to check the size of combined file
	 * whether it is equal to all split files combined
	 * whether the size of each piece is equal to given value or not etc
	 */

	public FileMerge(String string) {
		// TODO Auto-generated constructor stub
		outFileName = string;
		//outFile = new File(outFileName);
		outFile = new File(System.getProperty("user.dir")+"/peer_"+peerId+"/"+outFileName);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		//init the output file name
		//outFileName = cfg.fileName;
//		outFile = new File(outFileName);
	}

	public void mergeFiles(){
	    try {
	    	FileOutputStream outFileStream = new FileOutputStream(outFile);
	    	FileInputStream inpFile = null;

	    	//name of folder containing all the split files
	    	//listFiles() will return all the files in the folder
	    	String splitDirectoryPath = System.getProperty("user.dir")+"/peer_"+peerId;
	    	//File splitFilesFolder = new File(splitDirectoryPath);
	    	///File[] listOfFiles = splitFilesFolder.listFiles();
	    	//int numberOfFiles = listOfFiles.length;
	    	//read each file in the folder and write the buffer contents to output stream
	        for (int i=0;i<noOfSplits;i++) {
	        	//System.out.println("Printing file : "+i+"_"+outFileName);
	        	//System.out.println("Printing file : "+i+"_madhav.dat");
	        	File f = new File(splitDirectoryPath+"/"+i+"_"+outFileName);
	        	inpFile = new FileInputStream(f);
	        	int fileSize = (int) f.length();
	        	byte[] buffer = new byte[fileSize];
	            int lengthRead = inpFile.read(buffer, 0, fileSize);
	            if(lengthRead > 0){
	            	outFileStream.write(buffer);
//	            	outFileStream.flush();
	            }
	            inpFile.close();
	            //f.delete();
	        }
	        outFileStream.close();

	        //LogConfig.getLogRecord().debugLog("File Merge is sucessful");
	    } catch (Exception e) {
	    	//LogConfig.getLogRecord().debugLog("Error occured while merging file");
	        //System.out.println("Error occured while merging file");
	    }
	}

	public void deleteFiles(){
	    try {

	    	//name of folder containing all the split files
	    	//listFiles() will return all the files in the folder
	    	String splitDirectoryPath = System.getProperty("user.dir")+"/peer_"+peerId;
	    	//File splitFilesFolder = new File(splitDirectoryPath);
	    	///File[] listOfFiles = splitFilesFolder.listFiles();
	    	//int numberOfFiles = listOfFiles.length;
	    	//read each file in the folder and write the buffer contents to output stream
	        for (int i=0;i<noOfSplits;i++) {
	        	LogConfig.getLogRecord().debugLog("Printing file : "+i+"_"+outFileName);
	        	//System.out.println("Printing file : "+i+"_madhav.dat");
	        	File f = new File(splitDirectoryPath+"/"+i+"_"+outFileName);
	            f.delete();
	        }

	        LogConfig.getLogRecord().debugLog("File Merge is sucessful");
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
