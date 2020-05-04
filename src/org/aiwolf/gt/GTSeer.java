package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.client.lib.EstimateContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class GTSeer extends GTBasePlayer {

	int comingoutDay;
	boolean isCameout;
	static Deque<Judge> divinationQueue = new LinkedList<>();
	static Judge divination;
	Map<Agent, Species> myDivinationMap = new HashMap<>();
	List<Agent> whiteList = new ArrayList<>(); // divined to be human (could be possessed tho)
	List<Agent> blackList = new ArrayList<>(); // divined to be wolf
	List<Agent> grayList; // don't know species, not suspicious
	List<Agent> susWolves = new ArrayList<>(); // suspicious, but not divined
	List<Agent> possessedList = new ArrayList<>(); // think are possessed
	static Agent voteCandidate;
	static QLearningAgent qLearn = new QLearningAgent(0.1, 50); // discount=0.1, randFactor=50
	boolean susVote = false;
	boolean talkedToday = false;

	static GTState state;
	static String action;

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

	public void dayStart() {
		super.dayStart();
		talkedToday = false;
		divination = currentGameInfo.getDivineResult(); // who we divined night before
		if (divination != null) {
			divinationQueue.offer(divination);
			grayList.remove(divination.getTarget());
			if (divination.getResult() == Species.HUMAN) { // add human divination to white list
				whiteList.add(divination.getTarget());
			} else {
				blackList.add(divination.getTarget()); // add wolf divination to blacklist
				if (susWolves.contains(divination.getTarget())) {
					susWolves.remove(divination.getTarget()); // remove from sus list if divined wolf
				}
			}
			myDivinationMap.put(divination.getTarget(), divination.getResult()); // put in agent -> species map
		}
	}


	public Agent divine() {
		System.out.println("divining shit");
		getReward();
		if (!susWolves.isEmpty())
			return randomSelect(susWolves);
		else
			return randomSelect(grayList);

	}

	public void getReward() {
		int reward = 0;
		Agent lastExecuted = executedAgents.get(executedAgents.size() - 1);
		Agent divTarget = divination.getTarget();
		Species divType = divination.getResult();
		if ((divType == Species.WEREWOLF && divTarget == lastExecuted)
				|| (divType == Species.HUMAN && divTarget != lastExecuted)
				|| (susVote && voteCandidate == lastExecuted)) {
			reward = 1;
		} else if ((divType == Species.WEREWOLF && divTarget != lastExecuted)
				|| (divType == Species.HUMAN && divTarget == lastExecuted)
				|| (susVote && voteCandidate != lastExecuted)) {
			reward = -1;
		}
		qLearn.updateQTable(state, action, reward); // state, action, reward
	}

	@Override
	public String talk() {
		if (talkedToday) {
			return Talk.OVER;
		} else {
			chooseVote();
			divination = currentGameInfo.getDivineResult();
			GTState state = new GTState(divination.getResult(), !susWolves.isEmpty());
			// LearningAgent.playEpisode(state);
			action = qLearn.playAndGetAction(state);
			String say;
			switch (action) {
			case Talk.SKIP:
				say = Talk.OVER;
				break;
			case "Divine":
				say = (new Content(new DivinedResultContentBuilder(divination.getTarget(), divination.getResult()))).getText();
				break;
			case "Estimate":
				say = (new Content(new EstimateContentBuilder(GTSeer.voteCandidate, Role.WEREWOLF))).getText();
				break;
			default:
				say = Talk.SKIP;
				break;
			}
			
			talkedToday = true;
			return say;
		}
	}

	public void chooseVote() {
		List<Agent> aliveWolves = new ArrayList<>();

		for (Agent a : livingAgents) {
			if (comingoutMap.get(a) == Role.SEER) {
				if (a != me) {
					susWolves.add(a);
				}
			}
		}

		for (Agent a : blackList) {
			if (isAlive(a)) {
				aliveWolves.add(a);
			}
		}
		if (divination.getResult() == Species.WEREWOLF) {
			voteCandidate = divination.getTarget();
			susVote = false;
		} else if (!susWolves.isEmpty()) {
			voteCandidate = randomSelect(susWolves);
			susVote = true;
		} else if (!aliveWolves.isEmpty()) {
			voteCandidate = randomSelect(aliveWolves);
			susVote = false;
		} else {
			voteCandidate = randomSelect(grayList);
			susVote = false;
		}
	}

	@Override
	public void update(GameInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Agent vote() {
		return voteCandidate;
	}

	@Override
	public String getName() {
		return "GTSeer";
	}

}
