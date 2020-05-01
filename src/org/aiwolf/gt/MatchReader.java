package org.aiwolf.gt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MatchReader {
	static HashMap<String, Integer> mp = new HashMap<String, Integer>();
	MatchReader(){
		for(int i=1;i<=15;i++){
			String s = "Agent[" + (i < 10 ? "0" : "") + i + "]"; 
			mp.put(s, i);
		}
	}
	
	static void ReadMatch(String filepath_base, ArrayList<GameData> logs, int attention_N,int attention_role){
		logs.add(new GameData(DataType.MATCHSTART, 0, 0, 0, false));
		
		OUTER:for(int game_idx = 0; game_idx < 100; game_idx++){
			String filepath = filepath_base + Util.toString(game_idx, 3) + ".log";
			Scanner scan = new Scanner("");
			
			try{
				File file = new File(filepath);
				scan = new Scanner(file);
			}catch(FileNotFoundException e){
				System.out.println(e);
			}
			
			scan.useDelimiter("\n");
			int last_day = 0;
			boolean first = true;
			String attention_agent = "wasabi";
			int attention = -1;
			
			int N = 0;
			boolean ok=false;
			AgentStatus[] agents = new AgentStatus[15];
			for(int i=0;i<15;i++){
				agents[i] = new AgentStatus();
			}
			int last_turn = -1;
			int turn;
			String last_command = ""; 
			boolean iru = false;;
			while(scan.hasNext()){
				String str = scan.next();
				String[] list = str.split("[ ,]");
				int day = Integer.parseInt(list[0]);
				//System.out.println(str);
				if(last_day != day){
					if(ok){
						logs.add(new GameData(DataType.DAYCHANGE, day, 0, 0, false));
					}
					last_turn = -1;
				}
				if(first){		
					if(!list[1].equals("status") ){
						first = false;
						if(N != attention_N || !iru){
							break OUTER;
						}
						if(ok && N == attention_N){
							logs.add(new GameData(DataType.GAMESTART, day, attention, 0, false));
							for(int i=0;i<N;i++){
								logs.add(new GameData(DataType.ROLE, day, i, agents[i].role, false));
							}
						}
						
					}else{
						if(list[5].equals(attention_agent) ){
							iru = true;
							if(Util.role_string_to_int.get(list[3]) == attention_role ){
								ok=true;
								attention = N;
							}
						}
						agents[N].role = Util.role_string_to_int.get(list[3]);
						N++;
					}
				}
				
				if(list[1].equals("result") ){
					
					for(int i=0;i<N;i++){
						boolean b = (agents[i].role!=0 && agents[i].role!=3);
						boolean b_wins = (list[4].equals("VILLAGER"));
						if(b == b_wins){
							logs.add(new GameData(DataType.WINNER, day, i, 0, false));
						}
					}
					logs.add(new GameData(DataType.GAMEEND, day, 0, 0, false));
				}
				if(ok && !first){
					switch(list[1]){
					case "status":
						
						break;
					case "divine":
					{
						int italker = Integer.parseInt(list[2]) - 1;
						int object = Integer.parseInt(list[3]) - 1;
						if(attention == italker){
							logs.add(new GameData(DataType.DIVINED, day, italker, object, list[4].equals("HUMAN")));
						}
					}
						break;
					case "talk":
					{
						turn = Integer.parseInt(list[3]);
						if(last_turn!=turn){
							logs.add(new GameData(DataType.TURNSTART, day, 0, 0, false));
						}	
						//System.out.println(list[5]);
						switch(list[5]){
						case "VOTE":
						{
							int italker = Integer.parseInt(list[4]) - 1;
							int object = mp.get(list[6]) - 1;
							logs.add(new GameData(DataType.WILLVOTE, day, italker, object, false));
							
						}
							break;
						case "Skip":
						{
							int italker = Integer.parseInt(list[4]) - 1;
							logs.add(new GameData(DataType.SKIP, day, italker, 0, false));
						}
							break;
						case "COMINGOUT":
						{
							int italker = Integer.parseInt(list[4]) - 1;
							logs.add(new GameData(DataType.CO, day, italker, Util.role_string_to_int.get(list[7]), false));
						}
							break;
						case "DIVINED":
						{
							int italker = Integer.parseInt(list[4]) - 1;
							int object = mp.get(list[6]) - 1;
							logs.add(new GameData(DataType.TALKDIVINED, day, italker, object, list[7].equals("HUMAN")));
						}
							break;
						}
						
						
						last_turn = turn;
					}
						break;
					case "vote":
					{
						//System.out.println(last_command);
						int italker = Integer.parseInt(list[2]) - 1;
						int object = Integer.parseInt(list[3]) - 1;
						if(!last_command.equals("vote") || agents[italker].votefor != -1){
							for(int i=0;i<N;i++){
								agents[i].votefor = -1;
							}
							logs.add(new GameData(DataType.VOTESTART, day, 0, 0, false));
							
						}
					
						agents[italker].votefor = object;
						logs.add(new GameData(DataType.VOTE, day, italker, object, false));
					}
						break;
					case "execute":
					{
						int object = Integer.parseInt(list[2]) - 1;	
						logs.add(new GameData(DataType.EXECUTED, day, 0, object, false));
					}
						break;
					case "attack":
					{
						int object = Integer.parseInt(list[2]) - 1;	
						//System.out.println(list[3]);
						logs.add(new GameData(DataType.KILLED, day, 0, object, list[3].equals("true")));
					}
						break;
					}
				}
				last_command = list[1];
				last_day = day;
				
				/*for(int i = 0; i < list.length; i++){
					System.out.print(list[i] + " ");
				}
				System.out.println();
				*/		
			}

		scan.close();
		}
	}
}
