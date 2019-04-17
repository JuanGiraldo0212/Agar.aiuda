package connection;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Ball;
import model.Game;
import model.Player;

public class Server {
	
	public final static int SERVER_PORT=8000;
	public final static int SERVER_PORT_LOBBY=8001;
	public final static int SERVER_PORT_GAME=8002;
	private ServerSocket serverSocket;
	private ServerSocket serverSocketLobby;
	private ServerSocket serverSocketGame;
	private ArrayList<Socket> playersSockets;
	private AsignationThread asignationThread;
	private ArrayList<ServerLobbyThread> lobbyThreads;
	private TimerThread timerThread;
	private Game game;
	private ArrayList<String> userNames;
	private ArrayList<ServerCommunicationThread> serverThreads;
	
	public Server(int wait){
		initGameServer(wait);
	}
	
	public void initGameServer(int wait){
		try {
			game=new Game();
			//game.generateFood();
			userNames=new ArrayList<String>();
			playersSockets=new ArrayList<>();
			lobbyThreads=new ArrayList<ServerLobbyThread>();
			serverThreads=new ArrayList<ServerCommunicationThread>();
			serverSocket = new ServerSocket(SERVER_PORT);
			serverSocketLobby=new ServerSocket(SERVER_PORT_LOBBY);
			serverSocketGame=new ServerSocket(SERVER_PORT_GAME);
			asignationThread = new AsignationThread(this);
			asignationThread.start();
			timerThread=new TimerThread(asignationThread, wait);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getInfoGame() {
		String data="";
		for(int i=0;i<game.getPlayers().size();i++) {
			Player actual=game.getPlayers().get(i);
			String nick=actual.getNickName();
			Ball ball=actual.getBall();
			double posX=ball.getPosX();
			double posY=ball.getPosY();
			double radius=ball.getRadius();
			Color color=ball.getColor();
			if(i==game.getPlayers().size()-1) {
				data+=nick+","+posX+","+posY+","+color.getRGB()+","+radius;
				
			}
			else {
				data+=nick+","+posX+","+posY+","+color.getRGB()+","+radius+" ";
			}
		}
		
		return data;
	}
	
	public void upDatePlayer(String data) {
		String [] dataSplit=data.split(",");
		String nick=dataSplit[0];
		double posX=Double.parseDouble(dataSplit[1]);
		double posY=Double.parseDouble(dataSplit[2]);
		double radius=Double.parseDouble(dataSplit[3]);
		Player actual=game.getPlayer(nick);
		Ball ball=actual.getBall();
		ball.setPosX(posX);
		ball.setPosY(posY);
		ball.setRadius(radius);
	}
	
	public void starTimer() {
		timerThread.start();
	}
	

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public ArrayList<Socket> getPlayersSockets() {
		return playersSockets;
	}

	public void setPlayersSockets(ArrayList<Socket> playersSockets) {
		this.playersSockets = playersSockets;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public TimerThread getTimerThread() {
		return timerThread;
	}

	public void setTimerThread(TimerThread timerThread) {
		this.timerThread = timerThread;
	}

	public ArrayList<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(ArrayList<String> userNames) {
		this.userNames = userNames;
	}

	public ServerSocket getServerSocketLobby() {
		return serverSocketLobby;
	}

	public void setServerSocketLobby(ServerSocket serverSocketLobby) {
		this.serverSocketLobby = serverSocketLobby;
	}

	public ArrayList<ServerLobbyThread> getLobbyThreads() {
		return lobbyThreads;
	}

	public void setLobbyThreads(ArrayList<ServerLobbyThread> lobbyThreads) {
		this.lobbyThreads = lobbyThreads;
	}

	public ServerSocket getServerSocketGame() {
		return serverSocketGame;
	}

	public void setServerSocketGame(ServerSocket serverSocketGame) {
		this.serverSocketGame = serverSocketGame;
	}

	public ArrayList<ServerCommunicationThread> getServerThreads() {
		return serverThreads;
	}

	public void setServerThreads(ArrayList<ServerCommunicationThread> serverThreads) {
		this.serverThreads = serverThreads;
	}
	
	
	

}
