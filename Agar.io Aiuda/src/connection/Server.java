package connection;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocketFactory;

import Audio.LocutorTransmissionThreadServer;
import AudioViejito.HiloAudioUDPServer;
import model.Ball;
import model.Game;
import model.Player;

public class Server {
	
	public final static int SERVER_PORT=8000;
	public final static int SERVER_PORT_LOBBY=8001;
	public final static int SERVER_PORT_GAME=8002;
	public static final String KEYSTORE_LOCATION = "./Docs/keystore.jks";
	public static final String KEYSTORE_PASSWORD = "viejito";
	public static final String LOG_PATH = "./Docs/Logs.txt";
	public static final String MUSIC_PATH = "./Docs/Yoshi.wav";
	public static final String MULTICAST_IP = "localhost";
	public static final int MUSIC_PORT = 8001;
	
	
	private ServerSocket serverSocket;
	private ServerSocket serverSocketLobby;
	private ServerSocket serverSocketGame;
	private MulticastSocket serverSocketMusic;
	private ArrayList<Socket> playersSockets;
	private AsignationThread asignationThread;
	private ArrayList<ServerLobbyThread> lobbyThreads;
	private TimerThread timerThread;
	private HiloAudioUDPServer musicThread;
	private Game game;
	private ArrayList<String> userNames;
	private ArrayList<ServerCommunicationThread> serverThreads;
	
	public Server(int wait){
		//initGameServer(wait);
		musicThread = new HiloAudioUDPServer(this);
	}
	
	public void initGameServer(int wait){
		try {
			System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
			System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
			game=new Game();
			//game.generateFood();
			userNames=new ArrayList<String>();
			playersSockets=new ArrayList<>();
			lobbyThreads=new ArrayList<ServerLobbyThread>();
			serverThreads=new ArrayList<ServerCommunicationThread>();
			
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			serverSocket = ssf.createServerSocket(SERVER_PORT);
			
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
		
		data+="&";
		if(game.getArrFood().size()==0){
			//System.out.println("Vacio");
		}
		for(int i=0;i<game.getArrFood().size();i++){
			Ball actual=game.getArrFood().get(i);
			double posX=actual.getPosX();
			double posY=actual.getPosY();
			double radius=actual.getRadius();
			int color=actual.getColor().getRGB();
			if(i==game.getArrFood().size()-1){
				data+=posX+","+posY+","+radius+","+color;
			}
			else{
				data+=posX+","+posY+","+radius+","+color+" ";
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
		ArrayList<Ball> food=game.getArrFood();
		if(food.size()==0){
			//System.out.println("Vacio");
		}
		for(int i=0;i<food.size();i++){
			Ball actualBall=food.get(i);
			if(ball.eat(actualBall)){
				food.remove(actualBall);
				System.out.println("Come");
			}
		}
		
	}
	
	public String registerPlayer(String nickname, String email, String password) throws IOException
	{
		String[] datosUser = loginQuery(email, password);
		String result = "ExistingAccountException";
		if(datosUser[1] == null)
		{
			File logs = new File(LOG_PATH);
			PrintWriter pw = new PrintWriter(new FileWriter(logs, true));
			pw.write(email + "," + password + "," + nickname + "\n");
			pw.flush();
			pw.close();
			result = "You're registered";
		}
		return result;
	}
	
	public String[] loginQuery(String email, String password) throws IOException
	{
		String[] result = new String[3];
		BufferedReader br = new BufferedReader(new FileReader(new File(LOG_PATH)));
		String line;
		boolean found = false;
		while((line = br.readLine()) != null && !found)
		{
			String[] userData = line.split(",");
			if(email.equals(userData[1]))
			{
				result[1] = userData[1];
			}
			if(password.equals(userData[2]))
			{
				result[2] = userData[2];
			}
			if(result[1] != null && result[2] != null)
			{
				result[0] = userData[0];
			}
			if(result[1] != null)
			{
				found = true;
			}
		}
		br.close();
		return result;
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

	public MulticastSocket getServerSocketMusic() {
		return serverSocketMusic;
	}

	public void setServerSocketMusic(MulticastSocket serverSocketMusic) {
		this.serverSocketMusic = serverSocketMusic;
	}

	public HiloAudioUDPServer getMusicThread() {
		return musicThread;
	}

	public void setMusicThread(HiloAudioUDPServer musicThread) {
		this.musicThread = musicThread;
	}
	

}
