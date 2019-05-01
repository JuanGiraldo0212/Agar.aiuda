package Audio;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

import connection.Client;


public class CancionCliente  extends Thread{

	private Client client;
	
	public CancionCliente(Client client) 
	{
		this.client = client;
	}
	private AudioFormat getAudioFormat() {
		float sampleRate = 16000F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	public void run()
	{ 
            // play soundfile from server
            System.out.println("Client: reading from 127.0.0.1:6666");
            try
            {
            	MulticastSocket musicSocket = new MulticastSocket(Client.MUSIC_PORT);
            	client.setSocketMusic(musicSocket);
            	AudioInputStream audioInputStream;
            	musicSocket.joinGroup(InetAddress.getByName(client.MULTICAST_IP));
            	byte[] audioBuffer = new byte[10000];
            	while(true) 
            	{
	                if (musicSocket.isConnected())
	                {
	                    DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);
	    				musicSocket.receive(packet);
	    				System.out.println("recepción audio");
	    				byte audioData[] = packet.getData();
	    				InputStream byteInputStream = new ByteArrayInputStream(audioData);
						AudioFormat audioFormat = getAudioFormat();
						audioInputStream = new AudioInputStream(byteInputStream, audioFormat,
								audioData.length / audioFormat.getFrameSize());	    				
	                    play(audioInputStream);
	                }
                }
            }catch (Exception e) {
            	
            }
        

        System.out.println("Client: end");
    }


    private  synchronized void play(final AudioInputStream in) throws Exception 
    {
        try (Clip clip = AudioSystem.getClip()) {
            clip.open(in);
            clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            clip.drain();
        }
    }
}


