package gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import model.*;



public class Draw extends JPanel implements MouseMotionListener{

	private ClientGUI main;
	private BallThread thread;
	private Point mouse;
	private Ball playerBall;
	ArrayList<Ball> food;
	private ArrayList<Player> enemies;
	//private double scale;
	
	public Draw(ClientGUI main) {
		this.main=main;
		mouse=new Point();
		addMouseMotionListener(this);
		setLayout(new BorderLayout());
		setFocusable(true);
		//scale=1
		Game ok=main.getGame();
		Player actual=ok.getPlayer(main.getClient().getNick());
		playerBall = actual.getBall();
		thread=new BallThread(main, playerBall);
		thread.start();
		food = main.getGame().getArrFood();
		enemies = main.getGame().getPlayers();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2=(Graphics2D)g;
		//System.out.println(player.getRadius());
		g2.setColor(Color.WHITE);
		g2.drawImage(new ImageIcon("./Background.png").getImage(), 0, 0, this);
		//g2.fillRect(0, 0, GameGUI.ANCHO, GameGUI.LARGO);
		//g2.translate(Game.ANCHO/2, Game.LARGO/2);
		//g2.scale(50.0/player.getRadius(),50.0/player.getRadius());
		//g2.translate(-player.getPosX(), -player.getPosY());
		g2.setColor(playerBall.getColor());
		g2.fillOval((int)playerBall.getPosX()-(int)(playerBall.getRadius()/2), (int)(playerBall.getPosY()-(int)playerBall.getRadius()/2), (int)playerBall.getRadius(), (int)playerBall.getRadius());
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 30));
		g2.drawString("Puntaje: "+(int)playerBall.getRadius(), 20, 40);
		
		/*
			for(int i=0;i<food.size();i++) {
				if(playerBall.eat(playerBall.getPos(), food.get(i)))
				{
					try
					{
						main.getClient().getEatenBalls().add(food.get(i));
						main.getClient().getGame().getArrFood().remove(food.get(i));
					} catch (Exception e) {
					}
					
				}
				Ball actual=food.get(i);
				g2.setColor(actual.getColor());
				g2.fillOval((int)actual.getPosX(), (int)actual.getPosY(), (int)actual.getRadius(), (int)actual.getRadius());
			}
			*/
		
		for (int i = 0; i < enemies.size(); i++)
		{
			Ball actual= enemies.get(i).getBall();
			if(playerBall!=actual) {
				
				g2.setColor(actual.getColor());
				g2.fillOval((int)actual.getPosX(), (int)actual.getPosY(), (int)actual.getRadius(), (int)actual.getRadius());
			}
		}
		//System.out.println("Se actualizó draw");
	}
	
	
	public Point mousePos() {
		return mouse;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouse=arg0.getPoint();
		
	}
	
}
