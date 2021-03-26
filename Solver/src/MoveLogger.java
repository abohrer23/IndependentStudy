import java.util.LinkedList;
import cse131.Out;


/**
 * Move Logging Object to track moves in Voltorb flip and print them out in a clean readable manner as a CSV and a text log
 * @author evinjaff
 *
 */
public class MoveLogger {
	
	//LinkedList that will contain a printouts of the board in its states (for log file only)
	private LinkedList<String> boardStates;
	
	//LinkedList that will contain a series of integers of the tiles flipped over
	private LinkedList<Integer> tileSequence;
	
	//stores the exit status of the game
	private int exitStatus; // 1=win, 2=game over
	
	//keeps track of the number of turns
	private int turnCounter;

	//Keeps track of the values flipped
	private int ones;
	private int twos;
	private int threes;
	
	//name of algorithm ran (will have global and auto flags i think but idk)
	private String algo;
	
	private int fileNumber;
	
	public String getAlgo() {return algo;}
	
	
	public MoveLogger(int fileNumber) {
		//start from zero
		turnCounter = 0;
		//0 - default (quit or not finished)
		//1 - win
		//2 - gameover
		
		exitStatus = 0;
		boardStates = new LinkedList<String>();
		tileSequence = new LinkedList<Integer>();
		this.fileNumber = fileNumber;
		
	}
	
	public void log(Integer tilevalue, String Boardstate) {

		switch(tilevalue.intValue()){

			case 1:
				this.ones++;
				break;
			case 2:
				this.twos++;
				break;
			case 3:
				this.threes++;
				break;

		}

		tileSequence.add(tilevalue);
		boardStates.add(Boardstate);
		turnCounter++;
	}
	
	public void setExit(int s) {
		exitStatus = s;
		turnCounter++; //fixes an off-by-one error
	}
	
	public void setAlgorithm(String a) {
		algo = a;
	}

	
	/** TODO implement
	 * @return A string formatted in a text-based style for a file to log actions
	 */
	public String logprint() {
		String output = "";
		
		output += "Exit status: ";
		output += exitStatus; 
		output += " Turn counter: ";
		output += turnCounter;
		output += " Algorithm: ";
		output += algo;
		output += "\n";
		
		output += "Flipped tiles: ";
		output += tileSequence;
		output += "\n";
		
		output += "Board States: ";
		output += boardStates;
		output += "\n";
		return output;
	}
	
	/**
	 * 
	 * @return A string formatted for a Comma-Separated-Value file
	 * 
	 */
	public String csvprint() {
		String csv = "";
		
		csv += exitStatus; 
		csv += ", ";
		csv += turnCounter;
		csv += ", ";
		csv += algo;
		csv += "\n";
		
		csv += csvLinkedListInt(tileSequence);
		csv += "\n";
		
		csv += csvLinkedListStr(boardStates);
		csv += "\n";
		
		return csv;
	}
	
	/**
	 * This method creates a CSV file
	 */
	public void createCSV() {
		String filename = "testOutput"+fileNumber+".csv"; //TODO: figure out how to pick file names
		Out o = new Out(filename);
		o.print(csvprint());
		
		o.close();
		
	}
	
	/**
	 * This method creates a txt file
	 */
	public void createTXT() {
		String filename = "testOutput"+fileNumber+".txt"; //TODO: figure out how to pick file names
		Out o = new Out(filename);
		o.print(csvprint());
		
		o.close();
		
	}
	
	/**
	 * Prints boardStates
	 *TODO this looks kinda ugly ngl
	 */
	public void consoleprint() {
		System.out.println("consoleprint:");
		System.out.println("algorithm:" + algo);
		for (String temp : boardStates) {
		    System.out.println(temp);
		}	
	}
	
	/**
	 * 
	 * @param list LinkedList of Integers
	 * @return String with Integers separated by commas
	 */
	private String csvLinkedListInt(LinkedList<Integer> list) {
		String s = "";
		
		for (Integer i : list) {
			s += i;
			s += ", ";
		}
		return s;
	}
	
	/**
	 * 
	 * @param list LinkedList of Strings
	 * @return String with given Strings separated by commas and new lines taken out
	 */
	private String csvLinkedListStr(LinkedList<String> list) {
		String s = "";
		
		for (String i : list) {
			i = i.replace("\n", "\t"); //TODO 
			/* Problem is that i'm trying to find a better way to do line breaks w/o 
			 * a \n because that messes w csv. Ideally want to keep the whole board into 
			 * one cell?
			 * */
			
			s += i;
			s += ", ";
		}
		
		return s;
	}

	/**
	* 
	* CSV Format: algo, Number of turns, Exit status, # of 1s flipped, # of 2's flipped
	* @return a string formatted with brief quantitative sumnmaries of the game for quick and dirty analysis
	**/
	public String csvSummaryPrint() {
		String s = "";

		s += this.algo + ",";
		s += this.turnCounter + ",";
		s += this.exitStatus + ",";
		s += this.ones + ",";
		s += this.twos + ",";
		s += this.threes + ",";


		
		return s;
	}


	
	
}
