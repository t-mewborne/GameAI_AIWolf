package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class GTSeer extends GTBasePlayer {
	
	int comingoutDay;
	boolean isCameout;
	static Deque<Judge> divinationQueue = new LinkedList<>();
	Map<Agent, Species> myDivinationMap = new HashMap<>();
	List<Agent> whiteList = new ArrayList<>();		// divined to be human (could be possessed tho)
	List<Agent> blackList = new ArrayList<>();		// divined to be wolf
	List<Agent> grayList;							// don't know species, not suspicious
	List<Agent> susWolves = new ArrayList<>();		// suspicious, but not divined
	List<Agent> possessedList = new ArrayList<>();	// think are possessed
	static Agent voteCandidate;
	
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		comingoutDay = (int) (Math.random() * 3 + 1);
		isCameout = false;
		divinationQueue.clear();
		myDivinationMap.clear();
		whiteList.clear();
		blackList.clear();
		grayList = new ArrayList<>();
		susWolves.clear();
		possessedList.clear();
	}
	@Override
	public void dayStart() {
		super.dayStart();
		Judge divination = currentGameInfo.getDivineResult();	// who we divined night before
		if (divination != null) {
			divinationQueue.offer(divination);
			grayList.remove(divination.getTarget());
			if (divination.getResult() == Species.HUMAN) {		// add human divination to white list
				whiteList.add(divination.getTarget());
			} else {
				blackList.add(divination.getTarget());			// add wolf divination to blacklist
				if (susWolves.contains(divination.getTarget())) {
					susWolves.remove(divination.getTarget());	// remove from sus list if divined wolf
				}
			}
			myDivinationMap.put(divination.getTarget(), divination.getResult()); // put in agent -> species map
		}
	}

	@Override
	public Agent divine() {
		if (!susWolves.isEmpty())
			return randomSelect(susWolves);
		else 
			return randomSelect(grayList);
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
	public String getName() {
		return "GTSeer";
	}
	

}
