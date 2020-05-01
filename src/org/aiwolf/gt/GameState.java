package org.aiwolf.gt;

public class GameState {
	int game = 0;
	int day = 0;
	int turn = 0;
	AgentStatus[] agents;
	int N;
	int me;
	int w;
	
	void game_init(int _N){
		day = 0;
		turn = 0;
		for(int i=0;i<N;i++){
			agents[i].game_init();
		}
	}
	void day_init(int _N){
		turn = 0;
		for(int i=0;i<N;i++){
			agents[i].day_init();
		}
	}
	
	int cnt_vote(int a){
		int res = 0;
		for(int i=0;i<N;i++){
			if(agents[i].will_vote == a){
				res++;
			}
		}
		return res;
	}
	
	GameState(int _N){
		N = _N;
		agents = new AgentStatus[N];
		for(int i=0;i<N;i++){
			agents[i] = new AgentStatus();
		}
	}
}
