package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

import Audio.ClientTransmissionThread;
import gui.ClientGUI;

public class Client {
	public final static int SERVER_PORT=8000;
	public final static int SERVER_PORT_LOBBY=8001;
	public final static int SERVER_PORT_GAME=8002;
	public static final String TRUSTTORE_LOCATION = "./Docs/keystore.jks";
	private DataInputStream in;
	private DataOutputStream out;
	private String serverIp;
	private Socket socketConnection;
	private Socket socketLobby;
	private Socket socketGame;
	private String nick;
	private ClientGUI gui;
	private ClientComunicationThread clientThread;
	private char[] password = {'v','i','e','j','i','t', 'o'};
	
	public Client(String serverIp,String data,ClientGUI client) throws AccountNotFoundException, WrongPasswordException, ExistingAccountException{
		try {
			this.serverIp=serverIp;
			gui=client;
			System.setProperty("javax.net.ssl.trustStore", TRUSTTORE_LOCATION);
			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(new FileInputStream(TRUSTTORE_LOCATION), password);
			socketConnection = sf.createSocket(serverIp, SERVER_PORT);
			
			in=new DataInputStream(socketConnection.getInputStream());
			out=new DataOutputStream(socketConnection.getOutputStream());
			String [] userInfo=data.split(" ");
			nick=userInfo[0];
			out.writeUTF(data);
			String respond = in.readUTF();
			if(respond.equals("AccountNotFoundException"))
			{
				throw new AccountNotFoundException();
			}
			else if(respond.equals("WrongPasswordException"))
			{
				throw new WrongPasswordException();
			}
			else if(respond.equals("ExistingAccountException"))
			{
				throw new ExistingAccountException();
			}
			
		} catch (IOException | KeyStoreException |NoSuchAlgorithmException | CertificateException e) {
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
