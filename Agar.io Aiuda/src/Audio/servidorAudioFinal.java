package Audio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.io.File;
import java.nio.file.Files;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class servidorAudioFinal extends Thread {

	public static int SOCKET_AUDIO = 9786;
	public static String IP_AUDIO = "229.5.6.7";
	private byte audioBuffer[] = new byte[10000];
	private TargetDataLine targetDataLine;

	public void run() {
		setupAudio();
		broadcastAudio();
	}

	public servidorAudioFinal() {

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
			MulticastSocket socket = new MulticastSocket(SOCKET_AUDIO);
			InetAddress inetAddress = InetAddress.getByName(IP_AUDIO);
			socket.joinGroup(inetAddress);
			
			File file = new File ("./Musica/RISE.wav");
			audioBuffer = Files.readAllBytes(file.toPath());
			
			while (true) {
				int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9786);
					socket.send(packet);
				}
			}

		} catch (Exception ex) {
			 System.out.println(ex);
		}
	}

	private void setupAudio() {
		try {
			System.out.println("inicio setup");
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			System.out.println("fin setup");
		} catch (Exception ex) {
			 System.out.println(ex);
			ex.printStackTrace();
			System.exit(0);
		}
	}

}
