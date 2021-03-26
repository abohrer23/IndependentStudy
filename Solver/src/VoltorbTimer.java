
import java.util.LinkedList;
import java.util.List;

import genboards.MakeBoard;
import timing.utils.GenSizes;
import timing.Algorithm;
import timing.ExecuteAlgorithm;
import timing.InputProvider;
import timing.InputSpec;
import timing.output.Output;
import timing.utils.IntArrayGenerator;

public class VoltorbTimer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String arg = "minrisk -auto -global";
		
		for(int i=1; i < 50000; i=i*2) {
			
			MakeBoard m = new MakeBoard();
			
			int size = 1 + 2*i;
			//int batch = m.makeboard( (int) Math.pow( 5.0, (double)i ), 0) - 1;
			
			int batch = m.makeboard( size, 0) - 1;
			
			System.out.println(arg + batch);
			
			Main main = new Main(arg + batch);
		}


		}



}
