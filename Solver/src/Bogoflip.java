/**
 * 
 */

/**
 * 
 * BogoFlip is one of the worst flipping strategies. This will pick random tiles until they are flippable
 * @author evinjaff
 *
 */
public class Bogoflip extends Strategy {

	@Override
	public int[] choosetile(double[][] currentVProbabilities, double[][] currentOProbabilities,
			double[][] currentSProbabilities, int[][] knownBoard) {
		// TODO Auto-generated method stub
		
		//we could always pack in a while loop if we wanted to
		int [] ans = {-1, -1};
		do {
			ans[0] = (int)(Math.random()*5.0);
			ans[1] = (int)(Math.random()*5.0);
			
			System.out.println(knownBoard[ans[0]][ans[1]] == -1);
			System.out.println(knownBoard[ans[0]][ans[1]] != -1);
			
		} while(knownBoard[ans[0]][ans[1]] != -1);
		
		return ans;
	}

	@Override
	public void describesmethod() {
		// TODO Auto-generated method stub

	}

}
