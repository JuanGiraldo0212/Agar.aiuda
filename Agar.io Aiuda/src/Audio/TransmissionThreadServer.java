package Audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class TransmissionThreadServer extends Thread{

	private TargetDataLine targetDataLine;
	private final byte audioBuffer[] = new byte[10000];
	
	public void transmitir() {

		try {
			AudioFormat audioFormat = new AudioFormat(8000, 16, 2, true, false);
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}

		try (MulticastSocket multicastSocket = new MulticastSocket()) {

			//TODO
			InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
			multicastSocket.joinGroup(inetAddress);

			DatagramPacket packet;

			while (true) {
				
				int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9877);
					multicastSocket.send(packet);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		transmitir();
	}
	
	
	public static void main(String[] args) {
		TransmissionThreadServer hT = new TransmissionThreadServer();
		hT.start();
	}
}