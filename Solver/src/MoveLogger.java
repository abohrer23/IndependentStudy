import java.util.LinkedList;

/**
 * Move Logging Object to track moves in Voltorb flip and print them out in a clean readable manner as a CSV and a text log
 * @author evinjaff
 *
 */
public class MoveLogger {
	
	//LinkedList that will contain a printout of the board in its state (for logfile only)
	private LinkedList<String> BoardState;
	
	//LinkedList that will contain a series of integers of the tiles flipped over
	private LinkedList<Integer> tilesequence;
	
	//stores the exit status of the game
	private int exitstatus; // 1=win, 2=gameover
	
	//keeps track of the number of turns
	private int turncounter;
	
	
	public MoveLogger() {
		//start from zero
		turncounter = 0;
		exitstatus = 0;
		BoardState = new LinkedList<String>();
		tilesequence = new LinkedList<Integer>();
		
	}
	
	public void log(Integer tilevalue, String Boardstate) {
		tilesequence.add(tilevalue);
		BoardState.add(Boardstate);
		turncounter++;
	}
	
	public void setExit(int s) {
		exitstatus = s;
	}
	
	/** TODO implement
	 * @return A string formatted in a text-based style for a file to log actions
	 */
	public String logprint() {
		
		return null;
	}
	
	/**
	 * TODO implement
	 * @return A string formatted for a Comma-Separarated-Value file
	 * 
	 */
	public String csvprint() {
		String csv = "";
		
		
		
		return csv;
	}
	
	public void consoleprint() {
		System.out.println("consoleprint:");
		for (String temp : BoardState) {
		    System.out.println(temp);
		}
		
	}
	
	
}
