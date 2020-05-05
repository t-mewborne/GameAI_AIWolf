package org.aiwolf.gt;

import org.aiwolf.common.data.Agent;
//import org.aiwolf.common.data.Player;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class GTVillager extends GTBasePlayer {


	@Override
	public void dayStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void initialize(GameInfo arg0, GameSetting arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(GameInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Agent vote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String whisper() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Agent guard() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Agent divine() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Agent attack() {
		throw new UnsupportedOperationException();
	}

}
