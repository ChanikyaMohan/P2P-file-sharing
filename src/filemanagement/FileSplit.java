package filemanagement;

import java.io.*;

import init.CommonConfig;
import init.Initialization;
import init.LogConfig;

/* Uses common config file to read properties like input file name/size/piece size etc
 * initProperties() - create CommonConfig object with all the properties read from the cfg file
 * splitFile() - uses the parameters of cfg object and splits the file into required chunks
 *
 * The sequence to call
 * create FileSplit object with filename argument of file to be split
 * call initProperties() on FileSplit object to get parameters from common config
 * call splitFile() to actually split the file into chunks
 */
public class FileSplit implements Initialization {
    public CommonConfig cfg = null;
	private int peerId;
	int nofSplits= 0;
    public FileSplit(){
	}

	public FileSplit(int peerId,int noOfSplits,CommonConfig cmnCfg){
		this.cfg = cmnCfg;
		this.peerId = peerId;
		this.nofSplits = noOfSplits;
	}

	@Override
	public void init(){
	}

	public void splitFile(){
		FileInputStream inpFile = null;
		FileOutputStream outFile = null;

		 try {
			 inpFile = new FileInputStream(cfg.fileName);

			 //path of the directory where the split files need to be generated
			 String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerId+"/";
			 //File folder = new File(splitDirectoryPath);

			 for(int i=0; i<nofSplits; i++){
				 byte[] buffer = new byte[cfg.peiceSize];
				 int lengthRead = inpFile.read(buffer,0,cfg.peiceSize);
				 if(lengthRead > 0){
					 // the variable 'i' here indicates the number of split part
					 String splitFileName = i+"_"+cfg.fileName;
					 File file = new File(splitDirectoryPath+splitFileName);
					 //to make sure the parent directories exist
					 file.getParentFile().mkdirs();
					 outFile = new FileOutputStream(file);
					 outFile.write(buffer, 0, lengthRead);
					 outFile.flush();
					 outFile.close();
				 }
			 }
			 File dest = new File(System.getProperty("user.dir")+"/peer_"+peerId+"/"+cfg.fileName);
			 File src = new File(System.getProperty("user.dir")+"/"+cfg.fileName);
			 copyFileUsingStream(src, dest);
			 inpFile.close();
        } catch (IOException e) {
        	LogConfig.getLogRecord().debugLog("Error occured while reading,spiltting and writing file");
        }
	}

	private static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

}
