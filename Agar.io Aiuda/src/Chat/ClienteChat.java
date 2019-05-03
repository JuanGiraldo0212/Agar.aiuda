package Chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import Chat.ClienteChat;
import Chat.ClienteConexionServidorChat;
import connection.Client;

public class ClienteChat extends JPanel{

	private Logger log = Logger.getLogger(ClienteChat.class);
    private JTextArea mensajesChat;
    private Socket socket;
    
    private int puerto;
    private String host;
    private String usuario;
    
    public ClienteChat(String nick) {
    	// Elementos de la ventana
        mensajesChat = new JTextArea();
        mensajesChat.setEnabled(false); // El area de mensajes del chat no se debe de poder editar
        mensajesChat.setLineWrap(true); // Las lineas se parten al llegar al ancho del textArea
        mensajesChat.setWrapStyleWord(true); // Las lineas se parten entre palabras (por los espacios blancos)
        JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
        JTextField tfMensaje = new JTextField("");
        JButton btEnviar = new JButton("Enviar");
        
        // Colocacion de los componentes en la ventana
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(20, 20, 20, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollMensajesChat, gbc);
        
     // Restaura valores por defecto
        gbc.gridwidth = 1;        
        gbc.weighty = 0;
        
        gbc.fill = GridBagConstraints.HORIZONTAL;        
        gbc.insets = new Insets(0, 20, 20, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(tfMensaje, gbc);
        
     // Restaura valores por defecto
        gbc.weightx = 0;
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(btEnviar, gbc);
        
        this.setBounds(400, 100, 400, 500);
        this.setVisible(true);
        
     // configuracion inicial
        
         host = ServidorChat.elHost;
         puerto = ServidorChat.elPuerto;
         usuario = nick;
         
        log.info("Quieres conectarte a " + host + " en el puerto " + puerto + " con el nombre de ususario: " + usuario + ".");
        
        // Se crea el socket para conectar con el Sevidor del Chat
        try {
            socket = new Socket(host, puerto);
        } catch (UnknownHostException ex) {
            log.error("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        } catch (IOException ex) {
            log.error("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        }
        
        // Accion para el boton enviar
        btEnviar.addActionListener(new ClienteConexionServidorChat(socket, tfMensaje, usuario));
    }
    
    /**
     * Recibe los mensajes del chat reenviados por el servidor
     */
    public void recibirMensajesServidor(){
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            log.error("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException ex) {
            log.error("El socket no se creo correctamente. ");
        }
        
        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();
                mensajesChat.append(mensaje + System.lineSeparator());
            } catch (IOException ex) {
                log.error("Error al leer del stream de entrada: " + ex.getMessage());
                conectado = false;
            } catch (NullPointerException ex) {
                log.error("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    //TODO
    public static void main(String[] args) {
        // Carga el archivo de configuracion de log4J
        PropertyConfigurator.configure("log4j.properties");        
              
        ClienteChat c = new ClienteChat("rodri");
        
        JFrame pan = new JFrame();
        pan.add(c);
        pan.setVisible(true);
        
        c.recibirMensajesServidor();
    }
    
}
