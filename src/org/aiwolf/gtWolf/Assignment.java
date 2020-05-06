package org.aiwolf.gtWolf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Assignment {
	Random rnd = new Random();
	List<Integer> assignment = new ArrayList<Integer>();
	double score = 0;
	Assignment(){}
	int N;
	Assignment(int _N, ArrayList<Integer> fixed_list, int role){
		N = _N;
		boolean[] isfixed = new boolean[N];
		for(int i=0;i<N;i++){
			isfixed[i] =false;
		}
		for(int i : fixed_list){
			isfixed[i] = true;
		}
		assignment.clear();
		if(N==5){
			assignment.add(Util.VILLAGER);
			assignment.add(Util.VILLAGER);
			assignment.add(Util.SEER);
			assignment.add(Util.WEREWOLF);
			assignment.add(Util.POSSESSED);
		}else{
			for(int i=0;i<8;i++)assignment.add(Util.VILLAGER);
			assignment.add(Util.SEER);
			assignment.add(Util.MEDIUM);
			assignment.add(Util.BODYGUARD);
			for(int i=0;i<3;i++)assignment.add(Util.WEREWOLF);
			assignment.add(Util.POSSESSED);
		}
		List<Integer> extra = new ArrayList<Integer>();
		for(int i : fixed_list){
			if(assignment.get(i) != role){
				extra.add(assignment.get(i));
				assignment.set(i, role);
			}
		}
		{
			int cur = 0;
			for(int i=0;i<N;i++){
				if(cur >= extra.size())break;
				if(!isfixed[i] && assignment.get(i) == role){

					assignment.set(i, extra.get(cur));
					cur++;
				}
			}
		}
		
		
	}
	
	Assignment returncopy(){
		Assignment res = new Assignment();
		res.copyfrom(this);
		return res;
	}
	
	void copyfrom(Assignment c) {
		assignment.clear();
		for (int i = 0; i < c.assignment.size(); i++) {
			assignment.add(c.assignment.get(i));
		}
		N = c.N;
	}

	void randomswap(List<Integer> notfixed) {
		int i = 0;
		int j = 0;
		while(true){
			i = notfixed.get(rnd.nextInt(notfixed.size()));
			j = notfixed.get(rnd.nextInt(notfixed.size()));
			if(assignment.get(i)!= assignment.get(j)) break;
		}
		int t = assignment.get(i);
		assignment.set(i, assignment.get(j));
		assignment.set(j, t);
	}
	
	
	void calcScore(ScoreMatrix scorematrix){
		score = 0;
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){	
				score += scorematrix.scores[i][assignment.get(i)][j][assignment.get(j)];
			}
		}
	}
	
	long getHash(){
		long res = 0;
		
		for(int t : assignment){
			res = res * 8 + t;
		}
		
		return res;
	}
}
