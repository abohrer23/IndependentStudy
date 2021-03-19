/**
 * 
 */

/**
 * 
 * ScoreFirst will flip the tiles with the highest probability of scoring a 2 or 3 first
 * @author evinjaff
 *
 */
public class ScoreFirst extends Strategy {

	@Override
	public int[] choosetile(double[][] currentVProbabilities, double[][] currentOProbabilities,
			double[][] currentSProbabilities, int[][] knownBoard) {
		int [] ans = {-1, -1};
		double max = 0.0;
		for (int i = 0; i < currentSProbabilities.length; ++i) {
			for (int j = 0; j < currentSProbabilities[i].length; ++j) {
				if (currentSProbabilities[i][j] > max && knownBoard[i][j] == -1 && currentVProbabilities[i][j]!= 1.0) {
					ans[0] = i;
					ans[1] = j;
					max = currentSProbabilities[i][j];
				}
			}
		}
		if (ans[0] != -1 && ans[1] != -1) {
			return ans;
		}
		return null;
	}

	@Override
	public void describesmethod() {
		// TODO Auto-generated method stub

	}

}
