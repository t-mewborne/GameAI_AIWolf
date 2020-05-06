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
 * �?�?師役エージェントクラス
 */
public class GTPossessed extends GTBasePlayer {
	
	Deque<Judge> divinationQueue = new LinkedList<>();
	Map<Agent, Species> myDivinationMap = new HashMap<>();
	List<Agent> whiteList = new ArrayList<>();
	List<Agent> blackList = new ArrayList<>();
	List<Agent> grayList;
	List<Agent> semiWolves = new ArrayList<>();
	List<Agent> possessedList = new ArrayList<>();

	StateHolder sh2;
	boolean f = true;
	Parameters params;
	boolean seer = true;
	boolean doCO = false;
	boolean houkoku = true;
	boolean[] divined;
	boolean pos = false;
	boolean update_sh=true;
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
		divined = new boolean[numAgents];
		for(int i=0;i<numAgents;i++)divined[i] = false;
		
		if(numAgents == 5){
			seer = true;
		}else{
			seer = true;
		}
		
		ArrayList<Integer> fixed = new ArrayList<Integer>();
		fixed.add(meint);
		sh.process(params, gamedata);
		sh2.process(params, gamedata);
		

		gamedata.clear();
		
		sh.head = 0;
		sh2.head = 0;
		
		sh.game_init(fixed, meint,numAgents,Util.POSSESSED,params);
		sh2.game_init(fixed, meint,numAgents,Util.SEER,params);
		
		
		before = -1;
		
	}
	public void dayStart() {
		super.dayStart();
		houkoku = false;
	}
	protected Agent chooseVote() {
		gamedata.add(new GameData(DataType.VOTESTART, day, meint,meint, false));
		
		sh.process(params, gamedata);
		//System.out.println("alive = " + currentGameInfo.getAliveAgentList().size());
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh.rp.getProb(i, Util.WEREWOLF) + " ");
		}
		System.out.println();
		
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh.rp.getProb(i, Util.SEER) + " ");
		}
		System.out.println();
		
		
		double mn = -1;
		int c = 0;
		if(currentGameInfo.getAliveAgentList().size() <= 3){
			for(int i=0;i<numAgents;i++){
				if(i!=meint){
					if(sh.gamestate.agents[i].Alive){
						double score = 1 - sh.rp.getProb(i, Util.WEREWOLF);
						if(mn < score){
							mn = score;
							c=i;
						}
					}
				}
			}
		}else{
			//jinnrou rashii
			mn = -100;
			
			
			if(numAgents == 5){
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							double score = sh2.rp.getProb(i, Util.WEREWOLF) - sh.rp.getProb(i, Util.WEREWOLF);
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
			}
			else{
				for(int i=0;i<numAgents;i++){
					if(i!=meint){
						if(sh.gamestate.agents[i].Alive){
							double score = sh.rp.getProb(i, Util.WEREWOLF);
							if(mn < score){
								mn = score;
								c=i;
							}
						}
					}
				}
				int t = sh.gamestate.agents[c].will_vote;
				
				mn = -100;
				if(t==-1){
					for(int i=0;i<numAgents;i++){
						if(i!=meint){
							if(sh.gamestate.agents[i].Alive){
								double score = 1-sh.rp.getProb(i, Util.WEREWOLF);
								if(mn < score){
									mn = score;
									t=i;
								}
							}
						}
					}
				}
				c = t;
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
				if(numAgents == 5){
					c = -1;
					for(int i=0;i<numAgents;i++){
						if(i!=meint){
							if(sh.gamestate.agents[i].Alive){
								if(!divined[i]){
									if(sh.gamestate.agents[i].corole == Util.SEER){
										c = i;
									}
								}
							}
						}
					}
					c=-1;
					if(c!=-1){
	
						divined[c] = true;
						sh2.scorematrix.divined(sh2.gamestate, meint, c, false);
						return (new Content(new DivinedResultContentBuilder
								(currentGameInfo.getAgentList().get(c),
								Species.WEREWOLF))).getText();
					}
					else{
						mn = -1;
						for(int i=0;i<numAgents;i++){
							if(i!=meint){
								if(sh.gamestate.agents[i].Alive){
									if(!divined[i]){
										if(mn < sh.rp.getProb(i, Util.WEREWOLF)){
											mn = sh.rp.getProb(i, Util.WEREWOLF);
											c=i;
										}
									}
								}
							}
						}
						
						divined[c] = true;
						sh2.scorematrix.divined(sh2.gamestate, meint, c, true);
						return (new Content(new DivinedResultContentBuilder
								(currentGameInfo.getAgentList().get(c),
								Species.HUMAN))).getText();
					}
				}else{
					
					if(day != 2){
						mn = -100;
						for(int i=0;i<numAgents;i++){
							if(i!=meint){
								if(sh.gamestate.agents[i].Alive){
									if(!divined[i]){
										double score = 1 - sh.rp.getProb(i, Util.WEREWOLF);
										if(sh.gamestate.agents[i].corole !=-1){
											score -= 1.0;
										}
										if(mn < score){
											mn = score;
											c = i;
										}
									}
								}
							}
						}
						
						divined[c] = true;
						sh2.scorematrix.divined(sh2.gamestate, meint, c, false);
						return (new Content(new DivinedResultContentBuilder
								(currentGameInfo.getAgentList().get(c),
								Species.WEREWOLF))).getText();
					}else{
						mn = -100;
						for(int i=0;i<numAgents;i++){
							if(i!=meint){
								if(sh.gamestate.agents[i].Alive){
									if(!divined[i]){
										double score = sh.rp.getProb(i, Util.WEREWOLF);
										if(sh.gamestate.agents[i].corole !=-1){
											score -= 1.0;
										}
										if(mn < score){
											mn = score;
											c = i;
										}
									}
								}
							}
						}
						
						divined[c] = true;
						sh2.scorematrix.divined(sh2.gamestate, meint, c, true);
						return (new Content(new DivinedResultContentBuilder
								(currentGameInfo.getAgentList().get(c),
								Species.HUMAN))).getText();
					}
				}	
			}
		}
		
		if(currentGameInfo.getAliveAgentList().size() <= 3){
			if(!pos){
				pos = true;
				return (new Content(new ComingoutContentBuilder(me, Role.WEREWOLF))).getText();
			}
		}
		
		sh2.update();
		
		for(int i=0;i<numAgents;i++){
			System.out.print(sh2.rp.getProb(i, Util.WEREWOLF) + " ");
		}
		System.out.println();
		
		
		
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
		
		
		System.out.println("HI " + (c + 1));
		
		if (before != c) {
		//if(true){	
			voteCandidate = currentGameInfo.getAgentList().get(c);
			before = c;
			return (new Content(new VoteContentBuilder(voteCandidate))).getText();
		}
		before = c;
		
		if(numAgents == 5){
			if(sh.gamestate.cnt_vote(c) * 2 <= currentGameInfo.getAliveAgentList().size());
		}
		return Talk.SKIP;
	}
}
