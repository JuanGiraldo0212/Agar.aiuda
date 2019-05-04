package Audio;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioIndividualServidor extends Thread{
	
	private String ipClient;
	private TargetDataLine targetDataLine;
	private AudioInputStream audioStream;
	private DatagramSocket socketReq;
	private File file;
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];
	private DatagramSocket socketFormato ;
	private DatagramSocket socketMusica ;
	
	public AudioIndividualServidor () 
	{
		recibirSolicitud();
	}
	
	public void recibirSolicitud () {
		try {
			socketReq = new DatagramSocket(AudioIndividualCliente.AUDIO_REQUEST_PORT);
			byte[] reqBuffer = new byte[60000];
			
			System.out.println("server recibe solicitud");
			DatagramPacket request = new DatagramPacket(reqBuffer, reqBuffer.length);
			socketReq.receive(request);
			byte[]reqData = request.getData();
			String[] infoReq = new String(reqData).split(",");
			System.out.println("server tranformó solicitud");
			
			file = new File("./Musica/"+infoReq[0]+".wav");
			ipClient = infoReq[1];
			audioStream = AudioSystem.getAudioInputStream(file);
			System.out.println("Canción construida");
			
			setupAudio();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public void mandarAudio() {
		try {
			InetAddress inetAddress = InetAddress.getByName(ipClient);
			while(true) {
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					
					String infoFormat = audioStream.getFormat().getSampleRate()+" "+audioStream.getFormat().getSampleSizeInBits()+" "+audioStream.getFormat().getChannels();
					formatBuffer = infoFormat.getBytes();
					
					DatagramPacket packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, inetAddress,AudioIndividualCliente.FORMAT_PORT);
					socketFormato.send(packetFormat);
					
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, AudioIndividualCliente.AUDIO_PORT);
					socketMusica.send(packet);
					sleep(AudioCliente.TIME_SLEEP);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	@Override
	public void run() {
		mandarAudio();
	}
	
	
}
