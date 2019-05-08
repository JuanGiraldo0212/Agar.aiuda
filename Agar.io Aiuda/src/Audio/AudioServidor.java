package Audio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioServidor extends Thread {

	public final static int AUDIO_PORT = 9786;
	public final static int FORMAT_PORT = 9787;

	public final static String IP_DATOS = "224.0.0.2";
	
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];
	
	private AudioInputStream audioStream;
	private SourceDataLine sLine;
	
	private TargetDataLine targetDataLine;
//	private DatagramSocket socketMusica ;
//	private DatagramSocket socketFormato ;
	private File file;
	private String[]Canciones;
	
//	private MulticastSocket socketMusicaSer;
//	private MulticastSocket socketFormatSer;
	private InetAddress inetAddress;
	
	private DatagramSocket server;
	
	private DatagramPacket packetFormat;
	private DatagramPacket packetAudio;
	
	@Override
	public void run() {
		broadcastAudio();
	}

	public AudioServidor(String song) {
		try {
//			socketMusica = new DatagramSocket();
//			socketFormato = new DatagramSocket();
			file= new File("./Musica/"+song.trim()+".wav");
			audioStream= AudioSystem.getAudioInputStream(file);
			server = new DatagramSocket();
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
			inetAddress = InetAddress.getByName(IP_DATOS);
			
//			socketMusicaSer = new MulticastSocket(AUDIO_PORT);
//			socketFormatSer = new MulticastSocket(FORMAT_PORT);
//			
//			socketMusicaSer.setTimeToLive(255);
//			socketFormatSer.setTimeToLive(255);
//			
//			socketMusicaSer.joinGroup(inetAddress);
//			socketFormatSer.joinGroup(inetAddress);
			
			
			while (true) {
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					
					String infoFormat = audioStream.getFormat().getSampleRate()+" "+audioStream.getFormat().getSampleSizeInBits()+" "+audioStream.getFormat().getChannels();
					formatBuffer = infoFormat.getBytes();
					
					packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, inetAddress, FORMAT_PORT);
//					socketFormatSer.send(packetFormat);
					server.send(packetFormat);
					
					packetAudio = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, AUDIO_PORT);
//					socketMusicaSer.send(packet);
					server.send(packetAudio);
					
					sleep(AudioCliente.TIME_SLEEP);
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
			
			System.out.println("LISTO PARA MUSICA MULTICAST");
		} catch (Exception ex) {
			 System.out.println(ex);
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public String[] getCanciones() {
		return Canciones;
	}

	public void setCanciones(String[] canciones) {
		Canciones = canciones;
	}

	public static void main(String[] args) {
		AudioServidor as = new AudioServidor("pumped");
		as.start();
		
	}
	
	
}
