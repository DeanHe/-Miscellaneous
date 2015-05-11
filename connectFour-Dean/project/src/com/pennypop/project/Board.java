package com.pennypop.project;

/**
 * The Game board for connect-four, Currently there is no AI of computer player
 * involved, maybe add in the future currently the line size required to win is
 * four could be changed in future
 * 
 * @author Dean He
 */
public class Board {

	final int NUMPLAYER = 2;
	final int NUMROW;
	final int NUMCOL;
	int CurrentPlayer; // identifies the current player
	int grid[][]; // represents the grid of the game board
	final int Empty = -1; // empty space on the game board

	public Board(int circlesOnHeight, int circlesOnWidth) {
		NUMROW = circlesOnHeight;
		NUMCOL = circlesOnWidth;
		grid = new int[NUMROW][NUMCOL];
		EmptyGrid(grid);
	}

	public void setGrid(int index, int currentPlayer) {
		int rowAt = NUMCOL - 1 - index / NUMCOL;
		int colAt = index % NUMCOL;
		System.out.println(rowAt + " " + colAt);
		grid[rowAt][colAt] = currentPlayer;
	}

	public boolean checkWin() {
		// horizontal rows
		for (int row = 0; row < NUMROW; row++) {
			for (int col = 0; col < NUMCOL - 3; col++) {
				if (grid[row][col] > 0 && grid[row][col] == grid[row][col + 1]
						&& grid[row][col] == grid[row][col + 2]
						&& grid[row][col] == grid[row][col + 3]) {
					return true;
				}
			}
		}

		// vertical columns
		for (int col = 0; col < NUMCOL; col++) {
			for (int row = 0; row < NUMROW - 3; row++) {
				if (grid[row][col] > 0 && grid[row][col] == grid[row + 1][col]
						&& grid[row][col] == grid[row + 2][col]
						&& grid[row][col] == grid[row + 3][col]) {
					return true;
				}
			}
		}

		// diagonal lower left to upper right
		for (int row = 0; row < NUMROW - 3; row++) {
			for (int col = 0; col < NUMCOL - 3; col++) {
				if (grid[row][col] > 0 && grid[row][col] == grid[row + 1][col + 1]
						&& grid[row][col] == grid[row + 2][col + 2]
						&& grid[row][col] == grid[row + 3][col + 3]) {
					return true;
				}
			}
		}

		// diagonal upper left to lower right
		for (int row = NUMROW - 1; row >= 3; row--) {
			for (int col = 0; col < NUMCOL - 3; col++) {
				if (grid[row][col] > 0 && grid[row][col] == grid[row - 1][col + 1]
						&& grid[row][col] == grid[row - 2][col + 2]
						&& grid[row][col] == grid[row - 3][col + 3]) {
					return true;
				}
			}
		}

		return false;
	}

	public void EmptyGrid(int[][] board) {
		for (int i = 0; i < NUMROW; i++) {
			for (int j = 0; j < NUMCOL; j++) {
				board[i][j] = Empty;
			}
		}
	}
}
