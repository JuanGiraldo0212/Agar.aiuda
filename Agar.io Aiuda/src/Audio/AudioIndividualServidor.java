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

	public final static int AUDIO_PORT = 9788;
	public final static int FORMAT_PORT = 9789;
	public final static String IP_DATOS = "220.1.2.7";
	
	private TargetDataLine targetDataLine;
	private AudioInputStream audioStream;
	private DatagramSocket socketReq;
	private File file;
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];
	private DatagramSocket socketFormato ;
	private DatagramSocket socketMusica ;
	
	public void recibirSolicitud () {
		try {
			System.out.println("server recibe solicitud");
			socketReq = new DatagramSocket(AudioIndividualCliente.PORT_REQ);
			
			byte[] reqBuffer = new byte[60000];
			DatagramPacket request = new DatagramPacket(reqBuffer, reqBuffer.length);
			socketReq.receive(request);
			
			byte[]reqData = request.getData();
			String infoReq = new String(reqData);
			System.out.println("server tranformó solicitud");
			
			file = new File("./Musica/"+infoReq+".wav");
			audioStream = AudioSystem.getAudioInputStream(file);
			
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
		 
		byte[] buffer = new byte[256];
		 
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
			InetAddress inetAddress = InetAddress.getByName(IP_DATOS);
			while(true) {
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					
					String infoFormat = audioStream.getFormat().getSampleRate()+" "+audioStream.getFormat().getSampleSizeInBits()+" "+audioStream.getFormat().getChannels();
					formatBuffer = infoFormat.getBytes();
					
					DatagramPacket packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, inetAddress, FORMAT_PORT);
					socketFormato.send(packetFormat);
					
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, AUDIO_PORT);
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
