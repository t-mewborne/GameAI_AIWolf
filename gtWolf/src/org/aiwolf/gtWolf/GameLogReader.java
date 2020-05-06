package org.aiwolf.gtWolf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameLogReader {
	static Random rnd = new Random();
	public static void main(String args[]){
		new Util();
	
		new MatchReader();
		String filepath_base  = "C:\\pr\\wolf\\AI\\AIWolf-ver0.5.5\\log_cedec2018\\log_cedec2018\\";
		
		ArrayList<GameData> logs = new ArrayList<GameData>();
		
		int N = 15;
		int role = Util.VILLAGER;
		
		for(int i=50;i<=60;i++){
			if(i % 10 == 0){
				System.out.println(i);
			}
			String filepath = filepath_base + Util.toString(i, 3) + "\\";
			MatchReader.ReadMatch(filepath, logs, N, role);
			
		}
		System.out.println(logs.size());
		
		Parameters cur = new Parameters(N);
		Parameters ne = new Parameters(N);
		/*
		for(int i=0;i<100;i++){
			ne.transitFrom(cur, rnd);

			double ne_score = Evaluator.eval(ne, logs, N, role);
			double cur_score = Evaluator.eval(cur, logs, N, role);
			if(i%10 == 0)System.out.println(i + " " + cur_score + " " + ne_score + " " + Evaluator.acc);
			if(ne_score > cur_score){
				cur.copyFrom(ne);
				
				//cur.debug();
				//System.out.println(ne_score);
			}
		}
		*/

		cur.debug();
		
//		System.out.println("eval = " 
//		+ Evaluator.eval(cur, logs, N, role) + " " 
//				+ Evaluator.acc);
		
	}
}
