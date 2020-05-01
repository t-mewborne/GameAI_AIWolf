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
	Deque<Content> talkQueue = new LinkedList<>();
	Deque<Content> whisperQueue = new LinkedList<>();
	Agent voteCandidate;
	Agent declaredVoteCandidate;
	Agent attackVoteCandidate;
	Agent declaredAttackVoteCandidate;
	Map<Agent, Role> comingoutMap = new HashMap<>();
	int talkListHead;
	List<Agent> humans = new ArrayList<>();
	List<Agent> werewolves = new ArrayList<>();

	public void dayStart() {
		// TODO Auto-generated method stub
		
	}

	public void update(GameInfo arg0) {
		// TODO Auto-generated method stub
		
	}
	

	public void finish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "GTBasePlayer";
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
	public Agent vote() {
		return null;
	}

	@Override
	public String whisper() {
		return Talk.SKIP;
	}
	
	@Override
	public Agent attack() {
		return null;
	}
	
	public Agent divine() {
		return null;
	}
	
	@Override
	public Agent guard() {
		return null;
	}

}
