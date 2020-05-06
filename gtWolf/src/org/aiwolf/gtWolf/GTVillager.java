package org.aiwolf.gtWolf;

import java.util.ArrayList;

import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

/** 村人役エージェントクラス */
public class GTVillager extends GTBasePlayer {


	
	boolean f = true;
	Parameters params;
	boolean update_sh=true;
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		if(f){
			params = new Parameters(numAgents);
			sh = new StateHolder(numAgents);
			f=false;
		}
		ArrayList<Integer> fixed = new ArrayList<Integer>();
		fixed.add(meint);
		sh.process(params, gamedata);
		
		gamedata.clear();
		sh.head = 0;
		
		sh.game_init(fixed, meint,numAgents,Util.VILLAGER,params);
		update_sh=true;
		before = -1;
		
	}
	
	protected Agent chooseVote() {
		gamedata.add(new GameData(DataType.VOTESTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		
		
		double mn = -1;
		int c = 0;
		
		//if(sh.gamestate.cnt_vote(meint) * 2 >= currentGameInfo.getAliveAgentList().size()){
		if(numAgents == 5){
			c = chooseMostLikelyWerewolf();
		}else{
			c = chooseMostLikelyExecuted(getAliveAgentsCount() * 0.5);
			if(c == -1){
				c = chooseMostLikelyWerewolf();
			}
		}
		return currentGameInfo.getAgentList().get(c);
	}
	protected String chooseTalk() {
		gamedata.add(new GameData(DataType.TURNSTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		
		boolean condition = false;
		if(numAgents == 5){
			condition = ((day == 1 || day == 2) && sh.gamestate.turn <= 3 && sh.gamestate.turn >= 2);
		}else{
			condition = (day < max_day && sh.gamestate.turn <= 4 && sh.gamestate.turn >= 2);
		}

		if(condition){
			int tu = sh.gamestate.turn - 2;
			for(int j=0;j<rs;j++){
				System.out.print(Util.role_int_to_string[j] + " ");
			}
			System.out.println();
			for(int i=0;i<numAgents;i++){
				System.out.print("agent" + i + " " + agentkoudou[day][tu][i]);
				for(int j=0;j<rs;j++){
					System.out.print( " " + agentScore[day][tu][i][j]);
				}
				System.out.println();
			}
			if(day == 1 && sh.gamestate.turn == 2){
				pred = 0;
				double mm = 0;
				for(int i = 0;i < numAgents; i++) if(i!=meint && sh.gamestate.agents[i].Alive){
					if(mm < agentScore[day][tu][i][Util.WEREWOLF]){
						mm = agentScore[day][tu][i][Util.WEREWOLF];
						pred = i;
					}
				}
				System.out.println("pred = " + pred);
			}
			updateState(sh);
		}
		if(update_sh){
			update_sh = false;
			sh.serach(1000);
		}
		
		
		double mn = -1;
		int c = 0;
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh.rp.getProb(i, Util.WEREWOLF) + " ");
		}
		System.out.println();
		
		if(numAgents == 5){
			if(day == 1 && sh.gamestate.turn == 1){
				return Talk.SKIP;
			}
		}else{
			if(day == 1 && sh.gamestate.turn == 1){
				//return Talk.SKIP;
			}
		}
		if(numAgents == 5){
			c = chooseMostLikelyWerewolf();
		}else{
			c = chooseMostLikelyExecuted(getAliveAgentsCount() * 0.5);
			if(c == -1){
				c = chooseMostLikelyWerewolf();
			}
		}
		System.out.println("willvote " + (c + 1) + " " + mn);
		
		
		if(sh.gamestate.cnt_vote(meint) * 2 >= currentGameInfo.getAliveAgentList().size()){
			before = -1;
		}
		if(numAgents == 5){
			if(sh.gamestate.cnt_vote(c) * 2  < currentGameInfo.getAliveAgentList().size()){
				before = -1;
			}
		}
		if (before != c) {
		//if(true){	
			voteCandidate = currentGameInfo.getAgentList().get(c);
			before = c;
			return (new Content(new VoteContentBuilder(voteCandidate))).getText();
		}
		before = c;
		return Talk.SKIP;
	}

	public String whisper() {
		throw new UnsupportedOperationException();
	}

	public Agent attack() {
		throw new UnsupportedOperationException();
	}

	public Agent divine() {
		throw new UnsupportedOperationException();
	}

	public Agent guard() {
		throw new UnsupportedOperationException();
	}

}
