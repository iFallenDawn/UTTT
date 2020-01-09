//Jordan Wang and Elijah Paul
//Ultimate TicTacToe Utility
//Spec: Handles all the basic game logic for Ultimate Tic Tac Toe
//Basic game logic by Jordan Wang
//AI Logic by Elijah Paul
import java.util.*;

public class UTTTUtility
{
	//Checks if any of the boards has a winner
	public static boolean isWin(int[][][][] board, int player)
	{
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if (isWin(board[i][j], player))
					return true;
			}
		}
		return false;
	}
	//Checks if a single board has a winner
	public static boolean isWin(int[][] board, int player)
	{
		for (int i = 0; i < board.length; i++)
		{
			int count = 0;
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == player)
					count++;
			}
			if (count == board.length)
				return true;
			count = 0;
			for (int j = 0; j < board[i].length; j++)
			{
				if (board[j][i] == player)
					count++;
			}
			if (count == board.length)
				return true;
			count = 0;
		}
		int count = 0;
		for (int i = 0; i < board.length; i++)
		{
			if (board[i][i] == player)
				count++;
		}
		if (count == board.length)
			return true;
		count = 0;
		for (int i = 0; i < board.length; i++)
		{
			if (board[board.length - i - 1][i] == player)
				count++;
		}
		if (count == board.length)
			return true;
		return false;
	}

	//Checks if there is a tie (don't know how you'd end up with a tie)
	public static boolean isTie(int[][][][] board, int active)
	{
		if (possibleMoves(board, active).size() == 0)
			return true;
		return false;
	}

	public static ArrayList<Integer> possibleMoves(int[][] board)
	{
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int k = 0; k < board.length; k++)
		{
			for (int l = 0; l < board[k].length; l++)
			{
				if (board[k][l] == 0)
					moves.add(powerSeries(new int[]{k, l}, board.length));
			}
		}
		return moves;
	}

	public static ArrayList<Integer> possibleMoves(int[][][][] board, int active)
	{
		ArrayList<Integer> moves = new ArrayList<Integer>();
	//	System.out.println("ACTIVE: " + active);
		if (active == -1)
		{
			for (int i = 0; i < board.length; i++)
			{
				for (int j = 0; j < board[i].length; j++)
				{
					for (int k = 0; k < board[i][j].length; k++)
					{
						for (int l = 0; l < board[i][j][k].length; l++)
						{
					//		System.out.println(i + "" + j + "" + k + "" + l + " " + powerSeries(new int[]{i, j, k, l}, board.length));
							if (board[i][j][k][l] == 0)
								moves.add(powerSeries(new int[]{i, j, k, l}, board.length));
						}
					}
				}
			}
		}
		else
		{
			int i = active / board.length;
			int j = active % board.length;
			for (int k = 0; k < board[i][j].length; k++)
			{
				for (int l = 0; l < board[i][j][k].length; l++)
				{
				//	System.out.println(active + " " + i + "" + j + "" + k + "" + l + " " + powerSeries(new int[]{i, j, k, l}, board.length));
					if (board[i][j][k][l] == 0)
						moves.add(powerSeries(new int[]{i, j, k, l}, board.length));
				}
			}
		}
		return moves;
	}

	public static int powerSeries(int[] arr, int power)
	{
		int sum = 0;
		for (int i = 0; i < arr.length; i++)
		{
			sum += arr[i] * Math.pow(power, arr.length - i - 1);
		}
		return sum;
	}

	public static double boardVal(int[][][][] board, int player)
	{
		double sum = 0;
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				double w1 = boardVal(board[i][j], 1);
				double w2 = boardVal(board[i][j], 2);
				if (!(w1 >= 1 && w2 >= 1))
				{
					sum += Math.sqrt(w1);
					sum -= Math.sqrt(w2);
				}
				if (w1 == 0 && w2 == 0)
				{
					sum += placementVal(board[i][j], 1);
					sum -= placementVal(board[i][j], 2);
				}
			}
		}
		if (isWin(board, 1))
			return 1000;
		if (isWin(board, 2))
			return -1000;
		return sum;
	}
	
	//Created a weighting system based off what squares a player has marked in each board
	//Decided to weigh the center the greatest with .12, corners at .8, and edges at .05.
	//Helps with rating
	public static double placementVal(int[][] grid, int player)
	{
		double val = 0;
		if (grid[0][0] == player)
			val += 0.08;
		if (grid[0][1] == player)
			val += 0.05;
		if (grid[0][2] == player)
			val += 0.08;
		if (grid[1][0] == player)
			val += 0.05;
		if (grid[1][1] == player)
			val += 0.12;
		if (grid[1][2] == player)
			val += 0.05;
		if (grid[2][0] == player)
			val += 0.08;
		if (grid[2][1] == player)
			val += 0.05;
		return val;
	}
	
	public static int boardVal(int[][] grid, int player)
	{
		int rows = grid.length;
		int cols = grid[0].length;
		int count = 0;
		int weight = 0;
		//horizontal to the right
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				if(grid[i][j] == player)
					count++;
				else
				{
					count = 0;
				}
				if(count == 2 && j + 1 < grid.length && grid[i][j+1] == 0)
				{
					return 1;
				}
			}
			count = 0;
		}
		//horizontal to the left
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				if(grid[i][grid.length-j-1] == player)
					count++;
				else
				{
					count = 0;
				}
				if(count == 2 && grid.length-j-2 >= 0 && grid[i][grid.length-j-2] == 0)
				{
					return 1;
				}
			}
			count = 0;
		}
		for (int i = 0; i < grid.length; i++)
		{
			if(grid[i][0] == player && grid[i][1] == 0 && grid[i][2] == player)
			{
				return 1;
			}
		}
		//verticals down
		for(int i = 0; i < cols; i++)
		{
			for(int j = 0; j < rows; j++)
			{
				if(grid[j][i] == player)
				{
					count++;
					//System.out.println("i: " + i + "\tj:" + j+ " IF " + count);
				}
				else
				{
					count = 0;
					//System.out.println("i: " + i + "\tj:" + j+ " else " + count);
				}
				if(count == 2 && j + 1 < grid.length && grid[j+1][i] == 0)
				{
					return 1;
				}
			}
			count = 0;
		}
		//verticals up
		for(int i = 0; i < cols; i++)
		{
			for(int j = 0; j < rows; j++)
			{
				if(grid[grid.length-j-1][i] == player)
				{
					count++;
					//System.out.println("i: " + i + "\tj:" + j+ " IF " + count);
				}
				else
				{
					count = 0;
					//System.out.println("i: " + i + "\tj:" + j+ " else " + count);
				}
				if(count == 2 && grid.length-j-2 >= 0 && grid[grid.length-j-2][i] == 0)
				{
					return 1;
				}
			}
			count = 0;
		}
		for (int i = 0; i < grid.length; i++)
		{
			if(grid[0][i] == player && grid[1][i] == 0 && grid[2][i] == player)
			{
				return 1;
			}
		}
		//top left to bottom right
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(grid[i][j] == player)
				{
					if(i + 1 < rows && j + 1 < cols && grid[i+1][j+1] == player)
					{
						if(i + 2 < rows && j + 2 < cols && grid[i+2][j+2] == 0)
						{
							return 1;
						}
					}
				}
			}
			count = 0;
		}
		//bottom right to top left
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(grid[i][j] == player)
				{
					if(i - 1 > -1 && j - 1 > -1 && grid[i-1][j-1] == player)
					{
						if(i - 2 > -1 && j - 2 > -1 && grid[i-2][j-2] == 0)
						{
							return 1;
						}
					}
				}
			}
			count = 0;
		}
		if (grid[0][0] == player && grid[1][1] == 0 && grid[2][2] == player)
			return 1;
		//right to bottom left
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(grid[i][j] == player)
				{
					if(i + 1 < rows && j - 1 > -1 && grid[i+1][j-1] == player)
					{
						if(i + 2 < rows && j - 2 > -1 && grid[i+2][j-2] == 0)
						{
							return 1;
						}
					}
				}
			}
			count = 0;
		}
		//diagonals bottom left to top right
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(grid[i][j] == player)
				{
					if(i - 1 > -1 && j + 1 < rows && grid[i-1][j+1] == player)
					{
						if(i - 2 > -1 && j + 2 < rows && grid[i-2][j+2] == 0)
						{
							return 1;
						}
					}
				}
			}
			count = 0;
		}
		if (grid[0][2] == player && grid[1][1] == 0 && grid[2][0] == player)
			return 1;
		return weight;
	}

	public static int[][][][] deepcopy(int[][][][] bo)
	{
		int dim = bo.length;
		int[][][][] deep = new int[dim][dim][dim][dim];
		for (int i = 0; i < dim; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				for (int k = 0; k < dim; k++)
				{
					for (int l = 0; l < dim; l++)
					{
						deep[i][j][k][l] = bo[i][j][k][l];
					}
				}
			}
		}
		return deep;
	}

	public static int getPlayer(int player, int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			player = 3 - player;
		}
		return player;
	}

	public static Node getWithMove(int[][][][] state, int player, int move, int depth)
	{
	//	System.out.print(move + " ");
		int i = ((int) (move / (Math.pow(state.length, 3)))) % state.length;
		int j = ((int) (move / (Math.pow(state.length, 2)))) % state.length;
		int k = ((int) (move / (Math.pow(state.length, 1)))) % state.length;
		int l = move % state.length;
		int[][][][] sta = deepcopy(state);
		sta[i][j][k][l] = player;
	//	System.out.println("PLAYER: " + player);
	//	System.out.println(move + " " + i + " "+ j + " " + k + " " + l);
		return new Node(sta, player, k * state.length + l, depth, move);
	}
}