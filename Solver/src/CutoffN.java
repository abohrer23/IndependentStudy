/**
 * CutoffN alternates the strategy after an arbitrary number of turns provided by the user. It will play MinRisk() for n turns, then play ScoreFirst() for the rest
 * @author evinjaff
 *
 */
public class CutoffN extends Strategy {
	
	final int turncutoff;
	int turncount;
	
	public CutoffN(int turncutoff) {
		this.turncutoff = turncutoff;
		this.turncount = 1;
	}
	
	@Override
	public int[] choosetile(double[][] currentVProbabilities, double[][] currentOProbabilities,
			double[][] currentSProbabilities, int[][] knownBoard) {
		// TODO Auto-Generated method stub
		if(this.turncount >= this.turncutoff) {
			//Run NoRisk()
			int [] ans = new int[2];
	        
	        double minprobabaility = 1.0;
	        
	        for (int i = 0; i < currentVProbabilities.length; ++i) {
					for (int j = 0; j < currentVProbabilities[i].length; ++j) {
						if (currentVProbabilities[i][j] < minprobabaility && knownBoard[i][j] == -1) {
							
							ans[0] = i;
							ans[1] = j;
							
							minprobabaility = currentVProbabilities[i][j]; 
							
						}
					}
				}
	        this.turncount++;
	        return ans;
			
			
		}
		else {
			//Run ScoreFirst
			 int [] ans = {-1, -1};
				double max = 0.0;
				for (int i = 0; i < currentSProbabilities.length; ++i) {
					for (int j = 0; j < currentSProbabilities[i].length; ++j) {
						if (currentSProbabilities[i][j] > max && knownBoard[i][j] == -1) {
							ans[0] = i;
							ans[1] = j;
							max = currentSProbabilities[i][j];
							
						}
					}
				}
				this.turncount++;
				return ans;
		}
	}

	@Override
	public void describesmethod() {
		// TODO Auto-generated method stub

	}

}
