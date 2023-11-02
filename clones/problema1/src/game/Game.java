package game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class Game {

	public void play() {
		int player = 0;
		String[][] board = {{"⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬜"},
				{"⬜", "⬜", "⬜", "⬛", "⬛", "⬛", "⬜", "⬜"},
				{"⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬛", "⬜"},
				{"⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬛", "⬜"},
				{"⬜", "⬜", "⬜", "⬜", "⬜", "⬛", "⬜", "⬜"},
				{"⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬛", "⬜"},
				{"⬜", "⬜", "⬜", "⬜", "⬜", "⬜", "⬛", "⬜"},
				{"⬜", "⬜", "⬜", "⬛", "⬛", "⬛", "⬜", "⬜"}};

		for(String[] row : board) {
			for(String elem : row)
				System.out.print(" " + elem + " ");
			System.out.println("");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		for(int i=0; i<board[0].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[1].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[2].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[3].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[4].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[5].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[6].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[7].length; i++) board[0][i] = "⬜";
		board[1][3] = "⬛"; board[1][4] = "⬛"; board[1][5] = "⬛";
		board[2][2] = "⬛"; board[2][5] = "⬛"; 
		board[3][6] = "⬛"; 
		board[4][5] = "⬛"; 
		board[5][4] = "⬛"; 
		board[6][3] = "⬛"; 
		board[7][2] = "⬛"; board[7][3] = "⬛"; board[7][4] = "⬛"; board[7][5] = "⬛"; board[7][6] = "⬛";

		for(String[] row : board) {
			for(String r : row)
				System.out.print(" " + r + " ");
			System.out.println("");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		for(int i=0; i<board[0].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[1].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[2].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[3].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[4].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[5].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[6].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[7].length; i++) board[0][i] = "⬜";
		board[1][4] = "⬛"; 
		board[2][3] = "⬛"; board[2][3] = "⬛"; 
		board[3][4] = "⬛"; 
		board[4][4] = "⬛"; 
		board[5][4] = "⬛"; 
		board[6][4] = "⬛"; 
		board[7][2] = "⬛"; board[7][3] = "⬛"; board[7][4] = "⬛"; board[7][5] = "⬛"; 

		for(String[] row : board) {
			for(String r : row)
				System.out.print(" " + r + " ");
			System.out.println("");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		for(int i=0; i<board[0].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[1].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[2].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[3].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[4].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[5].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[6].length; i++) board[0][i] = "⬜";
		for(int i=0; i<board[7].length; i++) board[0][i] = "⬜";

		boolean playing = true;
		while(playing) {
			try {
				Random r = new Random();
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				int newCar = r.nextInt(8);
				board[0][newCar] = "🚙";
				board[7][player] = "🚗";
				
				for(String[] row : board) {
					for(String a : row)
						System.out.print(" " + a + " ");
					System.out.println("");
				}

				//input
				String key = br.readLine();

				if(key.equals("q")) {
					playing = false;
					break;
				} else if(key.equals("a") && player > 0) {
					board[7][player] = "⬜";
					player -= 1;
				} else if(key.equals("d") && player < 7) {
					board[7][player] = "⬜";
					player += 1;
				}
				if(board[7][player].equals("🚙") || board[6][player].equals("🚙")) {
					playing = false;
					System.out.println("Perdiste!");

					//move cars down
					for(int i=0; i<7; i++) 
						board[7-i] = board[6-i];

					for(int i=0; i<board[0].length; i++) board[0][i] = "⬜";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		Game game = new Game();
		game.play();
	}
}
