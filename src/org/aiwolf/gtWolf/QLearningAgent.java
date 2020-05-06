package org.aiwolf.gtWolf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class QLearningAgent {
	public double[][] learningMatrix;
	List<GTState> stateList = new ArrayList<>();// states
	List<String> talkList = new ArrayList<>(); // actions
	double discount;
	Random rand = new Random();
	int randFactor = 60; // level of randomness in selecting action (0-100)

	public QLearningAgent(double discount) {
		buildMatrix();

		this.discount = discount;
	}

	private void buildMatrix() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++)
				for (int l = 0; l < 2; l++)
					for(int m = 0; m<2; m++)
						stateList.add(new GTState(Species.HUMAN, i == 0, j == 0, l == 0, m==0));
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++)
				for (int l = 0; l < 2; l++)
					for(int m = 0; m<2; m++)
						stateList.add(new GTState(Species.WEREWOLF, i == 0, j == 0, l == 0, m==0));
		}

		talkList.add(Talk.SKIP);
		talkList.add("Divine");
		talkList.add("Estimate");
		talkList.add("ComeOut");

		try {
			loadMazeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < stateList.size(); i++) {
			for (int j = 0; j < talkList.size(); j++) {
				GTState state = stateList.get(i);
				String action = talkList.get(j);
				if ((!state.divined && action == "Divine") || (state.cameOut && action == "ComeOut")) {
					learningMatrix[i][j] = -1000.0;
				}

			}
		}

	}

	public void updateQTable(GTState state, String action, int reward) {
		System.out.println(
				state.divination + " " + state.suspect + " " + state.divined + " " + state.cameOut + " - state");
		System.out.println(stateList.indexOf(state) + " - state index");
		System.out.println(action + " - action");
		System.out.println(talkList.indexOf(action) + " - action index");
		System.out.println(reward + " = reward");
		learningMatrix[stateList.indexOf(state)][talkList.indexOf(action)] += reward;
		try {
			updateMazeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String playAndGetAction(GTState state, int gameNum) {
		if (gameNum > 8)
			randFactor = 10;
		int stateIndex = stateList.indexOf(state);
		Integer randActionIndex = possibleAction(stateIndex); // random possible action
		double maxQ = -1;
		int maxQAction = 0;
		int actionIndex = -1;
		for (double q : learningMatrix[stateIndex]) {
			actionIndex++;
			if (q > maxQ && learningMatrix[stateIndex][actionIndex] != -1000.0) {
				maxQ = q;
				maxQAction = actionIndex;
			}
		}
		learningMatrix[stateIndex][maxQAction] = learningMatrix[stateIndex][maxQAction] + discount * maxQ; // update

		if (rand.nextInt(100) < randFactor)
			return talkList.get(randActionIndex);
		else
			return talkList.get(maxQAction);
	}

	private Integer possibleAction(int stateIndex) {
		ArrayList<Integer> actions = new ArrayList<>();
		for (int i = 0; i < talkList.size(); i++) {
			if (learningMatrix[stateIndex][i] != -1000.0)
				actions.add(i); // i is the index of the action in talkList
		}

		return actions.get(rand.nextInt(actions.size()));
	}

	private static ArrayList<ArrayList<Double>> tempGrid = new ArrayList<ArrayList<Double>>();

	private void loadMazeFile() throws IOException {
		File file = new File("qTable2.txt");
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
	
	private void updateMazeFile() throws IOException {
		PrintWriter pw = null;
		File file = new File("qTable2.txt");
		if (!file.exists()) file.createNewFile();
		FileWriter fw = new FileWriter(file, false);
		pw = new PrintWriter(fw);
		//pw.print(x);

		for (int i = 0; i < talkList.size(); i++) {
			for (int j = 0; j < stateList.size(); j++) {
				pw.print(learningMatrix[j][i]);
				if (j < stateList.size() - 1) pw.print(",");
			}
			pw.println();
		}

		pw.close();
	}

}