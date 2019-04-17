package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.text.PlainDocument;


public class Game implements Serializable {
	
	//Constantes
	public final static int MAX_FOOD=5;
	
	
	public static long MATCH_TTL = 5000*60000;
	public static int MAX_PLAYERS = 5;
	public static int MIN_PLAYERS = 2;
	public static long MAX_LOBBY_TTL = 2000*60000;
	
	//Atributos
	
	//Relaciones
	//relaci�n que contiene las esferas que son comida
	private ArrayList<Ball> food;
	
	//relaci�n que contiene a los jugadores
	private ArrayList<Player> players;
		
	//Constructor
	
	
	//m�todos
	public Game() {
		
		food = new ArrayList<>();
		players = new ArrayList<>();
		
	}
	/**
	 * Genera 10.000 puntos de comida, cada uno con una posici�n al azar seg�n el maximo largo y ancho
	 * del screen que manejar� el servidor.
	 */
	public void generateFood () {
		food = new ArrayList<>();

		for (int i = 0; i < MAX_FOOD; i++) {		
			double posX = (Math.random() * 1000)+1;
			double posY = (Math.random() *1000)+1;
			
			Random rand = new Random(); 
			float r = rand.nextFloat(); 
			float g = rand.nextFloat(); 
			float b = rand.nextFloat(); 
			Color color = new Color (r,g,b);
			
			double radius = Ball.FOOD_RADIOUS;
			
			food.add( new Ball(posX, posY, color, radius));			
		}
		
	}
	
	public void addFood() {
		
			
				double posX = (Math.random() * 1000)+1;
				double posY = (Math.random() * 1000)+1;
				
				Random rand = new Random(); 
				float r = rand.nextFloat(); 
				float g = rand.nextFloat(); 
				float b = rand.nextFloat(); 
				Color color = new Color (r,g,b);
				
				double radius = Ball.FOOD_RADIOUS;
				
				food.add( new Ball(posX, posY, color, radius));	
			
		
	}
	
	/**
	 * Le asigna a cada uno de los usuarios una esfera que est� en una posicion al azar seg�n 
	 * el largo y ancho del screen que manejar� el servidor
	 */
	public ArrayList<Player> assignUsersBalls() {		
		for (int i = 0; i < players.size(); i++) {
			double posX = (Math.random() * 1000)+1;
			double posY = (Math.random() * 1000)+1;
			
			Random rand = new Random(); 
			float r = rand.nextFloat(); 
			float g = rand.nextFloat(); 
			float b = rand.nextFloat(); 
			Color color = new Color (r,g,b);
			
			double radius = Ball.USER_RADIOUS;
			double speed = Ball.USER_SPEED;
			
			Ball esfera = new Ball(posX, posY, color, radius);
			players.get(i).setBall(esfera);
		}
		return players;
	}

	public ArrayList<Ball> getArrFood() {
		return food;
	}



	public void setArrFood(ArrayList<Ball> arrFood) {
		this.food = arrFood;
	}
	
	public ArrayList<Ball> getFood() {
		return food;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void setFood(ArrayList<Ball> food) {
		this.food = food;
	}
	
	public Player getPlayer(String nick) {
		Player player=null;
		for(int i=0;i<players.size();i++) {
			Player actual=players.get(i);
			if(actual.getNickName().equals(nick)) {
				player=actual;
			}
		}
		return player;
	}
	
	
	

	
	

}