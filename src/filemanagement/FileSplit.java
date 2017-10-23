package filemanagement;

import java.io.*;
import init.CommonConfig;

/* Uses common config file to read properties like input file name/size/piece size etc
 * initProperties() - create CommonConfig object with all the properties read from the cfg file
 * splitFile() - uses the parameters of cfg object and splits the file into required chunks
 *
 * The sequence to call
 * create FileSplit object with filename argument of file to be split
 * call initProperties() on FileSplit object to get parameters from common config
 * call splitFile() to actually split the file into chunks
 */
public class FileSplit {
    public static String fileName = null;
    public static CommonConfig cfg = null;

	public FileSplit(String fName){
		fileName = fName;
	}

	public static void initProperties(){
		//create a config object with the given file
		//initialize the properties
		cfg = new CommonConfig(fileName);
		cfg.init();
	}

	public static void splitFile(){
		int inpFileLength = cfg.fileSize;
		int nofSplits = (int) Math.ceil(inpFileLength/cfg.peiceSize);

		FileInputStream inpFile = null;
		FileOutputStream outFile = null;

		 try {
			 inpFile = new FileInputStream(cfg.fileName);
			 for(int i=0; i<nofSplits; i++){
				 byte[] buffer = new byte[cfg.peiceSize];
				 int lengthRead = inpFile.read(buffer,0,cfg.peiceSize);
				 if(lengthRead > 0){
					 // the variable 'i' here indicates the number of split part
					 String splitFileName = "splitParts/"+cfg.fileName+"_"+i+1;
					 outFile = new FileOutputStream(new File(splitFileName));
					 outFile.write(buffer, 0, lengthRead);
					 outFile.flush();
					 outFile.close();
				 }
			 }
			 inpFile.close();
        } catch (IOException e) {
           System.out.println("Error occured while reading,spiltting and writing file");
        }
	}
}
