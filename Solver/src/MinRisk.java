public class MinRisk implements Algorithm{

	boolean autoplay; //Bool added for future proofing large simulations
	
    //returns the proper x and y coordinates where index 0 = x and index 1 = y
    public int[] choosetile(double[][] probabilities, int knownBoard [][]){
        int [] ans = new int[2];
        
        double minprobabaility = 1.0;
        
        for (int i = 0; i < probabilities.length; ++i) {
				for (int j = 0; j < probabilities[i].length; ++j) {
					if (probabilities[i][j] < minprobabaility && knownBoard[i][j] == -1) {
						
						ans[0] = i;
						ans[1] = j;
						
						minprobabaility = probabilities[i][j]; 
						
					}
				}
			}
        return ans;
        
    }

    //Print out a description of the method to the console
    public void describesmethod(){
        System.out.println("Min-Risk Method");
        System.out.print("Only picks the least likely to get blown up move");


    }

}