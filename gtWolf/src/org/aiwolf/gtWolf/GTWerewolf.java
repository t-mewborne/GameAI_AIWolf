package org.aiwolf.gtWolf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

/**
 * 人狼役エージェントクラス
 */
public class GTWerewolf extends GTBasePlayer {
	

	StateHolder sh2;
	boolean f = true;
	Parameters params;
	boolean seer = false;
	boolean doCO = false;
	boolean houkoku = true;
	boolean pos = false;
	boolean[] divined;
	boolean[] nakama;
	int votecnt = 0;
	boolean update_sh=true;
	boolean kyoujin_ikiteru = false;
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		if(f){
			params = new Parameters(numAgents);
			
			sh = new StateHolder(numAgents);
			sh2 = new StateHolder(numAgents);
			
			f=false;
		}
		update_sh=true;
		doCO = false;
		houkoku = true;
		pos = false;
		kyoujin_ikiteru = false;
		divined = new boolean[numAgents];
		for(int i=0;i<numAgents;i++)divined[i] = false;
		
		ArrayList<Integer> fixed = new ArrayList<Integer>();
		
		nakama = new boolean[numAgents];
		for(int i=0;i<numAgents;i++)nakama[i] = false;
		for( Agent a : gameInfo.getRoleMap().keySet()){
			fixed.add(a.getAgentIdx() - 1);
			nakama[a.getAgentIdx() - 1] = true;
		}
		sh.process(params, gamedata);
		sh2.process(params, gamedata);
		
		
		gamedata.clear();
		
		sh.head = 0;
		sh2.head = 0;
		
		sh.game_init(fixed, meint,numAgents,Util.WEREWOLF,params);
		fixed.clear();
		fixed.add(meint);
		
		if(numAgents == 5 && rnd.nextDouble() < 0.5){
			seer = true;
		}else{
			seer = false;
		}
		if(numAgents == 15 && gamecount >= 70){
			seer = true;
		}
		
		if(seer){
			sh2.game_init(fixed, meint,numAgents,Util.SEER,params);	
		}else{
			sh2.game_init(fixed, meint,numAgents,Util.VILLAGER,params);	
		}
		
		before = -1;
		
	}
	public void dayStart() {
		super.dayStart();
		houkoku = false;
		votecnt = 0;
	}
	protected Agent chooseVote() {
		gamedata.add(new GameData(DataType.VOTESTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		sh2.process(params, gamedata);
		//System.out.println("alive = " + currentGameInfo.getAliveAgentList().size());
		
		
		double mn = -1;
		int c = 0;
		if(currentGameInfo.getAliveAgentList().size() <= 3){
			votecnt++;
			if(votecnt  == 1){
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							double score = 1 - sh.rp.getProb(i, Util.POSSESSED);
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
			}else{
				c = -1;
				System.out.println("PP");
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							if(sh.gamestate.agents[i].votefor == meint){
								c = i;
							}
						}
					}
				}
				if(c==-1){
					for(int i=0;i<numAgents;i++)if(i!=meint)if(sh.gamestate.agents[i].Alive){
						double score = sh.rp.getProb(i, Util.POSSESSED);
						if(mn < score){ mn = score;c = i;}
					}
				}
			}
		}else{
			if(numAgents == 5){
			c = -1;
			mn = -1;
			for(int i=0;i<numAgents;i++)if(i!=meint)if(sh.gamestate.agents[i].Alive){
				double score = sh.gamestate.cnt_vote(i) + sh2.rp.getProb(i, Util.WEREWOLF);
				if(mn < score){
					mn = score;
					c=i;
				}
			}
			if(c == -1){
				mn = -1;
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = sh2.rp.getProb(i, Util.WEREWOLF);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
			}
			}else{
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = sh.gamestate.cnt_vote(i);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
			if(mn * 2 < currentGameInfo.getAliveAgentList().size()){
				mn = -1;
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							double score = sh2.rp.getProb(i, Util.WEREWOLF);
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
			}
			}
		}
	
		
			
		return currentGameInfo.getAgentList().get(c);
	}
	protected String chooseTalk() {
		gamedata.add(new GameData(DataType.TURNSTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		sh2.process(params, gamedata);
		
		updateState(sh);
		updateState(sh2);
		
		
		if(update_sh){
			System.out.println("SEARCH");
			update_sh = false;
			sh.serach(1000);
			sh2.serach(1000);
		}
		double mn = -1;
		int c = 0;
		
		if(seer){
		
		if(!doCO){
			doCO = true;
			return (new Content(new ComingoutContentBuilder(me, Role.SEER))).getText();
		}
		
		
		if(!houkoku){
			houkoku = true;
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						if(!divined[i]){
							double score = sh2.rp.getProb(i, Util.WEREWOLF);
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
			}
			if(nakama[c]){
				divined[c] = true;
				sh2.scorematrix.divined(sh2.gamestate, meint, c, true);
				return (new Content(new DivinedResultContentBuilder
						(currentGameInfo.getAgentList().get(c),
						Species.HUMAN))).getText();
			}else{
				divined[c] = true;
				sh2.scorematrix.divined(sh2.gamestate, meint, c, false);
				return (new Content(new DivinedResultContentBuilder
						(currentGameInfo.getAgentList().get(c),
						Species.WEREWOLF))).getText();
			}
		}
		
		}
		//System.out.println(day + " " +  sh.gamestate.turn);
		if(numAgents == 5){
			if(gamecount >= 50){
				if(day == 1 && sh.gamestate.turn == 1){
					return Talk.SKIP;
				}
			}
			
		}else if(numAgents == 15){
			if(gamecount >= 50){
				if(day == 1 && sh.gamestate.turn == 1){
					return Talk.SKIP;
				}
			}
		}
		if(currentGameInfo.getAliveAgentList().size() <= 3){
			if(!pos){
				pos = true;
				if(numAgents == 5){
					double all = 0;
					double alive = 0;
					for(int i=0;i<numAgents;i++){
						all+=sh.rp.getProb(i, Util.POSSESSED);
						if(sh.gamestate.agents[i].Alive){
							alive +=sh.rp.getProb(i, Util.POSSESSED);
						}
					}
					if(alive > 0.5 * all){
						kyoujin_ikiteru = true;
						System.out.println("kyojin");
						return (new Content(new ComingoutContentBuilder(me, Role.WEREWOLF))).getText();	
					}
				}else{
					double all = 0;
					double alive = 0;
					for(int i=0;i<numAgents;i++){
						all+=sh.rp.getProb(i, Util.POSSESSED);
						if(sh.gamestate.agents[i].Alive){
							alive +=sh.rp.getProb(i, Util.POSSESSED);
						}
					}
					if(alive > 0.5 * all){
						kyoujin_ikiteru = true;
						return (new Content(new ComingoutContentBuilder(me, Role.WEREWOLF))).getText();	
					}
					
				}
			}else if(kyoujin_ikiteru){
				if(numAgents == 0){
					System.out.println("kyojin");
					mn = -1;
					for(int i=0;i<numAgents;i++)if(i!=meint)if(sh.gamestate.agents[i].Alive){
						double score = 1 - sh.rp.getProb(i, Util.POSSESSED);
						if(mn < score){
							mn = score;
							c = i;
						}
					}		

					voteCandidate = currentGameInfo.getAgentList().get(c);
					before = c;
					return (new Content(new VoteContentBuilder(voteCandidate))).getText();
				}
			}
		}
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh2.rp.getProb(i, Util.WEREWOLF) + " ");
		}
		System.out.println();
		if(numAgents == 5){
			c = -1;
			mn = -1;
			for(int i=0;i<numAgents;i++)if(i!=meint)if(sh.gamestate.agents[i].Alive){
				double score = sh2.rp.getProb(i, Util.WEREWOLF);
				if(day != 1 || sh.gamestate.turn > 2){
					//score += sh.gamestate.cnt_vote(i);
				}
				//double score = sh2.rp.getProb(i, Util.WEREWOLF);
				if(mn < score){
					mn = score;
					c=i;
				}
			}
			if(c == -1){
				mn = -1;
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = sh2.rp.getProb(i, Util.WEREWOLF);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
			}
		}else{
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = sh.gamestate.cnt_vote(i);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
			if(mn * 2 < currentGameInfo.getAliveAgentList().size()){
				mn = -1;
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							double score = sh2.rp.getProb(i, Util.WEREWOLF);
							if(nakama[i])score -= 0.4;
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
			}
			
		}
		
		
		System.out.println("HI " + (c + 1) + " " + mn);
		if(numAgents == 5){
			if(sh.gamestate.cnt_vote(meint) * 2  >= currentGameInfo.getAliveAgentList().size()){
				before = -1;
			}
			if(sh.gamestate.cnt_vote(c) * 2  < currentGameInfo.getAliveAgentList().size()){
				before = -1;
			}
			
			
			
		}else{
			if(sh.gamestate.cnt_vote(meint) * 2 >= currentGameInfo.getAliveAgentList().size()){
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
	
	protected Agent attackVote() {
		sh.process(params, gamedata);
		sh.update();
		double mn = -1;
		int c = 0;
		
		if(numAgents == 5){
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = 1 - sh.rp.getProb(i, Util.POSSESSED);
						//double score = sh.rp.getProb(i, Util.SEER);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
		}else{
			for(int i=0;i<numAgents;i++){
				if(i!=meint && !nakama[i]){
					if(sh.gamestate.agents[i].Alive){
						double score = 1 - sh.rp.getProb(i, Util.POSSESSED);
						score += 0.2 * sh.rp.getProb(i, Util.SEER);
						score += 0.1 * sh.rp.getProb(i, Util.BODYGUARD);
						score += 0.1 * sh.rp.getProb(i, Util.MEDIUM);
						score += 3 * wincnt[i]/(gamecount + 0.01);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
		}
		
	
		
			
		return currentGameInfo.getAgentList().get(c);
	}
	
}
