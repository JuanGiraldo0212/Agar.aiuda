package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Audio.MakeSound;

public class Musica extends JPanel implements ActionListener{

	public static String BACK ="anterior";
	public static String STOP ="pausa";
	public static String NEXT ="siguiente";
	public static String START ="iniciar";
	
	JComboBox<String> jcCanciones;
	JButton btnAnterior;
	JButton btnSiguiente;
	JButton btnPausar;
	JButton btnEmpezar;
	
	int laCancion;
	MakeSound MS;
	
	public Musica () {
		MS = new MakeSound();
		setLayout(new GridLayout(2,1));
		jcCanciones = new JComboBox<String>(MS.getArrMusica());
		btnAnterior = new JButton("<<");
		btnSiguiente = new JButton(">>");
		btnPausar = new JButton("||");
		btnEmpezar = new JButton("Run");
		
		btnAnterior.setEnabled(true);
		btnSiguiente.setEnabled(true);
		btnPausar.setEnabled(true);
		btnEmpezar.setEnabled(true);
		
		JPanel jp1 = new JPanel();
		jp1.setLayout(new GridLayout(1,4));
		jp1.add(btnAnterior);
		jp1.add(btnPausar);
		jp1.add(btnEmpezar);
		jp1.add(btnSiguiente);
		
		add(jcCanciones);
		add(jp1);

		laCancion = jcCanciones.getSelectedIndex();
		
		btnAnterior.addActionListener(this);
		btnAnterior.setActionCommand(BACK);
		
		btnPausar.addActionListener(this);
		btnPausar.setActionCommand(STOP);
		
		btnEmpezar.addActionListener(this);
		btnEmpezar.setActionCommand(START);
		
		btnSiguiente.addActionListener(this);
		btnSiguiente.setActionCommand(NEXT);		
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		String ev = evento.getActionCommand();
		//TODO
		if(ev.equals(BACK)){
			MS.detener();
			MS.playSound(MS.buscarCancion(laCancion-1));
		}else if (ev.equals(STOP)) {
			MS.detener();
		}else if (ev.equals(NEXT)) {
			MS.detener();
			MS.playSound(MS.buscarCancion(laCancion+1));
		}else if (ev.equals(START)) {
			//MS.detener();
			MS.playSound(MS.buscarCancion(laCancion));
		}
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new Musica());
		frame.setVisible(true);
	}
	
	
}
