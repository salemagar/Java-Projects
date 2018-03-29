import javax.swing.*;

import sun.audio.*;
import javazoom.jl.decoder.JavaLayerException; //Needed to play mp3 files
import javazoom.jl.player.Player; //Libraries to play mp3 files in game

import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class HopperPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public final int WIDTH = 800;  //Width of page
	public final int HEIGHT = 800;  //Height of page
	public final int DELAY = 20;  //Used for setting the timer
	public final int speed = 10;
	
	private Image background = new ImageIcon("blackhole.gif").getImage();  //background image
	private Image ufo = new ImageIcon("ufo.png").getImage();  //character image
    private Image pillar = new ImageIcon("redlaser.png").getImage();  //column image
    private Image foreground = new ImageIcon("sun.png").getImage();  //foreground image
	
	private int gravity;  //Used to make the icon fall
	private int hopperMove;  //Used to make the hopper move
	private int score;  //Keeps track of player's score while he plays
	private int temp;  //Is used to compare score and high score
	private int highScore = 0;  //Keeps track of the player's highest score
	
	private boolean start, gameOver;  //Triggers the game start and when the player gets game over
	
	private Timer timer;  //Used to produce motion with repainter
	private Random rand;  //Used to generate random height for the columns
	
	private Rectangle hopper;  //The main character of the game 
	private ArrayList<Rectangle> blocks;
	
	public HopperPanel()
	{
		timer = new Timer(DELAY, new HopListener());  //Creating a new timer object and then making it accessible by HopListener ActionListener
		rand = new Random();  
		setPreferredSize(new Dimension(WIDTH-10, HEIGHT-10));  //Setting the size of the panel
		setFocusable(true);
		
		addMouseListener(new clickListener());  //Adds a listener
		addKeyListener(new hitListener());  //Adds a listener
		
		hopper = new Rectangle(WIDTH/2-200, HEIGHT/2-10, 60, 60);  //Sets the dimensions of the character
		blocks = new ArrayList<Rectangle>();  //Creates a column object
		
		addBlock(true);  //Adds columns
		addBlock(true);  //^
		addBlock(true);  //^
		addBlock(true);  //^
		timer.start();  //Starts the timer
	}
	
	public void music()
	{
		while(start || gameOver || !gameOver) //Plays the background music until the game is closed
		{
			try{
				FileInputStream BGM = new FileInputStream("gameMusic.mp3");
				Player player = new Player(BGM);
				player.play();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(JavaLayerException e){
				e.printStackTrace();
			}
		}
	}
	
	 public static void hopSound() {  //Plays sound when the character hops
	        AudioPlayer MGP = AudioPlayer.player;
	        AudioStream BGM;
	        AudioData MD;
	        AudioDataStream play = null;

	        try {
	            BGM = new AudioStream(new FileInputStream("hopSound.wav"));
	            MD = BGM.getData();
	            play = new AudioDataStream(MD);
	        } catch (IOException error) {
	        }

	        MGP.start(play);
	        
	    }

	public void addBlock(boolean start)  //Function to add columns
	{
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(space);
		
		if(start)
		{
			blocks.add(new Rectangle(WIDTH + width + blocks.size()*300, HEIGHT-height-120, width, height));
			blocks.add(new Rectangle(WIDTH + width + ((blocks.size()-1)*300), 0, width, HEIGHT-height-space));
		}
		else
		{
			blocks.add(new Rectangle(blocks.get(blocks.size()-1).x + 600, HEIGHT-height-120, width, height));
			blocks.add(new Rectangle(blocks.get(blocks.size()-1).x, 0, width, HEIGHT-height-space));
		}
	}
	
	public void paintBlock(Graphics page, Rectangle block)
	{
		page.drawImage(pillar, block.x, block.y, block.width, block.height, this);  //Places the pillar image over the column 
	}
	
	public void hop()  //Function to make the character hop
	{
		if(gameOver)
		{
			hopper = new Rectangle(WIDTH/2-200, HEIGHT/2-10, 60, 60);
			blocks.clear();
			hopperMove = 0;
			score = 0;
			
			addBlock(true);
			addBlock(true);
			addBlock(true);
			addBlock(true);
			gameOver = false;
		}
		if(!start)
		{
			start = true;
		}
		else if(!gameOver)
		{
			if(hopperMove > 0)
			{
				hopperMove = 0;
			}
			hopperMove -= 10;
		}
	}
	
	public class HopListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(start)
			{
				gravity ++;
			
				for(int counter = 0; counter < blocks.size(); counter++)
				{
					Rectangle block = blocks.get(counter);
					block.x -= speed;
					
				}
				
			
				if(gravity % 2 == 0 && hopperMove < 15)
				{
					hopperMove += 2;
				}
				hopper.y += hopperMove;
			
				for(int counter = 0; counter < blocks.size(); counter++)
				{
					Rectangle block = blocks.get(counter);
					if(block.x + block.width < 0)
					{
						blocks.remove(block);
					
						if(block.y == 0)
						{
							addBlock(false);
						}
					}
				}
				for(Rectangle block : blocks)
				{
					if(block.y == 0 && hopper.x + hopper.width/2 > block.x + block.width/2-speed && hopper.x + hopper.width/2 < block.x + block.width/2 + speed)
					{
						score++;
						temp = score;
						if(temp > highScore)
						{
							highScore = temp;
						}
					}
					if(block.intersects(hopper))
					{
						gameOver = true;
						hopper.x = block.x - hopper.width;
					}
					if(hopper.y > HEIGHT-180 || hopper.y <0)
					{
						gameOver = true;
					}
					if(hopper.y + hopperMove >= HEIGHT - 150)
					{
						hopper.y = HEIGHT - 80 - hopper.height;
					}
				
				}
				repaint();
			}
		}
	}
	
	public void paintComponent(Graphics page)
	{
	super.paintComponent(page);
	
		page.drawImage(background, 0, 0, WIDTH, HEIGHT, this);  //Places the background image on the background
        page.drawImage(foreground, 0, HEIGHT-210, WIDTH, 210, this);  //Places the foreground image on the foreground
        
        page.drawImage(ufo, hopper.x, hopper.y, hopper.width, hopper.height, this);  //Places the ufo image on the character
        
        for(Rectangle block : blocks)
        {
        	paintBlock(page, block);
        }
        
        page.setColor(Color.orange);  //Sets the font to orange
        
        page.setFont(new Font("Arial", 1, 60));
        if(!start)
        {
        	page.drawString("Hit Space", 250, HEIGHT/4);
        	page.drawString("OR", 350, HEIGHT/2-120);
        	page.drawString("Click to Start!", 200, HEIGHT/2 -50);
        }
        
        page.setFont(new Font("Arial", 1, 100));
        if(gameOver)
        {
        	page.drawString("GAME OVER!", 100, HEIGHT/2 - 50);  //Displays "GAME OVER!"
        	
        	page.setFont(new Font("Arial",1,40));
        	page.drawString("Hit Space to Start again!", 170, HEIGHT/2+50);
        	
        }
        if(!gameOver && start)
        {
        	page.setColor(Color.white);  //Sets the font to white for score
        	page.drawString(String.valueOf(score), WIDTH/2 - 25, 100);  //Displays the score in the game
        }
        page.setFont(new Font("Arial", 1, 30));
        if(gameOver || (!gameOver && start))
        {
        	page.setColor(Color.orange);  //Sets the font to back to orange for the high score 
        	page.drawString("HighScore: ", 550, HEIGHT/15);  //Displays the "HighScore: "
        	page.drawString(String.valueOf(highScore), 725, HEIGHT/15);  //Displays the number for high score
        }
        
        
	}
	
	public class hitListener implements KeyListener
	{

		public void keyPressed(KeyEvent k) {
			
		}

		public void keyReleased(KeyEvent k) {
			if(k.getKeyCode() == KeyEvent.VK_SPACE)
			{
				hop();
				hopSound();
			}
		}

		public void keyTyped(KeyEvent k) {
			
		}
		
	}
	
	public class clickListener implements MouseListener
	{
		public void mouseClicked(MouseEvent m) {
			hop();
			hopSound();
		}

		public void mouseEntered(MouseEvent m) {
				
		}

		public void mouseExited(MouseEvent m) {
			
		}

		public void mousePressed(MouseEvent m) {
			
		}

		public void mouseReleased(MouseEvent m) {
			
		}
	}
	

}
