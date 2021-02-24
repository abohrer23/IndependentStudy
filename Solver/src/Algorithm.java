
public interface Algorithm {
    
    //returns the proper x and y coordinates where index 0 = x and index 1 = y
    public int[] choosetile(int[][] probabilities, knownBoard[][]);

    //Print out a description of the method to the console
    public void describesmethod();


}