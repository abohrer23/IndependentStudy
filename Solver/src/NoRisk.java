public class NoRisk implements Algorithm{

    //returns the proper x and y coordinates where index 0 = x and index 1 = y
    public int[] choosetile(int[][] probabilities, int knownBoard [][]){
        int [] ans = new int[2];
        for (int i = 0; i < probabilities.length; ++i) {
				for (int j = 0; j < probabilities[i].length; ++j) {
					if (probabilities[i][j] == 0 && knownBoard[i][j] == -1) {
						ans[0] = i;
						ans[1] = j;
						break; //take out if changing to lowrisk instead of no risk
					}
				}
			}

        return ans;
    }

    //Print out a description of the method to the console
    public void describesmethod(){
        System.out.println("No-Risk Method");


    }

}