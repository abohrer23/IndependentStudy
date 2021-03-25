package genboards;

import java.util.Arrays;
import java.util.Scanner;
import java.io.File; 
import java.io.IOException;
import java.io.FileWriter;


public class MakeBoard {


	private int sample;

	//Default constructor 
	public MakeBoard() {
		this.sample = 0;
	}

	//TODO add level scaling
	public void makeboard(int quantity, int level) {

		int batch = 0;
		//Get Batch #
		try {
			Scanner scanner = new Scanner(new File("src/genboards/.boardconfig"));

			batch = Integer.parseInt(scanner.nextLine());

			scanner.close();
		}
		catch (Exception e){
			//Boardconfig not found - Make it!

			//File I/O code obtained from: https://www.w3schools.com/java/java_files_create.asp
			try {
				FileWriter myWriter = new FileWriter("src/genboards/.boardconfig");

				String defaultin = "0\n0";

				myWriter.write("0");


				myWriter.close();

				System.out.println(".boardconfig not found - created new boardconfig");

				Scanner scanner = new Scanner(new File("src/genboards/.boardconfig"));

				batch = Integer.parseInt(scanner.nextLine());

				scanner.close();


			} 
			catch (IOException ex) {
				System.out.println("The .boardconfig is incorrect. Delete the file and retry");
				e.printStackTrace();
			} 


		}


		//Pick from hardcoded options

		//Index 1: Options
		//Index 2: 0=Voltorbs, 1=2s, 2=3s

		int[][] masteralloc  = { {6, 3, 1} , {6, 0, 3} , {6, 5, 0}, {6, 2, 2}, {6, 4, 1}};

		//run for the number requested
		for(int ii=0;ii<quantity;ii++) {
			int[] fillinfo = masteralloc[(int)Math.floor(Math.random()*masteralloc.length)];



			int[][] board = new int[5][5];

			for(int i=0;i<board.length;i++){
				Arrays.fill(board[i], -1);
			}


			//Bogo insert

			//Put in n voltorbs
			for(int i=0;i<fillinfo[0];i++){
				int proposed[] = {-1, -1};
				do {
					proposed[0] = (int)(Math.random()*5.0);
					proposed[1] = (int)(Math.random()*5.0);

				} while(board[proposed[0]][proposed[1]] != -1);

				board[proposed[0]][proposed[1]] = 0;

			}

			//Put in m 2s
			for(int i=0;i<fillinfo[1];i++){
				int proposed[] = {-1, -1};
				do {
					proposed[0] = (int)(Math.random()*5.0);
					proposed[1] = (int)(Math.random()*5.0);

				} while(board[proposed[0]][proposed[1]] != -1);
				System.out.print("Insertion of 2 #" + i);
				board[proposed[0]][proposed[1]] = 2;
			}

			//Put in k 3s
			for(int i=0;i<fillinfo[2];i++){
				int proposed[] = {-1, -1};
				do {
					proposed[0] = (int)(Math.random()*5.0);
					proposed[1] = (int)(Math.random()*5.0);

				} while(board[proposed[0]][proposed[1]] != -1);

				board[proposed[0]][proposed[1]] = 3;
			}

			//Fill in the rest with 1s

			for(int i=0;i<board.length;i++){
				for(int j=0;j<board.length;j++){
					if(board[i][j] == -1){
						board[i][j] = 1;
					}
				}
			}



			writefile(board, batch);
		}

		//update the batch #


		try {

			System.out.println("Bump .boardconfig");

			batch++;

			FileWriter myWriter = new FileWriter("src/genboards/.boardconfig");

			myWriter.write(Integer.toString(batch));


			myWriter.close();


		} 
		catch (IOException ex) {
			System.out.println("The .boardconfig is incorrect. Delete the file and retry");
			ex.printStackTrace();
		} 



		return;
	}

	private void writefile(int[][] board, int batch){



		//Boardconfig not found - Make it


		String content = "5\n";

		//concat board entries onto string
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				content += board[i][j] + " ";
			}
			content += "\n";
		}

		//File I/O code obtained from: https://www.w3schools.com/java/java_files_create.asp
		try {
			String destination = "support_src/boards/resources/gennedboard-" + batch + "-" + this.sample + " .txt";
			FileWriter myWriter = new FileWriter(destination);


			myWriter.write(content);

			this.sample++;

			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} 


		return;

	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MakeBoard m = new MakeBoard();
		//quantity,level
		m.makeboard(1, 0);

	}

}

/* From Bulbagarden: Voltorb tile possibilites
Level 1
3 	1 	6 	24
0 	3 	6 	27
5 	0 	6 	32
2 	2 	6 	36
4 	1 	6 	48
Level 2
1 	3 	7 	54
6 	0 	7 	64
3 	2 	7 	72
0 	4 	7 	81
5 	1 	7 	96
Level 3
2 	3 	8 	108
7 	0 	8 	128
4 	2 	8 	144
1 	4 	8 	162
6 	1 	8 	192
Level 4
3 	3 	8 	216
0 	5 	8 	243
8 	0 	10 	256
5 	2 	10 	288
2 	4 	10 	324
Level 5
7 	1 	10 	384
4 	3 	10 	432
1 	5 	10 	486
9 	0 	10 	512
6 	2 	10 	576
Level 6
3 	4 	10 	648
0 	6 	10 	729
8 	1 	10 	768
5 	3 	10 	864
2 	5 	10 	972
Level 7
7 	2 	10 	1152
4 	4 	10 	1296
1 	6 	13 	1458
9 	1 	13 	1536
6 	3 	10 	1728
Level 8
0 	7 	10 	
8 	2 	10 	
5 	4 	10 	
2 	6 	10 	
7 	3 	10 	
 */