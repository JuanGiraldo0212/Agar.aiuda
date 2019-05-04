package Audio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import connection.Client;

public class AudioIndividualCliente extends Thread{
	
	public final static int AUDIO_PORT = 9790;
	public final static int FORMAT_PORT = 9789;
	public final static int AUDIO_REQUEST_PORT = 50911;
	private String song;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private boolean stop;
	private Client client;
	
	public AudioIndividualCliente(String cancion, Client client)
	{
		this.client = client;
		song = cancion;
		stop = true;
	}

	private void initiateAudio(String laCancion) {
		try {
			
			InetAddress inetAddress = InetAddress.getByName(client.getServerIp());
			DatagramSocket socketReq = new DatagramSocket(AUDIO_REQUEST_PORT);
			DatagramSocket socketFormat = new DatagramSocket(FORMAT_PORT);
			DatagramSocket socketMusica= new DatagramSocket(AUDIO_PORT);
			
			byte[] audioBuffer = new byte[60000];
			byte[] formatBuffer = new byte[60000];
			
			String ip = InetAddress.getLocalHost().getHostAddress();
			while (true) {
				while(stop){}
				System.out.println("solicitud");
				DatagramPacket request = new DatagramPacket((laCancion + "," + ip).getBytes(), (laCancion + "," + ip).getBytes().length, inetAddress, AUDIO_REQUEST_PORT);
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
			while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) 
			{
				while(stop){}
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
		stop = false;
		initiateAudio(song);
		
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}

	public void setAudioInputStream(AudioInputStream audioInputStream) {
		this.audioInputStream = audioInputStream;
	}

	public SourceDataLine getSourceDataLine() {
		return sourceDataLine;
	}

	public void setSourceDataLine(SourceDataLine sourceDataLine) {
		this.sourceDataLine = sourceDataLine;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
}
