package Audio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioCliente extends Thread {
	
	public final static int TIME_SLEEP = 300;
	
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private MulticastSocket socketMusica;
	private MulticastSocket socketFormat;
	
	public AudioCliente() {

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
			socketMusica = new MulticastSocket(AudioServidor.AUDIO_PORT);
			socketFormat = new MulticastSocket(AudioServidor.FORMAT_PORT);
			
			InetAddress inetAddress = InetAddress.getByName(AudioServidor.IP_DATOS);
			
			socketMusica.joinGroup(inetAddress);
			socketFormat.joinGroup(inetAddress);
			
			byte[] audioBuffer = new byte[60000];
			byte[] formatBuffer = new byte[60000];
			
			while (true) {
				DatagramPacket packetFormat = new DatagramPacket(formatBuffer, formatBuffer.length);
				socketFormat.receive(packetFormat);
				
				DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);
				socketMusica.receive(packet);
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
	
}
