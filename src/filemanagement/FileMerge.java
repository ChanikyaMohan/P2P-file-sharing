package filemanagement;

import java.io.*;

/* FileMerge takes name of the file to while all the split files should be merged
 * mergeFiles() - looks into splitParts folder reads each file and writes into output file
 */
public class FileMerge {
	public static File outFile = null;

	public FileMerge(String oFName){
		outFile = new File(oFName);
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
