package Audio;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;

import connection.Server;

public class IndividualMusicRequestServer extends Thread {
	
	public final static int PUERTO_SERVIDOR = 5000;
	public final static int FORMAT_PORT = 9786;
	public final static int AUDIO_PORT = 9787;
	public final static int TIME_SLEEP = 300;
	public final static int TAMANHO_BUFF = 60000;
	
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];	
	
	private Server server;

	
	public IndividualMusicRequestServer(Server server)
	{
		this.server = server;
	}
	
	@Override
	public void run() 
	{
		super.run();
		try {
            System.out.println("Iniciado el servidor UDP"); 
            //Siempre atendera peticiones
            while (true) {
            	byte[] buffer = new byte[TAMANHO_BUFF];
            	                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                //Recibo el datagrama
                server.getServerSocketMusica().receive(peticion);
                System.out.println("Recibo la informacion del cliente");
                 
                //Convierto lo recibido y mostrar el mensaje
                String mensaje = new String(peticion.getData());
                
               
                String nombreCancion = new String(mensaje);
                
                //Obtengo el puerto y la direccion de origen
                //Sino se quiere responder, no es necesario
                int puertoCliente = peticion.getPort();
                InetAddress direccionCliente = peticion.getAddress();                
                String respuesta = "preparamos: "+mensaje;
                buffer = respuesta.getBytes();
                
                for (IndividualAudioServer audioThread : server.getAudioIndividualServerThreads()) 
                {
					if(audioThread.getDireccionCliente().equals(direccionCliente))
					{
						audioThread.setPlaying(false);
						audioThread.getTargetDataLine().close();
					}
				}
                
                //creo el datagrama
                DatagramPacket paqueterespuesta = new DatagramPacket(buffer, buffer.length, direccionCliente, puertoCliente);
 
                //Envio la información
                
                server.getServerSocketMusica().send(paqueterespuesta);
                System.out.println("Envio la informacion del cliente");
                IndividualAudioServer audio = new IndividualAudioServer(server, nombreCancion, puertoCliente, direccionCliente);
                server.getAudioIndividualServerThreads().add(audio);
                audio.start();
             
            }
 
        } catch (SocketException ex) {
        	ex.printStackTrace();
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        	ex.printStackTrace();
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
}
