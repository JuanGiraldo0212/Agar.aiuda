package Audio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioServidor extends Thread {

	public final static int AUDIO_PORT = 9786;
	public final static String IP_DATOS = "239.1.2.2";
	
	private byte audioBuffer[] = new byte[60000];
	private TargetDataLine targetDataLine;
	private AudioInputStream audioStream;
	private DatagramSocket socket ;
	private File file;
	
	public void run() {
		broadcastAudio();
	}

	public AudioServidor(String song) {
		try {
			socket = new DatagramSocket();
			file= new File("./Musica/"+song);
			audioStream= AudioSystem.getAudioInputStream(file);
			setupAudio();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcastAudio() {
		try {		
			InetAddress inetAddress = InetAddress.getByName(IP_DATOS);
			while (true) {
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, AUDIO_PORT);
					socket.send(packet);
					sleep(300);
				}
			}
		} catch (Exception ex) {
			 System.out.println(ex);
		}
	}

	public void setupAudio() {
		try {
			AudioFormat audioFormat =audioStream.getFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
		} catch (Exception ex) {
			 System.out.println(ex);
			ex.printStackTrace();
			System.exit(0);
		}
	}

}
