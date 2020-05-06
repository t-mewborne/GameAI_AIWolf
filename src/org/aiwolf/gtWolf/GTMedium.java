package org.aiwolf.gtWolf;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

/**
 * 霊媒師役エージェントクラス
 */

public class GTMedium extends GTBasePlayer {
	Judge ident;
	
	int before = -1;
	boolean f = true;
	Parameters params;
	boolean doCO = false;
	boolean houkoku = true;
	int target;
	boolean white;
	boolean update_sh = true;
	
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		if(f){
			params = new Parameters(numAgents);
			
			sh = new StateHolder(numAgents);
			
			f=false;
		}
		
		doCO = false;
		houkoku = true;
		update_sh = true;
		
		sh.process(params, gamedata);
		gamedata.clear();
		sh.head = 0;
		
		ArrayList<Integer> fixed = new ArrayList<Integer>();
		fixed.add(meint);
		sh.game_init(fixed, meint,numAgents,Util.MEDIUM,params);
		
		before = -1;
		
	}
	
	public void dayStart() {
		super.dayStart();

		Judge ident = currentGameInfo.getMediumResult();
		if (ident != null) {
			houkoku = false;
			gamedata.add(new GameData(DataType.ID, 
					day, meint, 
					ident.getTarget().getAgentIdx() - 1, 
					ident.getResult() == Species.HUMAN));
			target =ident.getTarget().getAgentIdx() - 1;
			white = (ident.getResult() == Species.HUMAN);
		}
		sh.process(params, gamedata);
	}


	protected void init() {

	}

	protected Agent chooseVote() {
		gamedata.add(new GameData(DataType.VOTESTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
	
		
		double mn = -1;
		int c = 0;
		for(int i=0;i<numAgents;i++){
			if(i!=meint){
				if(sh.gamestate.agents[i].Alive){
					if(mn < sh.rp.getProb(i, Util.WEREWOLF)){
						mn = sh.rp.getProb(i, Util.WEREWOLF);
						c=i;
					}
				}
			}
		}
		return currentGameInfo.getAgentList().get(c);
	}
	protected String chooseTalk() {
		gamedata.add(new GameData(DataType.TURNSTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		updateState(sh);
		if(update_sh){
			System.out.println("SEARCH");
			update_sh = false;
			sh.serach(1000);
		}
		
		
		
		double mn = -1;
		int c = 0;
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh.rp.getProb(i, Util.WEREWOLF) + " ");
		}
		System.out.println();
		for(int i=0;i<numAgents;i++){
			if(i!=meint){
				if(sh.gamestate.agents[i].Alive){
					if(mn < sh.rp.getProb(i, Util.WEREWOLF)){
						mn = sh.rp.getProb(i, Util.WEREWOLF);
						c=i;
					}
				}
			}
		}
		System.out.println("HI " + (c + 1));
		
		
		if(!doCO){
			doCO = true;
			return (new Content(new ComingoutContentBuilder(me, Role.MEDIUM))).getText();

		}
		if(!houkoku){
			houkoku = true;
			return (new Content(new IdentContentBuilder(currentGameInfo.getAgentList().get(target),
					white ? Species.HUMAN : Species.WEREWOLF))).getText();
		}
		if (before != c) {
			voteCandidate = currentGameInfo.getAgentList().get(c);
			before = c;
			return (new Content(new VoteContentBuilder(voteCandidate))).getText();
		}
		before = c;
		return Talk.SKIP;
	}


}
