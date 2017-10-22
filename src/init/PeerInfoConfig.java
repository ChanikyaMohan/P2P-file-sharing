package init;


import java.io.*;
import java.util.*;

public class PeerInfoConfig implements Initialization
{
	public List<Peer> peersList = new ArrayList<Peer>();
	String filePath = "PeerInfo.cfg";

	public PeerInfoConfig(String filepath){
		this.filePath = filepath;
	}
	@Override
	public void init() {
		String st;
		int i1;
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
					   String[] tokens = st.split("\\s+");
					   Peer peer = new Peer(Integer.parseInt(tokens[0]),
							   tokens[1],Integer.parseInt(tokens[2]),
							   Boolean.parseBoolean(tokens[3]));

					   peersList.add(peer);

			}

			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}

	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}


}
