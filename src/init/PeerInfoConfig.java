package init;


import java.io.*;
import java.util.*;

public class PeerInfoConfig implements Initialization
{
	public List<Peer> peersList = new ArrayList<Peer>();
	String filePath = "PeerInfo.cfg";

	public PeerInfoConfig(){
	}

	public PeerInfoConfig(String filepath){
		this.filePath = filepath;
	}
	@Override
	public void init() {
		String st;
		//int i1;
		try {
			BufferedReader in = new BufferedReader(new FileReader(this.filePath));
			while((st = in.readLine()) != null) {
					   String[] tokens = st.split("\\s+");
					   Boolean hasFile = tokens[3].equals("1")? true:false;
					   Peer peer = new Peer(Integer.parseInt(tokens[0]),
							   tokens[1],Integer.parseInt(tokens[2]),
							   hasFile);

					   peersList.add(peer);

			}

			in.close();
		}
		catch (Exception ex) {
			LogConfig.getLogRecord().debugLog(ex.toString());
		}

	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		peersList.clear();
		init();

	}


}
