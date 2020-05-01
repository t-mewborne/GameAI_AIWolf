package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.List;

public class StateHolder {

	RolePrediction rp;
	ScoreMatrix scorematrix;
	GameState gamestate;
	int head;

	int v = 0;
	int times = 50;
	boolean debug = true;
	int N;
	StateHolder(int _N){
		N=_N;
		rp = new RolePrediction() ;
		scorematrix = new ScoreMatrix();
		gamestate = new GameState(N);
		head = 0;

		v = 0;
		times = 50;
		debug = true;
		if(N==15){
			times = 500;
		}
	}
	
	void game_init(List<Integer> fixed, int me, int N, int role, Parameters params){
		v=0;
		rp = new RolePrediction(N, fixed, role);
		gamestate.game_init(N);
		gamestate.me = me;
		scorematrix = new ScoreMatrix();
		scorematrix.init(N);
		scorematrix.params = params;
		if(debug)
			System.out.println("GAMESTART TYUMOKU : " + me);
	}
	
	void update(){
		rp.recalc(scorematrix, gamestate);
		rp.search(scorematrix, gamestate, times);
	}
	
	void serach(int t){
		rp.search(scorematrix, gamestate, t);
	}
	
	
	void process(Parameters params, ArrayList<GameData> logs){
	
		for(;head < logs.size();head++){
			GameData g = logs.get(head);
			switch(g.type){
			case TURNSTART:
			{
				if(debug)
					System.out.println("----------TURNSTART--------------" + gamestate.turn);
				if(N == 5){
					System.out.println(gamestate.day + " " + gamestate.turn);
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
			case ID:
			{
				if(debug)
					System.out.println(g.talker + " idented " + g.object + " as " + (g.white ? "V" : "W"));
				scorematrix.ident(gamestate, g.talker, g.object, g.white);
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
				scorematrix.divined(gamestate, g.talker, g.object, g.white);
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
					if(gamestate.agents[g.object].Alive){
						scorematrix.killed(gamestate, g.object);
						gamestate.agents[g.object].Alive = false;
					}
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
		
	}
}
