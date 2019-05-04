package Audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
 
public class indiCliente extends Thread{

	public final static int PUERTO_SERVIDOR = 5000;
	public final static int TAMANHO_BUFF = 60000;
	public final static int FORMAT_PORT = 9786;
	public final static int AUDIO_PORT = 9787;
	
	private DatagramSocket socketFormat;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private DatagramSocket socketMusica;
	
	public void realizarSolicitud (String cancion) {
		 byte[] buffer = new byte[TAMANHO_BUFF];
		 
	        try {
	            //Obtengo la localizacion de localhost
	            InetAddress direccionServidor = InetAddress.getByName("localhost");
	 
	            //Creo el socket de UDP
	            DatagramSocket socketUDP = new DatagramSocket();
	 
	            String mensaje = cancion;
	 
	            //Convierto el mensaje a bytes
	            buffer = mensaje.getBytes();
	 
	            //Creo un datagrama
	            DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
	 
	            //Lo envio con send
	            System.out.println("Envio el datagrama");
	            socketUDP.send(pregunta);
	 
	            //Preparo la respuesta
	            buffer = new byte[TAMANHO_BUFF];
	            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
	 
	            //Recibo la respuesta
	            socketUDP.receive(peticion);
	            System.out.println("Recibo la peticion");
	 
	            //Cojo los datos y lo muestro
	            mensaje = new String(peticion.getData());
	            System.out.println(mensaje);
	 
	            //cierro el socket
	            socketUDP.close();
	 
	        } catch (SocketException ex) {
//	            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (UnknownHostException ex) {
//	            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (IOException ex) {
//	            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
	        }
	}

	public void iniciarAudio() {
	
		try {	
			System.out.println("cliente inicia a recibir cancion");
		    socketFormat = new DatagramSocket(FORMAT_PORT);
			socketMusica = new DatagramSocket(AUDIO_PORT);
			
//			InetAddress inetAddress = InetAddress.getByName("localhost");
			
			byte[] audioBuffer = new byte[TAMANHO_BUFF];
			byte[] formatBuffer = new byte[TAMANHO_BUFF];
			
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
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private void playAudio() {
		byte[] buffer = new byte[TAMANHO_BUFF];
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
		iniciarAudio();
	}
	
	public static void main(String[] args) {
		indiCliente ic = new indiCliente();
		ic.realizarSolicitud("RISE");
		ic.run();
	}
	
}