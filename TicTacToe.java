package fun;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe {
	private int[][] scoreMemo;
	private int[][] board;
	private int[][] Dboard = {{ 1, 1, 0 }, { 0, 2, 0 }, { 0, 0, 0 }};
	
	TicTacToe(int[][] b) {
		scoreMemo = new int[(int) Math.pow(3, 9)][];
		board = new int[3][3];
		board = b;
	}

	int[] minimax(int state, boolean computer, int height) {
		if (height == 0) {
			return new int[] { 0, 0, 0, -1 };
		}
		if (scoreMemo[state] != null) {
			return scoreMemo[state];
		}
		//TODO
		int result = gameResult(!computer);
		if (result != 99) { // game result found
			if (result == 100) { // forked
				if (!computer) {
					return new int[] { height, (int) 1e5, 0, -1 };
				} else {
					return new int[] { -height, 0, (int) 1e5, -1 };
				}
			} else if (result < 0) {
				if (computer) {
					return new int[] { height, 0, 0, -result - 1};
				} else {
					return new int[] { -height, 0, 0, -result - 1};
				}
			} else {
				int i = result / 3;
				int j = result % 3;
				int newState;
				if (computer) {
					board[i][j] = 1;
					newState = state + (int) Math.pow(3, i * 3 + j);
				} else {
					board[i][j] = 2;
					newState = state + (int) Math.pow(3, i * 3 + j) * 2; // setting i*3+j bit to 2
				}
				int[] newScore = minimax(newState, !computer, height - 1);
				scoreMemo[newState] = newScore;
				board[i][j] = 0;
				return new int[] {newScore[0], newScore[1], newScore[2], result};
			}
		}
		
		int nextMove = -1;
		if (computer) { // maximizing score
			int maxScore = -99, minScore = 99;
			int wins = 0, loss = 0;
			int winScore = 0, losScore = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) if (board[i][j] == 0) {
					board[i][j] = 1;
					if (arEq(board, Dboard)) {
						board[i][j] = 1;
					}
					int newState = state + (int) Math.pow(3, i * 3 + j);
					int[] newScore = minimax(newState, !computer, height - 1);
					scoreMemo[newState] = newScore;
					if (maxScore < newScore[0] ||
							maxScore == newScore[0] && winScore < newScore[1]) {
						nextMove = i * 3 + j;
						maxScore = newScore[0];
						winScore = newScore[1];
						wins = newScore[1];
					} else if (maxScore == newScore[0] && winScore == newScore[1]) {
						wins += newScore[1];
						nextMove = i * 3 + j;
					}
					if (minScore > newScore[0] ||
							minScore == newScore[0] && losScore < newScore[2]) {
						minScore = newScore[0];
						losScore = newScore[1];
						loss = newScore[2];
					} else if (maxScore == newScore[0] && losScore == newScore[2]) {
						loss += newScore[2];
					}
					board[i][j] = 0;
				}
			}
			if (maxScore > 0) {
				wins = (int) 1e6;
				loss = 0;
			}
			return new int[] { maxScore, wins / 10, loss, nextMove };
		} else {
			int maxScore = -99, minScore = 99;
			int wins = 0, loss = 0;
			int winScore = 0, losScore = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) if (board[i][j] == 0) {
					board[i][j] = 2;
					int newState = state + 2 * (int) Math.pow(3, i * 3 + j); // setting i*3+j bit to 2
					int[] newScore = minimax(newState, !computer, height - 1);
					scoreMemo[newState] = newScore;
					//TODO
					if (minScore > newScore[0] ||
							minScore == newScore[0] && winScore < newScore[2]) {
						minScore = newScore[0];
						nextMove = i * 3 + j;
						winScore = newScore[2];
						wins = newScore[2];
					} else if (minScore == newScore[0] && winScore == newScore[2]) {
						wins += newScore[2];
						nextMove = i * 3 + j;
					}
					if (maxScore < newScore[0] ||
							maxScore == newScore[0] && losScore < newScore[1]) {
						maxScore = newScore[0];
						losScore = newScore[1];
						loss = newScore[1];
					} else if (maxScore == newScore[0] && losScore == newScore[1]) {
						loss += newScore[1];
					}
					board[i][j] = 0;
				}
			}
			if (minScore < 0) {
				wins = (int) 1e6;
				loss = 0;
			}
			return new int[] { minScore, loss, wins / 10, nextMove };
		}
	}
	
	private int gameResult(boolean computer) {
		int x = 2, yCount = 0, xCount = 0; 
		if (computer) {
			x = 1;
		}
		int m = -1, move = -1, forked = 0;
		
		for (int i = 0; i < 3; i++) { // row
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == x) {
					xCount++;
				} else if (board[i][j] == 0) {
					m = i * 3 + j;
				} else {
					yCount++;
				}
			}
			if (yCount == 0 && xCount == 2) {
				forked++;
				move = m;
			}
			if (xCount == 0 && yCount >= 2) {
				return -m - 1 + (2 - yCount) * 99;
			}
			yCount = 0;
			xCount = 0;
		}
		
		for (int i = 0; i < 3; i++) { // column
			for (int j = 0; j < 3; j++) {
				if (board[j][i] == x) {
					xCount++;
				} else if (board[j][i] == 0) {
					m = j * 3 + i;
				} else {
					yCount++;
				}
			}
			if (yCount == 0 && xCount == 2) {
				forked++;
				move = m;
			}
			if (xCount == 0 && yCount >= 2) {
				return -m - 1 + (2 - yCount) * 99;
			}
			yCount = 0;
			xCount = 0;
		}
		
		for (int i = 0; i < 3; i++) { // left diagonal
			if (board[i][i] == x) {
				xCount++;
			} else if (board[i][i] == 0) {
				m = i * 4;
			} else {
				yCount++;
			}
		}
		if (yCount == 0 && xCount == 2) {
			forked++;
			move = m;
		}
		if (xCount == 0 && yCount >= 2) {
			return -m - 1 + (2 - yCount) * 99;
		}
		yCount = 0;
		xCount = 0;
		
		for (int i = 0; i < 3; i++) { // right diagonal 
			if (board[i][2 - i] == x) {
				xCount++;
			} else if (board[i][2 - i] == 0) {
				m = i * 2 + 2;
			} else {
				yCount++;
			}
		}
		if (yCount == 0 && xCount == 2) {
			forked++;
			move = m;
		}
		if (xCount == 0 && yCount >= 2) {
			return -m - 1 + (2 - yCount) * 99;
		}
		
		if (forked > 1) {
			return 100;
		} else if (forked == 1) {
			return move;
		} else {
			return 99;
		}
	}
	
	static int playGame() {
		Scanner in = new Scanner(System.in);
//		try {
//			in = new Scanner(new File("src/fun/input"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		boolean computer = false;
		if (in.next().equals("1")) {
			computer = true;
		}
		int[][] board = new int[3][3];
		TicTacToe tt = new TicTacToe(board);
		int height = 9;
		if (!computer) {
			int r = in.nextInt();
			int c = in.nextInt();
			tt.board[r][c] = 1;
			height--;
		}
		for (; height > 0; height -= 2) {
			int next = tt.minimax(tt.getState(), computer, height)[3];
			tt.board[next / 3][next % 3] = computer ? 1 : 2;
			tt.printBoard();
			if (tt.gameResult(!computer) < -90) {
				return 1;
			}
			if (height == 1) {
				break;
			}
			int r = in.nextInt();
			int c = in.nextInt();
			tt.board[r][c] = !computer ? 1 : 2;
			if (tt.gameResult(computer) < -90) {
				return 2;
			}
		}
		in.close();
		return 0;
	}
	
	public static void main(String[] args) {
		int result = playGame();
		System.out.println(result);
//		int[][] board = {{ 1, 1, 0 },
//						 { 2, 2, 2 },
//						 { 1, 0, 0 }};
//		TicTacToe tt = new TicTacToe(board);
//		System.out.println(tt.gameResult(true));
	}

	private void printBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == 0) {
					System.out.print("_ ");
				} else if (board[i][j] == 1) {
					System.out.print("X ");
				} else {
					System.out.print("O ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	private int getState() {
		int state = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				state += board[i][j] * (int) Math.pow(3, i * 3 + j);
			}
		}
		return state;
	}
	
	private static boolean arEq(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			if (!Arrays.equals(a[i], b[i])) return false;
		}
		return true;
	}
}