package mino;

import java.awt.Color;
import java.awt.Graphics2D;

import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

//Tetrimino's Super Class
public class Mino {
	public Block b[] = new Block[4];
	public Block tempB[] = new Block[4];//This TempB array is used to pre-calculate collision handling due to changes in the position value of the block array called b.
	int autoDropCounter = 0;//the counter increases in every frame
	public int direction = 1; // There are 4 directions (1/2/3/4)
	
	boolean leftCollision, rightCollision, bottomCollision;
	
	public boolean active = true;//Mino's active state (if true, it is moving)
	
	public boolean deactivating;//Status of whether the bottom of Mino has collision
	
	int deactivateCounter = 0;//This is a variable that counts whether or not there is a collision at the bottom of Mino on a frame-by-frame basis.
	
	
	public void create(Color c) {
		for(int idx = 0; idx < b.length; idx++) {
			b[idx] = new Block(c);
		}
		
		for(int idx = 0; idx < tempB.length; idx++) {
			tempB[idx] = new Block(c);
		}
	}
	
	
	//set the x,y
	public void setXY(int x, int y) {}
	
	//update the x,y
	public void updateXY(int direction) {
		
		checkRotationCollision();
		if(leftCollision == false && rightCollision == false && bottomCollision == false) {
			this.direction = direction;
			b[0].x = tempB[0].x;
			b[0].y = tempB[0].y;
			b[1].x = tempB[1].x;
			b[1].y = tempB[1].y;
			b[2].x = tempB[2].x;
			b[2].y = tempB[2].y;
			b[3].x = tempB[3].x;
			b[3].y = tempB[3].y;
		}
	}
	
	//Mino’s status according to rotation
	public void getDirection1() {}
	public void getDirection2() {}
	public void getDirection3() {}
	public void getDirection4() {}
	
	
	public void checkMovementCollision() {
		
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;
		
		//check static block Collision
		checkStaticBlockCollision();
		
		//check frame collision
		//Left wall
		for(int i = 0; i < b.length; i++) {
			if(b[i].x == PlayManager.left_x) {
				leftCollision = true;
			}
		}
		//Right wall
		for(int i = 0; i < b.length; i++) {
			if(b[i].x + Block.SIZE == PlayManager.right_x) {
				rightCollision = true;
			}
		}
		//Bottom floor
		for(int i = 0; i < b.length; i++) {
			if(b[i].y + Block.SIZE == PlayManager.bottom_y) {
				bottomCollision = true;
			}
		}
		
	}
	
	public void checkRotationCollision() {
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;
		
		//Check static block collision
		checkStaticBlockCollision();
		
		//Check frame collision
		//Left wall
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x < PlayManager.left_x) {
				leftCollision = true;
			}
		}
		//Right wall
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x + Block.SIZE > PlayManager.right_x) {
				rightCollision = true;
			}
		}
		//Bottom floor
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].y + Block.SIZE > PlayManager.bottom_y) {
				bottomCollision = true;
			}
		}
	}
	
	//Check for collisions between blocks
	private void checkStaticBlockCollision() {
		
		for(int i = 0; i < PlayManager.staticBlocks.size(); i++) {
			
			int targetX = PlayManager.staticBlocks.get(i).x;//X of another block
			int targetY = PlayManager.staticBlocks.get(i).y;//Y of another block
			
			//check down
			for(int ii = 0; ii < b.length; ii++) {
				//A collision occurs when the Bottom_Y of one block is the same as the Top_Y of another block, and the Side_X between the two blocks is also the same.
				if(b[ii].y + Block.SIZE == targetY && b[ii].x == targetX) {
					bottomCollision = true;
				}
			}
			//check left
			for(int ii = 0; ii < b.length; ii++) {
				//A collision occurs when one block has the same Left_X of another block and the Bottom_Y of both blocks is also the same.
				if(b[ii].x - Block.SIZE == targetX && b[ii].y == targetY) {
					leftCollision = true;
				}
			}
			//check right
			for(int ii = 0; ii < b.length; ii++) {
				//A collision occurs when one block has the same Right_X of another block and the Bottom_Y of both blocks is also the same.
				if(b[ii].x + Block.SIZE == targetX && b[ii].y == targetY) {
					rightCollision = true;
				}
			}
		}
	}
	
	//Processing state changes in Mino
	public void update() {
		
		if(deactivating) {
			deactivating();
		}
		
		//Move the mino
		if(KeyHandler.upPressed) {//Rotate the Mino
			switch(direction) {
			case 1: getDirection2();break;
			case 2: getDirection3();break;
			case 3: getDirection4();break;
			case 4: getDirection1();break;
			}
			KeyHandler.upPressed = false;
			GamePanel.se.play(3, false);
		}
		
		checkMovementCollision();
		
		if(KeyHandler.downPressed) {//Press the key to quickly go down in block size units.
			//If the mino's bottom is not hitting, it can go down
			if(bottomCollision == false) {
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				
				//When moved down, reset the autoDropCounter
				autoDropCounter = 0;
			}
			KeyHandler.downPressed = false;
		}
		if(KeyHandler.leftPressed) {
			//If the mino's left is no hitting, it can go left
			if(leftCollision == false) {
				b[0].x -= Block.SIZE;
				b[1].x -= Block.SIZE;
				b[2].x -= Block.SIZE;
				b[3].x -= Block.SIZE;
			}
			KeyHandler.leftPressed = false;
		}
		if(KeyHandler.rightPressed) {
			//If the mino's right is no hitting, it can go right
			if(rightCollision == false) {
				b[0].x += Block.SIZE;
				b[1].x += Block.SIZE;
				b[2].x += Block.SIZE;
				b[3].x += Block.SIZE;
			}
			KeyHandler.rightPressed = false;
		}
		
		//If a collision occurs at the bottom of Mino, the deactivating value becomes true.(at 1/60frames)
		if(bottomCollision) {
			if(deactivating == false) {
				GamePanel.se.play(4, false);
			}
			deactivating = true;
		}else {
			//Automatically dropped in block size units.
			autoDropCounter++; //the counter increases in every frame
			if(autoDropCounter == PlayManager.dropInterval) {
				//the mino gose down
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				autoDropCounter = 0;
			}
		}
	}
	
	//Check for bottom collision persists
	private void deactivating() {
		
		deactivateCounter++;
		
		//Wait 45 frames until deactivate
		if(deactivateCounter == 45) {
			
			deactivateCounter = 0;
			checkMovementCollision(); //check if the bottom is still hitting
			
			//If the bottom is still hitting after 45 frames, deactivate the mino
			if(bottomCollision) {
				active = false;
			}
		}
	}
	
	
	public void draw(Graphics2D g2) {
		int margin =2;
		g2.setColor(b[0].c);
		g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
	}
}
