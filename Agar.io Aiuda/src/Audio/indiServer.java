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
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
 
public class indiServer extends Thread{

	public final static int PUERTO_SERVIDOR = 5000;
	public final static int FORMAT_PORT = 9786;
	public final static int AUDIO_PORT = 9787;
	public final static int TIME_SLEEP = 300;
	public final static int TAMANHO_BUFF = 60000;
	
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];	
	
private String nombreCancion;

private AudioInputStream audioStream;
private DatagramSocket socketMusica ;
private File file;
private TargetDataLine targetDataLine;
int puertoCliente;
private InetAddress direccionCliente;

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
					System.out.println(direccionCliente);
					DatagramPacket packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, direccionCliente, FORMAT_PORT);
					socketMusica.send(packetFormat);
					
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, direccionCliente, AUDIO_PORT);
					socketMusica.send(packet);
					sleep(TIME_SLEEP);
				}
			}
		} catch (Exception ex) {
			 System.out.println(ex);
		}
	}

	public void recibirSolicitud() {

        byte[] buffer = new byte[TAMANHO_BUFF];
 
        try {
            System.out.println("Iniciado el servidor UDP");
            //Creacion del socket
            socketMusica = new DatagramSocket(PUERTO_SERVIDOR);
 
            //Siempre atendera peticiones
            while (true) {
                 
                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                 
                //Recibo el datagrama
                socketMusica.receive(peticion);
                System.out.println("Recibo la informacion del cliente");
                 
                //Convierto lo recibido y mostrar el mensaje
                String mensaje = new String(peticion.getData());
                
               
                nombreCancion = new String(mensaje);
                
                //Obtengo el puerto y la direccion de origen
                //Sino se quiere responder, no es necesario
                puertoCliente = peticion.getPort();
                direccionCliente = peticion.getAddress();                
                mensaje = "preparamos: "+mensaje;
                buffer = mensaje.getBytes();
 
                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccionCliente, puertoCliente);
 
                //Envio la información
                
                socketMusica.send(respuesta);
                System.out.println("Envio la informacion del cliente");
                cargarCancion(nombreCancion);
            }
 
        } catch (SocketException ex) {
        	ex.printStackTrace();
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        	ex.printStackTrace();
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void cargarCancion (String nombreCancion) {
		try {
			System.out.println("Cancion cargada");
			System.out.println(nombreCancion);
			String path = "./Musica/"+nombreCancion.trim()+".wav";
			System.out.println(path);
			file= new File(path);
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
			start();
		} catch (IllegalThreadStateException ex) {
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		indiServer is = new indiServer();
		is.recibirSolicitud();
		System.out.println("se cargó la canción");
	}

 
}