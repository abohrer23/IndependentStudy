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
 * - calculate point totals from answer board
 * - check to see if game win (are there any 2s/3s still uncovered?)
 * - implement coin counter
 * 
 * */


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import javax.swing.*;

//Evin and Lane's imports
import cse131.ArgsProcessor;
import boards.boardSimulationFiles;
import java.math.BigInteger;

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
	
	//added
	In file = new In("two.txt");
	int[][] answer;
	int[][] knownBoard;
	double[][] currentVProbabilities;
	final static int COVERED = -1; //special value for still covered spots on the board
	static int SIZE;
	
	public static void main(String args[]) 
	{
		localArgs = args;
		new Main();
	}
	Main() 
	{
		ap = new ArgsProcessor(localArgs); //new
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		states = new LinkedList<int[][]>();
		numStates=new JLabel();
		prob=new JLabel("Probabilities <Voltorb, 1, Scoring>");
		start=new JButton("Start");
		update=new JButton("Update");
		reset=new JButton("Reset");
		play=new JButton("Play");
		add(start);
		add(update);
		start.setBounds(360, 60, 100, 40);
		//update.setBounds(360,120,100,40);
		//reset.setBounds(360,180,100,40);
		play.setBounds(360,120,100,40);	 //new	
		values=new int[5][5];
		nextValue=new int[5][5];
		showValues=new JTextField[5][5];
		columns=new int[5];
		vcolumns=new int[5];
		rows=new int[5];
		vrows=new int[5];
		accumulate=new double[5][5];

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
		setTitle("Voltorb Flip");
		setLayout(null);
		setSize(600, 450);
		add(reset);
		add(play);
		start.addActionListener(new actions());
		update.addActionListener(new actions());
		reset.addActionListener(new actions());
		play.addActionListener(new actions());
		setVisible(true);
		
		
		//auto starts - no need to press start
		setNumbers();
		startCalculating();
		
		
	}
	public class actions implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource()==start)
			{
				//start the stuff
				setNumbers();
				startCalculating();
			}
			if(e.getSource() == play){
				play();
			}
			if(e.getSource()==update)
			{
				update();
			}
			if(e.getSource()==reset)
				reset();
		}

		
	}
	/**
	Play: Will run an input board
	**/
	public void play() {
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
			
			for (int i = 0; i < SIZE; ++i) {
				for (int j = 0; j < SIZE; ++j) {
					if (currentVProbabilities[i][j] == 0 && knownBoard[i][j] == COVERED) {
						xcoord = i;
						ycoord = j;
						break; //take out if changing to lowrisk instead of no risk
					}
				}
			}
			//System.out.println("After for loops: x: " + xcoord + ", y: "+ycoord);
			
			if (xcoord != -1) {
				System.out.print("Autochose spot: (" + xcoord+","+ycoord+")" + "\t");
			} else {
				xcoord = ap.nextInt("x?");
				ycoord = ap.nextInt("y?");
				System.out.print("User chose spot: (" + xcoord+","+ycoord+") with prob: " +currentVProbabilities[xcoord][ycoord] + "\t");
			}
			
			
			//2. Flip. Check agains the board and call a game over if necessary
			
			//Flipped over

			boardify(answer[xcoord][ycoord], xcoord, ycoord);

			//startCalculating();

			update();
			prettyprint();
			System.out.println("\n");
			

			
			//3. Cleanup. Update the coin coint (TBA), exit the program if gameover, and finally update the text elements.


			

			
			
	}

	/**
	boardresult - tile value
	x - x coordinate
	y - y coordinate
	**/

	public void boardify(int boardResult, int x,  int y){

		
			switch(boardResult){
				//Flipped a Voltorb - Trigger Game Over
				case 0:
					knownBoard[x][y] = 0;
					values[x][y] = 0;
					prettyprint();
				reset();
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
	public void prettyprint(){
		
		boolean withProb = true; //also prints probabilities
		
		System.out.print("Board State:");
		if (withProb) {
			System.out.print("\t\t\tProbability of Voltorb");
		}
		System.out.println();
		System.out.println("   01234");
		//System.out.println("   _____");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(i + " |");
			for (int j = 0; j < SIZE; ++j) {
				char val = (knownBoard[i][j] == COVERED ? '▓': Character.forDigit(knownBoard[i][j],10)); 
				System.out.print(val);
			}
			System.out.print(" " + rows[i] + " " + vrows[i]);
			if (withProb) {
				System.out.print("\t\t\t" + currentVProbabilities[i][0] + "\t" + currentVProbabilities[i][1] + "\t" + currentVProbabilities[i][2]+ "\t" + currentVProbabilities[i][3] + "\t" + currentVProbabilities[i][4]);
			}
			System.out.println();
		}
		
		System.out.print("   ");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(columns[i]);
		}
		System.out.print("\n   ");
		for (int i = 0; i < SIZE; ++i) {
			System.out.print(vcolumns[i]);
		}
		System.out.println();
	
	}

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
			}
		}
		numStates.setText("Number of Possible Games: "+newSize);
	}
	public void reset()
	{
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
			}
			numStates.setText("");
		}
	}
	public void setNumbers()
	{
		
		/* ArgsProcessor file opening functionality is taken from Prof Cosgrove's 131 changes. He's the best
		 * Modified by Lane Bohrer for use here
		 * */
		ArgsProcessor file = null;
		try {
			file = boardSimulationFiles.createArgsProcessorFromFile(localArgs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		
		/*NEW text file setup:
		 * Size(int, currently hardcoded to 5)

		 * True board answers (25 ints, top left to bottom right, across then down)
		 * */

		
		SIZE = file.nextInt(); //not used yet but will let us do nxn
		

		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				answer[i][j] = file.nextInt();
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
	public void startCalculating()
	{
		int count=0;
		int numCombinations[];
		numCombinations=new int[5];
		for(int i=0;i<5;i++)
		{
			//need to replace these with (n choose vrows[i])
			
			//BigInteger perms = BigInteger.binom(SIZE,vrows[i]);
			//numCombinations[i] = (int)(perms);
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
		}
		//determine possible placements of voltorbs////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////
		for(int first=0;first<numCombinations[0];first++)
		{
			createPlacement(vrows[0],first,place);
			for(int i=0;i<5;i++)
			{
				if(place[i]==true)
					values[0][i]=0;
				else
					values[0][i]=-1;
			}
			for(int second=0;second<numCombinations[1];second++)
			{
				createPlacement(vrows[1],second,place);
				for(int i=0;i<5;i++)
				{
					if(place[i]==true)
						values[1][i]=0;
					else
						values[1][i]=-1;
				}
				for(int third=0;third<numCombinations[2];third++)
				{
					createPlacement(vrows[2],third,place);
					for(int i=0;i<5;i++)
					{
						if(place[i]==true)
							values[2][i]=0;
						else
							values[2][i]=-1;
					}
					for(int fourth=0;fourth<numCombinations[3];fourth++)
					{
						createPlacement(vrows[3],fourth,place);
						for(int i=0;i<5;i++)
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
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				System.out.print(	Math.round((accumulate[i][j]/(count*1.0))*10000)/10000.0	+"\t");
				accumulate[i][j]=0;
			}
			System.out.println();
		}
		//start of finding all possible assignments///////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		int numberOfStates=0;
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
		int counter;
		while(count>0)
		{
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
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
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
				probabilities[i][j].setText("<"+Math.round(accumulated[0][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[1][i][j]/alpha*100)/100.0+" , "+Math.round(accumulated[2][i][j]/alpha*100)/100.0+">\t");
				currentVProbabilities[i][j] = Math.round(accumulated[0][i][j]/alpha*100)/100.0;
			}
		}
		numStates.setText("Number of Possible States "+numberOfFinalStates);
		
		prettyprint();
		System.out.println("\n");
		
	}
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
	public void incrementValues(int values[][])
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(values[i][j]==0)
					accumulate[i][j]++;
			}
		}
	}
	public void incrementValues2(int values[][])
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(values[i][j]==5)
					accumulate[i][j]++;
			}
		}
	}
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
	public void setPlaced(boolean b0,boolean b1,boolean b2,boolean b3,boolean b4,boolean place[])
	{
		place[0]=b0;
		place[1]=b1;
		place[2]=b2;
		place[3]=b3;
		place[4]=b4;
	}
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
	public int getNumberOnOff(int num)
	{
		return (int)Math.pow(2, num);
	}
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
}








