package Audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import connection.Server;

public class CancionServer extends Thread{

	private Server server;
	
	public CancionServer(Server server) 
	{
		this.server = server;
	}

	public void run() {
		FileInputStream soundFile = null;
		
		try 
		{
			soundFile = new FileInputStream(server.MUSIC_PATH);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("server: " + soundFile);

		try
		{
			DatagramSocket datagramSocket = new DatagramSocket(server.MUSIC_PORT);
			InetAddress multicastIP = InetAddress.getByName(server.MULTICAST_IP);
			if (datagramSocket.isBound()) 
			{
				byte buffer[] = new byte[2048];
				int count;
				while ((count = soundFile.read(buffer)) != -1)
				{
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastIP, server.MUSIC_PORT);
					datagramSocket.send(packet);
					System.out.println("envio audio");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("server: shutdown");
	}
}
