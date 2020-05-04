package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Content;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class GTBasePlayer implements Player {


	Agent me;
	int day;
	boolean canTalk;
	boolean canWhisper;
	GameInfo currentGameInfo;
	List<Agent> livingAgents;
	List<Agent> executedAgents = new ArrayList<>();	// agents voted to be executed
	List<Agent> attackedAgents = new ArrayList<>(); // agents killed by wolves
	List<Judge> divinationList = new ArrayList<>(); // 
	List<Judge> identList = new ArrayList<>();
	Deque<Content> talkQueue = new LinkedList<>();	// messages to say
	Deque<Content> whisperQueue = new LinkedList<>();
	Agent voteCandidate;
	Agent declaredVoteCandidate;
	Agent attackVoteCandidate;
	Agent declaredAttackVoteCandidate;
	Map<Agent, Role> comingoutMap = new HashMap<>();
	int talkListHead;
	List<Agent> humans = new ArrayList<>();
	List<Agent> werewolves = new ArrayList<>();

	protected <T> T randomSelect(List<T> list) {
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get((int) (Math.random() * list.size()));
		}
	}
	
	public void dayStart() {
		canTalk = true;
		canWhisper = false;
		if (currentGameInfo.getRole() == Role.WEREWOLF) {
			canWhisper = true;
		}
		talkQueue.clear();
		whisperQueue.clear();
		declaredVoteCandidate = null;
		voteCandidate = null;
		declaredAttackVoteCandidate = null;
		attackVoteCandidate = null;
		talkListHead = 0;
		// 前日に追放されたエージェントを登録
		addExecutedAgent(currentGameInfo.getExecutedAgent());
		// 昨夜死亡した（襲撃された）エージェントを登録
		if (!currentGameInfo.getLastDeadAgentList().isEmpty()) {
			addKilledAgent(currentGameInfo.getLastDeadAgentList().get(0));
		}
	}

	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		day = -1;
		me = gameInfo.getAgent();
		livingAgents = new ArrayList<>(gameInfo.getAliveAgentList());
		livingAgents.remove(me);
		executedAgents.clear();
		attackedAgents.clear();
		divinationList.clear();
		identList.clear();
		comingoutMap.clear();
		humans.clear();
		werewolves.clear();
	}
	
	public void update(GameInfo gameInfo) {
		currentGameInfo = gameInfo;
		if (currentGameInfo.getDay() == day + 1) {
			day = currentGameInfo.getDay();
			return;
		}
		
		// find out who was executed & add to list
		addExecutedAgent(currentGameInfo.getLatestExecutedAgent());
		
		for (int i = talkListHead; i < currentGameInfo.getTalkList().size(); i++) {
			Talk talk = currentGameInfo.getTalkList().get(i);
			Agent talker = talk.getAgent();
			if (talker == me) {
				continue;
			}
			Content content = new Content(talk.getText());
			switch (content.getTopic()) {
			case COMINGOUT:
				comingoutMap.put(talker, content.getRole());
				break;
			case DIVINED:
				divinationList.add(new Judge(day, talker, content.getTarget(), content.getResult()));
				break;
			case IDENTIFIED:
				identList.add(new Judge(day, talker, content.getTarget(), content.getResult()));
				break;
			default:
				break;
			}
		}
		talkListHead = currentGameInfo.getTalkList().size();
		
		
	}
	

	public void finish() {
		// TODO Auto-generated method stub
		if (livingAgents.contains(me)) {
			GTSeer.qLearn.updateQTable(GTSeer.state, GTSeer.action, 2);
		} else if (executedAgents.contains(me))
			GTSeer.qLearn.updateQTable(GTSeer.state, GTSeer.action, -2);
		else
			GTSeer.qLearn.updateQTable(GTSeer.state, GTSeer.action, -1);	
	}


	public String getName() {
		return "GTBasePlayer";
	}


	private void addExecutedAgent(Agent executedAgent) {
		if (executedAgent != null) {
			livingAgents.remove(executedAgent);
			if (!executedAgents.contains(executedAgent)) {
				executedAgents.add(executedAgent);
			}
		}
	}
	
	private void addKilledAgent(Agent killedAgent) {
		if (killedAgent != null) {
			livingAgents.remove(killedAgent);
			if (!attackedAgents.contains(killedAgent)) {
				attackedAgents.add(killedAgent);
			}
		}
	}

	protected boolean isAlive(Agent agent) {
		return currentGameInfo.getStatusMap().get(agent) == Status.ALIVE;
	}
	
	public String talk() {
		return null; // if queue is empty, skip
	}


	public Agent vote() {
		return voteCandidate;
	}


	public String whisper() {
		return Talk.SKIP;
	}
	

	public Agent attack() {
		return null;
	}
	
	public Agent divine() {
		return null;
	}
	

	public Agent guard() {
		return null;
	}

}
