package AudioViejito;

import java.io.File;

/**
 * Hilo que se encarga de enviar el audio en vivo a todos los clientes que se 
 * encuentran en el multicast
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import connection.Server;

public class HiloAudioUDPServer extends Thread {

	private final byte audioBuffer[] = new byte[10000];
	private TargetDataLine targetDataLine;
	
	
	private Server server;

	public HiloAudioUDPServer(Server server) {

		this.server = server;
	}
	
	public void run() {
		
		try {
			server.setServerSocketMusic(new MulticastSocket(server.MUSIC_PORT));
			setupAudio();
			broadcastAudio();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 16000F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	private void broadcastAudio() {
		try {
			MulticastSocket socket = server.getServerSocketMusic();
			InetAddress inetAddress = InetAddress.getByName(server.MULTICAST_IP);
			socket.joinGroup(inetAddress);
			while (true) {
				int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, server.MUSIC_PORT);
					socket.send(packet);
				}
			}

		} catch (Exception ex) {
			// Handle exceptions
		}
	}

	
	
	private void setupAudio() {
		try {
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getAudioInputStream(new File(server.MUSIC_PATH));
			targetDataLine.open(audioFormat);
			targetDataLine.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

}
