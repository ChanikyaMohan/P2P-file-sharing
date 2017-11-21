package scheduler;

import java.util.Timer;

import handler.PeerHandler;
import init.Initialization;

public class UnchokeRegularScheduler implements Initialization {
	public int OptUnchokeIntl;
	public int UnchokeIntl;
	public PeerHandler phandler;
	public int peferredn;
	private Timer _Opttimer;
	private Timer _Unchoketimer;
	private boolean _set;
	private int peerID;
	public UnchokeRegularScheduler(int peerid, int OptUnchokeIntl, int UnchokeIntl, int maxk,PeerHandler phandle){
		this.OptUnchokeIntl = OptUnchokeIntl;
		this.UnchokeIntl = UnchokeIntl;
		this.phandler = phandle;
		this.peerID = peerid;
		this.peferredn = maxk; //number of preferred neighbours allowed
		_Opttimer = new Timer(true);
		_Unchoketimer = new Timer(true);
		this._set = false;
	}

	public void run() {
		if (this._set)
			return;
		synchronized(this){
			UnchokeSchedulerTask unChokeTask = new UnchokeSchedulerTask(this.peerID, this.phandler,this.peferredn );
			OptimisticUnchokeSchedulerTask optUnchokeTask = new OptimisticUnchokeSchedulerTask(this.phandler);
			_Unchoketimer.scheduleAtFixedRate(unChokeTask, 0, this.UnchokeIntl*1000);
			_Opttimer.scheduleAtFixedRate(optUnchokeTask, 0, this.OptUnchokeIntl*1000);
			this._set = true;
		}
	}
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		_Opttimer.cancel();
		_Unchoketimer.cancel();
		this._set = false;

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}


}
