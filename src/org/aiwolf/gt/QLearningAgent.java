package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.client.lib.EstimateContentBuilder;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

public class QLearningAgent {
	double[][] learningMatrix;
	List<State> stateList = new ArrayList<>();
	List<String> talkList = new ArrayList<>();
	double discount;
	Random rand = new Random();
	int goalState;

	public QLearningAgent(double discount, int goalState) {
		//this.stateList = //TODO
		buildMatrix();
		
		
		//this.actionList = //TODO
		this.learningMatrix = new double[stateList.size()][talkList.size()];
		this.discount = discount;
		this.goalState = goalState;
	}
	
	private void buildMatrix() {
		stateList.add(new State(Species.HUMAN, false));
		stateList.add(new State(Species.HUMAN, true));
		stateList.add(new State(Species.WEREWOLF, false));
		stateList.add(new State(Species.WEREWOLF, true));
		
		Judge ident = GTSeer.divinationQueue.poll();
		
		talkList.add(Talk.SKIP);
		talkList.add((new Content(new DivinedResultContentBuilder(ident.getTarget(), ident.getResult()))).getText());
		talkList.add((new Content(new EstimateContentBuilder(GTSeer.voteCandidate,Role.WEREWOLF))).getText());
	}

	public void playEpisode(State state) {
		//int state = rand.nextInt(learningMatrix.length);
		//while (state = goalState) {
			//int action = rand.nextInt(learningMatrix.length);
			int action = possibleAction(state);
			double maxQ = -1;
			for (double q : learningMatrix[action]) if (q > maxQ) maxQ = q;
			
			learningMatrix[state][action] = pathMatrix[state][action] + discount * maxQ;
			state = action;
		//}
	}
	
	private Integer possibleAction(State state) {
		ArrayList<Integer> actions = new ArrayList<>();
		
		for (int i = 1; i < pathMatrix[state].length; i++) {
			if (pathMatrix[state][i] != -1) actions.add(i);
		}
			
		return actions.get(rand.nextInt(actions.size()));
	}
	
	public void test(int initialState) {
		int state = initialState;
		while (state != goalState) {
			int action = 0;
			double maxQ = -1;
			for (int i = 0; i < learningMatrix.length; i++) {
				if (learningMatrix[state][i] > maxQ) {
					maxQ = learningMatrix[state][i];
				    action = i;
				}
			}
			
			System.out.println("Went from " + state + " to " + action);
			state = action;
		}
		System.out.println("Arrived!");
	}

}

class State {
	Species divination;
	Boolean suspect; 		//True if susWolves is not empty (announce/vote)
	
	public State(Species divination, Boolean suspect) {
		this.divination = divination;
		this.suspect = suspect;
	}
	
}