package org.aiwolf.gtWolf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Parameters {
	List<Params> params = new ArrayList<Params>(); 
	Params werewolf_co_rate;
	Params possessed_co_rate;
	Params werewolf_divined_black;
	Params possessed_divined_black;
	Params trust;
	Params werewolf_taben;
	
	Parameters(int N){
		if(N == 5){
		werewolf_co_rate = new Params(0.1,0,1,"werewolf_co_rate",0.3);
		possessed_co_rate  = new Params(0.9,0,1,"possessed_co_rate",0.3);
		werewolf_divined_black  = new Params(0.5,0,1,"werewolf_divined_black",0.3);
		possessed_divined_black  = new Params(0.5,0,1,"possessed_divined_black",0.3);
		trust = new Params(0.6,0,1,"trust",0.3);
		werewolf_taben = new Params(0.0,-1,1,"werewolf_taben",0.3);
		}else{
			werewolf_co_rate = new Params(0.3,0,1,"werewolf_co_rate",0.3);
			possessed_co_rate  = new Params(0.9,0,1,"possessed_co_rate",0.3);
			werewolf_divined_black  = new Params(0.5,0,1,"werewolf_divined_black",0.3);
			possessed_divined_black  = new Params(0.5,0,1,"possessed_divined_black",0.3);
			trust = new Params(0.1,0,1,"trust",0.3);
			werewolf_taben = new Params(0.0,-1,1,"werewolf_taben",0.3);
		}
		params.add(werewolf_co_rate);
		params.add(possessed_co_rate);
		//params.add(werewolf_divined_black);
		//params.add(possessed_divined_black);
		params.add(trust);
		params.add(werewolf_taben);
	}
	
	
	

	void transitFrom(Parameters p, Random rnd){
		copyFrom(p);
		for(int t = 0; t < 1; t++){
			int i = rnd.nextInt(p.params.size());
			double n = p.params.get(i).value;
			n += p.params.get(i).diff * (2 * rnd.nextDouble() - 1.0);
			n = Math.min(n, p.params.get(i).mx);
			n = Math.max(n, p.params.get(i).mn);
			params.get(i).value = n;
		}
	}
	void copyFrom(Parameters p){
		for(int i=0;i<p.params.size();i++){
			params.get(i).value = p.params.get(i).value;
		}
	}
	void debug(){
		for(int i = 0;i < params.size();i++){
			System.out.println(params.get(i).name + ": " +params.get(i).value);
		}
	}
}
