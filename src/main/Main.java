package main;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		JFrame window = new JFrame("Tetris");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		//Add GamePanel to the window
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();//Resize the window to fit the panel
		
		window.setLocationRelativeTo(null);//Set the window to be centered on the screen
		window.setVisible(true);
		
		gp.LaunchGame();
		
	}

}
