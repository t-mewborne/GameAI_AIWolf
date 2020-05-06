package org.aiwolf.gtWolf;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;
import org.aiwolf.gtWolf.GTState;
import org.aiwolf.gtWolf.QLearningAgent;

// Seer class for when assigned to seer

public class GTSeer extends GTBasePlayer {
	int comingoutDay;
	boolean isCameout;

	Judge divination;

	boolean[] divined;
	boolean f = true;
	Parameters params;
	boolean doCO = false;
	boolean houkoku = true;
	boolean pos;
	boolean update_sh = true;

	// variables for our AI
	List<Agent> whiteList = new ArrayList<>(); // divined to be human (could be possessed tho)
	List<Agent> blackList = new ArrayList<>(); // divined to be wolf
	List<Agent> grayList; // don't know species, not suspicious
	List<Agent> susWolves = new ArrayList<>(); // suspicious, but not divined
	List<Agent> possessedList = new ArrayList<>(); // think are possessed
	static Agent voteCandidate;
	static QLearningAgent qLearn = new QLearningAgent(0.1); // , 50); // discount=0.1, randFactor=50
	boolean susVote = false;
	boolean talkedToday = false;
	// List<Agent> alives = currentGameInfo.getAliveAgentList();
	static GTState state;
	static String action;

	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		if (f) {
			params = new Parameters(numAgents);

			sh = new StateHolder(numAgents);

			f = false;
		}
		update_sh = true;
		pos = false;
		doCO = false;
		houkoku = true;

		
		
		whiteList.clear();
		blackList.clear();
		grayList = new ArrayList<>();
		susWolves.clear();
		possessedList.clear();

		grayList = gameInfo.getAliveAgentList();
		grayList.remove(me);

		divined = new boolean[numAgents];
		for (int i = 0; i < numAgents; i++)
			divined[i] = false;
		ArrayList<Integer> fixed = new ArrayList<Integer>();
		fixed.add(meint);
		sh.process(params, gamedata);
		gamedata.clear();
		sh.head = 0;
		sh.game_init(fixed, meint, numAgents, Util.SEER, params);

		before = -1;

	}

	public void dayStart() {
		super.dayStart();
		talkedToday = false;
		divination = currentGameInfo.getDivineResult();
		if (day > 1)
			getReward();
		updateDead();
		if (divination != null) {
			divined[divination.getTarget().getAgentIdx() - 1] = true;
			houkoku = false;
			gamedata.add(new GameData(DataType.DIVINED, day, meint, divination.getTarget().getAgentIdx() - 1,
					divination.getResult() == Species.HUMAN));
			grayList.remove(divination.getTarget());
			if (divination.getResult() == Species.HUMAN) { // add human divination to white list
				whiteList.add(divination.getTarget());
			} else {
				blackList.add(divination.getTarget()); // add wolf divination to blacklist
				if (susWolves.contains(divination.getTarget())) {
					susWolves.remove(divination.getTarget()); // remove from sus list if divined wolf
				}
			}
		}
		sh.process(params, gamedata);
	}

	protected void init() {

	}

	protected Agent chooseVote() {

		gamedata.add(new GameData(DataType.VOTESTART, day, meint, meint, false));
		sh.process(params, gamedata);

		chooseVoteCandidate(); // decide who to vote for
		return voteCandidate;
	}

	public void chooseVoteCandidate() {
		if (divination == null)
			return;
		if (divination.getResult() == Species.WEREWOLF) {
			voteCandidate = divination.getTarget();
			susVote = false;
		} else if (!blackList.isEmpty()) {
			voteCandidate = randomSelect(blackList);
			susVote = false;
		} else if (!susWolves.isEmpty()) {
			voteCandidate = randomSelect(susWolves);
			susVote = true;
		} else {
			voteCandidate = randomSelect(grayList);
			susVote = false;
		}
	}

	protected String chooseTalk() {
		gamedata.add(new GameData(DataType.TURNSTART, day, meint, meint, false));
		sh.process(params, gamedata);
		updateState(sh);
		if (update_sh) {
			update_sh = false;
			sh.serach(1000);
		}
		double mn = -1;
		int c = 0;

		if (talkedToday) {
			return Talk.OVER;
		} else {
			chooseVoteCandidate();
			divination = currentGameInfo.getDivineResult();
			Boolean earlyDays = currentGameInfo.getDay() < 3;
			if (divination == null)
				state = new GTState(Species.HUMAN, !susWolves.isEmpty(), false, isCameout, earlyDays);
			else
				state = new GTState(divination.getResult(), !susWolves.isEmpty(), true, isCameout, earlyDays);
			action = qLearn.playAndGetAction(state, gamenum);
			String say;
			switch (action) {
			case Talk.SKIP:
				say = Talk.SKIP;
				break;
			case "Divine":
				if (divination == null)
					say = (new Content(new EstimateContentBuilder(GTSeer.voteCandidate, Role.WEREWOLF))).getText();
				else
					say = (new Content(new DivinedResultContentBuilder(divination.getTarget(), divination.getResult())))
							.getText();
				break;
			case "Estimate":
				say = (new Content(new EstimateContentBuilder(GTSeer.voteCandidate, Role.WEREWOLF))).getText();
				break;
			case "ComeOut":
				say = (new Content(new ComingoutContentBuilder(me, Role.SEER))).getText();
			default:
				say = Talk.SKIP;
				break;
			}
			talkedToday = true;
			return say;
		}
	}

	public Agent divine() {
		sh.process(params, gamedata);
		sh.update();
		List<Agent> alives = currentGameInfo.getAliveAgentList();
		alives.removeIf(a -> a == me);
		for (AgentInfo a : agents) {
			Agent agent = intToAgent.get(a.index);
			if (a.COrole == Role.SEER && !susWolves.contains(agent) && !blackList.contains(agent)
					&& alives.contains(agent))
				susWolves.add(agent);
		}

		if (!susWolves.isEmpty())
			return randomSelect(susWolves);
		else if (!grayList.isEmpty())
			return randomSelect(grayList);
		else
			return randomSelect(alives);
	}

	public void getReward() {
		int reward = 0;
		Agent lastExecuted = currentGameInfo.getLatestExecutedAgent();
		Species divType = Species.HUMAN;
		if (currentGameInfo.getLatestExecutedAgent() == me || currentGameInfo.getAttackedAgent() == me)
			reward = -2;
		else if (divination != null) {
			Agent divTarget = divination.getTarget();
			divType = divination.getResult();

			if ((divType == Species.WEREWOLF && divTarget == lastExecuted)
					|| (divType == Species.HUMAN && divTarget != lastExecuted)
					|| (susVote && voteCandidate == lastExecuted)
					|| (action == "ComeOut" && currentGameInfo.getAliveAgentList().contains(me)
							|| (currentGameInfo.getGuardedAgent() == me)))
				reward = 2;
			else if ((divType == Species.WEREWOLF && divTarget != lastExecuted)
					|| (divType == Species.HUMAN && divTarget == lastExecuted)
					|| (susVote && voteCandidate != lastExecuted))
				reward = -2;
		} else if (action == "ComeOut" && currentGameInfo.getAliveAgentList().contains(me)
				|| currentGameInfo.getGuardedAgent() == me)
			reward = 2;
		else if (susVote && voteCandidate != lastExecuted)
			reward = -1;

		Boolean divined = divination != null;
		Boolean earlyDays = currentGameInfo.getDay() < 3;
		state = new GTState(divType, !susWolves.isEmpty(), divined, isCameout, earlyDays);
		qLearn.updateQTable(state, action, reward); // state, action, reward
	}

	public void updateDead() {
		Agent killed = currentGameInfo.getAttackedAgent();
		Agent executed = currentGameInfo.getLatestExecutedAgent();

		whiteList.remove(killed);
		whiteList.remove(executed);
		blackList.remove(killed);
		blackList.remove(executed);
		grayList.remove(killed);
		grayList.remove(executed);
		susWolves.remove(killed);
		susWolves.remove(executed);

	}

}