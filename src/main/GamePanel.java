package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	//Screen Size
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	final int FPS = 60;
	Thread gameThread;
	PlayManager pm;
	public static Sound music = new Sound();//background music
	public static Sound se = new Sound();//sound effect
	
	
	public GamePanel() {
		
		//Panel Settings
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setBackground(Color.black);
		this.setLayout(null);
		
		// Implement Key Listener
		this.addKeyListener(new KeyHandler());
		this.setFocusable(true);//You can get keyinput while the window is focused with the KeyHandler class
		
		pm = new PlayManager();
	}

	//start Game
	public void LaunchGame() {
		gameThread = new Thread(this);
		gameThread.start();
		
		music.play(0, true);
		music.loop();
	}

	
	//설명 : 60FPS으로 게임 화면(UI) 처리
	@Override
	public void run() {
		
		//Game Loop
		double drawInterval = 1000000000/FPS;//60프레임 처리시 각 프레임이 처리되는 시간(=나노초),1초(=100000000나노초) 
		double delta = 0;//(delta time)직전 프레임을 처리하는데 걸린 시간
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;//프레임 처리 시간을 누적시킴
			lastTime = currentTime;
			
			if(delta >= 1) {//update, repaint 메서드를 초당 60회 호출
				update();
				repaint();
				delta--;
			}
		}
		
	}
	
	private void update() {
		if(KeyHandler.pausePressed == false && pm.gameOver == false) {
			pm.update();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		pm.draw(g2);
	}
}
