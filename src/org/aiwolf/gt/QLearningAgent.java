package org.aiwolf.gt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.aiwolf.client.lib.Content;
//import org.aiwolf.client.lib.DivinedResultContentBuilder;
//import org.aiwolf.client.lib.EstimateContentBuilder;
//import org.aiwolf.common.data.Judge;
//import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

//import game.Game;
//import game.Point;

public class QLearningAgent {
	public double[][] learningMatrix;
	List<GTState> stateList = new ArrayList<>();// states
	List<String> talkList = new ArrayList<>(); // actions
	double discount;
	Random rand = new Random();
	int randFactor; // level of randomness in selecting action (0-100)
	// int goalState;

	public QLearningAgent(double discount, int randFactor) {
		// this.stateList = //TODO
		buildMatrix();

		this.discount = discount;
		this.randFactor = randFactor; // level of randomness in selecting action (0-100)
		// this.goalState = goalState;
	}

	private void buildMatrix() {
		stateList.add(new GTState(Species.HUMAN, false));
		stateList.add(new GTState(Species.HUMAN, true));
		// stateList.add(new State(Species.WEREWOLF, false));
		stateList.add(new GTState(Species.WEREWOLF, true));

		// Judge ident = GTSeer.divinationQueue.poll();
		// Judge ident = GTSeer.divination;

//		talkList.add(Talk.SKIP);
//		talkList.add((new Content(new DivinedResultContentBuilder(ident.getTarget(), ident.getResult()))).getText());
//		talkList.add((new Content(new EstimateContentBuilder(GTSeer.voteCandidate, Role.WEREWOLF))).getText());

		talkList.add(Talk.SKIP);
		talkList.add("Divine");
		talkList.add("Estimate");
//		for (int i = 0; i < stateList.size(); i++) {
//			for (int j = 0; j < talkList.size(); j++) {
//				learningMatrix[i][j] = 0.0;
//			}
//		}
		try {
			loadMazeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//	public void playEpisode(GTState state) {
//		// int state = rand.nextInt(learningMatrix.length);
//		// while (state = goalState) {
//		// int action = rand.nextInt(learningMatrix.length);
//		Integer action = possibleAction(state);
//		double maxQ = -1;
//		for (double q : learningMatrix[action])
//			if (q > maxQ)
//				maxQ = q;
//
//		learningMatrix[state][action] = pathMatrix[state][action] + discount * maxQ; //update
//		state = action;
//		// }
//	}

	public void updateQTable(GTState state, String action, int reward) {
		learningMatrix[stateList.indexOf(state)][talkList.indexOf(action)] += reward;
	}

	public String playAndGetAction(GTState state) {
		int stateIndex = stateList.indexOf(state);
		Integer randActionIndex = possibleAction(stateIndex); // random possible action
		double maxQ = -1;
		int maxQAction = 0;
		int actionIndex = -1;
		for (double q : learningMatrix[stateIndex]) {
			actionIndex++;
			if (q > maxQ)
				maxQ = q;
			maxQAction = actionIndex;
		}
		learningMatrix[stateIndex][actionIndex] = learningMatrix[stateIndex][actionIndex] + discount * maxQ; // update

		if (rand.nextInt(100) < randFactor)
			return talkList.get(randActionIndex);
		else
			return talkList.get(maxQAction);
	}

	private Integer possibleAction(int stateIndex) {
		ArrayList<Integer> actions = new ArrayList<>();
		for (int i = 0; i < learningMatrix[stateIndex].length; i++) {
			if (learningMatrix[stateIndex][i] != -1)
				actions.add(i); // i is the index of the action in talkList
		}

		return actions.get(rand.nextInt(actions.size()));
	}

//	public void test(int initialState) {
//		int state = initialState;
//		while (state != goalState) {
//			int action = 0;
//			double maxQ = -1;
//			for (int i = 0; i < learningMatrix.length; i++) {
//				if (learningMatrix[state][i] > maxQ) {
//					maxQ = learningMatrix[state][i];
//					action = i;
//				}
//			}
//
//			System.out.println("Went from " + state + " to " + action);
//			state = action;
//		}
//		System.out.println("Arrived!");
//	}

	private static ArrayList<ArrayList<Double>> tempGrid = new ArrayList<ArrayList<Double>>();

	private void loadMazeFile() throws IOException {
		File file = new File("qTable.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		while ((st = br.readLine()) != null) {
			ArrayList<Double> tempLine = new ArrayList<Double>();
			String[] line = st.split(",");
			for (String t : line) {
				tempLine.add(Double.parseDouble(t));
			}
			tempGrid.add(tempLine);
		}
		br.close();

		ArrayList<Double> firstLine = tempGrid.get(0);
		int width = firstLine.size();
		int height = tempGrid.size();
		learningMatrix = new double[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Double value = tempGrid.get(y).get(x);
				learningMatrix[x][y] = value;
				// if (value == 2) g.goal = new Point(x,y);
			}
		}
		for (String i : talkList) {
			System.out.println(i);
		}
	}

}

//class State {
//	Species divination;	// what they divined someone to be
//	Boolean suspect; 	// True if susWolves is not empty (you have something to announce) (announce/vote)
//
//	public State(Species divination, Boolean suspect) {
//		this.divination = divination;
//		this.suspect = suspect;
//	}
