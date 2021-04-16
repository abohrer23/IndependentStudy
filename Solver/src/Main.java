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


//import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.*;
import genboards.MakeBoard;
//Evin and Lane's imports
import cse131.ArgsProcessor;
import timing.Ticker;
//import boards.boardSimulationFiles;
//import java.math.BigInteger;

//TODO: bug where some probabilities are 0 and shouldn't be? see photo in DMs
public class Main extends JFrame 
{
	public Ticker ticker; //= new Ticker();
	
	//ticker.tick();
	
	long startTime;// = System.nanoTime();
	//methodToTime();
	long endTime;// = System.nanoTime();

	//long duration = (endTime - startTime); 
	
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
	int score;
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

	In file = new In("two.txt");
	int[][] answer;
	int[][] knownBoard;
	double[][] currentVProbabilities;
	double[][] currentSProbabilities;
	double[][] currentOProbabilities;

	final static int COVERED = -1; //flag value for still covered spots on the board
	int SIZE;
	int countforV;
	int possibleStatesCount;
	int numberOfFinalStates;

	ArrayList<boolean[]>[] permutations;
	
	
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
		
		Ticker ticker = new Ticker();
		
		long startTime = System.currentTimeMillis();
		//methodToTime();
		//long endTime;// = System.nanoTime();

		//long duration = (endTime - startTime);
		
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




		//values=new int[5][5];
		//nextValue=new int[5][5];
		showValues=new JTextField[5][5];
//		columns=new int[5];
//		vcolumns=new int[5];
//		rows=new int[5];
//		vrows=new int[5];
//		accumulate=new double[5][5];

		globalstrat = "";
		//globalfilename = "";
		//files = new File[0];
		runningGlobal = false;

		//2-D Answer Board
		//answer = new int[5][5];

		//current known (flipped over) board
//		knownBoard = new int[5][5];
//		for (int i = 0; i < 5; ++i) {
//			for (int j = 0; j < 5; ++j){
//				knownBoard[i][j] = COVERED; //flag for unknown, will print as a blank/covered space
//			}
//		}

//		//current voltorb probabilities
//		currentVProbabilities = new double[5][5];
//		//current scoring probabilites
//		currentSProbabilities = new double[5][5];
//		//current one-tile probabilites
//		currentOProbabilities = new double[5][5];

		Cols=new JTextField[5];
		Rows=new JTextField[5];
		Vcols=new JTextField[5];
		Vrows=new JTextField[5];
//		place=new boolean[5];
		probabilities=new JLabel[5][5];

		for(int i=0;i<SIZE;i++)
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
//			columns[i]=-1;
//			vcolumns[i]=-1;
//			rows[i]=-1;
//			vrows[i]=-1;
			//Setup for loop to initalize 2-D arrays inside existing for loop. Lane, you could possilby use this for loop to add scanned characters in
			for(int j=0;j<SIZE;j++)
			{
//				accumulate[i][j]=0;
//				nextValue[i][j]=0;
//				values[i][j]=0;
				showValues[i][j]=new JTextField();
				add(showValues[i][j]);
				showValues[i][j].setBounds((j*40)+30,(i*20)+60, 40, 20);
				probabilities[i][j]=new JLabel("");
				add(probabilities[i][j]);
				probabilities[i][j].setBounds(120*j,230+34*i,120,32);
				showValues[i][j].setText("");			
			}
			ticker.tick();
		}
		add(prob);
		prob.setBounds(10,200,400,30);
		add(numStates);
		numStates.setBounds(10,175,400,30);
		ticker.tick();
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


		/*
			* Evin's Multithreading corner
			*
			*
		*/
		// https://www.geeksforgeeks.org/multithreading-in-java/
		try {
            // Displaying the thread that is running
            System.out.println(
                "Thread " + Thread.currentThread().getId()
                + " is running");
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }

		
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
			ticker.tick();
			
			
		}
		
		ticker.tick();
		
		//Global Execution
			play(a);
			ticker.tick();
			reset();
			ticker.tick();

		/*

		int n = 4; // Number of threads because Lane and I have puny CPUs
        for (int i = 0; i < n; i++) {
            MultithreadingDemo object
                = new MultithreadingDemo();
            object.start();
        }
		
		*/
		
		
		System.out.print(simCounter + " = " + totalSims);
		ticker.tick();
		
		} while(simCounter < totalSims );
		
		csvstats(stats);
		
		//export ticks

		//auto starts - no need to press start
		//play();
		
		long endTime = System.currentTimeMillis();
		
		long duration =  endTime - startTime;
		
		FileWriter myWriter;
		
		//String s = "strategy, size, time (ns), ticks\n";
		
		String s = logger.getAlgo() + ", " + totalSims + ", " + duration + "," + ticker.getTickCount() + ","  + "\n";
		
		try {
			myWriter = new FileWriter("timing.csv", true);
			System.out.print(s);
			myWriter.write(s);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("File io error");
			e.printStackTrace();
		}

		


	}

	public String globalconfig(String strategy) {
				
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

		//simCounter++;
		String strategy = globalstrat;
		ticker.tick();

		//Algorithm a;

		choosestrategy(strategy);
		ticker.tick();

		String loggerAlgo = strategy.replace("-auto", "");
		ticker.tick();

		logger.setAlgorithm(loggerAlgo);

		setNumbers();
		startCalculating();	
		ticker.tick();

		//Run auto if desired 
		//Loop until a game over/tie

		if(strategy.contains("-auto")) {
			ticker.tick();


			//keep running until you can
			while(true) {


				int[] solution = a.choosetile(currentVProbabilities, currentOProbabilities, currentSProbabilities, knownBoard);

				if(solution != null) {
					xcoord = solution[0];
					ycoord = solution[1];
					ticker.tick();

					//If a flip isn't successful, stop
					//Note: A switch statement could be put here if you want to do stuff like log files or do something depending on flip outcomes
					int tilevalue = flip(xcoord, ycoord);
					ticker.tick();
					if(tilevalue != 0) {
						
						return;
					}

				}
				else {
					ticker.tick();
					//Cannot find a safe solution - withdraw
					cleanup(false, false, true);
					//fix returning later
					return;
				}


			}


		}

		//If none of those flags are present, run normal
		else {
			ticker.tick();
			int[] solution = a.choosetile(currentVProbabilities, currentOProbabilities, currentSProbabilities, knownBoard);
			ticker.tick();
			if(solution != null) {
				ticker.tick();
				xcoord = solution[0];
				ycoord = solution[1];
				//If a flip isn't successfull, stop
				//Note: A switch statement could be put here if you wan't to do stuff like log files or do something depending on flip outcomes
				if(flip(xcoord, ycoord) != 0) {
					ticker.tick();
					return;
				}
			}
			else {
				ticker.tick();
				//NoRisk cannot find a safe solution
				cleanup(false, false, true);
				//fix returning later
				return;

			}


		}


		return;
	}

	public void csvstats(LinkedList<String> list) {
		StringBuilder output = new StringBuilder();
		output.append("Algorithm, Turns, Exit Status, Number of Ones, Number of Twos, Number of Threes, Score\n");
		for (String d : list) {
			output.append( (d + "\n") );
		}

		FileWriter myWriter;
		try {
			myWriter = new FileWriter("stats.csv");
			System.out.print(output.toString());
			myWriter.write(output.toString());
			myWriter.close();
		} catch (IOException e) {
			System.out.println("File io error");
			e.printStackTrace();
		}



	}

	/**
	 * Interprets command string to pick strategy
	 * @param strategy The command string input
	 */
	public Algorithm choosestrategy(String strategy) {
		//  Auto-generated method stub
		ticker = new Ticker(); //.tick();
		//int xcoord = -1;
		//int ycoord = -1;

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
		ticker.tick();
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
		ticker.tick();
		update();
		ticker.tick();
		//check if the board is won/lost
		boolean alldone = true;
		boolean gameover = false;
		for(int i=0;i<knownBoard.length;i++) {
			for(int j=0;j<knownBoard.length;j++) {
				ticker.tick();

				if(knownBoard[i][j] == 0) {
					gameover = true;
					alldone = false;
					break;
					
				}

				//should it be flipped over?

				if( (answer[i][j] == 2 ||  answer[i][j] == 3) && (answer[i][j] != knownBoard[i][j]) ) {
					alldone = false;
					ticker.tick();
					//break; //the dirty sneaky culprit
				}


			}
		}
		//Game over is #1 Priority - check first
		if(gameover) {
			ticker.tick();
			
			score = 0;

			int flippedTile = answer[xcoord][ycoord];
			logger.log(flippedTile, getBoardState());

			logger.setExit(2, score);

			cleanup(false, false, false);

			return 2;
		}
		else if(alldone) {
			ticker.tick();
			int flippedTile = answer[xcoord][ycoord];
			score *= flippedTile;
			logger.log(flippedTile, getBoardState());

			logger.setExit(1, score);

			cleanup(true, false, false);

			return 1;
		}
		else {
			ticker.tick();
			prettyprint();
			int flippedTile = answer[xcoord][ycoord];
			score *= flippedTile;
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
		ticker.tick();
		if(withdraw) {
			System.out.println("Could not decide on another move, so chose to withdraw");
			//System.exit(2);
			ticker.tick();
			return 2;
			// 2 denotes withdraw status
		}

		if(win){
			ticker.tick();
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

			System.out.println("Score: " + score);
			//System.exit(0);
			return 0;
		}
		else {
			ticker.tick();
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
			System.out.println("Score: " + score);
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

		ticker.tick();
		switch(boardResult){
		//Flipped a Voltorb - Trigger Game Over
		case 0:
			knownBoard[x][y] = 0;
			values[x][y] = 0;
			prettyprint();
			System.out.println("BOMB!");
			ticker.tick();
			break;

			//Flipped a 1 - Do nothing but just update board
		case 1:
			knownBoard[x][y] = 1;
			values[x][y] = 1;
			System.out.println("1 Flipped");
			ticker.tick();
			break;


			//Flipped a 2 - Update Board and Coin Count
		case 2:
			knownBoard[x][y] = 2;
			values[x][y] = 2;
			System.out.println("2 Flipped");
			ticker.tick();
			break;

			//Flipped a 3 - Update Board and Coin Count
		case 3:
			knownBoard[x][y] = 3;
			values[x][y] = 3;
			System.out.println("3 Flipped!");
			ticker.tick();
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
		//adjust printing for nxn
		System.out.println("   ");
		for (int i = 0; i < SIZE; i++) {
			System.out.print(i);
		}
		System.out.println("\n");
		state = state.concat("   ");
		for (int i = 0; i < SIZE; i++) {
			state = state.concat(i+"");
		}
		state = state.concat("\n");
		//state = state.concat("\n   01234\n");
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
				ticker.tick();
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
		for(int i=0;i<3;i++) {
			for(int j=0;j<SIZE;j++) {
				for(int k=0;k<SIZE;k++) {
					accumulated[i][j][k]=0;
					ticker.tick();
				}
			}
		}
		
		for(int k=0;k<numberOfFinalStates;k++)
		{
			add=true;
			values=states.get(k);
			ticker.tick();
			for(int i=0;i<SIZE;i++)
			{
				for(int j=0;j<SIZE;j++)
				{
					//old
					//if(!showValues[i][j].getText().equals(""))
					//using knownBoard now
					ticker.tick();
					if (!(knownBoard[i][j] == COVERED))
					{
						//old
						//tempInt=Integer.parseInt(showValues[i][j].getText());
						//updated to take in from known board
						tempInt=knownBoard[i][j];
						if(values[i][j]!=tempInt)
							add=false;
						ticker.tick();
					}
				}
			}
			if(add)
			{
				newStates.add(values);
				newSize++;
				ticker.tick();
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
					
					
					ticker.tick();
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
//				if(accumulated[0][i][j]>maxvoltorb&&showValues[i][j].getText().equals(""))
//					maxvoltorb=accumulated[0][i][j];
//				if(accumulated[2][i][j]>maxscore&&showValues[i][j].getText().equals(""))
//					maxscore=accumulated[2][i][j];
				//probabilities[i][j].setText("<"+Math.round(accumulated[0][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[1][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[2][i][j]/alpha*100)/100.0+">\t");
				ticker.tick();
			}
		}
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
//				if(accumulated[0][i][j]==maxvoltorb&&showValues[i][j].getText().equals(""))
//					probabilities[i][j].setForeground(Color.red);
//				else if(accumulated[2][i][j]==maxscore&&showValues[i][j].getText().equals(""))
//					probabilities[i][j].setForeground(Color.blue);
//				else
//					probabilities[i][j].setForeground(Color.black);
				
//				probabilities[i][j].setText("<"+accumulated[0][i][j]+" , "+accumulated[1][i][j]+" , "+accumulated[2][i][j]+">\t");
				currentVProbabilities[i][j] = accumulated[0][i][j];
				currentOProbabilities[i][j] = accumulated[1][i][j];
				currentSProbabilities[i][j] = accumulated[2][i][j];
				
				ticker.tick();
			}
		}
		numStates.setText("Number of Possible Games: "+newSize);
		ticker.tick();
	}
	/**
	 * 
	 */
	public void reset()
	{
		logger.consoleprint();
		///logger.createCSV();
		//logger.createTXT();

		stats.add(logger.csvSummaryPrint());

		//Make new logger object
		logger = new MoveLogger(simCounter++);
		states.clear();
		for(int i=0;i<SIZE;i++)
		{
//			Cols[i].setText("");
//			Rows[i].setText("");
//			Vcols[i].setText("");
//			Vrows[i].setText("");
			place[i]=false;
			columns[i]=-1;
			vcolumns[i]=-1;
			rows[i]=-1;
			vrows[i]=-1;
			ticker.tick();
			for(int j=0;j<SIZE;j++)
			{
				accumulate[i][j]=0;
				nextValue[i][j]=0;
				values[i][j]=0;
//				showValues[i][j].setText("");	
//				probabilities[i][j].setForeground(Color.black);
//				probabilities[i][j].setText("");

				//added
				knownBoard[i][j] = COVERED; //flag for unknown, will print as a blank/covered space
				answer[i][j] = 0;
				currentVProbabilities[i][j] = 0;
				currentSProbabilities[i][j] = 0;
				currentOProbabilities[i][j] = 0;
				ticker.tick();

			}
			numStates.setText("");
			ticker.tick();

		}

		//Recursion bad
		return;
	}
	
	/**
	 * do definitions of arrays etc that depend on size
	 */
	private void createArrays() {
		values=new int[SIZE][SIZE];
		nextValue=new int[SIZE][SIZE];
		columns=new int[SIZE];
		vcolumns=new int[SIZE];
		rows=new int[SIZE];
		vrows=new int[SIZE];
		accumulate=new double[SIZE][SIZE];

		//2-D Answer Board
		answer = new int[SIZE][SIZE];

		knownBoard = new int[SIZE][SIZE];
		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j){
				knownBoard[i][j] = COVERED; //flag for unknown, will print as a blank/covered space
			}
		}

		//current voltorb probabilities
		currentVProbabilities = new double[SIZE][SIZE];
		//current scoring probabilites
		currentSProbabilities = new double[SIZE][SIZE];
		//current one-tile probabilites
		currentOProbabilities = new double[SIZE][SIZE];

		place=new boolean[SIZE];

		for (int i = 0;  i < SIZE; ++i){
			columns[i]=-1;
			vcolumns[i]=-1;
			rows[i]=-1;
			vrows[i]=-1;
			for (int j = 0; j < SIZE; j++){
				accumulate[i][j]=0;
				nextValue[i][j]=0;
				values[i][j]=0;
			}
		}
		
		//syntax weird https://www.geeksforgeeks.org/array-of-arraylist-in-java/ 
		// these will be lists of the possible permutations (boolean arrays) that show the voltorb positions given the number of voltorbs (index in big array)
		permutations = new ArrayList[SIZE+1];
        for (int i = 0; i < permutations.length; i++) {
            permutations[i] = new ArrayList<boolean[]>();
        }
        
        permutationSetup(SIZE, new boolean[SIZE], 0);
        
        sortPermutations();
        //printPermutations();
	}
	
	/**
	 * method to print out permutations (use for debugging)
	 */
	private void printPermutations() {
		for (int i = 0; i < permutations.length; i++) {
            for (int j = 0; j < permutations[i].size(); j++) {
                for (int k = 0; k < permutations[i].get(j).length; k++) {
                	System.out.print(permutations[i].get(j)[k]);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
	}
	
	/**
	 * recursive method for getting all permutations of the voltorb boolean arrays
	 * @param n size (could probably not be a parameter ngl)
	 * @param arr array to add to
	 * @param i current index in array
	 */
	private void permutationSetup (int n, boolean arr[], int i)
	{
	    if (i == n) {
	    	addToPermutations(arr);
	        return;
	    }
	  
	    // First assign "0" at ith position
	    // and try for all other permutations
	    // for remaining positions
	    arr[i] = false;
	    permutationSetup (n, arr, i + 1);
	  
	    // And then assign "1" at ith position
	    // and try for all other permutations
	    // for remaining positions
	    arr[i] = true;
	    permutationSetup (n, arr, i + 1);
	}
	
	/**
	 * helper for permutationSetup, adds array to the correct arraylist based on number of voltorbs
	 * @param arr array to add
	 */
	private void addToPermutations (boolean[] arr) {
		int numTrue = 0;
		for (int i = 0; i < arr.length; ++i) {
			if (arr[i] == true) {
				numTrue++;
			}
		}
		
		boolean[] copy = arr.clone();
		permutations[numTrue].add(copy);
	}
	
	private void sortPermutations() {
		for (int i = 0; i < permutations.length; i++) {
			Collections.sort(permutations[i], new SortFalseFirst());
		}
	}
	
	class SortFalseFirst implements Comparator<boolean[]> {
	    // Used for sorting in ascending order of
	    // name
	    public int compare(boolean[] a, boolean[] b)
	    {
	    	for (int i = a.length-1; i>=0; i--) {
	    		if (a[i]!=b[i]) {
	    			if (a[i]) {
	    				return 1;
	    			}
	    			return -1;
	    		}
	    	}
	        return 0;
	    }
	}
	
	/**
	 *  
	 * 
	 */
	public void setNumbers()
	{

		/* ArgsProcessor file opening functionality is taken from Prof Cosgrove's 131 changes. He's the best
		 * Has been updated to use file input directly
		 * Modified by Lane Bohrer for use here
		 * */

		if(runningGlobal) {

			//thank you Internet very cool https://www.geeksforgeeks.org/file-listfiles-method-in-java-with-examples/

			File parentFile = new File("support_src/boards/resources"); 
			ticker.tick();

			FileFilter filter = new FileFilter() { 

				public boolean accept(File parentFile) 
				{ 

					ticker.tick();

					//for batch #, could do something like
					return parentFile.getName().contains("-"+(totalSims)+"-");
					//return true;

				} 
			}; 
			// System.out.println(parentFile.listFiles(filter));
			//File[] newfiles = parentFile.listFiles(filter);


			files = parentFile.listFiles(filter); 
			
			//System.out.println("files" + files);
			
			totalSims = files.length;

			Arrays.parallelSort(files);
			ticker.tick();

			try {
				in = new Scanner(files[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("oopsies");
				ticker.tick();
			}
			ticker.tick();
			runningGlobal = false;

		} else {
			try {
				in = new Scanner(files[simCounter]);
				ticker.tick();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("oopsies");
				ticker.tick();
			}

		}

		/*NEW text file setup:
		 * Size(int)

		 * True board answers (25 ints, top left to bottom right, across then down)
		 * */


		SIZE = in.nextInt(); //not used yet but will let us do nxn
		
		createArrays();

		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				answer[i][j] = in.nextInt();
				ticker.tick();
			}
		}

		//calculate point and voltorb counts, going horizontal first
		for (int i = 0; i < SIZE; ++i) {
			int countPoints = 0;
			int countVoltorbs = 0;
			for (int j = 0; j < SIZE; ++j) {
				if (answer[i][j] == 0) {
					countVoltorbs++;
					ticker.tick();
				}
				else {
					countPoints += answer[i][j];
					ticker.tick();
				}
			}
			rows[i] = countPoints;
			vrows[i] = countVoltorbs;
			ticker.tick();
		}

		//sums over columns (going up/down)
		for (int i = 0; i < SIZE; ++i) {
			int countPoints = 0;
			int countVoltorbs = 0;
			ticker.tick();
			for (int j = 0; j < SIZE; ++j) {
				if (answer[j][i] == 0) {
					countVoltorbs++;
					ticker.tick();
				}
				else {
					countPoints += answer[j][i];
					ticker.tick();
				}
			}
			columns[i] = countPoints;
			vcolumns[i] = countVoltorbs;
			ticker.tick();
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
				ticker.tick();
				//showValues[i][j].setText(Integer.toString(knownBoard[i][j]));
			}
		}
	}
	
	/**
	 * Recursive method to find Voltorb Placements
	 * @param numCombinations
	 */
	private void findVoltorbPlacements(int[] numCombinations, int iterator) {
		
		//what is the base case!
		if (iterator == SIZE) { //or SIZE, depends on including testCols in the last call or separate
			//do something?
			//testCols here
			if (testVCols(values)) {
				tempValues=new int[SIZE][SIZE];
				incrementValues(values);
				copyMatrix(values,tempValues);
				states.add(tempValues);
				countforV++;

				ticker.tick();
			}
			return;
		}
		
		//what is the recursive substructure!
		for(int first=0;first<numCombinations[iterator];first++) {
			createPlacement(vrows[iterator],first,place);
			ticker.tick();
			for(int i=0;i<SIZE;i++)
			{
				if(place[i]==true)
					values[iterator][i]=0;
				else
					values[iterator][i]=-1;

				ticker.tick();
			}
		
			findVoltorbPlacements(numCombinations, iterator + 1);
			
		}
	}

	//ooooh recursion use helper method for things that happen once 
	/**
	 * recursive method for finding possible states, calls its helper
	 */
	private void findAllPossibleStates(){
		
		possibleStatesCount = 0;

		int[] max = new int[SIZE];
		int[] min = new int[SIZE];

		for (int i = 0; i < SIZE; ++i){
			min[i] = (rows[i]+vrows[i]-SIZE+1)/2;
			max[i] = (rows[i]+vrows[i]-SIZE);
			if(max[i]>(SIZE-vrows[i])){
				max[i]=SIZE-vrows[i];
			}
			ticker.tick();
		}

		
		while(countforV>0){
			values=states.pop();
			copyMatrix(values,nextValue);
			
			possibleStatesRecursive(max,min, nextValue, 0);
			
			ticker.tick();
			countforV--;
		}
	}
	
	/**
	 * Recursive helper for findAllPossibleStates
	 * @param max array of max vals
	 * @param min array of min vals
	 * @param nextValue array for nextValue
	 * @param iterator recursive iterator
	 */
	private void possibleStatesRecursive(int[] max, int[] min, int[][] nextValue, int iterator) {
		
		//base! case!
		if (iterator == SIZE) {
			if(testCols(nextValue))
			{
				possibleStatesCount++;
				tempValues=new int[SIZE][SIZE];
				copyMatrix(nextValue,tempValues);
				incrementValues2(nextValue);
				states.add(tempValues);
				ticker.tick();
			}
			return;
		}
		
		int counter;
		ticker.tick();
		for(int firstouter=min[iterator];firstouter<=max[iterator];firstouter++)
		{
			for(int first=0;first<getNumCombinations(vrows[iterator], firstouter);first++)
			{
				
				generate(firstouter,vrows[iterator],first,place);
				counter=0;
				ticker.tick();
				for(int i=0;i<SIZE;i++) {
					ticker.tick();

					if(nextValue[iterator][i]!=0)
					{
						if(place[counter])
							nextValue[iterator][i]=5; //what does 5 mean??
						else
							nextValue[iterator][i]=-1;
						counter++;
					}

				}
				//recurse
				possibleStatesRecursive(max, min, nextValue, iterator+1); 
				ticker.tick();
			}
		}
	}
	
	/**
	 * Method called to figure out NumberOfFinalStates, will call a recursive helper
	 */
	private void computeFinalStates(){
		int[] nums = new int[SIZE];

		int numCom[] =new int[SIZE];
		for(int i=0;i<SIZE;i++)
			numCom[i]=0; ticker.tick();
			
		while(possibleStatesCount>0){
			//prep work
			for(int i=0;i<SIZE;i++) {
				numCom[i]=0;
				ticker.tick();
			}
			values=states.pop();
			changeToValues(values,numCom); //gets... number of.. something in each row. voltorbs maybe?
			for (int i = 0; i < SIZE; ++i) {
				nums[i] = getNumberOnOff(numCom[i]); //2^numCom[i]
				ticker.tick();
			}
			
			
			computeFinalStatesRecursive(nums, numCom,  0);
			//computeFinalStatesIterative(nums, numCom);

			possibleStatesCount--;
		}
	}
	
	/**
	 * Recursive helper for computeFinalStates
	 * @param nums array created in computeFinalStates
	 * @param numCom array created in computeFinalStates
	 * @param iterator recursive counter, goes through size
	 */
	private void computeFinalStatesRecursive(int[] nums, int[] numCom, int iterator) {
		if (iterator == SIZE) { //check after last row
			if(testConstraints(values))
			{
				tempValues=new int[SIZE][SIZE];
				copyMatrix(values,tempValues);
				states.add(tempValues);
				numberOfFinalStates++;
			}
			ticker.tick();
			return;
		}

		for(int first=0;first<nums[iterator];first++)
		{
			getBinaryPlacement(nums[iterator],first,place, numCom[iterator]);
			//make placement
			countforV=0;
			for(int i=0;i<SIZE;i++)
			{
				ticker.tick();

				if(values[iterator][i]==2||values[iterator][i]==3)
				{
					if(place[countforV])
					{
						values[iterator][i]=3;
						ticker.tick();
					}
					else
						values[iterator][i]=2;
					countforV++;

					ticker.tick();
				}
			}
			boolean skip = false;
			if(!isValid(values[iterator],iterator))
			{
				if(outOfBounds(values[iterator],iterator))
					return;
				else
					skip = true;
				
				/* 
				 	find a way to speed this up? used to be:
					if(outOfBounds(x,x))
						break;
					else
						continue;
				*/
			}

			//recurse
			if (!skip) {
			computeFinalStatesRecursive(nums, numCom, iterator+1);
			}
		}

	}
	
	/**
	 * 
	 */
	public void startCalculating() {
		countforV=0;

		int numCombinations[]=new int[SIZE];
		for (int i=0;i<SIZE;i++) {

			numCombinations[i] = ncr(SIZE, vrows[i]);
			ticker.tick();

		}
		//determine possible placements of voltorbs////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////

		//new
		findVoltorbPlacements(numCombinations, 0);
		
		///end of finding voltorbs////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		System.out.println("Number of possible states before adding cards: "+countforV);
		System.out.println("Probabilities of Voltorbs:");
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				System.out.print(	Math.round((accumulate[i][j]/(countforV*1.0))*10000)/10000.0	+"\t");
				accumulate[i][j]=0;
				ticker.tick();
			}
			System.out.println();
		}
		//start of finding all possible assignments///////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		

		ticker.tick();

		//new
		findAllPossibleStates();		
		

		System.out.println("New number of states  "+possibleStatesCount);
		System.out.println("Probability of Scoring:");
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				System.out.print(	Math.round(accumulate[i][j]/(possibleStatesCount*1.0)*10000)/10000.0	+"\t");
				ticker.tick();
			}
			System.out.println();
		}

		//end of finding all possible assignments for greater than 1 slots/////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//////now to actually compute real states
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		numberOfFinalStates=0;

		//new
		computeFinalStates();
		//comebackhere
		

		System.out.println("Final States "+numberOfFinalStates);

		//end of computations////now have a set of possible real states////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		//calculate the probabilities
		double accumulated[][][] = new double[3][SIZE][SIZE];
		for(int i=0;i<3;i++)
			for(int j=0;j<SIZE;j++)
				for(int k=0;k<SIZE;k++)
					accumulated[i][j][k]=0; ticker.tick();

		for(int k=0;k<numberOfFinalStates;k++)
		{
			values=states.get(k);
			for(int i=0;i<SIZE;i++)
			{
				for(int j=0;j<SIZE;j++)
				{
					ticker.tick();
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
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				ticker.tick();
				alpha=(accumulated[0][i][j]+accumulated[1][i][j]+accumulated[2][i][j]);

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
	public boolean debugger(int values[]) //wat
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
		
		//ticker.tick();

	}
	
	/**
	 * @param values
	 * @param num
	 */
	public void changeToValues(int values[][],int num[])
	{
		for(int i=0;i<SIZE;i++)
		{
			for(int j=0;j<SIZE;j++)
			{
				if(values[i][j]==-1)
					values[i][j]=1;
				if(values[i][j]==5)
				{
					values[i][j]=2;
					num[i]++;
				}
				ticker.tick();
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
		for(int i=0;i<SIZE;i++)
		{	
			maxrows+=values[i];
			ticker.tick();

		}
		if(maxrows>rows[num])
			return true;
		
		ticker.tick();
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
		for(int i=0;i<SIZE;i++)
		{	
			maxrows+=values[i];
			ticker.tick();

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

		for(int j=0;j<SIZE;j++)
		{
			maxcol=0;
			for(int k=0;k<SIZE;k++)
			{
				//iterate through columns
				maxcol+=values[k][j];
				ticker.tick();
			}
			if(maxcol!=columns[j])
			{
				return false;			
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
		
		int[] max = new int[SIZE];
		int[] min = new int[SIZE];

		for (int i = 0; i < SIZE; ++i){
			min[i] = (columns[i]+vcolumns[i]-SIZE-1)/2;
			max[i] = (columns[i]+vcolumns[i]-SIZE);
			if(max[i]>(SIZE-vcolumns[i])){
				max[i]=SIZE-vcolumns[i];
			}
			ticker.tick();
		}

		
		for (int i = 0; i < SIZE; ++i){
		int count2 = 0;
			for(int j=0;j<SIZE;j++){
				if(values[j][i]==5)
					count2++;
				
				ticker.tick();
			}
			if(count2<min[i]||count2>max[i])
				return false;
		}

		ticker.tick();

		return true;
	}

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
				
				ticker.tick();
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
				if(values[i][j]==5) // flag value = 5? for a voltorb i am pretty sure
					accumulate[i][j]++;
				
				ticker.tick();
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
		for(int i=0;i<SIZE;i++)
		{
			count=0;
			for(int j=0;j<SIZE;j++)
			{
				if(values[j][i]==0)
					count++;
				
				ticker.tick();
			}
			if(count!=vcolumns[i])
				return false;
			
			
			ticker.tick();

		}

		return true;
	}

	/**
	 * @param numMines
	 * @param whichOne
	 * @param place
	 */
	public void createPlacement(int numMines,int whichOne,boolean place[])	{

		setPlacedNew(place, permutations[numMines].get(whichOne));
		
		ticker.tick();
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
		ticker.tick();
		
		place[0]=b0;
		place[1]=b1;
		place[2]=b2;
		place[3]=b3;
		place[4]=b4;
		

	}
	
	/**
	 * @param place place array
	 * @param current current array to transfer in
	 */
	private void setPlacedNew(boolean[] place, boolean[] current) {
		for (int i = 0; i < SIZE; ++i) {
			place[i] = current[i];
		}
	}
	
	
	/**
	 * @param in
	 * @param out
	 */
	public void copyMatrix(int in[][],int out[][])
	{
		for(int i=0;i<in.length;i++)
		{
			for(int j=0;j<in[0].length;j++)
			{
				out[i][j]=in[i][j];
				ticker.tick();
			}
		}
	}
	
	/**
	 * @param num
	 * @return
	 */
	public int getNumberOnOff(int num)	{
		ticker.tick();
		return (int)Math.pow(2, num);
	}
	
	
	private void printBoolArr(boolean[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
	}

	/**
	 * @param num
	 * @param curr
	 * @param place
	 */
	public void getBinaryPlacement(int num,int curr,boolean place[], int exp) { //switch num to just the exponent
		//curr iterates 0 to num.
		//does this iterate through the first "num" permutations?
		//could sort the array(list)s somehow and use that?
		
		//actually i think it does the permutations with (possible) trues only in the first num=2^x places?
		//can work with that in a similar way as the other method
		//could pass in numComs instead of nums? that would avoid taking a log oofity
		
	//	boolean[] newChoice = null;
		
		int localCount = 0;
		for (int i = 0; i <= exp; i++) {
			for (int j = 0; j < ncr(exp,i); j++) {
				
				if (localCount == curr) {
					setPlacedNew(place,permutations[i].get(j));
					return;
					//newChoice = permutations[i].get(j);
				}
				localCount++;
			}
		}
		
		
		
		
	//	System.out.print("\tnum: "+num+", aka 2^"+exp+", curr: " +curr+"\t");
//		printBoolArr(newChoice);
//		System.out.println();
		/*
		if(num==1) {
//			System.out.print("new: ");
//			printBoolArr(newChoice);
//			System.out.println("\told: false,false,false,false,false");
			setPlaced(false,false,false,false,false,place);
		}
		else if(num==2) {
			if(curr==0) {
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,false,false,false");
				setPlaced(false,false,false,false,false,place);
			}
			else if(curr==1) {
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,false,false,false");
				setPlaced(true,false,false,false,false,place);
			}
		}
		else if(num==4) {
			if(curr==0) {
				setPlaced(false,false,false,false,false,place);
			}
			else if(curr==1)
				setPlaced(true,false,false,false,false,place);
			else if(curr==2)
				setPlaced(false,true,false,false,false,place);
			else if(curr==3)
				setPlaced(true,true,false,false,false,place);
		}
		else if(num==8) {
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
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,false,false,false");
				setPlaced(false,false,false,false,false,place);
				break;
			case 1:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,false,false,false");
				setPlaced(true,false,false,false,false,place);
				break;
			case 2:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,false,false,false");
				setPlaced(false,true,false,false,false,place);
				break;
			case 3:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false, true,false,false");
				setPlaced(false,false,true,false,false,place);
				break;
			case 4:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,false,true,false");
				setPlaced(false,false,false,true,false,place);
				break;
			case 5:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,false,false,true");
				setPlaced(false,false,false,false,true,place);
				break;
			case 6:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,false,false,false");
				setPlaced(true,true,false,false,false,place);
				break;
			case 7:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,true,false,false");
				setPlaced(true,false,true,false,false,place);
				break;
			case 8:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,false,true,false");
				setPlaced(true,false,false,true,false,place);
				break;
			case 9:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,false,false,true");
				setPlaced(true,false,false,false,true,place);
				break;
			case 10:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,true,false,false");
				setPlaced(false,true,true,false,false,place);
				break;
			case 11:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,false,true,false");
				setPlaced(false,true,false,true,false,place);
				break;
			case 12:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,false,false,true");
				setPlaced(false,true,false,false,true,place);
				break;
			case 13:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,true,true,false");
				setPlaced(false,false,true,true,false,place);
				break;
			case 14:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,true,false,true");
				setPlaced(false,false,true,false,true,place);
				break;
			case 15:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,false,true,true");
				setPlaced(false,false,false,true,true,place);
				break;
			case 16:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,true,false,false");
				setPlaced(true,true,true,false,false,place);
				break;
			case 17:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,false,true,false");
				setPlaced(true,true,false,true,false,place);
				break;
			case 18:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,false,false,true");
				setPlaced(true,true,false,false,true,place);
				break;
			case 19:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,true,true,false");
				setPlaced(true,false,true,true,false,place);
				break;
			case 20:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,true,false,true");
				setPlaced(true,false,true,false,true,place);
				break;
			case 21:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,false,true,true");
				setPlaced(true,false,false,true,true,place);
				break;
			case 22:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,true,true,false");
				setPlaced(false,true,true,true,false,place);
				break;
			case 23:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,true,false,true");
				setPlaced(false,true,true,false,true,place);
				break;
			case 24:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,false,true,true");
				setPlaced(false,true,false,true,true,place);
				break;
			case 25:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,false,true,true,true");
				setPlaced(false,false,true,true,true,place);
				break;
			case 26:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,true,true,false");
				setPlaced(true,true,true,true,false,place);
				break;
			case 27:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,true,false, true");
				setPlaced(true,true,true,false,true,place);
				break;
			case 28:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,false,true, true");
				setPlaced(true,true,false,true,true,place);
				break;
			case 29:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,false,true,true, true");
				setPlaced(true,false,true,true,true,place);
				break;
			case 30:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: false,true,true,true, true");
				setPlaced(false,true,true,true,true,place);
				break;
			case 31:
//				System.out.print("new: ");
//				printBoolArr(newChoice);
//				System.out.println("\told: true,true,true,true, true");
				setPlaced(true,true,true,true,true,place);
				break;
			}
		}*/
		
	}
	
	/**
	 * @param numV
	 * @param numScoring
	 * @param index
	 * @param place
	 */
	public void generate(int numV,int numScoring,int index,boolean place[])	{
		
		if(numV==0) {
			setPlacedNew(place,permutations[numV].get(0));
			return;
		}
		//System.out.println("index: "+ index + "\tfor len: "+permutations[numV].size()+"\t should have: "+getNumCombinations(numScoring, numV));
		setPlacedNew(place,permutations[numV].get(index));		
		
	}
	
	/**
	 * @param numScoring number of scoring tiles
	 * @param numVoltorbs number of voltorbs
	 * @return number of combinations (SIZE - numScoring choose numVoltorbs)
	 */
	public int getNumCombinations(int numScoring,int numVoltorbs)	{
		return ncr(SIZE - numScoring, numVoltorbs);
	}

	/** Factorial method to call in ncr,
	gotten from https://www.geeksforgeeks.org/java-program-for-factorial-of-a-number/
	 */
	int factorial(int n) { 
		ticker.tick();

		if (n == 0) 
			return 1; 
		
		return n*factorial(n-1); 
	} 

	/** ncr calculator
	@param n integer n
	@param k integer k
	@return n choose k
	 */
	int ncr(int n, int k) {
		ticker.tick();
		int num = factorial(n);
		int denom = (factorial(k))*(factorial(n-k));
		return (num/denom);
		
	}


}




