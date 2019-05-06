package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServidorChat {

	public static int elPuerto = 12345;
	public static String elHost = "172.30.179.16";

	public ServidorChat() {
		// Carga el archivo de configuracion de log4J
		PropertyConfigurator.configure("log4j.properties");
		Logger log = Logger.getLogger(ServidorChat.class);

		int puerto = elPuerto;
		int maximoConexiones = 10; // Maximo de conexiones simultaneas
		ServerSocket servidor = null;
		Socket socket = null;
		ServerMensajesChat mensajes = new ServerMensajesChat();

		try {
			// Se crea el serverSocket
			servidor = new ServerSocket(puerto, maximoConexiones);

			// Bucle infinito para esperar conexiones
			while (true) {
				log.info("Servidor a la espera de conexiones.");
				socket = servidor.accept();
				log.info("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");

				ServerConexionClienteChat cc = new ServerConexionClienteChat(socket, mensajes);
				cc.start();
			}
		} catch (IOException ex) {
			log.error("Error: " + ex.getMessage());
		} finally {
			try {
				socket.close();
				servidor.close();
			} catch (IOException ex) {
				log.error("Error al cerrar el servidor: " + ex.getMessage());
			}
		}
	}
	
	public static void main(String[] args) {
		ServidorChat sc = new ServidorChat();
	}
}