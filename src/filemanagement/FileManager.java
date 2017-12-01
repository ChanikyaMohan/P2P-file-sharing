package filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import handler.PeerHandler;
import init.CommonConfig;
import init.Initialization;
import init.LogConfig;
import init.Peer;
import init.PeerInfoConfig;

public class FileManager implements Initialization{
	private int peerId;
	private PeerInfoConfig peerInfo;
	private CommonConfig cfg;
	public int noOfSplits;
	private Peer peer;
	private FileSplit fileSplit;
	private FileMerge fileMerge;
	private BitSet availableParts;

	public FileManager(int peerId, PeerInfoConfig peerInfo, CommonConfig cmnCfg){
		this.peerId = peerId;
		this.peerInfo = peerInfo;
		this.cfg = cmnCfg;
		this.availableParts = new BitSet();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		double inpFileLength = (double) cfg.fileSize;
		double splitPieceSize = (double) cfg.peiceSize;
		noOfSplits =  (int) Math.ceil(inpFileLength/splitPieceSize);

		//get the current peer with peer id from peerslist
		for(Peer p : peerInfo.peersList){
			if(p.id == this.peerId){
				peer = p;
				break;
			}
		}

		//clear previous folders if any and create new folders
		createPeerFolder();
		fileSplit = new FileSplit(peerId,noOfSplits,cfg);
//		fileSplit.init();
		fileMerge = new FileMerge(peerId,noOfSplits,cfg.fileName);
		fileMerge.init();
		//available parts
		this.availableParts = new BitSet(noOfSplits);
		generateSplitsIfHave();


	}

	private void generateSplitsIfHave() {
		// TODO Auto-generated method stub
		//if the peer has file
		//split the file into pieces
		if(peer.isFile){
			fileSplit.splitFile();
			getAvailablePartsFromFilePieces();
			LogConfig.getLogRecord().debugLog("File is split into pieces");
		}
	}

	private void createPeerFolder() {
		// TODO Auto-generated method stub
		//path of the directory where the split files need to be generated
		 String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+this.peerId+"/";
		 File folder = new File(splitDirectoryPath);
		 if(!folder.exists()){
			boolean result = folder.mkdir();
			if(result){
				LogConfig.getLogRecord().debugLog("Successfully created "+folder.getAbsolutePath());
			} else {
				LogConfig.getLogRecord().debugLog("Failed creating "+folder.getAbsolutePath());
			}
		 } else {
			 LogConfig.getLogRecord().debugLog("Folder already exists");
			 //and clean all files in the directory (if any) before adding new split files
			 Arrays.stream(new File(splitDirectoryPath).listFiles()).forEach(File::delete);
		 }
	}

	public byte[] getBytefromtheIndex(int index){
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerId;
		String splitFileName = index+"_"+cfg.fileName;
		File file = new File(splitDirectoryPath+"/"+splitFileName);
		return getByteArrayFromFile(file);
	}

	public void savePiece(int index, byte[] buf){
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerId;
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
        	LogConfig.getLogRecord().debugLog("file not found");
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

	public void getAvailablePartsFromFilePieces(){
		String splitDirectoryPath =  System.getProperty("user.dir")+"/peer_"+peerId;
		File[] files = new File(splitDirectoryPath).listFiles();
		for(File f:files){
			if (f.isFile()) {
				String[] n = f.getName().split("_");
				if (n.length>1){
					int pieceIndex = Integer.parseInt(n[0]);
					if (pieceIndex >= 0)
						this.availableParts.set(pieceIndex);
				}
		    }
		}
		LogConfig.getLogRecord().debugLog("file split available parts= "+this.availableParts);
	}

	public BitSet getCurrentAvailableParts(){
		return this.availableParts;
	}

	public BitSet savePart(int index, byte[] buf){
		savePiece(index,buf);
		//update bit field
//		getAvailablePartsFromFilePieces();
		availableParts.set(index);
		PeerHandler.getInstance().getPeer(this.peerId).setsaveparts(availableParts);
		LogConfig.getLogRecord().debugLog("availbale parts updated for"+ index);
		if(checkIfAllPiecesAreReceived()){
			//merge all pieces
			fileMerge.mergeFiles();
			LogConfig.getLogRecord().fileComplete();
			PeerHandler.getInstance().getPeer(this.peerId).isFile =true;
			//p.isFile = true;
		}
		return availableParts;
	}

	public Boolean checkIfAllPiecesAreReceived() {
		// TODO Auto-generated method stub
		LogConfig.getLogRecord().debugLog("availableParts.cardinality()" + availableParts.cardinality());
		if(availableParts.cardinality() >= noOfSplits){
			return true;
		}
		return false;
	}

	public void deletedparts(){
		fileMerge.deleteFiles();
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

}
