package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import model.Player;

public class AsignationThread extends Thread{

	private Server  server;
	private boolean inTime;
	
	public AsignationThread(Server server){
		this.server=server;
		inTime=true;
	} 
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while(server.getPlayersSockets().size()<5 && inTime){
			try {
				Socket socket=server.getServerSocket().accept();
				if(inTime) {
					
					if(server.getPlayersSockets().size()==1) {
						server.starTimer();
					}
					server.getPlayersSockets().add(socket);
					System.out.println("Un cliente se ha conectado");
					DataInputStream in =new DataInputStream(socket.getInputStream());
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					
					String[] userData= in.readUTF().split(" ");
					System.out.println("Se conecto: "+userData[0]);
					server.getUserNames().add(userData[0]);
					Player player=new Player(userData[0]);
					server.getGame().getPlayers().add(player);
					ServerLobbyThread lobbyThread=new ServerLobbyThread(server);
					server.getLobbyThreads().add(lobbyThread);
					lobbyThread.start();
					ServerCommunicationThread serverThread= new ServerCommunicationThread(server);
					server.getServerThreads().add(serverThread);
					serverThread.start();
				}
				else {
					System.out.println("Ya no hay tiempo");
				}
				}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Ya no se puede aceptar mas clientes");
		server.getGame().assignUsersBalls();
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public boolean isTimeOut() {
		return inTime;
	}

	public void setTimeOut(boolean timeOut) {
		this.inTime = timeOut;
	}

	public boolean isInTime() {
		return inTime;
	}

	public void setInTime(boolean inTime) {
		this.inTime = inTime;
	}
	
	
	
}
