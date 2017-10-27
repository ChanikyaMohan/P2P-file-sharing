package filemanagement;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;

import init.CommonConfig;
import init.Initialization;

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
    public String fileName = null;
    public CommonConfig cfg = null;
	private int peerID;

    public FileSplit(){
	}

	public FileSplit(String fName, int peerID){
		fileName = fName;
		this.peerID = peerID;
	}

	@Override
	public void init(){
		//create a config object with the cfg file
		//initialize the properties
		cfg = new CommonConfig();
		cfg.init();
	}

	public void splitFile(){
		double inpFileLength = (double) cfg.fileSize;
		double splitPieceSize = (double) cfg.peiceSize;
		int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);

		FileInputStream inpFile = null;
		FileOutputStream outFile = null;

		 try {
			 inpFile = new FileInputStream(cfg.fileName);

			 //path of the directory where the split files need to be generated
			 String splitDirectoryPath =  System.getProperty("user.dir")+"\\peer_"+peerID;
			 File folder = new File(splitDirectoryPath);
			 if(!folder.exists()){
				boolean result = folder.mkdir();
				if(result){
					System.out.println("Successfully created "+folder.getAbsolutePath());
				} else {
					System.out.println("Failed creating "+folder.getAbsolutePath());
				}
			 } else {
				 System.out.println("Folder already exists");
				//and clean all files in the directory (if any) before adding new split files
				 Arrays.stream(new File(splitDirectoryPath).listFiles()).forEach(File::delete);
			 }

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
			 inpFile.close();
        } catch (IOException e) {
           System.out.println("Error occured while reading,spiltting and writing file");
        }
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	public BitSet getCurrentAvailableParts(){
		double inpFileLength = (double) cfg.fileSize;
		double splitPieceSize = (double) cfg.peiceSize;
		int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);

		BitSet availableParts = new BitSet(nofSplits);
		String splitDirectoryPath =  System.getProperty("user.dir")+"\\peer_"+peerID;
		File[] files = new File(splitDirectoryPath).listFiles();
		for(File f:files){
			if (f.isFile()) {
		        int pieceIndex = f.getName().charAt(0) - '0';
		        availableParts.set(pieceIndex);
		    }
		}
		return availableParts;
	}
}
