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
	private BitSet availableParts;
	int nofSplits= 0;
    public FileSplit(){
	}

	public FileSplit(String fName, int peerID){
		fileName = fName;
		this.peerID = peerID;
		this.availableParts = new BitSet();
	}

	@Override
	public void init(){
		//create a config object with the cfg file
		//initialize the properties
		cfg = new CommonConfig();
		cfg.init();
		double inpFileLength = (double) cfg.fileSize;
		double splitPieceSize = (double) cfg.peiceSize;
		nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);
		this.availableParts = new BitSet(nofSplits);

	}

	public void splitFile(){
		//double inpFileLength = (double) cfg.fileSize;
		//double splitPieceSize = (double) cfg.peiceSize;
		//int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);

		FileInputStream inpFile = null;
		FileOutputStream outFile = null;

		 try {
			 inpFile = new FileInputStream(cfg.fileName);

			 //path of the directory where the split files need to be generated
			 String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerID+"/";
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
		 getAvailablePartsFromFilePieces();
	}

	public byte[] getBytefromtheIndex(int index){
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerID;
		 String splitFileName = index+"_"+cfg.fileName;
		 File file = new File(splitDirectoryPath+"/"+splitFileName);
		return getByteArrayFromFile(file);
	}

	public void savePiece(int index, byte[] buf){
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerID;
		 String splitFileName = index+"_"+cfg.fileName;
		 File file = new File(splitDirectoryPath+"/"+splitFileName);
		 file.getParentFile().mkdirs();
		 FileOutputStream outFile = null;
		try {
			outFile = new FileOutputStream(file);
			outFile.write(buf, 0, buf.length);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {

			try {
				outFile.close();
				outFile.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}


	}

	public BitSet getAvailablePartsFromFilePieces(){
		//double inpFileLength = (double) cfg.fileSize;
		//double splitPieceSize = (double) cfg.peiceSize;
		//int nofSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);

		//this.availableParts = new BitSet(nofSplits);
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerID;
		File[] files = new File(splitDirectoryPath).listFiles();
		for(File f:files){
			if (f.isFile()) {
				String[] n = f.getName().split("_");
				int pieceIndex = Integer.parseInt(n[0]);
				if (pieceIndex >= 0)
					this.availableParts.set(pieceIndex);
		    }
		}
		System.out.println("file split available parts= "+this.availableParts);
		return this.availableParts;
	}

	 private byte[] getByteArrayFromFile(File file){
	        FileInputStream fstream = null;
	        try {
	        	fstream = new FileInputStream(file);
	            byte[] fileBytes = new byte[(int) file.length()];
	            int bytesRead = fstream.read(fileBytes, 0, (int) file.length());
	            fstream.close();
	            assert (bytesRead == fileBytes.length);
	            assert (bytesRead == (int) file.length());
	            return fileBytes;
	        } catch (FileNotFoundException e) {
	        		System.out.println("file not found");
	        } catch (IOException e) {

	        }
	        finally {
	            if (fstream != null) {
	                try {
	                	fstream.close();
	                }
	                catch (IOException ex) {}
	            }
	        }
	        return null;

	    }

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	public BitSet getCurrentAvailableParts(){
		return this.availableParts;
	}
}
