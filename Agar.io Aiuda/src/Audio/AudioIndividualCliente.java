package Audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioIndividualCliente extends Thread{
	
	String song;
	
	public AudioIndividualCliente(String cancion) {
		song = cancion;
	}
	
	public final static int PORT_REQ = 9788;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
//	private MulticastSocket socketMusica;
//	private MulticastSocket socketFormat;
//	private MulticastSocket socketReq;
	String hostname;
	
	public AudioIndividualCliente() {

	}

	private void initiateAudio(String laCancion) {
		try {
			
			InetAddress inetAddress = InetAddress.getByName(AudioServidor.IP_DATOS);
			DatagramSocket socketReq = new DatagramSocket(PORT_REQ);
			DatagramSocket socketFormat = new DatagramSocket(AudioIndividualServidor.FORMAT_PORT);
			DatagramSocket socketMusica= new DatagramSocket(AudioIndividualServidor.AUDIO_PORT);
			
			byte[] audioBuffer = new byte[60000];
			byte[] formatBuffer = new byte[60000];
			
			while (true) {
				
				System.out.println("solicitud");
				DatagramPacket request = new DatagramPacket(laCancion.getBytes(), laCancion.getBytes().length, inetAddress, PORT_REQ);
				socketReq.send(request);
				System.out.println("solicitud enviada");
							
				System.out.println("recibir formato");
				DatagramPacket packetFormat = new DatagramPacket(formatBuffer, formatBuffer.length);
				socketFormat.receive(packetFormat);
				System.out.println("formato cargado");
				
				System.out.println("recibir audio");
				DatagramPacket packetAudio = new DatagramPacket(audioBuffer, audioBuffer.length);
				socketMusica.receive(packetAudio);
				System.out.println("audio cargado");
				
				try {
					byte audioData[] = packetAudio.getData();
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
					System.out.println("Sonido");
					playAudio();
				} catch (Exception e) {
					System.out.println("erro");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void playAudio() {
		byte[] buffer = new byte[60000];
		try {
			int count;
			while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
				if (count > 0) {
					sleep(AudioCliente.TIME_SLEEP);
					sourceDataLine.write(buffer, 0, count);		
				}
			}
		} catch (Exception e) {
		}
	}
	
	@Override
	public void run() {
		initiateAudio(song);
	}
	
}
