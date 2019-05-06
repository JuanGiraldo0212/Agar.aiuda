package Audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioCliente2 extends Thread {
	
	public final static int TIME_SLEEP = 300;
	public final static int AUDIO_PORT = 9786;
	public final static int FORMAT_PORT = 9787;
	public final static String IP_DATOS = "239.1.2.2";
	
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private MulticastSocket socketMusica;
	private MulticastSocket socketFormat;
//	private Boolean isPlaying;
	public AudioCliente2()
	{
//		isPlaying = true;
		try {
			socketMusica = new MulticastSocket(AUDIO_PORT);
			socketFormat = new MulticastSocket(FORMAT_PORT);
			
			InetAddress inetAddress = InetAddress.getByName(IP_DATOS);
			System.out.println(inetAddress);
			socketMusica.joinGroup(inetAddress);
			socketFormat.joinGroup(inetAddress);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void playAudio() {
		byte[] buffer = new byte[60000];
		try {
			int count;
			while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
				if (count > 0) {
					sleep(TIME_SLEEP);
					sourceDataLine.write(buffer, 0, count);		
				}
			}
		} catch (Exception e) {
		}
	}

	private void initiateAudio() {
		try {
			System.out.println("preparando inicio de audio");
			byte[] audioBuffer = new byte[60000];
			byte[] formatBuffer = new byte[60000];
			
			while (true) {
				System.out.println("recibiendo formato");
				DatagramPacket packetFormat = new DatagramPacket(formatBuffer, formatBuffer.length);
				socketFormat.receive(packetFormat);
				System.out.println("formato recibido");
				
				System.out.println("recibiendo cancion");
				DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);
				socketMusica.receive(packet);
				System.out.println("canción recibida");
				try {
					byte audioData[] = packet.getData();
					byte formatData[] = packetFormat.getData();
					
					String infoFormato = new String(formatData);
					String[] data  = infoFormato.split(" ");

					float data0 = Float.parseFloat(data[0]);
					int data1 = Integer.parseInt(data[1]);
					double data2 = Double.parseDouble(data[2]);					
					
					AudioFormat af = new AudioFormat(data0,data1,(int)data2, true, false);
					
					InputStream byteInputStream = new ByteArrayInputStream(audioData);
					
					AudioFormat audioFormat = af;	
					audioInputStream = new AudioInputStream(byteInputStream, audioFormat,audioData.length / audioFormat.getFrameSize());					
					DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
					sourceDataLine.open(audioFormat);
					sourceDataLine.start();
					playAudio();
				} catch (Exception e) {
					System.out.println("erro");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		initiateAudio();
	}
	
	public static void main(String[] args) {
		AudioCliente2 ac = new AudioCliente2();
		ac.start();
	}
	
}