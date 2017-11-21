package init;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class CommonConfig implements Initialization{

	public int nofNeighbour;
	public int unchokeIntvl;
	public int opsUnchokeIntvl;
	public String fileName;
	public int fileSize;
	public int peiceSize;
	HashMap<String, String> values = new HashMap<String, String>();

	String filePath = "Common.cfg";
	public CommonConfig(){
	}
	public CommonConfig(String filepath){
		this.filePath = filepath;
	}
	@Override
	public void init() {
		String st;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			while((st = in.readLine()) != null) {
				String[] tokens = st.split("\\s+");
				values.put(tokens[0], tokens[1]);
			}
			in.close();
		}
		catch (Exception ex) {
			LogConfig.getLogRecord().debugLog(ex.toString());
		}
		nofNeighbour = Integer.parseInt(values.get("NumberOfPreferredNeighbors"));
		unchokeIntvl = Integer.parseInt(values.get("UnchokingInterval"));
		opsUnchokeIntvl = Integer.parseInt(values.get("OptimisticUnchokingInterval"));
		fileSize = Integer.parseInt(values.get("FileSize"));
		peiceSize = Integer.parseInt(values.get("PieceSize"));
		fileName = values.get("FileName");
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		values.clear();
		init();

	}

}
