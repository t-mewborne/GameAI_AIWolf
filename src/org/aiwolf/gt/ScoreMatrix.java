package org.aiwolf.gt;

public class ScoreMatrix {
	double[][][][] scores;
	Parameters params;
	void init(int N){
		int M = 0;
		if(N == 5) M = 4;
		else if(N == 15) M = 6;
		scores = new double[N][M][N][M];
		for(int i1 = 0; i1 < N; i1++){
			for(int r1 = 0; r1 < M; r1++){
				for(int i2 = 0; i2 < N; i2++){
					for(int r2 = 0; r2 < M; r2++){
						scores[i1][r1][i2][r2] = 0;
					}
				}
			}
		}
	}
	
	
	void killed(GameState gamestate, int a){
		scores[a][Util.WEREWOLF][a][Util.WEREWOLF] += 10;
	}
	
	void talk_co(GameState gamestate, int a, int role){
		if(a < 0 || a >= gamestate.N) return;
		if(gamestate.N == 15){	
			
			if(role == Util.SEER){
				if(gamestate.turn == 1){
					scores[a][Util.VILLAGER][a][Util.VILLAGER] += 30;
					scores[a][Util.BODYGUARD][a][Util.BODYGUARD] += 10;
					scores[a][Util.MEDIUM][a][Util.MEDIUM] += 10;
					//scores[a][Util.WEREWOLF][a][Util.WEREWOLF] += Util.nlog(params.werewolf_co_rate.value);
					scores[a][Util.WEREWOLF][a][Util.WEREWOLF] += 1;
					
					//scores[a][Util.POSSESSED][a][Util.POSSESSED] += Util.nlog(params.possessed_co_rate.value);
				//scores[a][Util.VILLAGER][a][Util.VILLAGER] += 10;
				}
			}else if(role == Util.MEDIUM){
				scores[a][Util.VILLAGER][a][Util.VILLAGER] += 30;
				scores[a][Util.BODYGUARD][a][Util.BODYGUARD] += 30;
				//scores[a][Util.SEER][a][Util.SEER] += 30;
				//scores[a][Util.WEREWOLF][a][Util.WEREWOLF] += 1.5;
				//scores[a][Util.POSSESSED][a][Util.POSSESSED] += 1.5;
				scores[a][Util.MEDIUM][a][Util.MEDIUM] -= 10;
			}
		}
	}
	
	void firstTurnEnd(GameState gamestate){
		if(gamestate.N == 5){
			for(int i = 0; i < gamestate.N; i++){
				if(gamestate.agents[i].corole == -1){
					scores[i][Util.SEER][i][Util.SEER] += 10;
					scores[i][Util.WEREWOLF][i][Util.WEREWOLF] += Util.nlog(1-params.werewolf_co_rate.value);
					scores[i][Util.POSSESSED][i][Util.POSSESSED] += Util.nlog(1-params.possessed_co_rate.value);
					
				}else if(gamestate.agents[i].corole == Util.SEER){
					scores[i][Util.VILLAGER][i][Util.VILLAGER] += 10;
					scores[i][Util.WEREWOLF][i][Util.WEREWOLF] += Util.nlog(params.werewolf_co_rate.value);
					scores[i][Util.POSSESSED][i][Util.POSSESSED] += Util.nlog(params.possessed_co_rate.value);
				}
			}
		}else{
			for(int i = 0; i < gamestate.N; i++){
				if(gamestate.agents[i].corole != Util.MEDIUM){
					scores[i][Util.MEDIUM][i][Util.MEDIUM] += 10;
				}
				if(gamestate.agents[i].corole != Util.SEER){
					//scores[i][Util.MEDIUM][i][Util.MEDIUM] += 10;
					scores[i][Util.SEER][i][Util.SEER] += 50;
					//scores[i][Util.WEREWOLF][i][Util.WEREWOLF] += Util.nlog(1-params.werewolf_co_rate.value);
					//scores[i][Util.POSSESSED][i][Util.POSSESSED] += Util.nlog(1-params.possessed_co_rate.value);
					scores[i][Util.POSSESSED][i][Util.POSSESSED]  += 3;
				}else if(gamestate.agents[i].corole == Util.SEER){
					//scores[i][Util.VILLAGER][i][Util.VILLAGER] += 10;
					//scores[i][Util.WEREWOLF][i][Util.WEREWOLF] += Util.nlog(params.werewolf_co_rate.value);
					//scores[i][Util.POSSESSED][i][Util.POSSESSED] += Util.nlog(params.possessed_co_rate.value);
				}
			
			}
			
		}
	}
	
	void divined(GameState gamestate, int a, int b, boolean white){
		if(b < 0 || b >= gamestate.N) return;
		if(white){
			scores[b][Util.WEREWOLF][b][Util.WEREWOLF]+=50;
		}else{
			scores[b][Util.WEREWOLF][b][Util.WEREWOLF]-=50;
		}
	}
	
	
	void ident(GameState gamestate, int a, int b, boolean white){
		if(b < 0 || b >= gamestate.N) return;
		if(gamestate.N == 15){
		if(white){
			scores[a][Util.MEDIUM][b][Util.WEREWOLF]+=30;
		}else{
			scores[a][Util.MEDIUM][b][Util.WEREWOLF]-=30;
		}
		}
	}
	void talk_divined(GameState gamestate, int a, int b, boolean white){
		if(b < 0 || b >= gamestate.N) return;
		if(gamestate.agents[a].target == -1){
			gamestate.agents[a].target = b;
		}else{
			return;
		}
		if(white){

			if(gamestate.N == 5){
				scores[a][Util.SEER][b][Util.WEREWOLF]+=10;
			
				//scores[a][Util.SEER][a][Util.SEER] += 0;
				if(true){
						scores[a][Util.POSSESSED][a][Util.POSSESSED] 
									+= Util.nlog(1 - params.possessed_divined_black.value);
						scores[a][Util.WEREWOLF][a][Util.WEREWOLF] 
									+=  Util.nlog(1 - params.werewolf_divined_black.value);
				}
			}else{
				scores[a][Util.SEER][b][Util.WEREWOLF]+=30;
			}
		}else{
			//scores[a][Util.SEER][a][Util.SEER] += 0.5;
			
			if(gamestate.N == 5){
				if(true){
					scores[a][Util.POSSESSED][a][Util.POSSESSED] 
							+=  Util.nlog(params.possessed_divined_black.value);
					scores[a][Util.WEREWOLF][a][Util.WEREWOLF] 
							+=  Util.nlog(params.werewolf_divined_black.value);
					}
				scores[a][Util.SEER][b][Util.VILLAGER]+=1.0;
				scores[a][Util.SEER][b][Util.POSSESSED]+=1.0;
			}else{
				scores[a][Util.SEER][b][Util.VILLAGER]+=0.2;
				scores[a][Util.SEER][b][Util.POSSESSED]+=0.2;
				scores[a][Util.SEER][b][Util.BODYGUARD]+=0.2;
				scores[a][Util.SEER][b][Util.MEDIUM]+=0.2;
				//scores[a][Util.SEER][b][Util.WEREWOLF] -= 1.5;
				
			}
		}
	}
	void vote(GameState gamestate, int a, int b){
		if(b < 0 || b >= gamestate.N) return;
		gamestate.agents[a].votefor = b;
		if(gamestate.N == 5){
			if(gamestate.me != a){
				scores[a][Util.VILLAGER][b][Util.WEREWOLF] -= 0.3;
				scores[a][Util.SEER][b][Util.WEREWOLF] -= 0.3;
			}
		}
		else{
			if(gamestate.me != a){
				for(int i=0;i<2;i++){
				scores[a][Util.VILLAGER][b][Util.nothumans[i]] -= 0.01;
				scores[a][Util.SEER][b][Util.nothumans[i]] -= 0.01;
				scores[a][Util.BODYGUARD][b][Util.nothumans[i]] -= 0.01;
				scores[a][Util.MEDIUM][b][Util.nothumans[i]] -= 0.01;
				}
				scores[a][Util.WEREWOLF][b][Util.WEREWOLF] += 0.05;
			}
		}
		//scores[a][Util.POSSESSED][b][Util.WEREWOLF] += 0.2;
	}
	void will_vote(GameState gamestate, int a, int b){
		if(b < 0 || b >= gamestate.N) return;
		if(gamestate.N == 5){
			if(gamestate.agents[a].will_vote == b)return;
			if(gamestate.day!=1 || gamestate.turn !=1){
			if(gamestate.me != a){
				scores[a][Util.VILLAGER][b][Util.WEREWOLF] -= params.trust.value * 0.1;
				scores[a][Util.SEER][b][Util.WEREWOLF] -= params.trust.value;
				scores[a][Util.WEREWOLF][a][Util.WEREWOLF] -= params.werewolf_taben.value;
			}
			gamestate.agents[a].will_vote = b;
			}
		}else{
			if(gamestate.agents[a].will_vote == b)return;
			if(gamestate.agents[a].will_vote != -1 && gamestate.agents[a].will_vote !=b){
				if(gamestate.me != a){
					//scores[a][Util.WEREWOLF][a][Util.WEREWOLF] += 0.1;
					//scores[a][Util.POSSESSED][a][Util.POSSESSED] += 0.1;
				}
			}
			gamestate.agents[a].will_vote = b;
			if(gamestate.me != a){
				scores[a][Util.VILLAGER][b][Util.WEREWOLF] -= params.trust.value;
				scores[a][Util.BODYGUARD][b][Util.WEREWOLF] -= params.trust.value;
				scores[a][Util.MEDIUM][b][Util.WEREWOLF] -= params.trust.value;
				scores[a][Util.SEER][b][Util.WEREWOLF] -= params.trust.value;
				scores[a][Util.WEREWOLF][b][Util.WEREWOLF] += 0.01;
			
				scores[a][Util.WEREWOLF][a][Util.WEREWOLF] -= params.werewolf_taben.value;
			}
		}
	}
}
