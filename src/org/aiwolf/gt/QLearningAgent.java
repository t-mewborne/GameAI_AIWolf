package org.aiwolf.gt;

import java.util.ArrayList;
import java.util.Random;

public class QLearningAgent {
	double[][] learningMatrix;
	int[][] pathMatrix;
	double discount;
	Random rand = new Random();
	int goalState;

	public QLearningAgent(int[][] pathMatrix, double discount, int goalState) {
		this.pathMatrix = pathMatrix;
		this.learningMatrix = new double[pathMatrix.length][pathMatrix.length];
		this.discount = discount;
		this.goalState = goalState;
	}

	public void playEpisode(State state) {
		//int state = rand.nextInt(learningMatrix.length);
		//while (state != goalState) {
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
	
}

class Action{
	
}

