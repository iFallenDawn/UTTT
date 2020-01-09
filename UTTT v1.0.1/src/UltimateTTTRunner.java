//Jordan Wang and Elijah Paul
//Ultimate Tic Tac Toe
//Spec: Creates a 3x3 grid of Tic Tac Toe boards for the user to play on and handles user input.
//Primarily handled by Jordan Wang

import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.*; // JGui stuff
import java.util.*;
import javax.swing.Timer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

public class UltimateTTTRunner extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Double> ratingPoints = new ArrayList<Double>();
	private ArrayList<Double> turnList = new ArrayList<Double>();
	private boolean hints = false;
	//The 'mark' booleans are for if the player wants to mark certain moves as good or bad.
	private boolean markGray = false;
	private boolean markRed = false;
	private boolean markYellow = false;
	private boolean player1Turn = true;
	private final Color redMarker = new Color(255,100,100);
	private final Color yellowMarker = new Color(255,255,153);
	private final Color grayMarker = Color.GRAY.brighter();
	private final Font standard = new Font("Calibri", Font.BOLD, 12);
	private Timer tmrThinker;
	private TimerListener tmrListener;
//	private UltimateTTT game; This was when the original 2 player version was made.
	private int dim = 3;
	private int turns = 0;
	private JButton[][][][] singleGrid = new JButton[dim][dim][dim][dim];
	private JButton btnInstructions, btnMarkgray, btnMarkred, btnMarkyellow, btnHint, btnReset, btnGraph;
	//biggerboard row, biggerboard column, board row, board column - how the 4d JButton array is instantiated
	private JPanel[][] allGames = new JPanel[dim][dim];
	private JPanel pnlInfo = new JPanel();
	private JTextArea jtaMoves, jtaRating;
	private JScrollPane scroll;
	private ButtonListener listener;
	private int active = -1;
	private JLabel lblInstructions;
	private BruteForceTree bft;
	private int[][][][] game = new int[dim][dim][dim][dim];
	private int count = 0;
	//The JFreeChart library is used to create a graph at the end of the game.
	private JFreeChart chart;
	private boolean win = false;
	
	public UltimateTTTRunner()
	{
		super("Wang and Paul Ultimate TicTacToe");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		JPanel cp = new JPanel();
	//	ImageIcon image = new ImageIcon("instructions.jpg");
		ImageIcon image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("instructions.jpg")));
		lblInstructions = new JLabel(image);
		cp.setLayout(new GridLayout(dim, dim));
	//	game = new UltimateTTT(dim, dim);
		game = new int[dim][dim][dim][dim];
		listener = new ButtonListener();
		turnList.add(0.0);
		ratingPoints.add(0.0);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 10);

		//adding functionality/looks to the buttons
		btnGraph = new JButton("Graph of Ratings");
		btnGraph.setFont(standard);
		btnGraph.addActionListener(listener);
		btnGraph.setEnabled(false);
		
		btnInstructions = new JButton("Instructions");
		btnInstructions.setFont(standard);
		btnInstructions.addActionListener(listener);

		btnMarkgray = new JButton("Mark Gray");
		btnMarkgray.setFont(standard);
		btnMarkgray.addActionListener(listener);

		btnMarkred = new JButton("Mark Red");
		btnMarkred.addActionListener(listener);
		btnMarkred.setPreferredSize(new Dimension(98,20));
		btnMarkred.setFont(standard);

		btnMarkyellow = new JButton("Mark Yellow");
		btnMarkyellow.addActionListener(listener);
		btnMarkyellow.setPreferredSize(new Dimension(103,20));
		btnMarkyellow.setFont(standard);

		btnHint = new JButton("Enable Hints");
		btnHint.addActionListener(listener);
		btnHint.setPreferredSize(new Dimension(103,20));
		btnHint.setFont(standard);

		btnReset = new JButton("Play again?");
		btnReset.addActionListener(listener);
		btnReset.setPreferredSize(new Dimension(98,25));
		btnReset.setEnabled(false);
		btnReset.setFont(standard);

		//scrapped idea
//		btnUndo = new JButton("Undo");
//		btnUndo.addActionListener(listener);
//		btnUndo.setPreferredSize(new Dimension(98,25));
//		btnUndo.setEnabled(false);
//		btnUndo.setFont(standard);

		//JPanels to help with layout, to make it somewhat look nice
		pnlInfo.setLayout(new BorderLayout());

		JPanel pnlEnd = new JPanel();
		pnlEnd.setLayout(new BorderLayout());
//		pnlEnd.add(btnUndo, BorderLayout.SOUTH);
		pnlEnd.add(btnReset, BorderLayout.NORTH);

		JPanel pnlMarkers = new JPanel();
		pnlMarkers.setLayout(new BorderLayout());
		pnlMarkers.add(btnMarkred, BorderLayout.WEST);
		pnlMarkers.add(btnMarkgray, BorderLayout.CENTER);
		pnlMarkers.add(btnMarkyellow, BorderLayout.EAST);

		pnlInfo.add(pnlEnd, BorderLayout.WEST);
		pnlInfo.add(pnlMarkers, BorderLayout.NORTH);
		pnlInfo.add(btnInstructions, BorderLayout.SOUTH);
		pnlInfo.add(btnHint, BorderLayout.EAST);
		pnlInfo.add(btnGraph, BorderLayout.CENTER);
		
		//nested for loops to deal with the 4D jbutton array and add them to the GUI
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				allGames[pRow][pCol] = new JPanel();
				allGames[pRow][pCol].setLayout(new GridLayout(dim, dim));
				allGames[pRow][pCol].setBorder(border);
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
						singleGrid[pRow][pCol][btnRow][btnCol] = new JButton();
						singleGrid[pRow][pCol][btnRow][btnCol].setPreferredSize(new Dimension(20, 20));
						singleGrid[pRow][pCol][btnRow][btnCol].addActionListener(listener);
						allGames[pRow][pCol].add(singleGrid[pRow][pCol][btnRow][btnCol]);
					}
				}
				cp.add(allGames[pRow][pCol]);
			}
		}
		
		//textbox to store the moves made
		jtaMoves = new JTextArea(20, 25);
		jtaMoves.setFont(new Font("Calibri", Font.PLAIN, 14));
		jtaMoves.append("Turns\tP1\tP2");
		jtaMoves.setEditable(false);
		jtaMoves.setLineWrap(true);

		scroll = new JScrollPane(jtaMoves);

		//displays how well the game thinks you're doing
		jtaRating = new JTextArea(1, 1);
		jtaRating.setFont(new Font("Calibri", Font.PLAIN, 18));
		jtaRating.append("Rating: ");
		jtaRating.setEditable(false);

		BruteForceTree.reset();
		//adding everything to the GUI
		c.add(cp, BorderLayout.CENTER);
		c.add(scroll, BorderLayout.EAST);
		c.add(jtaRating, BorderLayout.NORTH);
		c.add(pnlInfo, BorderLayout.SOUTH);
		setSize(700, 500);
		setVisible(true);

		tmrListener = new TimerListener();

		tmrThinker = new Timer(10, tmrListener);
		tmrThinker.stop();
	}
	public static void main(String[] args)
	{
	//	System.out.println(UTTTUtility.powerSeries(new int[]{1, 2, 2}, 3));
		new UltimateTTTRunner();
	}

	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//responses based off which butotn is pressed
			clearBack();
			Object source = event.getSource();
			if(source == btnInstructions)
			{
				JOptionPane.showMessageDialog(null, lblInstructions, "About", JOptionPane.PLAIN_MESSAGE, null);
			}
			else if(source == btnMarkgray)
			{
				markRed = false;
				markYellow = false;
				markGray = !markGray;
				if(markGray)
				{
					enableAllButtons();
					btnMarkgray.setText("Enabled");
					btnMarkred.setText("Mark Red");
					btnMarkyellow.setText("Mark Yellow");
				}
				else if (active != -1)
				{
					disableButtons();
					btnMarkgray.setText("Mark Gray");
					enableButtons(active / 3, active % 3);
				}
				else
				{
					btnMarkgray.setText("Mark Gray");
					enableAllButtons();
				}
				//System.out.println("MarkGray: " + markGray);
			}
			else if(source == btnMarkred)
			{
				markRed = !markRed;
				markYellow = false;
				markGray = false;
				if(markRed)
				{
					enableAllButtons();
					btnMarkgray.setText("Mark Gray");
					btnMarkred.setText("Enabled");
					btnMarkyellow.setText("Mark Yellow");
				}
				else if (active != -1)
				{
					btnMarkred.setText("Mark Red");
					disableButtons();
					enableButtons(active / 3, active % 3);
				}
				else
				{
					btnMarkred.setText("Mark Red");
					enableAllButtons();
				}
				//System.out.println("MarkRed: " + markRed);
			}
			else if(source == btnMarkyellow)
			{
				markRed = false;
				markYellow = !markYellow;
				markGray = false;
				if(markYellow)
				{
					enableAllButtons();
					btnMarkgray.setText("Mark Gray");
					btnMarkred.setText("Mark Red");
					btnMarkyellow.setText("Enabled");
				}
				else if (active != -1)
				{
					btnMarkyellow.setText("Mark Yellow");
					disableButtons();
					enableButtons(active / 3, active % 3);
				}
				else
				{
					btnMarkyellow.setText("Mark Yellow");
					enableAllButtons();
				}
				//System.out.println("MarkYellow: " + markYellow);
			}
			else if(source == btnHint)
			{
				if (bft != null && !win)
				{
					btnHint.setEnabled(false);
					hints = true;
					//BruteForceTree bft = new BruteForceTree(game.getState(), 2, active);
					setNextMoves(bft.getRoot().getChildren());
				}
			}
			else if(source == btnReset)
			{
				dispose();
				new UltimateTTTRunner();
			}
//			else if(source == btnUndo)
//			{
//				//discontinued
//			}
			else if(source == btnGraph)
			{
				drawGraph();
			}
			else
			{
				if(markGray || markRed || markYellow)
				{
					for(int pRow = 0; pRow < dim; pRow++)
					{
						for(int pCol = 0; pCol < dim; pCol++)
						{
							for(int btnRow = 0; btnRow < dim; btnRow++)
							{
								for(int btnCol = 0; btnCol < dim; btnCol++)
								{
									if(source == singleGrid[pRow][pCol][btnRow][btnCol])
									{
										if(singleGrid[pRow][pCol][btnRow][btnCol].getBackground() != grayMarker && markGray == true)
											singleGrid[pRow][pCol][btnRow][btnCol].setBackground(grayMarker);
										else if(singleGrid[pRow][pCol][btnRow][btnCol].getBackground() != redMarker && markRed == true)
											singleGrid[pRow][pCol][btnRow][btnCol].setBackground(redMarker);
										else if(singleGrid[pRow][pCol][btnRow][btnCol].getBackground() != yellowMarker && markYellow == true)
											singleGrid[pRow][pCol][btnRow][btnCol].setBackground(yellowMarker);
										else
											singleGrid[pRow][pCol][btnRow][btnCol].setBackground(new JButton().getBackground());
									}
								}
							}
						}
					}
				}
				else
				{
					for(int pRow = 0; pRow < dim; pRow++)
					{
						for(int pCol = 0; pCol < dim; pCol++)
						{
							for(int btnRow = 0; btnRow < dim; btnRow++)
							{
								for(int btnCol = 0; btnCol < dim; btnCol++)
								{
									if(singleGrid != null && source == singleGrid[pRow][pCol][btnRow][btnCol])
									{
										singleGrid[pRow][pCol][btnRow][btnCol].removeActionListener(this);
										turns++;
										jtaMoves.append("\n" + turns + "\t" + tableMoves(pRow, pCol, btnRow, btnCol));
										if(player1Turn == true)
										{
										//	game.updateBoards(pRow, pCol, btnRow, btnCol, 1);
											game[pRow][pCol][btnRow][btnCol] = 1;
											for(int i = 0; i <= count; i++)
											{
												turnList.add(turns + i*1.0/(count+1));
											}
											if (turns != 1)
												bft.makeMove(UTTTUtility.powerSeries(new int[]{pRow, pCol, btnRow, btnCol}, singleGrid.length));
											updateGUI();
											//if(game.checkWin(1))
											if(UTTTUtility.isWin(game, 1))
											{
												win = true;
												ratingPoints.add(10.);
												JOptionPane.showMessageDialog (null, "Player 1 won!");
												btnReset.setEnabled(true);
												tmrThinker.removeActionListener(tmrListener);
												tmrThinker.stop();
												disableAllButtons();
												btnGraph.setEnabled(true);
											}
										//	player1Turn = game.playerTurn(player1Turn);
											player1Turn = !player1Turn;
										}
									/*	else
										{
											game.updateBoards(pRow, pCol, btnRow, btnCol, 2);
											updateGUI();
											if(game.checkWin(2))
											{
												JOptionPane.showMessageDialog (null, "Player 2 won!");
												dispose();
												new UltimateTTTRunner();
											}
											player1Turn = game.playerTurn(player1Turn);
										}*/
										disableButtons();
									//	if(game.getGames()[btnRow][btnCol].checkDraw() || game.getGames()[pRow][pCol].checkDraw())
										active = 3 * btnRow + btnCol;
										if(UTTTUtility.possibleMoves(game, active).isEmpty() && !win)
										{
											active = -1;
											enableAllButtons();
										}
										else if (!win)
										{
											enableButtons(btnRow, btnCol);
										}
									}
								}
							}
						}
					}
					tmrThinker.stop();
					if (!win)
					{
						if (bft == null)
						bft = new BruteForceTree(game, 1, active);
					//	bft = new BruteForceTree(game.getState(), 1, active);
						int move = bft.bestMove();
					//	System.out.println(move);
						int btnCol = move % 3;
						move /= 3;
						int btnRow = move % 3;
						move /= 3;
						int pCol = move % 3;
						move /= 3;
						int pRow = move % 3;
					//	game.updateBoards(pRow, pCol, btnRow, btnCol, 2);
						game[pRow][pCol][btnRow][btnCol] = 2;
						jtaMoves.append("\t" + tableMoves(pRow, pCol, btnRow, btnCol));
						updateGUI();
						singleGrid[pRow][pCol][btnRow][btnCol].removeActionListener(this);
					//	if(game.checkWin(2))
						if(UTTTUtility.isWin(game, 2))
						{
							win = true;
							JOptionPane.showMessageDialog (null, "Player 2 won!");
							btnReset.setEnabled(true);
							tmrThinker.removeActionListener(tmrListener);
							tmrThinker.stop();
							disableAllButtons();
							btnGraph.setEnabled(true);
						}
						else
						{
						//	player1Turn = game.playerTurn(player1Turn);
							player1Turn = !player1Turn;
							disableButtons();
							enableButtons(btnRow, btnCol);
							active = 3 * btnRow + btnCol;
						}
						double rating = bft.getRating();
						jtaRating.setText("Rating: ");
						if(rating > 10)
							ratingPoints.add(10.0);
						else if(rating < -10)
							ratingPoints.add(-10.0);
						else
							ratingPoints.add(rating);
						if (Math.abs(rating) > 10)
							jtaRating.append("#");
						if (rating > 0)
							jtaRating.append("+");
						else if (rating < 0)
							jtaRating.append("-");
						rating = Math.abs(rating);
						if (rating > 10)
							jtaRating.append("" + ((((int) (Math.round(1000 / rating) + 2)) / 2) - 1));
						else
							jtaRating.append(String.format("%.2f", rating));
						int min = bft.min();
						int max = bft.max();
						jtaRating.append("\t\t\t\t\t(Depth: " + min + " - " + max + ")");
						bft.makeMove(UTTTUtility.powerSeries(new int[]{pRow, pCol, btnRow, btnCol}, singleGrid.length));
						if (hints && !win)
						{
							setNextMoves(bft.getRoot().getChildren());
						}
						count = 0;
						tmrThinker.start();
					}
				}
			}
		}
	}

	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//System.out.println("poop");
			if (count < 30 * turns && bft.getRoot().complete == false)
			{
				count++;
				bft.loop();
				double rating = bft.getRating();
				jtaRating.setText("Rating: ");
				if(rating > 10)
					ratingPoints.add(10.0);
				else if(rating < -10)
					ratingPoints.add(-10.0);
				else
					ratingPoints.add(rating);
				if (Math.abs(rating) > 10)
					jtaRating.append("#");
				if (rating > 0)
					jtaRating.append("+");
				else if (rating < 0)
					jtaRating.append("-");
				rating = Math.abs(rating);
				if (rating > 10)
					jtaRating.append("" + ((((int) (Math.round(1000 / rating) + 2)) / 2) - 1));
				else
					jtaRating.append(String.format("%.2f", rating));
				int min = bft.min();
				int max = bft.max();
				jtaRating.append("\t\t\t\t\t(Depth: " + min + " - " + max + ")");
				//jtaRating.append(" (" + count + ")");
				if (hints)
				{
					setNextMoves(bft.getRoot().getChildren());
				}
			}
		}
	}
/*
	public void setNextMoves(ArrayList<Integer> moves)
	{
		for (int move : moves)
		{
			int btnCol = move % 3;
			move /= 3;
			int btnRow = move % 3;
			move /= 3;
			int pCol = move % 3;
			move /= 3;
			int pRow = move % 3;
			singleGrid[pRow][pCol][btnRow][btnCol].setBackground(Color.GREEN);

		}
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				for (int k = 0; k < 3; k++)
				{
					for (int l = 0; l < 3; l++)
					{
						int move = UTTTUtility.powerSeries(new int[]{i, j, k, l}, 3);
						if (!moves.contains(move) && singleGrid[i][j][k][l].getBackground().equals(Color.GREEN))
							singleGrid[i][j][k][l].setBackground(new JButton().getBackground());
					}
				}
			}
		}
	}
*/
	public void setNextMoves(ArrayList<Node> moves)
	{
		for (Node n : moves)
		{
			int move = n.move;
			int btnCol = move % 3;
			move /= 3;
			int btnRow = move % 3;
			move /= 3;
			int pCol = move % 3;
			move /= 3;
			int pRow = move % 3;
			double rating = n.maxVal();
			singleGrid[pRow][pCol][btnRow][btnCol].setMargin(new Insets(0, 0, 0, 0));
		//	singleGrid[pRow][pCol][btnRow][btnCol].setFont(new Font("Arial", Font.PLAIN, 6));
			singleGrid[pRow][pCol][btnRow][btnCol].setText("");
			if (Math.abs(rating) > 10)
				singleGrid[pRow][pCol][btnRow][btnCol].setText(singleGrid[pRow][pCol][btnRow][btnCol].getText() + "#");
			if (rating > 0)
				singleGrid[pRow][pCol][btnRow][btnCol].setText(singleGrid[pRow][pCol][btnRow][btnCol].getText() + "+");
			else if (rating < 0)
				singleGrid[pRow][pCol][btnRow][btnCol].setText(singleGrid[pRow][pCol][btnRow][btnCol].getText() + "-");
			rating = Math.abs(rating);
			if (rating > 10)
				singleGrid[pRow][pCol][btnRow][btnCol].setText(singleGrid[pRow][pCol][btnRow][btnCol].getText() + "" + ((((int) (Math.round(1000 / rating) + 1)) / 2) - 1));
			else
				singleGrid[pRow][pCol][btnRow][btnCol].setText(singleGrid[pRow][pCol][btnRow][btnCol].getText() + String.format("%.2f", rating));
		//	singleGrid[pRow][pCol][btnRow][btnCol].setText();

		}
		ArrayList<Integer> mos = new ArrayList<Integer>();
		for (Node n : moves)
			mos.add(n.move);
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				for (int k = 0; k < 3; k++)
				{
					for (int l = 0; l < 3; l++)
					{
						int move = UTTTUtility.powerSeries(new int[]{i, j, k, l}, 3);
						if (!mos.contains(move) && !singleGrid[i][j][k][l].getText().equals(""))
							singleGrid[i][j][k][l].setText("");
					}
				}
			}
		}
	}


	public String tableMoves(int pRow, int pCol, int btnRow, int btnCol)
	{
		String result = "";
		if(pCol == 0)
			result+= "A";
		else if(pCol == 1)
			result+= "B";
		else if(pCol == 2)
			result+= "C";
		result+= 3 - pRow;

		if(btnCol == 0)
			result+= "a";
		else if(btnCol == 1)
			result+= "b";
		else if(btnCol == 2)
			result+= "c";
		result+= 3 - btnRow;
		return result;
	}

	public void clearBack()
	{
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
						if (singleGrid[pRow][pCol][btnRow][btnCol].getBackground() == Color.GREEN)
							singleGrid[pRow][pCol][btnRow][btnCol].setBackground(new JButton().getBackground());
					}
				}
			}
		}
	}
	
	//For creating the graph of moves at the end of the game
	//Every time a move is made by the player, the rating is stored in the graphData XY Series
	public void drawGraph()
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries graphData = new XYSeries("Ratings");
	//	System.out.println(turnList.size() + "\t" + ratingPoints.size());
		for(int i = 0; i < turnList.size(); i++)
		{
			graphData.add(turnList.get(i), ratingPoints.get(i));
		}
		//for(int i = 0; i < turnList.size(); i++)
			//System.out.println(turnList.get(i) + " " + ratingPoints.get(i));
		dataset.addSeries(graphData);
		chart = ChartFactory.createXYLineChart("Performance", "Turns", "Rating", dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.WHITE);
		
		TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setStandardTickUnits(ticks);
		range.setRange(-10.5, 10.5);
		
		//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
		//plot.setRenderer(renderer);
	    //renderer.setBaseShapesVisible(true);
	   // renderer.setBaseShapesFilled(true);
	    
	    //Stroke stroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
	    //renderer.setBaseOutlineStroke(stroke);
		
		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setStandardTickUnits(ticks);
		
		//NumberFormat format = NumberFormat.getNumberInstance();
		//format.setMaximumFractionDigits(2);
		//XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
		//renderer.setBaseItemLabelGenerator(generator);
	    //renderer.setBaseItemLabelsVisible(true);		
			
		ChartFrame frame = new ChartFrame("Where did you blunder?", chart);
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setResizable(false);
		frame.getChartPanel().setDomainZoomable(false);
		frame.getChartPanel().setRangeZoomable(false);
		frame.setVisible(true);
		frame.setSize(500,400);
		
	}
	
	//disables all the buttons and removes text for game reset
	public void disableAllButtons()
	{
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
						singleGrid[pRow][pCol][btnRow][btnCol].removeActionListener(listener);
						singleGrid[pRow][pCol][btnRow][btnCol].setText(null);
					}
				}
			}
		}
	}
	//disables buttons
	public void disableButtons()
	{
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
						singleGrid[pRow][pCol][btnRow][btnCol].setEnabled(false);
					}
				}
			}
		}
	}

	//only enables buttons in the next tic tac toe board the player is sent to
	public void enableButtons(int row, int col)
	{
		for(int i = 0; i < dim; i++)
		{
			for(int j = 0; j < dim; j++)
			{
				singleGrid[row][col][i][j].setEnabled(true);
			}
		}
	}
	//enables all the buttons, for game reset
	public void enableAllButtons()
	{
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
						singleGrid[pRow][pCol][btnRow][btnCol].setEnabled(true);
					}
				}
			}
		}
	}
	//responsive gui based off what the user does
	public void updateGUI()
	{
		for(int pRow = 0; pRow < dim; pRow++)
		{
			for(int pCol = 0; pCol < dim; pCol++)
			{
				for(int btnRow = 0; btnRow < dim; btnRow++)
				{
					for(int btnCol = 0; btnCol < dim; btnCol++)
					{
					//	if(game.getGames()[pRow][pCol].getBoard()[btnRow][btnCol] == 1)
						if(game[pRow][pCol][btnRow][btnCol] == 1)
						{
							singleGrid[pRow][pCol][btnRow][btnCol].setBackground(Color.RED);
						}
				//		else if(game.getGames()[pRow][pCol].getBoard()[btnRow][btnCol] == 2)
						else if(game[pRow][pCol][btnRow][btnCol] == 2)
						{
							singleGrid[pRow][pCol][btnRow][btnCol].setBackground(Color.YELLOW);
						}
					//	else if (singleGrid[pRow][pCol][btnRow][btnCol].getBackground() != Color.GRAY)
					//	{
					//		singleGrid[pRow][pCol][btnRow][btnCol].setBackground(null);
					//	}
					}
				}
			}
		}
	}
}
//https://docs.riddles.io/ultimate-tic-tac-toe/rules