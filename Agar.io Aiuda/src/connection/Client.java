package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import gui.ClientGUI;

public class Client {
	public final static int SERVER_PORT=8000;
	public final static int SERVER_PORT_LOBBY=8001;
	public final static int SERVER_PORT_GAME=8002;
	private DataInputStream in;
	private DataOutputStream out;
	private String serverIp;
	private Socket socketConnection;
	private Socket socketLobby;
	private Socket socketGame;
	private String nick;
	private ClientGUI gui;
	private ClientComunicationThread clientThread;
	
	public Client(String serverIp,String data,ClientGUI client){
		try {
			this.serverIp=serverIp;
			gui=client;
			socketConnection=new Socket(serverIp, SERVER_PORT);
			in=new DataInputStream(socketConnection.getInputStream());
			out=new DataOutputStream(socketConnection.getOutputStream());
			String [] userInfo=data.split(" ");
			nick=userInfo[0];
			out.writeUTF(data);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public void startLobbySocket() {
		try {
			socketLobby=new Socket(serverIp,SERVER_PORT_LOBBY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startGameComm() {
		try {
			socketGame=new Socket(serverIp,SERVER_PORT_GAME);
			clientThread=new ClientComunicationThread(this);
			clientThread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	public void upDateGame(String[] players,String[] food) {
		gui.upDateGame(players,food);
	}

	public Socket getSocketLobby() {
		return socketLobby;
	}

	public void setSocketLobby(Socket socketLobby) {
		this.socketLobby = socketLobby;
	}

	public ClientGUI getGui() {
		return gui;
	}

	public void setGui(ClientGUI gui) {
		this.gui = gui;
	}

	public Socket getSocketGame() {
		return socketGame;
	}

	public void setSocketGame(Socket socketGame) {
		this.socketGame = socketGame;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	

}
