/*
 * Zakary Littlefield
 * March 28, 2010

 * Free to use this code for whatever purpose
 * 
 * There are no guarantees on success or failure
 * zlittlefield@gmail.com
 * 
 * 
 * 
 * Modified for use in Independent Study by
 * Lane Bohrer and Evin Jaff
 * a.bohrer@wustl.edu, evin@wustl.edu
 * Spring 2021
 */


/* TODO
 *
 * - check to see if game win (are there any 2s/3s still uncovered?)
 * - implement coin counter
 * 
 * */


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.*;

//Evin and Lane's imports
import cse131.ArgsProcessor;
//import boards.boardSimulationFiles;
//import java.math.BigInteger;

public class Main extends JFrame 
{
	private static final long serialVersionUID = 1L;
	ArgsProcessor ap;
	static String[] localArgs;
	JTextField showValues[][];
	JTextField Cols[];
	JTextField Rows[];
	JTextField Vcols[];
	JTextField Vrows[];
	JLabel prob;
	JLabel probabilities[][];
	JButton start;
	JButton update;
	JButton reset;
	JButton play;

	MoveLogger logger;

	//Debug controsl
	JButton minrisk;

	JLabel numStates;
	int values[][];
	double accumulate[][];
	int tempValues[][];
	int nextValue[][];
	int columns[];
	int vcolumns[];
	int rows[];
	int vrows[];
	boolean place[];
	LinkedList<int[][]> states;

	LinkedList<String> stats;

	String globalstrat;
	int simCounter;
	int totalSims;
	//String globalfilename;
	File[] files;
	Scanner in;
	boolean runningGlobal;

	//added
	In file = new In("two.txt");
	int[][] answer;
	int[][] knownBoard;
	double[][] currentVProbabilities;
	double[][] currentSProbabilities;
	double[][] currentOProbabilities;

	final static int COVERED = -1; //special value for still covered spots on the board
	static int SIZE;

	public static void main(String args[]) 
	{
		//Thank you HackerRank
		//https://www.hackerrank.com/challenges/java-stdin-and-stdout-1/problem
		
		Scanner scanner = new Scanner(System.in);
		
		/*
		System.out.print("Choose strategy");
		String myString = scanner.next();
		while(scanner.hasNext()) {
			String val = scanner.next();
			System.out.println(9 + ": " + val );
			myString += val;
			
		}
		scanner.close();
		*/
		
		System.out.println("Please enter your Strategy: (type \"GUI\" if you want to use the GUI)");
        String input = scanner.nextLine();
        System.out.println("User Input from console: " + input);
		
		localArgs = args;
        
        if(input.equals("") || input.equals("GUI")){
        	new Main("");
        }
        else {
        	new Main(input);
        }
        
		
	}
	Main(String args) 
	{
		stats = new LinkedList<String>();
		ap = new ArgsProcessor(localArgs); //new
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		states = new LinkedList<int[][]>();
		numStates=new JLabel();
		prob=new JLabel("Probabilities <Voltorb, 1, Scoring>");
		start=new JButton("Start");
		update=new JButton("Update");
		reset=new JButton("Reset");
		play=new JButton("Play");
		
		
		totalSims = 0;
		simCounter = 0;
		logger = new MoveLogger(simCounter);
		files = new File[0];

		//Jbttons for quick access
		minrisk=new JButton("minRisk -auto");


		add(start);
		add(update);
		start.setBounds(360, 60, 100, 40);
		//update.setBounds(360,120,100,40);
		//reset.setBounds(360,180,100,40);
		play.setBounds(360,120,100,40);	 //new	
		minrisk.setBounds(360, 180, 100, 40);




		values=new int[5][5];
		nextValue=new int[5][5];
		showValues=new JTextField[5][5];
		columns=new int[5];
		vcolumns=new int[5];
		rows=new int[5];
		vrows=new int[5];
		accumulate=new double[5][5];

		globalstrat = "";
		//globalfilename = "";
		//files = new File[0];
		runningGlobal = false;

		//2-D Answer Board
		answer = new int[5][5];

		//current known (flipped over) board
		knownBoard = new int[5][5];
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 5; ++j){
				knownBoard[i][j] = COVERED; //flag for unknown, will print as a blank/black space
			}
		}

		//current voltorb probabilities
		currentVProbabilities = new double[5][5];
		//current scoring probabilites
		currentSProbabilities = new double[5][5];
		//current one-tile probabilites
		currentOProbabilities = new double[5][5];

		Cols=new JTextField[5];
		Rows=new JTextField[5];
		Vcols=new JTextField[5];
		Vrows=new JTextField[5];
		place=new boolean[5];
		probabilities=new JLabel[5][5];

		for(int i=0;i<5;i++)
		{
			Cols[i]=new JTextField("");
			Rows[i]=new JTextField("");
			Vcols[i]=new JTextField("");
			Vrows[i]=new JTextField("");
			place[i]=false;
			add(Cols[i]);
			add(Rows[i]);
			add(Vcols[i]);
			add(Vrows[i]);
			Cols[i].setBounds((i*40)+30, 160, 20, 20);
			Vcols[i].setBounds((i*40)+50, 160, 20, 20);
			Rows[i].setBounds(230, (i*20)+60, 20, 20);
			Vrows[i].setBounds(250, (i*20)+60, 20, 20);
			columns[i]=-1;
			vcolumns[i]=-1;
			rows[i]=-1;
			vrows[i]=-1;
			//Setup for loop to initalize 2-D arrays inside existing for loop. Lane, you could possilby use this for loop to add scanned characters in
			for(int j=0;j<5;j++)
			{
				accumulate[i][j]=0;
				nextValue[i][j]=0;
				values[i][j]=0;
				showValues[i][j]=new JTextField();
				add(showValues[i][j]);
				showValues[i][j].setBounds((j*40)+30,(i*20)+60, 40, 20);
				probabilities[i][j]=new JLabel("");
				add(probabilities[i][j]);
				probabilities[i][j].setBounds(120*j,230+34*i,120,32);
				showValues[i][j].setText("");			
			}
		}
		add(prob);
		prob.setBounds(10,200,400,30);
		add(numStates);
		numStates.setBounds(10,175,400,30);
		/*
		setTitle("Voltorb Flip");
		setLayout(null);
		setSize(600, 450);
		add(reset);
		add(play);
		start.addActionListener(new actions());
		update.addActionListener(new actions());
		reset.addActionListener(new actions());
		play.addActionListener(new actions());
		setVisible(true)*/
		/*
		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				if (currentVProbabilities[i][j] == 0 && knownBoard[i][j] == COVERED) {
					xcoord = i;
					ycoord = j;
					break; //take out if changing to lowrisk instead of no risk
				}
			}
		}
		 */

		Algorithm a = null;

		String strategy;
		
		do {
		if(globalstrat.equals("")) {
			//Use GUI if no command line input
			if( args == "" ) {
			strategy = ap.nextString("What strategy would you like to implement? (Note, type -auto afterwards to auto run a board");
			}
			else {
				strategy = args;
			}
			String slice = globalconfig(strategy); 

			System.out.println(slice);

			a = choosestrategy(strategy);
			
			
		}
		
		//Global Execution
			play(a);
			reset();
		
		
		System.out.print(simCounter + " = " + totalSims);
		
		} while(simCounter < totalSims );
		
		csvstats(stats);

		//auto starts - no need to press start
		//play();

		


	}

	public String globalconfig(String strategy) {
		// TODO Auto-generated method stub
		
		Algorithm a;
		
		if(strategy.contains("-global")) {

			String tempstrat = strategy;
			//Stash the strategy as the global strategy and remove flag

			String slice = tempstrat.substring(tempstrat.indexOf("-global")+7);
			totalSims = Integer.parseInt(slice);

			globalstrat = tempstrat.replace("-global", "");

			runningGlobal = true;
			
			return slice;


		}
		else {
			return null;
		}
		
	}
	public class actions implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource()==start)
			{
				//start the stuff
				//play();
			}
			if(e.getSource() == play){
				//play();
			}
			if(e.getSource()==update)
			{
				update();
			}
			//if(e.getSource()==reset)
			//	reset();
		}


	}
	/**
	Play: Will play your move on the board
	 **/
	/**
	 * 
	 */
	public void play(Algorithm a) {
		//1. Input. For the time being this is just a board coordinate to play at. Using input for now, but an algorithm auto-input
		//is doable in the future
		// This method could also be abstract if we ever decided to refactor this to OOP due to multiple methods of input.

		//int xcoord = ap.nextInt("x?");
		//int ycoord = ap.nextInt("y?");


		/* Code in progress for autoplay
		 * */
		int xcoord = -1;
		int ycoord = -1;
		//double lowProb = 1.0;


		//simCounter++;
		String strategy = globalstrat;

		//Algorithm a;

		//setNumbers();
		//startCalculating();	


		choosestrategy(strategy);

		

		String loggerAlgo = strategy.replace("-auto", "");


		logger.setAlgorithm(loggerAlgo);

		setNumbers();
		startCalculating();	

		//Run auto if desired 
		//Loop until a game over/tie

		if(strategy.contains("-auto")) {


			//keep running until you can
			while(true) {


				int[] solution = a.choosetile(currentVProbabilities, currentOProbabilities, currentSProbabilities, knownBoard);

				if(solution != null) {
					xcoord = solution[0];
					ycoord = solution[1];

					//If a flip isn't successful, stop
					//Note: A switch statement could be put here if you want to do stuff like log files or do something depending on flip outcomes
					int tilevalue = flip(xcoord, ycoord);

					if(tilevalue != 0) {

						return;
					}
					/*
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 */
				}
				else {
					//Cannot find a safe solution - withdraw
					cleanup(false, false, true);
					//fix returning later
					return;
				}


			}


		}

		//If none of those flags are present, run normal
		else {

			int[] solution = a.choosetile(currentVProbabilities, currentOProbabilities, currentSProbabilities, knownBoard);

			if(solution != null) {
				xcoord = solution[0];
				ycoord = solution[1];
				//If a flip isn't successfull, stop
				//Note: A switch statement could be put here if you wan't to do stuff like log files or do something depending on flip outcomes
				if(flip(xcoord, ycoord) != 0) {
					return;
				}
			}
			else {
				//NoRisk cannot find a safe solution
				cleanup(false, false, true);
				//fix returning later
				return;

			}


		}


		return;
	}

	private void csvstats(LinkedList<String> list) {

		String s = "Algorithm, Turns, Exit Status, Number of Ones, Number of Twos, Number of Threes\n";
		for (String d : list) {
			s += d + "\n";
		}

		FileWriter myWriter;
		try {
			myWriter = new FileWriter("src/stats.csv");
			System.out.print(s);
			myWriter.write(s);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("File io error");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	/**
	 * Interprets command string to pick strategy
	 * @param strategy The command string input
	 */
	private Algorithm choosestrategy(String strategy) {
		//  Auto-generated method stub

		int xcoord = -1;
		int ycoord = -1;

		Algorithm a;


		//Parse out which method is desired
		if( strategy.contains("minrisk") ) {
			a = new MinRisk();
		}
		else if(strategy.contains("norisk")) {
			a = new NoRisk();
		}
		else if(strategy.contains("scorefirst")) {
			a = new ScoreFirst();
		}
		else if(strategy.contains("bogoflip")) {
			a = new Bogoflip();
		}
		else if(strategy.contains("cutoffn")) {
			int turncount = ap.nextInt("Switch from lowest Voltorb to highest scoring after how many turns?");
			a = new CutoffN(turncount);
		}
		else {
			//default is norisk for now
			a = new NoRisk();
		}

		return a;


	}
	/**
	 * @param xcoord - X-coordinate to flip
	 * @param ycoord - Y-coordinate to flip
	 * @return integer value based on outcome status 0 if you should keep playing, 1 you've won, 2 you've triggered a game over
	 */
	public int flip(int xcoord, int ycoord) {

		//Flipped over

		System.out.println("answer="+answer[xcoord][ycoord]);
		boardify(answer[xcoord][ycoord], xcoord, ycoord);

		update();

		//check if the board is won/lost
		boolean alldone = true;
		boolean gameover = false;
		for(int i=0;i<knownBoard.length;i++) {
			for(int j=0;j<knownBoard.length;j++) {

				if(knownBoard[i][j] == 0) {
					gameover = true;
					alldone = false;
					break;
				}

				//should it be flipped over?

				if( (answer[i][j] == 2 ||  answer[i][j] == 3) && (answer[i][j] != knownBoard[i][j]) ) {
					alldone = false;
					//break; //the dirty sneaky culprit
				}


			}
		}
		//Game over is #1 Priority - check first
		if(gameover) {
			cleanup(false, false, false);
			logger.setExit(2);
			return 2;
		}
		else if(alldone) {
			cleanup(true, false, false);
			logger.setExit(1);
			return 1;
		}
		else {
			prettyprint();
			Integer flippedTile = answer[xcoord][ycoord];
			logger.log(flippedTile, getBoardState());
			return 0;
		}

		//3. Cleanup. Update the coin coint (TBA), exit the program if gameover, and finally update the text elements.

	}


	/**
	 * Gracefully exits a game at its close. Also will handle any sort of logging/autoplay for large simulations
	 * @param win - is true if the gameboard is cleaned up because of a win, false if it is because of a loss
	 */
	public int cleanup(boolean win, boolean loop, boolean withdraw) {

		if(withdraw) {
			System.out.println("Could not decide on another move, so chose to withdraw");
			//System.exit(2);

			return 2;
			// 2 denotes withdraw status
		}

		if(win){
			prettyprint();
			System.out.println("        d8b        888                           \n" + 
					"        Y8P        888                           \n" + 
					"                   888                           \n" + 
					"888  888888 .d8888b888888 .d88b. 888d888888  888 \n" + 
					"888  888888d88P\"   888   d88\"\"88b888P\"  888  888 \n" + 
					"Y88  88P888888     888   888  888888    888  888 \n" + 
					" Y8bd8P 888Y88b.   Y88b. Y88..88P888    Y88b 888 \n" + 
					"  Y88P  888 \"Y8888P \"Y888 \"Y88P\" 888     \"Y88888 \n" + 
					"                                             888 \n" + 
					"                                        Y8b d88P \n" + 
					"                                         \"Y88P\"");
			//System.exit(0);
			return 0;
		}
		else {
			prettyprint();
			System.out.println("┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"███▀▀▀██┼███▀▀▀███┼███▀█▄█▀███┼██▀▀▀\n" + 
					"██┼┼┼┼██┼██┼┼┼┼┼██┼██┼┼┼█┼┼┼██┼██┼┼┼\n" + 
					"██┼┼┼▄▄▄┼██▄▄▄▄▄██┼██┼┼┼▀┼┼┼██┼██▀▀▀\n" + 
					"██┼┼┼┼██┼██┼┼┼┼┼██┼██┼┼┼┼┼┼┼██┼██┼┼┼\n" + 
					"███▄▄▄██┼██┼┼┼┼┼██┼██┼┼┼┼┼┼┼██┼██▄▄▄\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"███▀▀▀███┼▀███┼┼██▀┼██▀▀▀┼██▀▀▀▀██▄┼\n" + 
					"██┼┼┼┼┼██┼┼┼██┼┼██┼┼██┼┼┼┼██┼┼┼┼┼██┼\n" + 
					"██┼┼┼┼┼██┼┼┼██┼┼██┼┼██▀▀▀┼██▄▄▄▄▄▀▀┼\n" + 
					"██┼┼┼┼┼██┼┼┼██┼┼█▀┼┼██┼┼┼┼██┼┼┼┼┼██┼\n" + 
					"███▄▄▄███┼┼┼─▀█▀┼┼─┼██▄▄▄┼██┼┼┼┼┼██▄\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼██┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼██┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼████▄┼┼┼▄▄▄▄▄▄▄┼┼┼▄████┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼▀▀█▄█████████▄█▀▀┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼█████████████┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼██▀▀▀███▀▀▀██┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼██┼┼┼███┼┼┼██┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼█████▀▄▀█████┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼┼███████████┼┼┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼▄▄▄██┼┼█▀█▀█┼┼██▄▄▄┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼▀▀██┼┼┼┼┼┼┼┼┼┼┼██▀▀┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼▀▀┼┼┼┼┼┼┼┼┼┼┼▀▀┼┼┼┼┼┼┼┼┼┼┼\n" + 
					"┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼┼");

			//exit of 1 because game over
			//System.exit(1);
			return 1;


		}

	}


	/**
	 * boardify manages updating over the board with its given value and location
	 * @param boardResult - The result of the flipped over tile. Either a -1, 0, 1, 2, or 3
	 * @param x - The x-coordinate where the flip happens
	 * @param y
	 * @author evinjaff
	 * 
	 */
	public void boardify(int boardResult, int x,  int y){


		switch(boardResult){
		//Flipped a Voltorb - Trigger Game Over
		case 0:
			knownBoard[x][y] = 0;
			values[x][y] = 0;
			prettyprint();
			System.out.println("BOMB!");
			break;

			//Flipped a 1 - Do nothing but just update board
		case 1:
			knownBoard[x][y] = 1;
			values[x][y] = 1;
			System.out.println("1 Flipped");
			break;


			//Flipped a 2 - Update Board and Coin Count
		case 2:
			knownBoard[x][y] = 2;
			values[x][y] = 2;
			System.out.println("2 Flipped");

			break;

			//Flipped a 3 - Update Board and Coin Count
		case 3:
			knownBoard[x][y] = 3;
			values[x][y] = 3;
			System.out.println("3 Flipped!");

			break;
		}


	}

	//Pretty-print the state of the board
	/**
	 * 
	 */
	public String prettyprint(){

		boolean withProb = true; //also prints probabilities

		String state = "";
		System.out.print("Board State:");
		state = state.concat("Board State:");
		if (withProb) {
			System.out.print("\t\t\tProbability of Voltorb");
			state = state.concat("\t\t\tProbability of Voltorb");
		}
		System.out.println();
		System.out.println("   01234\n");
		state = state.concat("\n   01234\n");
		//System.out.println("   _____");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(i + " |");
			state = state.concat(i + " |");
			for (int j = 0; j < SIZE; ++j) {
				char val = (knownBoard[i][j] == COVERED ? '▓': Character.forDigit(knownBoard[i][j],10)); 
				state = state.concat(Character.toString(val));
				System.out.print(val);
			}
			System.out.print(" " + rows[i] + " " + vrows[i]);
			state = state.concat(" " + rows[i] + " " + vrows[i]);
			if (withProb) {
				System.out.print("\t\t\t" + currentVProbabilities[i][0] + "\t" + currentVProbabilities[i][1] + "\t" + currentVProbabilities[i][2]+ "\t" + currentVProbabilities[i][3] + "\t" + currentVProbabilities[i][4]);
				state = state.concat("\t\t\t" + currentVProbabilities[i][0] + "\t" + currentVProbabilities[i][1] + "\t" + currentVProbabilities[i][2]+ "\t" + currentVProbabilities[i][3] + "\t" + currentVProbabilities[i][4]);
				//System.out.println("\nScoring Prob:");
				//System.out.print("\t\t\t" + currentSProbabilities[i][0] + "\t" + currentSProbabilities[i][1] + "\t" + currentSProbabilities[i][2]+ "\t" + currentSProbabilities[i][3] + "\t" + currentSProbabilities[i][4]);
			}
			state = state.concat("\n");
			System.out.println();
		}

		System.out.print("   ");
		state = state.concat("   ");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(columns[i]);
			state = state.concat(Integer.toString(columns[i]));
		}
		System.out.print("\n   ");
		state = state.concat("\n   ");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(vcolumns[i]);
			state = state.concat(Integer.toString(vcolumns[i]));
		}
		System.out.println();
		state = state.concat("\n");

		return state;

	}

	public String getBoardState() {
		String state = "";
		//state = state.concat("Board State:\n");
		//state = state.concat("   01234\n");
		for (int i = 0; i < SIZE; ++i) {
			//state = state.concat(i + " |");
			for (int j = 0; j < SIZE; ++j) {
				char val = (knownBoard[i][j] == COVERED ? ' ': Character.forDigit(knownBoard[i][j],10)); 
				state = state.concat(Character.toString(val));
			}
			//state = state.concat(" " + rows[i] + " " + vrows[i]);
			//state = state.concat("\n");
		}

		//state = state.concat("   ");
		for (int i = 0; i < SIZE; ++i) {
			//state = state.concat(Integer.toString(columns[i]));
		}
		//state = state.concat("\n   ");
		for (int i = 0; i < SIZE; ++i) {
			//state = state.concat(Integer.toString(vcolumns[i]));
		}
		//state = state.concat("\n");

		return state;
	}

	/**
	 * 
	 */
	public void update()
	{
		int numberOfFinalStates=states.size();
		LinkedList<int[][]> newStates;
		newStates=new LinkedList<int[][]>();
		boolean add=false;
		int newSize=0;
		int tempInt;
		double accumulated[][][];
		accumulated=new double[3][SIZE][SIZE];
		for(int i=0;i<3;i++)
			for(int j=0;j<SIZE;j++)
				for(int k=0;k<SIZE;k++)
					accumulated[i][j][k]=0;
		for(int k=0;k<numberOfFinalStates;k++)
		{
			add=true;
			values=states.get(k);
			for(int i=0;i<SIZE;i++)
			{
				for(int j=0;j<SIZE;j++)
				{
					//old
					//if(!showValues[i][j].getText().equals(""))
					//using knownBoard now
					if (!(knownBoard[i][j] == COVERED))
					{
						//old
						//tempInt=Integer.parseInt(showValues[i][j].getText());

						//updated to take in from known board
						tempInt=knownBoard[i][j];
						if(values[i][j]!=tempInt)
							add=false;
					}
				}
			}
			if(add)
			{
				newStates.add(values);
				newSize++;
			}
		}


		for(int k=0;k<newSize;k++)
		{
			values=newStates.get(k);
			for(int i=0;i<SIZE;i++)
			{
				for(int j=0;j<SIZE;j++)
				{
					if(values[i][j]==0)
						accumulated[0][i][j]++;
					if(values[i][j]==1)
						accumulated[1][i][j]++;
					if(values[i][j]==2||values[i][j]==3)
						accumulated[2][i][j]++;
				}
			}
		}
		double alpha;
		double maxscore=0;
		double maxvoltorb=0;
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				alpha=(accumulated[0][i][j]+accumulated[1][i][j]+accumulated[2][i][j]);
				accumulated[0][i][j]=Math.round(accumulated[0][i][j]/alpha*100)/100.0;
				accumulated[1][i][j]=Math.round(accumulated[1][i][j]/alpha*100)/100.0;
				accumulated[2][i][j]=Math.round(accumulated[2][i][j]/alpha*100)/100.0;
				if(accumulated[0][i][j]>maxvoltorb&&showValues[i][j].getText().equals(""))
					maxvoltorb=accumulated[0][i][j];
				if(accumulated[2][i][j]>maxscore&&showValues[i][j].getText().equals(""))
					maxscore=accumulated[2][i][j];
				//probabilities[i][j].setText("<"+Math.round(accumulated[0][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[1][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[2][i][j]/alpha*100)/100.0+">\t");
			}
		}
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				if(accumulated[0][i][j]==maxvoltorb&&showValues[i][j].getText().equals(""))
					probabilities[i][j].setForeground(Color.red);
				else if(accumulated[2][i][j]==maxscore&&showValues[i][j].getText().equals(""))
					probabilities[i][j].setForeground(Color.blue);
				else
					probabilities[i][j].setForeground(Color.black);
				probabilities[i][j].setText("<"+accumulated[0][i][j]+" , "+accumulated[1][i][j]+" , "+accumulated[2][i][j]+">\t");
				currentVProbabilities[i][j] = accumulated[0][i][j];
				currentOProbabilities[i][j] = accumulated[1][i][j];
				currentSProbabilities[i][j] = accumulated[2][i][j];
			}
		}
		numStates.setText("Number of Possible Games: "+newSize);
	}
	/**
	 * 
	 */
	public void reset()
	{
		logger.consoleprint();
		logger.createCSV();
		logger.createTXT();

		stats.add(logger.csvSummaryPrint());

		//Make new logger object
		logger = new MoveLogger(simCounter++);
		states.clear();
		for(int i=0;i<SIZE;i++)
		{
			Cols[i].setText("");
			Rows[i].setText("");
			Vcols[i].setText("");
			Vrows[i].setText("");
			place[i]=false;
			columns[i]=-1;
			vcolumns[i]=-1;
			rows[i]=-1;
			vrows[i]=-1;
			for(int j=0;j<SIZE;j++)
			{
				accumulate[i][j]=0;
				nextValue[i][j]=0;
				values[i][j]=0;
				showValues[i][j].setText("");	
				probabilities[i][j].setForeground(Color.black);
				probabilities[i][j].setText("");

				//added
				knownBoard[i][j] = COVERED; //flag for unknown, will print as a blank/black space
				answer[i][j] = 0;
				currentVProbabilities[i][j] = 0;
				currentSProbabilities[i][j] = 0;
				currentOProbabilities[i][j] = 0;

			}
			numStates.setText("");

		}

		/*
		//start next game
		play();
		 */
		//Recursion bad
		return;
	}
	/**
	 *  
	 * 
	 */
	public void setNumbers()
	{

		/* ArgsProcessor file opening functionality is taken from Prof Cosgrove's 131 changes. He's the best
		 * Modified by Lane Bohrer for use here
		 * */

		if(runningGlobal) {

			//thank you Internet very cool https://www.geeksforgeeks.org/file-listfiles-method-in-java-with-examples/

			File parentFile = new File("support_src/boards/resources"); 
			

			FileFilter filter = new FileFilter() { 

				public boolean accept(File parentFile) 
				{ 
					//return true;

					//for batch #, could do something like
					return parentFile.getName().contains("-"+(totalSims)+"-");

				} 
			}; 
			//System.out.print("-"+(totalSims)+"-");
			// System.out.println(parentFile.listFiles(filter));
			//File[] newfiles = parentFile.listFiles(filter);


			files = parentFile.listFiles(filter); 
			
			System.out.print("files" + files);
			
			totalSims = files.length;

			Arrays.parallelSort(files);

			//for (File f : files) {
			//	System.out.println(f);
			//}



			try {
				in = new Scanner(files[0]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("oopsies");
			}

			runningGlobal = false;

		} else {
			try {
				in = new Scanner(files[simCounter]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("oopsies");
			}

		}

		/*NEW text file setup:
		 * Size(int, currently hardcoded to 5)

		 * True board answers (25 ints, top left to bottom right, across then down)
		 * */


		SIZE = in.nextInt(); //not used yet but will let us do nxn


		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				answer[i][j] = in.nextInt();
			}
		}

		//calculate point and voltorb counts, going horizontal first
		for (int i = 0; i < SIZE; ++i) {
			int countPoints = 0;
			int countVoltorbs = 0;
			for (int j = 0; j < SIZE; ++j) {
				if (answer[i][j] == 0) {
					countVoltorbs++;
				}
				else {
					countPoints += answer[i][j];
				}
			}
			rows[i] = countPoints;
			vrows[i] = countVoltorbs;
		}

		//sums over columns (going up/down)
		for (int i = 0; i < SIZE; ++i) {
			int countPoints = 0;
			int countVoltorbs = 0;
			for (int j = 0; j < SIZE; ++j) {
				if (answer[j][i] == 0) {
					countVoltorbs++;
				}
				else {
					countPoints += answer[j][i];
				}
			}
			columns[i] = countPoints;
			vcolumns[i] = countVoltorbs;
		}

	}

	//never called?
	/**
	 * 
	 */
	public void displayNumbers()
	{
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				//old
				showValues[i][j].setText(Integer.toString(values[i][j]));
				//showValues[i][j].setText(Integer.toString(knownBoard[i][j]));
			}
		}
	}
	/**
	 * 
	 */
	public void startCalculating()
	{
		int count=0;
		int numCombinations[];
		numCombinations=new int[SIZE];
		for(int i=0;i<SIZE;i++)
		{

			numCombinations[i] = ncr(SIZE, vrows[i]);

			//genericized to an ncr call

			/*
			if(vrows[i]==0)
				numCombinations[i]=1;
			else if(vrows[i]==1)
				numCombinations[i]=5;
			else if(vrows[i]==2)
				numCombinations[i]=10;
			else if(vrows[i]==3)
				numCombinations[i]=10;
			else if(vrows[i]==4)
				numCombinations[i]=5;
			else if(vrows[i]==5)
				numCombinations[i]=1;
			 */

		}
		//determine possible placements of voltorbs////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////

		//TODO
		for(int first=0;first<numCombinations[0];first++)
		{
			createPlacement(vrows[0],first,place);
			for(int i=0;i<SIZE;i++)
			{
				if(place[i]==true)
					values[0][i]=0;
				else
					values[0][i]=-1;
			}
			for(int second=0;second<numCombinations[1];second++)
			{
				createPlacement(vrows[1],second,place);
				for(int i=0;i<SIZE;i++)
				{
					if(place[i]==true)
						values[1][i]=0;
					else
						values[1][i]=-1;
				}
				for(int third=0;third<numCombinations[2];third++)
				{
					createPlacement(vrows[2],third,place);
					for(int i=0;i<SIZE;i++)
					{
						if(place[i]==true)
							values[2][i]=0;
						else
							values[2][i]=-1;
					}
					for(int fourth=0;fourth<numCombinations[3];fourth++)
					{
						createPlacement(vrows[3],fourth,place);
						for(int i=0;i<SIZE;i++)
						{
							if(place[i]==true)
								values[3][i]=0;
							else
								values[3][i]=-1;
						}
						for(int fifth=0;fifth<numCombinations[4];fifth++)
						{
							createPlacement(vrows[4],fifth,place);
							for(int i=0;i<5;i++)
							{
								if(place[i]==true)
									values[4][i]=0;
								else
									values[4][i]=-1;
							}
							//code for checking
							if(testVCols(values))
							{
								tempValues=new int[5][5];
								incrementValues(values);
								copyMatrix(values,tempValues);
								states.add(tempValues);
								count++;
							}
						}
					}
				}
			}
		}
		///end of finding voltorbs////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		System.out.println("Number of possible states before adding cards: "+count);
		System.out.println("Probabilities of Voltorbs:");
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				System.out.print(	Math.round((accumulate[i][j]/(count*1.0))*10000)/10000.0	+"\t");
				accumulate[i][j]=0;
			}
			System.out.println();
		}
		//start of finding all possible assignments///////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		int numberOfStates=0;

		int[] max = new int[SIZE];
		int[] min = new int[SIZE];
		int[] outertemps = new int[SIZE];
		int iterator = 0;

		for (int i = 0; i < SIZE; ++i){
			min[i] = (rows[i]+vrows[i]-SIZE-1)/2;
			max[i] = (rows[i]+vrows[i]-SIZE);
			if(max[i]>(SIZE-vrows[i])){
				max[i]=SIZE-vrows[i];
			}
		}

		// Replaced by for loop above
		int min0,max0,min1,max1,min2,max2,min3,max3,min4,max4;
		min0=(rows[0]+vrows[0]-4)/2;
		min1=(rows[1]+vrows[1]-4)/2;
		min2=(rows[2]+vrows[2]-4)/2;
		min3=(rows[3]+vrows[3]-4)/2;
		min4=(rows[4]+vrows[4]-4)/2;
		max0=(rows[0]+vrows[0]-5);
		if(max0>(5-vrows[0]))
			max0=5-vrows[0];
		max1=(rows[1]+vrows[1]-5);
		if(max1>(5-vrows[1]))
			max1=5-vrows[1];
		max2=(rows[2]+vrows[2]-5);
		if(max2>(5-vrows[2]))
			max2=5-vrows[2];
		max3=(rows[3]+vrows[3]-5);
		if(max3>(5-vrows[3]))
			max3=5-vrows[3];
		max4=(rows[4]+vrows[4]-5);
		if(max4>(5-vrows[4]))
			max4=5-vrows[4];



		//new code

		/*while(count>0){
			values=states.pop();
			copyMatrix(values,nextValue);
			numberOfStates = findPossibleAssignments(max,min,outertemps,iterator, numberOfStates);
			count--;
		}*/

		/*old code*/
		int counter;
		while(count>0){
			values=states.pop();
			copyMatrix(values,nextValue);
			for(int firstouter=min0;firstouter<=max0;firstouter++)
			{
				for(int first=0;first<getNumCombinations(firstouter,vrows[0]);first++)
				{
					generate(firstouter,vrows[0],first,place);
					counter=0;
					for(int i=0;i<5;i++)
						if(nextValue[0][i]!=0)
						{
							if(place[counter])
								nextValue[0][i]=5;
							else
								nextValue[0][i]=-1;
							counter++;
						}
					for(int secondouter=min1;secondouter<=max1;secondouter++)
					{
						for(int second=0;second<getNumCombinations(secondouter,vrows[1]);second++)
						{
							generate(secondouter,vrows[1],second,place);
							counter=0;
							for(int i=0;i<5;i++)
								if(nextValue[1][i]!=0)
								{
									if(place[counter])
										nextValue[1][i]=5;
									else
										nextValue[1][i]=-1;
									counter++;
								}
							for(int thirdouter=min2;thirdouter<=max2;thirdouter++)
							{
								for(int third=0;third<getNumCombinations(thirdouter,vrows[2]);third++)
								{
									generate(thirdouter,vrows[2],third,place);
									counter=0;
									for(int i=0;i<5;i++)
										if(nextValue[2][i]!=0)
										{
											if(place[counter])
												nextValue[2][i]=5;
											else
												nextValue[2][i]=-1;
											counter++;
										}
									for(int fourthouter=min3;fourthouter<=max3;fourthouter++)
									{
										for(int fourth=0;fourth<getNumCombinations(fourthouter,vrows[3]);fourth++)
										{
											generate(fourthouter,vrows[3],fourth,place);
											counter=0;
											for(int i=0;i<5;i++)
												if(nextValue[3][i]!=0)
												{
													if(place[counter])
														nextValue[3][i]=5;
													else
														nextValue[3][i]=-1;
													counter++;
												}
											for(int fifthouter=min4;fifthouter<=max4;fifthouter++)
											{
												for(int fifth=0;fifth<getNumCombinations(fifthouter,vrows[4]);fifth++)
												{
													generate(fifthouter,vrows[4],fifth,place);
													counter=0;
													for(int i=0;i<5;i++)
														if(nextValue[4][i]!=0)
														{
															if(place[counter])
																nextValue[4][i]=5;
															else
																nextValue[4][i]=-1;
															counter++;
														}
													if(testCols(nextValue))
													{
														numberOfStates++;
														tempValues=new int[5][5];
														copyMatrix(nextValue,tempValues);
														incrementValues2(nextValue);
														states.add(tempValues);
													}
												}
											}	
										}
									}									
								}
							}	
						}
					}							
				}
			}
			count--;
		}


		System.out.println("New number of states  "+numberOfStates);
		System.out.println("Probability of Scoring:");
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				System.out.print(	Math.round(accumulate[i][j]/(numberOfStates*1.0)*10000)/10000.0	+"\t");
			}
			System.out.println();
		}

		//end of finding all possible assignments for greater than 1 slots/////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//////now to actually compute real states
		////////////////////////////////////////////////////////////////////////////////////////////////
		int numberOfFinalStates=0;
		int num0;
		int num1;
		int num2;
		int num3;
		int num4;
		int numCom[];

		numCom=new int[5];
		for(int i=0;i<5;i++)
			numCom[i]=0;
		while(numberOfStates>0)
		{
			for(int i=0;i<5;i++)
				numCom[i]=0;
			values=states.pop();
			changeToValues(values,numCom);
			num0=getNumberOnOff(numCom[0]);
			num1=getNumberOnOff(numCom[1]);
			num2=getNumberOnOff(numCom[2]);
			num3=getNumberOnOff(numCom[3]);
			num4=getNumberOnOff(numCom[4]);
			for(int first=0;first<num0;first++)
			{
				getBinaryPlacement(num0,first,place);
				//make placement
				count=0;
				for(int i=0;i<5;i++)
				{
					if(values[0][i]==2||values[0][i]==3)
					{
						if(place[count]==true)
						{
							values[0][i]=3;
						}
						else
							values[0][i]=2;
						count++;
					}
				}
				if(!isValid(values[0],0))
				{
					if(outOfBounds(values[0],0))
						break;
					else
						continue;
				}
				//System.out.println("After Row 1");
				for(int second=0;second<num1;second++)
				{
					getBinaryPlacement(num1,second,place);
					//make placement
					count=0;
					for(int i=0;i<5;i++)
					{
						if(values[1][i]==2||values[1][i]==3)
						{
							if(place[count]==true)
								values[1][i]=3;
							else
								values[1][i]=2;
							count++;
						}
					}
					if(!isValid(values[1],1))
					{
						if(outOfBounds(values[1],1))
							break;
						else
							continue;
					}
					//System.out.println("After Row 2");
					for(int third=0;third<num2;third++)
					{
						getBinaryPlacement(num2,third,place);
						//make placement
						count=0;
						for(int i=0;i<5;i++)
						{
							if(values[2][i]==2||values[2][i]==3)
							{
								if(place[count]==true)
									values[2][i]=3;
								else
									values[2][i]=2;
								count++;
							}
						}
						if(!isValid(values[2],2))
						{
							if(outOfBounds(values[2],2))
								break;
							else
								continue;
						}
						//System.out.println("After Row 3");
						for(int fourth=0;fourth<num3;fourth++)
						{
							getBinaryPlacement(num3,fourth,place);
							//make placement
							count=0;
							for(int i=0;i<5;i++)
							{
								if(values[3][i]==2||values[3][i]==3)
								{
									if(place[count]==true)
										values[3][i]=3;
									else
										values[3][i]=2;
									count++;
								}
							}
							if(!isValid(values[3],3))
							{
								if(outOfBounds(values[3],3))
									break;
								else
									continue;
							}
							//System.out.println("After Row 4");
							for(int fifth=0;fifth<num4;fifth++)
							{
								getBinaryPlacement(num4,fifth,place);
								//make placement
								count=0;
								for(int i=0;i<5;i++)
								{
									if(values[4][i]==2||values[4][i]==3)
									{
										if(place[count]==true)
											values[4][i]=3;
										else
											values[4][i]=2;
										count++;
									}
								}
								if(!isValid(values[4],4))
								{
									if(outOfBounds(values[4],4))
										break;
									else
										continue;
								}
								//System.out.println("After Row 5");
								if(testConstraints(values))
								{
									tempValues=new int[5][5];
									copyMatrix(values,tempValues);
									states.add(tempValues);
									numberOfFinalStates++;
								}
							}
						}
					}
				}
			}

			numberOfStates--;
		}


		System.out.println("Final States "+numberOfFinalStates);

		//end of computations////now have a set of possible real states////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		//calculate the probabilities
		double accumulated[][][];
		accumulated=new double[3][5][5];
		for(int i=0;i<3;i++)
			for(int j=0;j<5;j++)
				for(int k=0;k<5;k++)
					accumulated[i][j][k]=0;

		for(int k=0;k<numberOfFinalStates;k++)
		{
			values=states.get(k);
			for(int i=0;i<5;i++)
			{
				for(int j=0;j<5;j++)
				{
					if(values[i][j]==0)
						accumulated[0][i][j]++;
					if(values[i][j]==1)
						accumulated[1][i][j]++;
					if(values[i][j]==2||values[i][j]==3)
						accumulated[2][i][j]++;
				}
			}
		}
		double alpha;
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				alpha=(accumulated[0][i][j]+accumulated[1][i][j]+accumulated[2][i][j]);
				probabilities[i][j].setText("<"/*+Math.round(accumulated[0][i][j]/alpha*100)/100.0*/+" , "+Math.round(accumulated[1][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[2][i][j]/alpha*100)/100.0+">\t");
				//Update data-feeding arrays
				currentVProbabilities[i][j] = Math.round(accumulated[0][i][j]/alpha*100)/100.0;
				currentOProbabilities[i][j] = Math.round(accumulated[1][i][j]/alpha*100)/100.0;
				currentSProbabilities[i][j] = Math.round(accumulated[2][i][j]/alpha*100)/100.0;
			}
		}
		numStates.setText("Number of Possible States "+numberOfFinalStates);

		prettyprint();
		System.out.println("\n");


	}
	/**
	 * @param values
	 * @return
	 */
	public boolean debugger(int values[])
	{
		if(values[0]!=1)
			return false;
		else if(values[1]!=1)
			return false;
		else if(values[2]!=0)
			return false;
		else if(values[3]!=2)
			return false;
		else if(values[4]!=3)
			return false;
		else
			return true;

	}
	/**
	 * @param values
	 * @param num
	 */
	public void changeToValues(int values[][],int num[])
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(values[i][j]==-1)
					values[i][j]=1;
				if(values[i][j]==5)
				{
					values[i][j]=2;
					num[i]++;
				}
			}
		}
	}
	/**
	 * @param values
	 * @param num
	 * @return
	 */
	public boolean outOfBounds(int values[],int num)
	{
		int maxrows=0;
		for(int i=0;i<5;i++)
		{	
			maxrows+=values[i];
		}
		if(maxrows>rows[num])
			return true;
		return false;
	}
	/**
	 * @param values
	 * @param num
	 * @return
	 */
	public boolean isValid(int values[],int num)
	{
		int maxrows=0;
		for(int i=0;i<5;i++)
		{	
			maxrows+=values[i];
		}
		if(maxrows==rows[num])
			return true;
		return false;
	}
	/**
	 * @param values
	 * @return
	 */
	public boolean testConstraints(int values[][])
	{
		int maxcol;
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				maxcol=0;
				for(int k=0;k<5;k++)
				{
					//iterate through columns
					maxcol+=values[k][j];
				}
				if(maxcol!=columns[j])
				{
					return false;			
				}
			}			
		}
		return true;  
	}
	/**
	 * @param values
	 * @return
	 */
	public boolean testCols(int values[][])
	{
		int min0,max0,min1,max1,min2,max2,min3,max3,min4,max4;
		min0=(columns[0]+vcolumns[0]-4)/2;
		min1=(columns[1]+vcolumns[1]-4)/2;
		min2=(columns[2]+vcolumns[2]-4)/2;
		min3=(columns[3]+vcolumns[3]-4)/2;
		min4=(columns[4]+vcolumns[4]-4)/2;
		max0=(columns[0]+vcolumns[0]-5);
		if(max0>(5-vcolumns[0]))
			max0=5-vcolumns[0];
		max1=(columns[1]+vcolumns[1]-5);
		if(max1>(5-vcolumns[1]))
			max1=5-vcolumns[1];
		max2=(columns[2]+vcolumns[2]-5);
		if(max2>(5-vcolumns[2]))
			max2=5-vcolumns[2];
		max3=(columns[3]+vcolumns[3]-5);
		if(max3>(5-vcolumns[3]))
			max3=5-vcolumns[3];
		max4=(columns[4]+vcolumns[4]-5);
		if(max4>(5-vcolumns[4]))
			max4=5-vcolumns[4];
		int count;
		count=0;
		for(int j=0;j<5;j++)
		{
			if(values[j][0]==5)
				count++;
		}
		if(count<min0||count>max0)
			return false;
		count=0;
		for(int j=0;j<5;j++)
		{
			if(values[j][1]==5)
				count++;
		}
		if(count<min1||count>max1)
			return false;
		count=0;
		for(int j=0;j<5;j++)
		{
			if(values[j][2]==5)
				count++;
		}
		if(count<min2||count>max2)
			return false;
		count=0;
		for(int j=0;j<5;j++)
		{
			if(values[j][3]==5)
				count++;
		}
		if(count<min3||count>max3)
			return false;
		count=0;
		for(int j=0;j<5;j++)
		{
			if(values[j][4]==5)
				count++;
		}
		if(count<min4||count>max4)
			return false;

		return true;
	}

	/*
		int[] max = new int[SIZE];
		int[] min = new int[SIZE];

		for (int i = 0; i < SIZE; ++i){
			min[i] = (columns[i]+vcolumns[i]-SIZE-1)/2;
			max[i] = (rows[i]+vrows[i]-SIZE);
			if(max[i]>(SIZE-vcolumns[i])){
				max[i]=SIZE-vcolumns[i];
			}
		}

		for (int i = 0; i < SIZE; ++i) {
			int count=0;
			for(int j=0;j<SIZE;j++){
				if(values[j][i]==5){
					count++;
				}
			}
			if(count<min[i]||count>max[i])
				return false;
			} 


		return true;

	}*/

	/**
	 * @param values
	 */
	public void incrementValues(int values[][])
	{
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				if(values[i][j]==0)
					accumulate[i][j]++;
			}
		}
	}
	/**
	 * @param values
	 */
	public void incrementValues2(int values[][])
	{
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				if(values[i][j]==5) // flag value = 5?
					accumulate[i][j]++;
			}
		}
	}
	/**
	 * @param values
	 * @return
	 */
	public boolean testVCols(int values[][])
	{
		int count;
		for(int i=0;i<5;i++)
		{
			count=0;
			for(int j=0;j<5;j++)
			{
				if(values[j][i]==0)
					count++;
			}
			if(count!=vcolumns[i])
				return false;
		}

		return true;
	}
	/**
	 * @param numMines
	 * @param whichOne
	 * @param place
	 */
	public void createPlacement(int numMines,int whichOne,boolean place[])
	{
		if(numMines==0)
			setPlaced(false,false,false,false,false,place);
		else if(numMines==1)
		{
			switch(whichOne)
			{
			case 0:
				setPlaced(true,false,false,false,false,place);
				break;
			case 1:
				setPlaced(false,true,false,false,false,place);
				break;
			case 2:
				setPlaced(false,false,true,false,false,place);
				break;
			case 3:
				setPlaced(false,false,false,true,false,place);
				break;
			case 4:
				setPlaced(false,false,false,false,true,place);
				break;
			}
		}
		else if(numMines==2)
		{
			switch(whichOne)
			{
			case 0:
				setPlaced(true,true,false,false,false,place);//11000
				break;
			case 1:
				setPlaced(true,false,true,false,false,place);//10100
				break;
			case 2:
				setPlaced(true,false,false,true,false,place);//10010
				break;
			case 3:
				setPlaced(true,false,false,false,true,place);//10001
				break;
			case 4:
				setPlaced(false,true,true,false,false,place);//01100
				break;
			case 5:
				setPlaced(false,true,false,true,false,place);//01010
				break;
			case 6:
				setPlaced(false,true,false,false,true,place);//01001
				break;
			case 7:
				setPlaced(false,false,true,true,false,place);//00110
				break;
			case 8:
				setPlaced(false,false,false,true,true,place);//00011
				break;
			case 9:
				setPlaced(false,false,true,false,true,place);//00101
				break;
			}

		}
		else if(numMines==3)
		{			
			switch(whichOne)
			{
			case 0:
				setPlaced(false,false,true,true,true,place);//11000
				break;
			case 1:
				setPlaced(false,true,false,true,true,place);//10100
				break;
			case 2:
				setPlaced(false,true,true,false,true,place);//10010
				break;
			case 3:
				setPlaced(false,true,true,true,false,place);//10001
				break;
			case 4:
				setPlaced(true,false,false,true,true,place);//01100
				break;
			case 5:
				setPlaced(true,false,true,false,true,place);//01010
				break;
			case 6:
				setPlaced(true,false,true,true,false,place);//01001
				break;
			case 7:
				setPlaced(true,true,false,false,true,place);//00110
				break;
			case 8:
				setPlaced(true,true,true,false,false,place);//00011
				break;
			case 9:
				setPlaced(true,true,false,true,false,place);//00101
				break;
			}

		}
		else if(numMines==4)
		{
			switch(whichOne)
			{
			case 0:
				setPlaced(false,true,true,true,true,place);
				break;
			case 1:
				setPlaced(true,false,true,true,true,place);
				break;
			case 2:
				setPlaced(true,true,false,true,true,place);
				break;
			case 3:
				setPlaced(true,true,true,false,true,place);
				break;
			case 4:
				setPlaced(true,true,true,true,false,place);
				break;
			}
		}
		else if(numMines==5)
		{
			setPlaced(true,true,true,true,true,place);
		}
	}
	/**
	 * @param b0
	 * @param b1
	 * @param b2
	 * @param b3
	 * @param b4
	 * @param place
	 */
	public void setPlaced(boolean b0,boolean b1,boolean b2,boolean b3,boolean b4,boolean place[])
	{
		place[0]=b0;
		place[1]=b1;
		place[2]=b2;
		place[3]=b3;
		place[4]=b4;
	}
	/**
	 * @param in
	 * @param out
	 */
	public void copyMatrix(int in[][],int out[][])
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				out[i][j]=in[i][j];
			}
		}
	}
	/**
	 * @param num
	 * @return
	 */
	public int getNumberOnOff(int num)
	{
		return (int)Math.pow(2, num);
	}
	/**
	 * @param num
	 * @param curr
	 * @param place
	 */
	public void getBinaryPlacement(int num,int curr,boolean place[])
	{
		if(num==1)
		{
			setPlaced(false,false,false,false,false,place);
		}
		else if(num==2)
		{
			if(curr==0)
				setPlaced(false,false,false,false,false,place);
			else if(curr==1)
				setPlaced(true,false,false,false,false,place);
		}
		else if(num==4)
		{
			if(curr==0)
				setPlaced(false,false,false,false,false,place);
			else if(curr==1)
				setPlaced(true,false,false,false,false,place);
			else if(curr==2)
				setPlaced(false,true,false,false,false,place);
			else if(curr==3)
				setPlaced(true,true,false,false,false,place);
		}
		else if(num==8)
		{
			switch(curr)
			{
			case 0:
				setPlaced(false,false,false,false,false,place);
				break;
			case 1:
				setPlaced(true,false,false,false,false,place);
				break;
			case 2:
				setPlaced(false,true,false,false,false,place);
				break;
			case 3:
				setPlaced(false,false,true,false,false,place);
				break;
			case 4:
				setPlaced(true,true,false,false,false,place);
				break;
			case 5:
				setPlaced(true,false,true,false,false,place);
				break;
			case 6:
				setPlaced(false,true,true,false,false,place);
				break;
			case 7:
				setPlaced(true,true,true,false,false,place);
				break;
			}
		}
		else if(num==16)
		{
			switch(curr)
			{
			case 0:
				setPlaced(false,false,false,false,false,place);
				break;
			case 1:
				setPlaced(true,false,false,false,false,place);
				break;
			case 2:
				setPlaced(false,true,false,false,false,place);
				break;
			case 3:
				setPlaced(false,false,true,false,false,place);
				break;
			case 4:
				setPlaced(false,false,false,true,false,place);
				break;
			case 5:
				setPlaced(true,true,false,false,false,place);
				break;
			case 6:
				setPlaced(true,false,true,false,false,place);
				break;
			case 7:
				setPlaced(true,false,false,true,false,place);
				break;
			case 8:
				setPlaced(false,true,true,false,false,place);
				break;
			case 9:
				setPlaced(false,true,false,true,false,place);
				break;
			case 10:
				setPlaced(false,false,true,true,false,place);
				break;
			case 11:
				setPlaced(true,true,true,false,false,place);
				break;
			case 12:
				setPlaced(true,true,false,true,false,place);
				break;
			case 13:
				setPlaced(true,false,true,true,false,place);
				break;
			case 14:
				setPlaced(false,true,true,true,false,place);
				break;
			case 15:
				setPlaced(true,true,true,true,false,place);
				break;
			}
		}
		else if(num==32)
		{
			switch(curr)
			{
			case 0:
				setPlaced(false,false,false,false,false,place);
				break;
			case 1:
				setPlaced(true,false,false,false,false,place);
				break;
			case 2:
				setPlaced(false,true,false,false,false,place);
				break;
			case 3:
				setPlaced(false,false,true,false,false,place);
				break;
			case 4:
				setPlaced(false,false,false,true,false,place);
				break;
			case 5:
				setPlaced(false,false,false,false,true,place);
				break;
			case 6:
				setPlaced(true,true,false,false,false,place);
				break;
			case 7:
				setPlaced(true,false,true,false,false,place);
				break;
			case 8:
				setPlaced(true,false,false,true,false,place);
				break;
			case 9:
				setPlaced(true,false,false,false,true,place);
				break;
			case 10:
				setPlaced(false,true,true,false,false,place);
				break;
			case 11:
				setPlaced(false,true,false,true,false,place);
				break;
			case 12:
				setPlaced(false,true,false,false,true,place);
				break;
			case 13:
				setPlaced(false,false,true,true,false,place);
				break;
			case 14:
				setPlaced(false,false,true,false,true,place);
				break;
			case 15:
				setPlaced(false,false,false,true,true,place);
				break;
			case 16:
				setPlaced(true,true,true,false,false,place);
				break;
			case 17:
				setPlaced(true,true,false,true,false,place);
				break;
			case 18:
				setPlaced(true,true,false,false,true,place);
				break;
			case 19:
				setPlaced(true,false,true,true,false,place);
				break;
			case 20:
				setPlaced(true,false,true,false,true,place);
				break;
			case 21:
				setPlaced(true,false,false,true,true,place);
				break;
			case 22:
				setPlaced(false,true,true,true,false,place);
				break;
			case 23:
				setPlaced(false,true,true,false,true,place);
				break;
			case 24:
				setPlaced(false,true,false,true,true,place);
				break;
			case 25:
				setPlaced(false,false,true,true,true,place);
				break;
			case 26:
				setPlaced(true,true,true,true,false,place);
				break;
			case 27:
				setPlaced(true,true,true,false,true,place);
				break;
			case 28:
				setPlaced(true,true,false,true,true,place);
				break;
			case 29:
				setPlaced(true,false,true,true,true,place);
				break;
			case 30:
				setPlaced(false,true,true,true,true,place);
				break;
			case 31:
				setPlaced(true,true,true,true,true,place);
				break;
			}
		}
	}
	/**
	 * @param n
	 * @param V
	 * @param index
	 * @param place
	 */
	public void generate(int n,int V,int index,boolean place[])
	{
		if(V==0)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
			else if(n==1)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,false,false,false,false,place);
					break;
				case 1:
					setPlaced(false,true,false,false,false,place);
					break;
				case 2:
					setPlaced(false,false,true,false,false,place);
					break;
				case 3:
					setPlaced(false,false,false,true,false,place);
					break;
				case 4:
					setPlaced(false,false,false,false,true,place);
					break;
				}
			}
			else if(n==2)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,true,false,false,false,place);//11000
					break;
				case 1:
					setPlaced(true,false,true,false,false,place);//10100
					break;
				case 2:
					setPlaced(true,false,false,true,false,place);//10010
					break;
				case 3:
					setPlaced(true,false,false,false,true,place);//10001
					break;
				case 4:
					setPlaced(false,true,true,false,false,place);//01100
					break;
				case 5:
					setPlaced(false,true,false,true,false,place);//01010
					break;
				case 6:
					setPlaced(false,true,false,false,true,place);//01001
					break;
				case 7:
					setPlaced(false,false,true,true,false,place);//00110
					break;
				case 8:
					setPlaced(false,false,false,true,true,place);//00011
					break;
				case 9:
					setPlaced(false,false,true,false,true,place);//00101
					break;
				}
			}
			else if(n==3)
			{
				switch(index)
				{
				case 0:
					setPlaced(false,false,true,true,true,place);//11000
					break;
				case 1:
					setPlaced(false,true,false,true,true,place);//10100
					break;
				case 2:
					setPlaced(false,true,true,false,true,place);//10010
					break;
				case 3:
					setPlaced(false,true,true,true,false,place);//10001
					break;
				case 4:
					setPlaced(true,false,false,true,true,place);//01100
					break;
				case 5:
					setPlaced(true,false,true,false,true,place);//01010
					break;
				case 6:
					setPlaced(true,false,true,true,false,place);//01001
					break;
				case 7:
					setPlaced(true,true,false,false,true,place);//00110
					break;
				case 8:
					setPlaced(true,true,true,false,false,place);//00011
					break;
				case 9:
					setPlaced(true,true,false,true,false,place);//00101
					break;
				}
			}
			else if(n==4)
			{
				switch(index)
				{
				case 0:
					setPlaced(false,true,true,true,true,place);
					break;
				case 1:
					setPlaced(true,false,true,true,true,place);
					break;
				case 2:
					setPlaced(true,true,false,true,true,place);
					break;
				case 3:
					setPlaced(true,true,true,false,true,place);
					break;
				case 4:
					setPlaced(true,true,true,true,false,place);
					break;
				}
			}	
			else if(n==5)
			{
				setPlaced(true,true,true,true,true,place);
			}
		}
		else if(V==1)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
			else if(n==1)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,false,false,false,false,place);
					break;
				case 1:
					setPlaced(false,true,false,false,false,place);
					break;
				case 2:
					setPlaced(false,false,true,false,false,place);
					break;
				case 3:
					setPlaced(false,false,false,true,false,place);
					break;
				}
			}
			else if(n==2)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,true,false,false,false,place);
					break;
				case 1:
					setPlaced(true,false,true,false,false,place);
					break;
				case 2:
					setPlaced(true,false,false,true,false,place);
					break;
				case 3:
					setPlaced(false,true,true,false,false,place);
					break;
				case 4:
					setPlaced(false,true,false,true,false,place);
					break;
				case 5:
					setPlaced(false,false,true,true,false,place);
					break;
				}
			}
			else if(n==3)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,true,true,false,false,place);
					break;
				case 1:
					setPlaced(true,true,false,true,false,place);
					break;
				case 2:
					setPlaced(true,false,true,true,false,place);
					break;
				case 3:
					setPlaced(false,true,true,true,false,place);
					break;
				}
			}
			else if(n==4)
			{
				setPlaced(true,true,true,true,false,place);
			}			
		}
		else if(V==2)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
			else if(n==1)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,false,false,false,false,place);
					break;
				case 1:
					setPlaced(false,true,false,false,false,place);
					break;
				case 2:
					setPlaced(false,false,true,false,false,place);
					break;
				}
			}
			else if(n==2)
			{
				switch(index)
				{
				case 0:
					setPlaced(true,true,false,false,false,place);
					break;
				case 1:
					setPlaced(false,true,true,false,false,place);
					break;
				case 2:
					setPlaced(true,false,true,false,false,place);
					break;
				}
			}
			else if(n==3)
			{
				setPlaced(true,true,true,false,false,place);
			}
		}
		else if(V==3)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
			else if(n==1)
			{
				if(index==0)
				{
					setPlaced(true,false,false,false,false,place);
				}
				else if(index==1)
				{
					setPlaced(false,true,false,false,false,place);
				}
			}
			else if(n==2)
			{
				setPlaced(true,true,false,false,false,place);
			}
		}
		else if(V==4)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
			else if(n==1)
			{
				setPlaced(true,false,false,false,false,place);
			}
		}
		else if(V==5)
		{
			if(n==0)
			{
				setPlaced(false,false,false,false,false,place);
			}
		}
	}
	/**
	 * @param n
	 * @param v
	 * @return
	 */
	public int getNumCombinations(int n,int v)
	{
		if(n==0)
		{
			return 1;
		}
		else if(n==1)
		{
			if(v==0)
				return 5;
			else if(v==1)
				return 4;
			else if(v==2)
				return 3;
			else if(v==3)
				return 2;
			else if(v==4)
				return 1;
		}
		else if(n==2)
		{
			if(v==0)
				return 10;
			else if(v==1)
				return 6;
			else if(v==2)
				return 3;
			else if(v==3)
				return 1;
		}
		else if(n==3)
		{
			if(v==0)
				return 10;
			else if(v==1)
				return 4;
			else if(v==2)
				return 1;
		}
		else if(n==4)
		{
			if(v==0)
				return 5;
			else if(v==1)
				return 1;
		}
		else if(n==5)
		{
			return 1;
		}
		return -1;
	}

	/** Factorial method to call in ncr,
	gotten from https://www.geeksforgeeks.org/java-program-for-factorial-of-a-number/
	 */
	static int factorial(int n){ 
		if (n == 0) 
			return 1; 

		return n*factorial(n-1); 
	} 

	/** ncr calculator
	@param n integer n
	@param k integer k
	@return n choose k
	 */
	static int ncr(int n, int k){
		int num = factorial(n);
		int denom = (factorial(k))*(factorial(n-k));
		return (num/denom);
	}

	/**
	@param max array of max vals
	@param min array of min vals
	@param outertemps temporary iterators used in method
	@param iterator keeps track of level of recursion
	@param numberOfStates count of how many states have been found
	 */
	int findPossibleAssignments(int[] max, int[] min, int[] outertemps, int iterator, int numberOfStates){		

		for(outertemps[iterator]=min[iterator]; outertemps[iterator]<=max[iterator]; outertemps[iterator]++){
			for(int comboCount = 0; comboCount<getNumCombinations(outertemps[iterator],vrows[iterator]); comboCount++){
				generate(outertemps[iterator],vrows[iterator],comboCount,place);
				int counter=0;

				for(int i=0;i<SIZE;i++)
					if(nextValue[iterator][i]!=0)
					{
						if(place[counter])
							nextValue[iterator][i]=5; //5 is a magic num?
						else
							nextValue[iterator][i]=-1; //-1 as well
						counter++;
					}

				if (iterator == SIZE-1){
					if(testCols(nextValue)){
						numberOfStates++;
						tempValues=new int[5][5];
						copyMatrix(nextValue,tempValues);
						incrementValues2(nextValue);
						states.add(tempValues);
					}
					return numberOfStates;
				}else{
					//recurse 
					numberOfStates = findPossibleAssignments(max, min, outertemps, ++iterator, numberOfStates);
				}
			}

		}
		return numberOfStates;


	}

}




