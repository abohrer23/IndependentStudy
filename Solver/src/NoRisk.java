public class NoRisk extends Strategy{

    //returns the proper x and y coordinates where index 0 = x and index 1 = y
    public int[] choosetile(double[][] probabilities, double[][] currentOProbabilities, double[][] currentSProbabilities, int knownBoard [][]){
        int [] ans = new int[2];
        for (int i = 0; i < probabilities.length; ++i) {
				for (int j = 0; j < probabilities[i].length; ++j) {
					if (probabilities[i][j] == 0 && knownBoard[i][j] == -1) {
						ans[0] = i;
						ans[1] = j;
						//Stop and give back output
						return ans;
					}
				}
			}
        return null;
        
    }

    //Print out a description of the method to the console
    public void describesmethod(){
        System.out.println("No-Risk Method");
        System.out.print("Only picks safe moves, if it cannot find any it will give up and return null, which is interpreted as a quit!");


    }

}