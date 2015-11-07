//Import
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;


/*		
 * 		Name:					Zachary Tan
 * 		Date:					Wednesday June 17, 2015
 * 		Program Description:	The program will output the game Concentration
 * 
 */

public class Concentration {
	
	// Assign and declare variable name for objects
	private JFrame frmConcentration;

	private JButton[] buttonArray;
	private LinkedList<Integer> cards;

	private JPanel panelWelcomeScreen;
	private JPanel panelGameScreen;
	private JPanel panelInstructions;
	private JPanel panelLeaderboard;
	private JTextField txtTime;
	private JList<String> lstNames;
	private JList<String> lstScores;
	private DefaultListModel<String> namesModel;
	private DefaultListModel<String> scoresModel;
	
	private Timer gameTimer;
	private DecimalFormat time;
	
	// Declare and initialize variables
	private int clickCount;
	private int firstButtonClicked;
	private int secondButtonClicked;
	private int matchesToGo;
	private int minutes;
	private int seconds;
	
	// Declare and assign text file to string variable
	private final String SAVEFILE = "saveinfo.zacoist";
	
	// Set up a delay from the user has picked two cards and assign the back side images to the buttons
	private class GameDelay extends SwingWorker<Void, Object> {
		int buttonA, buttonB;

		@Override
		protected Void doInBackground() throws Exception {
			buttonA = firstButtonClicked;
			buttonB = secondButtonClicked;
			Thread.sleep(500);
			return null;
		}

		@Override
		protected void done() {
			buttonArray[buttonA].setEnabled(true);

			// Flip cards over
			buttonArray[buttonA]
					.setIcon(scaleImage(new ImageIcon("src/images/backside.png")));
			buttonArray[buttonB]
					.setIcon(scaleImage(new ImageIcon("src/images/backside.png")));
		}
	} 	//End of GameDelay class

	// Creates and runs the form
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Concentration window = new Concentration();
					window.frmConcentration.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	} 	// End of main method

	// Creates the application, set the timer's seconds and loads scores to save file
	public Concentration() {
		initialize();

		// Create DecimalFormat
		time = new DecimalFormat("00");
		
		// Load scores from save file
		loadScores(SAVEFILE);
	}

	// Creates the layout for the panels
	private void initialize() {
		//Initialize frame
		frmConcentration = new JFrame();
		frmConcentration.setUndecorated(true);
		frmConcentration.setTitle("Concentration");
		frmConcentration.setBounds(100, 100, 720, 480);
		frmConcentration.setLocationRelativeTo(null);
		frmConcentration.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmConcentration.getContentPane().setLayout(new CardLayout(0, 0));

		// Initialize panels
		// panelWelcomeScreen
		panelWelcomeScreen = new JPanel();
		panelWelcomeScreen.setBackground(Color.WHITE);
		frmConcentration.getContentPane().add(panelWelcomeScreen,
				"name_215409814606689");
		panelWelcomeScreen.setLayout(null);

		// panelScreen
		panelGameScreen = new JPanel();
		panelGameScreen.setBackground(Color.WHITE);
		panelGameScreen.setVisible(false);
		frmConcentration.getContentPane().add(panelGameScreen,
				"name_215409820193481");
		panelGameScreen.setLayout(null);

		// panelInstruction
		panelInstructions = new JPanel();
		panelInstructions.setBackground(Color.WHITE);
		frmConcentration.getContentPane().add(panelInstructions,
				"name_219560570796730");
		panelInstructions.setLayout(null);

		// panelLeaderBoard
		panelLeaderboard = new JPanel();
		panelLeaderboard.setBorder(new CompoundBorder());
		panelLeaderboard.setBackground(Color.WHITE);
		frmConcentration.getContentPane().add(panelLeaderboard,
				"name_219666500893455");
		panelLeaderboard.setLayout(null);

		// Set up panelWelcomeScreen

		// Title
		JLabel lblTitle = new JLabel("Welcome to Concentration");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblTitle.setBounds(155, 137, 431, 50);
		panelWelcomeScreen.add(lblTitle);

		// Start Game Button
		JButton btn = new JButton("Start Game");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelWelcomeScreen, panelGameScreen);
				resetGame();
			}
		});
		btn.setBounds(279, 219, 139, 23);
		panelWelcomeScreen.add(btn);

		// Instruction Button
		JButton btnInstructions = new JButton("Instructions");
		btnInstructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelWelcomeScreen, panelInstructions);
			}
		});
		btnInstructions.setBounds(279, 253, 139, 23);
		panelWelcomeScreen.add(btnInstructions);

		// LeaderBoard Button
		JButton btnLeaderboard = new JButton("Leaderboard");
		btnLeaderboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelWelcomeScreen, panelLeaderboard);
			}
		});
		btnLeaderboard.setBounds(279, 287, 139, 23);
		panelWelcomeScreen.add(btnLeaderboard);

		// Exit Button
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save scores to save file
				saveScores(SAVEFILE);
				
				System.exit(0);
			}
		});
		btnExit.setBounds(279, 320, 139, 23);
		panelWelcomeScreen.add(btnExit);

		// ---------------------------------------------

		// Set up panelGameScreen
		buttonArray = new JButton[28];

		// Game buttons from 0-27
		JButton btn00 = new JButton("");
		btn00.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(0);
			}
		});
		btn00.setBounds(10, 21, 60, 90);
		panelGameScreen.add(btn00);
		buttonArray[0] = btn00;

		JButton btn01 = new JButton("");
		btn01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(1);
			}
		});
		btn01.setBounds(80, 21, 60, 90);
		panelGameScreen.add(btn01);
		buttonArray[1] = btn01;

		JButton btn02 = new JButton("");
		btn02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(2);
			}
		});
		btn02.setBounds(150, 21, 60, 90);
		panelGameScreen.add(btn02);
		buttonArray[2] = btn02;

		JButton btn03 = new JButton("");
		btn03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(3);
			}
		});
		btn03.setBounds(220, 21, 60, 90);
		panelGameScreen.add(btn03);
		buttonArray[3] = btn03;

		JButton btn04 = new JButton("");
		btn04.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(4);
			}
		});
		btn04.setBounds(290, 21, 60, 90);
		panelGameScreen.add(btn04);
		buttonArray[4] = btn04;

		JButton btn05 = new JButton("");
		btn05.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(5);
			}
		});
		btn05.setBounds(360, 21, 60, 90);
		panelGameScreen.add(btn05);
		buttonArray[5] = btn05;

		JButton btn07 = new JButton("");
		btn07.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(7);
			}
		});
		btn07.setBounds(10, 122, 60, 90);
		panelGameScreen.add(btn07);
		buttonArray[7] = btn07;

		JButton btn08 = new JButton("");
		btn08.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(8);
			}
		});
		btn08.setBounds(80, 122, 60, 90);
		panelGameScreen.add(btn08);
		buttonArray[8] = btn08;

		JButton btn06 = new JButton("");
		btn06.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(6);
			}
		});
		btn06.setBounds(430, 21, 60, 90);
		panelGameScreen.add(btn06);
		buttonArray[6] = btn06;

		JButton btn09 = new JButton("");
		btn09.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(9);
			}
		});
		btn09.setBounds(150, 122, 60, 90);
		panelGameScreen.add(btn09);
		buttonArray[9] = btn09;

		JButton btn10 = new JButton("");
		btn10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(10);
			}
		});
		btn10.setBounds(220, 122, 60, 90);
		panelGameScreen.add(btn10);
		buttonArray[10] = btn10;

		JButton btn11 = new JButton("");
		btn11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(11);
			}
		});
		btn11.setBounds(290, 122, 60, 90);
		panelGameScreen.add(btn11);
		buttonArray[11] = btn11;

		JButton btn12 = new JButton("");
		btn12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(12);
			}
		});
		btn12.setBounds(360, 122, 60, 90);
		panelGameScreen.add(btn12);
		buttonArray[12] = btn12;

		JButton btn13 = new JButton("");
		btn13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(13);
			}
		});
		btn13.setBounds(430, 122, 60, 90);
		panelGameScreen.add(btn13);
		buttonArray[13] = btn13;

		JButton btn18 = new JButton("");
		btn18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(18);
			}
		});
		btn18.setBounds(290, 223, 60, 90);
		panelGameScreen.add(btn18);
		buttonArray[18] = btn18;

		JButton btn19 = new JButton("");
		btn19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(19);
			}
		});
		btn19.setBounds(360, 223, 60, 90);
		panelGameScreen.add(btn19);
		buttonArray[19] = btn19;

		JButton btn20 = new JButton("");
		btn20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(20);
			}
		});
		btn20.setBounds(430, 223, 60, 90);
		panelGameScreen.add(btn20);
		buttonArray[20] = btn20;

		JButton btn17 = new JButton("");
		btn17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(17);
			}
		});
		btn17.setBounds(220, 223, 60, 90);
		panelGameScreen.add(btn17);
		buttonArray[17] = btn17;

		JButton btn16 = new JButton("");
		btn16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(16);
			}
		});
		btn16.setBounds(150, 223, 60, 90);
		panelGameScreen.add(btn16);
		buttonArray[16] = btn16;

		JButton btn15 = new JButton("");
		btn15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(15);
			}
		});
		btn15.setBounds(80, 223, 60, 90);
		panelGameScreen.add(btn15);
		buttonArray[15] = btn15;

		JButton btn14 = new JButton("");
		btn14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(14);
			}
		});
		btn14.setBounds(10, 223, 60, 90);
		panelGameScreen.add(btn14);
		buttonArray[14] = btn14;

		JButton btn25 = new JButton("");
		btn25.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(25);
			}
		});
		btn25.setBounds(290, 324, 60, 90);
		panelGameScreen.add(btn25);
		buttonArray[25] = btn25;

		JButton btn26 = new JButton("");
		btn26.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(26);
			}
		});
		btn26.setBounds(360, 324, 60, 90);
		panelGameScreen.add(btn26);
		buttonArray[26] = btn26;

		JButton btn27 = new JButton("");
		btn27.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(27);
			}
		});
		btn27.setBounds(430, 324, 60, 90);
		panelGameScreen.add(btn27);
		buttonArray[27] = btn27;

		JButton btn24 = new JButton("");
		btn24.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(24);
			}
		});
		btn24.setBounds(220, 324, 60, 90);
		panelGameScreen.add(btn24);
		buttonArray[24] = btn24;

		JButton btn23 = new JButton("");
		btn23.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(23);
			}
		});
		btn23.setBounds(150, 324, 60, 90);
		panelGameScreen.add(btn23);
		buttonArray[23] = btn23;

		JButton btn22 = new JButton("");
		btn22.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(22);
			}
		});
		btn22.setBounds(80, 324, 60, 90);
		panelGameScreen.add(btn22);
		buttonArray[22] = btn22;

		JButton btn21 = new JButton("");
		btn21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClicked(21);
			}
		});
		btn21.setBounds(10, 324, 60, 90);
		panelGameScreen.add(btn21);
		buttonArray[21] = btn21;

		// Main Menu Button
		JButton btnToMainMenu = new JButton("Main Menu");
		btnToMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelGameScreen, panelWelcomeScreen);
			}
		});
		btnToMainMenu.setBounds(582, 393, 112, 37);
		panelGameScreen.add(btnToMainMenu);

		// Time Setup
		JLabel lblTime = new JLabel("Time:");
		lblTime.setFont(new Font("Tahoma", Font.PLAIN, 26));
		lblTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblTime.setBounds(494, 111, 200, 50);
		panelGameScreen.add(lblTime);

		txtTime = new JTextField();
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		txtTime.setFont(new Font("Tahoma", Font.PLAIN, 36));
		txtTime.setText("00:00");
		txtTime.setEditable(false);
		txtTime.setBounds(500, 162, 184, 50);
		panelGameScreen.add(txtTime);
		txtTime.setColumns(10);
		
		//Reset button
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop timer
				gameTimer.stop();

				resetGame();
			}
		});
		btnReset.setBounds(540, 235, 112, 37);
		panelGameScreen.add(btnReset);

		// ---------------------------------------------
		// Set up panelInstructions

		// Instruction Labels
		JLabel lblInstruction = new JLabel("Instructions");
		lblInstruction.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);
		lblInstruction.setHorizontalTextPosition(SwingConstants.CENTER);
		lblInstruction.setBounds(234, 11, 200, 50);
		panelInstructions.add(lblInstruction);

		JLabel lblStep1 = new JLabel(
				"1. Concentration is a card-based game where the user (you) must pick a card at random.");
		lblStep1.setBounds(10, 71, 525, 50);
		panelInstructions.add(lblStep1);

		JLabel lblStep2 = new JLabel(
				"2. Once you've selectected a card, select another one.");
		lblStep2.setBounds(10, 120, 525, 50);
		panelInstructions.add(lblStep2);

		JLabel lblStep3 = new JLabel(
				"3. If both cards have the same picture, they dissapear.  If not, they reflip over.");
		lblStep3.setBounds(10, 174, 525, 50);
		panelInstructions.add(lblStep3);

		JLabel lblStep4 = new JLabel(
				"4. Continue obtaining pairs until all cards have dissapeared.");
		lblStep4.setBounds(10, 229, 525, 50);
		panelInstructions.add(lblStep4);

		JLabel lblStep5 = new JLabel(
				"5. Once all 14 pairs have dissapeared, you won!  Scores can be compared by time. Can you beat Concentration the fastest?");
		lblStep5.setBounds(10, 281, 626, 50);
		panelInstructions.add(lblStep5);

		// Main Menu Button
		JButton btnMainMenu = new JButton("Main Menu");
		btnMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelInstructions, panelWelcomeScreen);
			}
		});
		btnMainMenu.setBounds(10, 393, 112, 37);
		panelInstructions.add(btnMainMenu);

		// ---------------------------------------------

		// Set up panelLeaderboard

		// Score (Username)
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblUsername.setBounds(21, 20, 200, 50);
		panelLeaderboard.add(lblUsername);

		// Score (Time)
		JLabel lblScore = new JLabel("Time");
		lblScore.setFont(new Font("Tahoma", Font.PLAIN, 36));
		lblScore.setBounds(244, 20, 200, 50);
		panelLeaderboard.add(lblScore);

		// Main Menu Button
		JButton btnMainMenu2 = new JButton("Main Menu");
		btnMainMenu2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelLeaderboard, panelWelcomeScreen);
			}
		});
		btnMainMenu2.setBounds(515, 207, 112, 37);
		panelLeaderboard.add(btnMainMenu2);
		
		namesModel = new DefaultListModel<String>();
		lstNames = new JList<String>(namesModel);
		lstNames.setEnabled(false);
		lstNames.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		lstNames.setBackground(Color.WHITE);
		lstNames.setBounds(21, 81, 200, 333);
		panelLeaderboard.add(lstNames);
		
		scoresModel = new DefaultListModel<String>();
		lstScores = new JList<String>(scoresModel);
		lstScores.setEnabled(false);
		lstScores.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		lstScores.setBackground(Color.WHITE);
		lstScores.setBounds(244, 81, 200, 333);
		panelLeaderboard.add(lstScores);
		// ----------------------------------------------
	}	// End of initialize method
	
	//Form color and frame size
	public Color getFrameContentPaneBackground() {
		return frmConcentration.getContentPane().getBackground();
	}		

	public void setFrameContentPaneBackground(Color background) {
		frmConcentration.getContentPane().setBackground(background);
	}

	private void resetGame() {
		// Reset GUI
		for (JButton b : buttonArray) {
			b.setVisible(true);
			b.setEnabled(true);
		}

		// Create deck of 28 cards
		cards = new LinkedList<Integer>();
		for (int i = 1; i <= 14; i++) {
			cards.add(i);
			cards.add(i);
		}

		// Shuffle deck
		Collections.shuffle(cards);

		// Set each button icon to card back
		for (int i = 0; i < 28; i++) {
			buttonArray[i]
					.setIcon(scaleImage(new ImageIcon(
							"src/images/backside.png")));
		}

		// Reset click counter
		clickCount = 0;

		// Reset match counter
		matchesToGo = 14;

		// Reset timer
		seconds = 0;
		minutes = 0;
		txtTime.setText("00:00");
		gameTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;

				if (seconds == 60) {
					minutes++;
					seconds = 0;
				}

				// Update timer on GUI
				txtTime.setText(time.format(minutes) + ":"
						+ time.format(seconds));
			}
		});
		gameTimer.start();
	}	// End of reset method

	private void buttonClicked(int buttonID) {
		// Reveal card
		buttonArray[buttonID].setIcon(scaleImage(new ImageIcon(
				"src/images/" + cards.get(buttonID) + ".png")));

		// Save first card clicked
		if (clickCount == 0) {
			firstButtonClicked = buttonID;
		} else if (clickCount == 1) {
			// Check if user clicks the same card twice
			if (firstButtonClicked == buttonID) {
				clickCount--;
			} else {
				secondButtonClicked = buttonID;
			}
		}	

		// Increment click count
		clickCount++;

		// If we've clicked two cards
		if (clickCount == 2) {
			// Check for match
			if (cards.get(firstButtonClicked) == cards.get(secondButtonClicked)) {
				// Hide the cards
				buttonArray[firstButtonClicked].setVisible(false);
				buttonArray[secondButtonClicked].setVisible(false);

				// Decrement match counter
				matchesToGo--;

				if (matchesToGo == 0) {
					gameOver();
				}
			} else { // If not matching
				GameDelay delay = new GameDelay();
				delay.execute();
			}

			// Reset click count
			clickCount = 0;
		}
	}	// End of buttonClicked method

	private void gameOver() {
		// Stop timer
		gameTimer.stop();

		// Switch to leader boards screen
		switchPanel(panelGameScreen, panelLeaderboard);

		// Get user's name
		String username = JOptionPane.showInputDialog(frmConcentration,
				"Congratulations! You've won! Enter your name here:",
				"Winner!", JOptionPane.DEFAULT_OPTION);

		// Add user's name and score to leaderboards
		namesModel.addElement(username);
		scoresModel.addElement(time.format(minutes) + ":" + time.format(seconds) + '\n');
	} 	// End of gameOver method
	
	// Switch between panels
	private void switchPanel(JPanel from, JPanel to) {
		from.setVisible(false);
		to.setVisible(true);
	} 	//End of switchPanel method
	
	// Read the scores
	private void loadScores(String filename) {
		try {
			Scanner read = new Scanner(new File(filename));
			
			String input;
			
			// Read the names
			while(!(input = read.nextLine()).equals("----é")) {
				namesModel.addElement(input);
				System.out.println(input);
			}
			
			// Read the times
			while(read.hasNextLine()) {
				scoresModel.addElement(read.nextLine());
			}
			
			read.close();
		} catch (FileNotFoundException e) {
			// If file not found, create new file
			new File(filename);
		}
	} 	// End of loadScores method
	
	// Save the scores
	private void saveScores(String filename) {
		try {
			PrintWriter write = new PrintWriter(new File(filename));
			
			// Saves the names
			for (int i = 0; i < namesModel.size(); i++) {
				write.println(namesModel.getElementAt(i));
			}

			// Delineate data
			write.println("----é");
			
			// Saves the times
			for (int i = 0; i < scoresModel.size(); i++) {
				write.println(scoresModel.getElementAt(i));
			}
			
			write.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
	} 	// End of saveScores method
	
	// Set images to scale with button
	private ImageIcon scaleImage(ImageIcon toScale) {
		return new ImageIcon((toScale.getImage()).getScaledInstance(60, 90,
				java.awt.Image.SCALE_SMOOTH));
	}	// End of scaleImage method
}
