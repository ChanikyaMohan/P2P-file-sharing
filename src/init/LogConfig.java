package init;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;



public final class LogConfig implements Initialization{
	//private final static Logger logger = Logger.getLogger(LogConfig.class.getName());
	//private static final LogConfig _log = new LogConfig (Logger.getLogger("P2P-file-sharing"));


	private static Logger logger;
    private static LogConfig logRecord = new LogConfig();
    static String logSuffix;
    private boolean _debug = false;
    private int peerid;

    static
	{
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %5$s%6$s%n");
	}

    private LogConfig()
    {
        Logger _l = Logger.getLogger(LogConfig.class.getName());
        logger = _l;
    }

    public static LogConfig getLogRecord()
    {
        return logRecord;
    }

    public void setLoggerForPeer(int peerId)
    {
    	this.peerid = peerId;
        logSuffix = ": Peer" + " " + Integer.toString(peerId);
                String filename = "log_peer_" + Integer.toString(peerId) + ".log";

        try
        {
            Handler loggerHandler = new FileHandler(filename);
            Formatter formatter = (Formatter) Class.forName("java.util.logging.SimpleFormatter").newInstance();
            loggerHandler.setFormatter(formatter);
            loggerHandler.setLevel(Level.parse("INFO"));
            logger.addHandler(loggerHandler);

           /* ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            loggerHandler.setLevel(Level.parse("INFO"));
            logger.addHandler(consoleHandler);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getLineNumber()
    {
        int lineNumber = 0;
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("getLineNumber") == 0)
            {
                currentIndex = i + 1;
                break;
            }
        }

        lineNumber = stackTraceElement[currentIndex].getLineNumber();

        return lineNumber;
    }

    public static void closeLogger()
    {
        for(Handler h : logger.getHandlers())
        {
            h.close();
        }
    }

    public void debugLog(String msg)
    {
    	if(_debug == true)
    		logger.log(Level.INFO, "|Peer ID: " + this.peerid + "|Message: "+ msg);
    }

    public void connectTo (int peerId) {
        String msg = logSuffix + " makes a connection to Peer "+ Integer.toString(peerId) ;

        logger.log (Level.INFO,msg);
    }

    public void isConnected(int peerId)
    {
        String msg = logSuffix + " is connected from Peer " + Integer.toString(peerId) ;
        logger.log (Level.INFO,msg);
    }

    public void choked (int peerId) {
         String msg = logSuffix + " is choked by " + Integer.toString(peerId);
        logger.log (Level.INFO,msg);
    }


    public void changeOfPrefereedNeighbors (List<Integer> preferredNeighbours) {

        String neighbours = converttoString(preferredNeighbours);
         String msg = logSuffix + " has preferred neighbors "+ neighbours;
        logger.log (Level.INFO,msg);
    }

    private String converttoString(List<Integer> preferredNeighbours) {

        StringBuilder s =new StringBuilder();
        int count =0;
        for(Integer i : preferredNeighbours)
        {
            s.append(Integer.toString(i));
           // i++;
            count++;
            if(count!=preferredNeighbours.size())
            {
                s.append(",");
            }
            else
                break;
        }
        return s.toString();
    }


    public void changeOfOptimisticallyUnchokedNeighbors (int unchokeNeighbours) {
        final String msg = logSuffix + " has the optimistically unchoked neighbor " + Integer.toString(unchokeNeighbours);
        logger.log(Level.INFO, msg);
    }

    public void unchoked (int peerId) {
        String msg = logSuffix + " is unchoked by " + Integer.toString(peerId);
        logger.log(Level.INFO,msg);
    }

    public void receivedHave (int peerId, int pieceIdx) {
        String msg = logSuffix + " received the 'have' message from " + Integer.toString(peerId)+ " for the piece " + Integer.toString(pieceIdx);
        logger.log (Level.INFO,msg);
    }

    public void receivedInterested (int peerId) {
        String msg = logSuffix + " received the 'interested' message from "+ Integer.toString(peerId);
        logger.log (Level.INFO,msg);
    }

    public void receivedNotInterested (int peerId) {
        final String msg = logSuffix + " received the 'not interested' message from "+Integer.toString(peerId);
        logger.log (Level.INFO,msg);
    }

    public void pieceDownloaded (int peerId, int pieceIndex, int currNumberOfPieces) {
        final String msg = logSuffix + " has downloaded the piece " + Integer.toString(pieceIndex) +" from "+ Integer.toString(peerId) +" Now the number of pieces it has is " + Integer.toString(currNumberOfPieces);
        logger.log(Level.INFO,msg);
    }

    public void fileComplete () {
        final String msg = logSuffix + " has downloaded the complete file.";
        logger.log (Level.INFO,msg);
    }
	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}
}