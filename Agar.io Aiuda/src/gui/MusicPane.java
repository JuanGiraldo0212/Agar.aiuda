package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MusicPane extends JPanel implements ActionListener{

	public final static String STOP = "pausa";
	public final static String START = "iniciar";
	
	private JComboBox<String> jcCanciones;
	private JButton jbPausa;
	private JButton jbIniciar;
	
	public MusicPane (String[] canciones) {
		
		setLayout(new GridLayout(1,2));
		jcCanciones = new JComboBox<String>(canciones);
		jbPausa = new JButton("||");
		jbIniciar = new JButton("Escuchar");
		
		JPanel jp1 = new JPanel();
		jp1.setLayout(new GridLayout(1,2));
		jp1.add(jbPausa);
		jp1.add(jbIniciar);
		
		add(jcCanciones);
		add(jp1);
		
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		String e = evento.getActionCommand();
		if(e.equals(STOP)) {
			
		}else if (e.equals(START)) {
			
		}
		
	}
	
	public static void main(String[] args) {
		JFrame fra = new JFrame();
		
		fra.setSize(400,80);
		
		String[]Canciones = new String[4];
		Canciones[0] = "Legends Never Die";
		Canciones[1] = "pumped";
		Canciones[2] = "RISE";
		Canciones[3] = "Yoshi";
		MusicPane ms = new MusicPane(Canciones);
		
		fra.add(ms);
		fra.setVisible(true);
	}
	

	
}
