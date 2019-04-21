package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import connection.Client;
import model.Ball;
import model.Game;
import model.Player;

//import Conection.Client;
//import Conection.Server;
//import Model.Game;
//import Model.Player;

public class ClientGUI extends JFrame implements ActionListener{
	public final static int ANCHO=1421;
	public final static int LARGO=1000;
	public final static String LOGIN="Log In";
	public final static String REGISTER="Register";
	public final static String SIGN_IN ="Sign In";

	private Client client;
	private LobbyThread lobbyThread;
	private Draw draw;
	private JPanel aux;
	private JPanel first;
	private JButton btnLogIn;
	private JButton btnRegister;
	private LogInPane logIn;
	private RegisterPane register;
	private LobbyPane lobby;
	private Game game; 
	//private Client client;

	

	public ClientGUI() {
		setTitle("Agar.io");
		setLayout(new BorderLayout());
		setResizable(false);
		//draw=new Draw(this);
		//add(draw);
		first=new JPanel();
		first.setLayout(new BorderLayout());
		aux=new JPanel();
		btnLogIn=new JButton(LOGIN);
		btnRegister=new JButton(REGISTER);
		aux.add(btnLogIn);
		aux.add(btnRegister);
		logIn=new LogInPane(this);
		register=new RegisterPane(this);
		btnRegister.addActionListener(this);
		btnRegister.setActionCommand(REGISTER);
		btnLogIn.addActionListener(this);
		btnLogIn.setActionCommand(LOGIN);
		first.add(aux,BorderLayout.SOUTH);
		first.add(logIn,BorderLayout.CENTER);
		add(first);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}

	
	public void upDate() {
		draw.repaint();
		draw.revalidate();
	}
	
	public static void main(String[] args) {
		ClientGUI clientGUI = new ClientGUI();
		clientGUI.setVisible(true);
	}
	
	public void register(String serverIp,String data) {
		 client=new Client(serverIp, data,this);
		
	}
	
	public void lobby() {
		remove(first);
		lobby=new LobbyPane();
		add(lobby,BorderLayout.CENTER);
		client.startLobbySocket();
		lobbyThread=new LobbyThread(client);
		lobbyThread.start();
		setSize(new Dimension(400,200));
	}
	
	public void game() {
		game=new Game();
		//Poner panel game
	}
	
	public void upDateGame(String[] players,String[] food) {
		if(game==null) {
			game=new Game();
			ArrayList<Player> playerList=new ArrayList<Player>();
			System.out.println("Se crea el mundo");
			for(int i=0;i<players.length;i++) {
				String[] data=players[i].split(",");
				Player actual=new Player(data[0]);
				double posX=Double.parseDouble(data[1]);
				double posY=Double.parseDouble(data[2]);
				double radius=Double.parseDouble(data[4]);
				Color color=new Color(Integer.parseInt(data[3]));
				Ball ball=new Ball(posX, posY, color, radius);
				actual.setBall(ball);
				playerList.add(actual);
				
			}
			game.setPlayers(playerList);
			ArrayList<Ball> arrFood=new ArrayList<>();
			if(food.length==0){
				System.out.println("Vacio");
			}
			for(int i=0;i<food.length;i++){
				String[] data=food[i].split(",");
				double posX=Double.parseDouble(data[0]);
				double posY=Double.parseDouble(data[1]);
				double radius=Double.parseDouble(data[2]);
				int color=Integer.parseInt(data[3]);
				Ball ball=new Ball(posX, posY, new Color(color), radius);
				arrFood.add(ball);
			}
			game.setArrFood(arrFood);
			draw=new Draw(this);
			remove(lobby);
			add(draw,BorderLayout.CENTER);
			pack();
			setSize(new Dimension(1000,1000));
		}
		else {
			ArrayList<Player> jugadores=game.getPlayers();
			for(int i=0;i<players.length;i++) {
				String[] data=players[i].split(",");
				Player actual=jugadores.get(i);
				if(!actual.getNickName().equals(client.getNick())) {
					
					double posX=Double.parseDouble(data[1]);
					double posY=Double.parseDouble(data[2]);
					double radius=Double.parseDouble(data[4]);
					Color color=new Color(Integer.parseInt(data[3]));
					Ball ball=actual.getBall();
					ball.setPosX(posX);
					ball.setPosY(posY);
					ball.setRadius(radius);
					ball.setColor(color);
				}
				else{
					
					double radius=Double.parseDouble(data[4]);
					
					Ball ball=actual.getBall();
					ball.setRadius(radius);
					
				}
			}
			ArrayList<Ball> arrFood=game.getArrFood();
			if(food.length==0){
				System.out.println("Se borra");
				game.setArrFood(new ArrayList<>());
			}
			for(int i=0;i<food.length;i++){
				String[] data=food[i].split(",");
				Ball actual=arrFood.get(i);
				double posX=Double.parseDouble(data[0]);
				double posY=Double.parseDouble(data[1]);
				double radius=Double.parseDouble(data[2]);
				int color=Integer.parseInt(data[3]);
				actual.setColor(new Color(color));
				actual.setPosX(posX);
				actual.setPosY(posY);
				actual.setRadius(radius);
				
			}
		}
		draw.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String comand=e.getActionCommand();
		if(comand.equals(REGISTER)){
			//logIn.register();
			first.remove(logIn);
			first.add(register,BorderLayout.CENTER);
			pack();
			
		}
		else if(comand.equals(LOGIN))
		{
			first.remove(register);
			first.add(logIn,BorderLayout.CENTER);
			pack();
		}
		else if(comand.equals(SIGN_IN))
		{
			try
			{
				
				//logIn.signIn(logIn.getTxtMail().getText(), logIn.getTxtPass().getText(), logIn.getTxtNick().getText());
				btnRegister.setEnabled(false);
				logIn.getBtnStart().setEnabled(true);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	/*
	public void startGame(){
		
		System.out.println("se trata de crear cliente");
		Client client = new Client(logIn.getTxtNick().getText(), logIn.getTxtMail().getText(), logIn.getTxtPass().getText());
		while(client.getClientGetsSendsGameDataThread() == null) 
		{
			
		}
		while(!client.getClientGetsSendsGameDataThread().isGameReady())
		{
			
		}
		System.out.println("creó cliente");
		this.client = client;
		System.out.println("se asignó el cliente a la interfaz");
		remove(logIn);
		remove(aux);
		draw=new Draw(this);
		add(draw,BorderLayout.CENTER);
		draw.setVisible(true);
		setSize(ANCHO,LARGO);
		revalidate();
	}
	*/
	public Point mousePos() {
		return draw.mousePos();
	}

	public Draw getDraw() {
		return draw;
	}

	public JPanel getAux() {
		return aux;
	}

	public JButton getBtnLogIn() {
		return btnLogIn;
	}

	public JButton getBtnRegister() {
		return btnRegister;
	}

	public LogInPane getLogIn() {
		return logIn;
	}
	
	/*
	public Client getClient() {
		return client;
	}
	*/

	public void setDraw(Draw draw) {
		this.draw = draw;
	}

	public void setAux(JPanel aux) {
		this.aux = aux;
	}

	public void setBtnLogIn(JButton btnLogIn) {
		this.btnLogIn = btnLogIn;
	}

	public void setBtnRegister(JButton btnRegister) {
		this.btnRegister = btnRegister;
	}

	public void setLogIn(LogInPane logIn) {
		this.logIn = logIn;
	}
	/*
	public void setClient(Client client) {
		this.client = client;
	}
	*/


	public LobbyPane getLobby() {
		return lobby;
	}


	public void setLobby(LobbyPane lobby) {
		this.lobby = lobby;
	}


	public Client getClient() {
		return client;
	}


	public void setClient(Client client) {
		this.client = client;
	}


	public Game getGame() {
		return game;
	}


	public void setGame(Game game) {
		this.game = game;
	}

	
	

}
