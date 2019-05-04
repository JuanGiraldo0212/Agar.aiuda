package Audio;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
 
public class indiServer extends Thread{

	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];	
	
String nombreCancion;

private AudioInputStream audioStream;
private DatagramSocket socketMusica ;
private DatagramSocket socketFormato ;
private File file;
private TargetDataLine targetDataLine;
int puertoCliente;
private InetAddress direccion;

@Override
	public void run() {
		indiAudio();
	}

	public void indiAudio() {
		try {		
			System.out.println("server inicia transmision musica");
			while (true) {
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					
					String infoFormat = audioStream.getFormat().getSampleRate()+" "+audioStream.getFormat().getSampleSizeInBits()+" "+audioStream.getFormat().getChannels();
					formatBuffer = infoFormat.getBytes();
					
					DatagramPacket packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, direccion, indiCliente.FORMAT_PORT);
					socketFormato.send(packetFormat);
					
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, direccion, indiCliente.AUDIO_PORT);
					socketMusica.send(packet);
					sleep(AudioCliente.TIME_SLEEP);
				}
			}
		} catch (Exception ex) {
			 System.out.println(ex);
		}
	}

	public void recibirSolicitud() {

        byte[] buffer = new byte[indiCliente.TAMANHO_BUFF];
 
        try {
            System.out.println("Iniciado el servidor UDP");
            //Creacion del socket
            DatagramSocket socketUDP = new DatagramSocket(indiCliente.PUERTO_SERVIDOR);
 
            //Siempre atendera peticiones
            while (true) {
                 
                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                 
                //Recibo el datagrama
                socketUDP.receive(peticion);
                System.out.println("Recibo la informacion del cliente");
                 
                //Convierto lo recibido y mostrar el mensaje
                String mensaje = new String(peticion.getData());
                
                nombreCancion = mensaje;
                
                //Obtengo el puerto y la direccion de origen
                //Sino se quiere responder, no es necesario
                puertoCliente = peticion.getPort();
                direccion = peticion.getAddress();
 
                mensaje = "preparamos: "+mensaje;
                buffer = mensaje.getBytes();
 
                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
 
                //Envio la información
                System.out.println("Envio la informacion del cliente");
                socketUDP.send(respuesta);
                 
            }
 
        } catch (SocketException ex) {
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void cargarCancion () {
		try {
			System.out.println("Cancion cargada");
			socketMusica = new DatagramSocket(indiCliente.AUDIO_PORT);
			socketFormato = new DatagramSocket(indiCliente.FORMAT_PORT);
			file= new File("./Musica/"+nombreCancion+".wav");
			audioStream= AudioSystem.getAudioInputStream(file);
			setupAudio();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	
	public static void main(String[] args) {
		indiServer is = new indiServer();
		is.recibirSolicitud();
		is.cargarCancion();
		is.run();
	}

 
}