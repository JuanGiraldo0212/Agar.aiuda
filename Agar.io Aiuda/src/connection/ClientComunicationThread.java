package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.Player;

public class ClientComunicationThread extends Thread{

	private Client client;
	
	public ClientComunicationThread(Client client) {
		
		this.client=client;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			System.out.println("Se crea hilo Server comm");
			DataInputStream in = new DataInputStream(client.getSocketGame().getInputStream());
			DataOutputStream out= new DataOutputStream(client.getSocketGame().getOutputStream());
			while(true) {
				System.out.println("Entra al ciclo");
				String data=in.readUTF();
				System.out.println("Entra info");
				String[] splitData=data.split(" ");
				client.upDateGame(splitData);
				Player player=client.getGui().getGame().getPlayer(client.getNick());
				double posX=player.getBall().getPosX();
				double posY=player.getBall().getPosY();
				double radius=player.getBall().getRadius();
				String send=client.getNick()+","+posX+","+posY+","+radius;
				out.writeUTF(send);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
