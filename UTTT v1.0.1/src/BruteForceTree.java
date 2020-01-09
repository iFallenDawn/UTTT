//Elijah Paul
//Brute Force Tree
//Spec: 
import java.util.*;

public class BruteForceTree
{
	private Node root;
	private ArrayList<Node> allNodes;
	private static final int base = 5;
	private static int depth = base;
	private static final int scale = 2;
	private int move;
	private double rating;
	private int max;

	public BruteForceTree(int[][][][] state, int player, int active)
	{
		root = new Node(UTTTUtility.deepcopy(state), player, active, 0, 0);
	}

	public void compute()
	{
		allNodes = root.getAll();
		allNodes.add(root);

		for (int i = 0; i < allNodes.size(); i++)
		{
			Node n = allNodes.get(i);
			if (n.expanded() == false && n.depth < depth)
			{
				n.expand();
			}
			if ((n.depth < depth || n.expanded) && n.complete == false)
				allNodes.addAll(n.getChildren());
		}
		for (int i = 0; i < 25; i++)
			loop();
		for (Node n : allNodes)
			n.resetVal();
		rating = root.maxVal();
		move = root.getMove();

		if (allNodes.size() < 50000)
			depth++;
		if (allNodes.size() < 5000)
			depth++;
		if (allNodes.size() < 500)
			depth++;

	}

	public void loop()
	{
		if (root.complete == false)
		{
			Node next = root;
			for (int j = 0; j < scale; j++)
			{
				for (Node n : allNodes)
					n.resetVal();
				rating = root.maxVal();
				move = root.getMove();
				next = root;
				if (next.complete == false)
				{
					next = next.getChild(move);
					while (next.depth < next.maxDepth() - depth / 2)
					{
						if (next.complete == false)
						{
							next = next.randomGoodMove();
						}
						next.resetVal();
					}
					int maxDepth = next.maxDepth();
					ArrayList<Node> nextSet = next.getAll();
					for (int i = 0; i < nextSet.size(); i++)
					{
						Node n = nextSet.get(i);
						n.resetVal();
						if (n.expanded() == false && n.depth < maxDepth + depth / 2)
						{
							n.expand();
						}
						if (n.depth < maxDepth + depth / 2 && n.complete == false)
							nextSet.addAll(n.getChildren());
					}
				}
			}
			allNodes = root.getAll();
			max = root.maxDepth();
		}
	}

	public int bestMove()
	{
		compute();
		return move;
	}

	public int move()
	{
		return move;
	}

	public int nextMove()
	{
		for (Node n : allNodes)
		{
			if (n.depth == 1 && n.move == move)
				return n.getMove();
		}
		return 0;
	}

	public ArrayList<Integer> getAllNext()
	{
		for (Node n : allNodes)
		{
			if (n.depth == 1 && n.move == move)
				return n.allNextMoves();
		}
		return new ArrayList<Integer>();

	}

	public ArrayList<Integer> getAll()
	{
		return root.allNextMoves();
	}

	public static void reset()
	{
		depth = base;
	}

	public double getRating()
	{
		return rating;
	}

	public void makeMove(int move)
	{
		root.reduceDepth();
		root.expand();
		root = root.getChild(move);
	}

	public int min()
	{
		return root.minDepth();
	}

	public int max()
	{
		return max;
	}

	public Node getRoot() { return root; }


}