package org.aiwolf.gt;

import java.util.ArrayList;

public class Evaluator {
	static double acc = 0;
	static double eval(Parameters params, ArrayList<GameData> logs, int N, int role){
		

		RolePrediction rp = new RolePrediction() ;
		ScoreMatrix scorematrix = new ScoreMatrix();
		GameState gamestate = new GameState(N);
		int cnt = 0;
		int seikai = 0;
		double loss = 0;
		int v = 0;
		boolean debug = false;
		int times = 50;
		if(N==15){
			times = 200;
		}
		for(GameData g : logs){
			
			switch(g.type){
			case TURNSTART:
			{
				if(debug)
					System.out.println("----------TURNSTART--------------");
				if(N == 5){
					if(gamestate.day == 1 && gamestate.turn == 1){
						scorematrix.firstTurnEnd(gamestate);
					}
				}else{
					if(gamestate.day == 1 && gamestate.turn == 1){
						scorematrix.firstTurnEnd(gamestate);
					}
				}
				
				gamestate.turn++;
				rp.recalc(scorematrix, gamestate);
				rp.search(scorematrix, gamestate, times);
				
				
				/*
				for(int t = 0; t < Math.min(rp.assignments.size(), 10); t++){
					System.out.print(rp.assignments.get(t).score + " ");
					for(int i=0;i<N;i++){
						System.out.print(rp.assignments.get(t).assignment.get(i) + " ");
					}
					System.out.println();
				}
				*/
				
			}
			break;
			case ROLE:
			{
				gamestate.agents[g.talker].role = g.object;
			
			}
			break;
			case DAYCHANGE:
			{
				gamestate.day++;
				gamestate.day_init(N);
				if(debug)
					System.out.println("----------DAYCHANGE--------------");
			}
			break;
			case VOTESTART:
			{
				//if(gamestate.day == 1 && v == 0){
				rp.recalc(scorematrix, gamestate);
				rp.search(scorematrix, gamestate, times);
				if(gamestate.agents[gamestate.me].Alive && gamestate.day <= 3){
					
					int pred_role = Util.WEREWOLF;
					if(debug){
					for(int t = 0; t < Math.min(rp.assignments.size(), 10); t++){
						System.out.print(rp.assignments.get(t).score + " ");
						for(int i=0;i<N;i++){
							System.out.print(rp.assignments.get(t).assignment.get(i) + " ");
						}
						System.out.println();
					}
					}
					if(debug){
						for(int i=0;i<N;i++){
							System.out.print(rp.getProb(i, pred_role) + " ");
						}
						System.out.println();
					}
					v = 1;
					cnt++;
					loss += Math.log(rp.getProb(gamestate.w, Util.WEREWOLF));
					double mn = -1;
					int pred = 0;
					
					for(int i = 0; i < N; i++){
						if(gamestate.agents[i].Alive){
							if(mn < rp.getProb(i, pred_role)){
								mn = rp.getProb(i, pred_role);
								pred = i;
							}
						}
					}
					
					if(gamestate.agents[pred].role == pred_role){
						if(debug)
							System.out.println("seikai!");
						seikai++;
					}else{
						if(debug)
							System.out.println("hazure");
					}
				}
				if(debug)
					System.out.println("----------VOTESTART--------------");
			}
			break;
			case WILLVOTE:
			{
				if(debug)
					System.out.println(g.talker + " willvote " + g.object);
				scorematrix.will_vote(gamestate, g.talker, g.object);
			}
			break;
			case TALKDIVINED:
			{
				if(debug)
					System.out.println(g.talker + " divined " + g.object + " as " + (g.white ? "V" : "W"));
				scorematrix.talk_divined(gamestate, g.talker, g.object, g.white);
			}
			break;
			case CO:
			{
				scorematrix.talk_co(gamestate, g.talker, g.object);
				gamestate.agents[g.talker].corole = g.object;
				if(debug)
					System.out.println(g.talker + " CO " + Util.role_int_to_string[g.object] );
				
			}
			break;
			case VOTE:
			{
				scorematrix.vote(gamestate, g.talker, g.object);
				if(debug)
					System.out.println(g.talker + " vote for " + g.object);
			}
			break;
			case DIVINED:
			{
				if(debug)
					System.out.println(g.talker + " divined " + g.object + " " + (g.white ? "V" : "W"));	
			}
			break;
			case EXECUTED:
			{
				gamestate.agents[g.object].Alive = false;
				if(debug)
					System.out.println("executed " + g.object);
			}
			break;
			case KILLED:
			{
				if(g.white){
					scorematrix.killed(gamestate, g.object);
					gamestate.agents[g.object].Alive = false;
				}
				if(debug)
					System.out.println("killed " + g.object + " " +  (g.white ? "" : "un") + "successfully");
			}
			break;
			case WINNER:
			{
				gamestate.agents[g.talker].wincnt++;
				
				if(debug)
					System.out.println("winner : " + g.talker);
			}
			break;
			case GAMESTART:
			{
				v=0;
				ArrayList<Integer> fixed = new ArrayList<Integer>();
				fixed.add(g.talker);
				rp = new RolePrediction(N, fixed, role);
				gamestate.game_init(N);
				gamestate.me = g.talker;
				scorematrix = new ScoreMatrix();
				scorematrix.init(N);
				scorematrix.params = params;
				if(debug)
					System.out.println("GAMESTART TYUMOKU : " + g.talker);
			}
			break;
			case MATCHSTART:
			{
				gamestate = new GameState(N);
				if(debug)
					System.out.println("MATCHSTART");
			}
			break;
			case GAMEEND:
			{
				gamestate.game++;
				if(debug)
					System.out.println("GAMEEND");
			}
			break;
			}
			
		}
		//System.out.println(cnt);
		acc =  (double)seikai / cnt;
		//System.out.println((double)seikai / cnt);
		//return (double)loss / cnt;
		return (double)seikai / cnt;
		
	}
}
