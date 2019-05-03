package Audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioCliente extends Thread {
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private AudioInputStream audio;	
	private MulticastSocket socket;
	
	public AudioCliente(String song) {
		File file = new File("./Musica/"+song);
		try {
			audio = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	private void playAudio() {
		byte[] buffer = new byte[60000];
		try {
			int count;
			while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
				if (count > 0) {
					sleep(300);
					sourceDataLine.write(buffer, 0, count);		
				}
			}
		} catch (Exception e) {
		}
	}

	private void initiateAudio() {
		try {
			socket = new MulticastSocket(AudioServidor.AUDIO_PORT);
			InetAddress inetAddress = InetAddress.getByName(AudioServidor.IP_DATOS);
			socket.joinGroup(inetAddress);
			byte[] audioBuffer = new byte[60000];
			while (true) {
				DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);
				socket.receive(packet);
				try {
					byte audioData[] = packet.getData();
					InputStream byteInputStream = new ByteArrayInputStream(audioData);
					AudioFormat audioFormat = audio.getFormat();				
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
