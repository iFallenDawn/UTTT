//Elijah Paul
//Brute Force Tree
//Spec: 
import java.util.*;

public class Node
{
	private ArrayList<Node> children;
	private int[][][][] state;
	private int active;
	private int player;
	public double value;
	private double maxVal = Integer.MAX_VALUE;
	public int depth;
	public boolean complete, expanded;
	public int move;

	public Node(int[][][][] state, int player, int active, int depth, int move)
	{
	//	System.out.println(player);
		this.state = state;
		this.active = active;
		this.player = player;
		this.depth = depth;
		this.move = move;
		value = UTTTUtility.boardVal(state, player);
		if (UTTTUtility.isWin(state, player))
		{
		//	System.out.print("WIN AT DEPTH: " + depth);
			if (player == 1)
				value = 1000;
			else
				value = -1000;
			complete = true;
		}
		else if (UTTTUtility.isTie(state, active))
		{
			complete = true;
		}
	}

	public void expand()
	{
		if (!complete && !expanded)
		{
			children = new ArrayList<Node>();
			expanded = true;
			ArrayList<Integer> moves = UTTTUtility.possibleMoves(state, active);
		//	System.out.println("MOVES: " + moves);
			for (int m : moves)
			{
				Node n = UTTTUtility.getWithMove(state, 3 - player, m, depth + 1);
				children.add(n);
			/*	if (Math.abs(n.value) > 100)
				{
					children = new ArrayList<Node>();
					children.add(n);
					break;
				} */
			}
		}
	}

	public double maxVal()
	{
		if (maxVal != Integer.MAX_VALUE)
			return maxVal;
		if (Math.abs(value) == 1000)
			return value / (depth + 1);
		if (children == null)
			return value;
		double max = children.get(0).maxVal();
		if (player == 2)
		{
			for (Node n : children)
			{
				double val = n.maxVal();
				if (val > max)
					max = val;
			}
		}
		else if (player == 1)
		{
			for (Node n : children)
			{
				double val = n.maxVal();
				if (val < max)
					max = val;
			}
		}
		maxVal = max;
		return max;
	}

	public int getMove()
	{
		expand();
		double max = children.get(0).maxVal();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		indices.add(0);
		int index = 0;
		if (player == 2)
		{
			for (int i = 1; i < children.size(); i++)
			{
				double val = children.get(i).maxVal();
				if (val > max)
				{
					indices = new ArrayList<Integer>();
					indices.add(i);
					max = val;
					index = i;
				}
				else if (val == max)
				{
					indices.add(i);
				}
			}
		}
		else if (player == 1)
		{
			for (int i = 1; i < children.size(); i++)
			{
				double val = children.get(i).maxVal();
				if (val < max)
				{
					indices = new ArrayList<Integer>();
					indices.add(i);
					max = val;
					index = i;
				}
				else if (val == max)
				{
					indices.add(i);
				}
			}
		}
		index = (int) (Math.random() * indices.size());
		return children.get(indices.get(index)).move;
	}

	public ArrayList<Integer> allNextMoves()
	{
		double max = children.get(0).maxVal();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		indices.add(0);
		if (player == 2)
		{
			for (int i = 1; i < children.size(); i++)
			{
				double val = children.get(i).maxVal();
				if (val > max)
				{
					indices = new ArrayList<Integer>();
					indices.add(i);
					max = val;
				}
				else if (val == max)
				{
					indices.add(i);
				}
			}
		}
		else if (player == 1)
		{
			for (int i = 1; i < children.size(); i++)
			{
				double val = children.get(i).maxVal();
				if (val < max)
				{
					indices = new ArrayList<Integer>();
					indices.add(i);
					max = val;
				}
				else if (val == max)
				{
					indices.add(i);
				}
			}
		}
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int index : indices)
		{
			moves.add(children.get(index).move);
		}
		return moves;

	}

	public Node randomGoodMove()
	{
		expand();

		ArrayList<Double> maxVals = new ArrayList<Double>();
		if (player == 2)
		{
			for (Node n : children)
			{
				if (n.maxVal() > 10)
					maxVals.add(0.0);
				else
					maxVals.add(n.maxVal());
			}
		}
		else
		{
			for (Node n : children)
			{
				if (n.maxVal() < 10)
					maxVals.add(0.0);
				else
					maxVals.add(-n.maxVal());
			}
		}
		double sum = 0;
		for (int i = 0; i < maxVals.size(); i++)
		{
			maxVals.set(i, Math.pow(2, maxVals.get(i)));
			sum += maxVals.get(i);
		}
		for (int i = 0; i < maxVals.size(); i++)
		{
			maxVals.set(i, maxVals.get(i) / sum);
		}
		//System.out.println(maxVals);
		double choice = Math.random();
		for (int i = 0; i < maxVals.size(); i++)
		{
			choice -= maxVals.get(i);
			if (choice <= 0)
				return children.get(i);
		}
		return null;
	}

	public ArrayList<Node> getChildren()
	{
		return children;
	}

	public boolean expanded() { return expanded; }

	public String toString()
	{
		String result = active + "\t";
		result += player + "\t";
		result += value + "\t";
		result += depth + "\t";
		result += move + "\n";
		return result;
	}

	public ArrayList<Node> getAll()
	{
		ArrayList<Node> result = new ArrayList<Node>();
		result.add(this);
		if (children != null)
		{
			for (Node n : children)
				result.addAll(n.getAll());
		}
		return result;
	}

	public Node getChild(int move)
	{
		for (Node n : children)
		{
			if (n.move == move)
				return n;
		}
		return null;
	}

	public void resetVal()
	{
		maxVal = Integer.MAX_VALUE;
	}

	public int maxDepth()
	{
	//	if (complete)
	//		return Integer.MAX_VALUE;
		if (children == null)
			return depth;
		int max = depth;
	//	if (depth >= 20)
	//		System.out.print(depth);
		for (Node c : children)
		{
			int d = c.maxDepth();
			if (d > max)
				max = d;
		}
	//	if (depth >= 20)
	//		System.out.print(max);
		return max;
	}

	public void reduceDepth()
	{
		depth--;
		if (children != null)
		{
			for (Node n : children)
				n.reduceDepth();
		}
	}

	public int minDepth()
	{
		if (complete)
			return Integer.MAX_VALUE;
		int min = Integer.MAX_VALUE;
		if (children != null)
		{
			for (Node n : children)
			{
				int nMin = n.minDepth();
				if (nMin < min)
					min = nMin;
			}
		}
		else
			return maxDepth();
		return min;
	}
}