package Audio;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import connection.Server;


public class servidorAudioFinal extends Thread
{
	


		private Server server;
		private final byte audioBuffer[] = new byte[10000];
		private TargetDataLine targetDataLine;

		public void run()
		{
			setupAudio();
			broadcastAudio();
		}
		public servidorAudioFinal(Server server)
		{
			
			this.server = server;
			
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
			try
			{
				MulticastSocket socket = new MulticastSocket(server.MUSIC_PORT);
				InetAddress inetAddress = InetAddress.getByName(server.MULTICAST_IP);
				socket.joinGroup(inetAddress);
				while (true) {
					int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
					if (count > 0) {
						DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, server.MUSIC_PORT);
						socket.send(packet);
					}
				}

			} catch (Exception ex) {
				// Handle exceptions
			}
		}

		
		
		private void setupAudio() {
			try {
				AudioFormat audioFormat = getAudioFormat();
				DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
				targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
				targetDataLine.open(audioFormat);
				targetDataLine.start();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(0);
			}
		}

	

	
}
