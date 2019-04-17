package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RegisterPane extends JPanel implements ActionListener{
	
	public final static String START="Start";
	ClientGUI main;
	JPanel aux;
	JPanel aux2;
	JPanel aux3;
	JPanel aux4;
	JLabel lblNick;
	JTextField txtNick;
	JLabel lblMail;
	JTextField txtMail;
	JLabel lblPass;
	JTextField txtPass;
	JButton btnStart;
	
	public RegisterPane(ClientGUI main) {
		
		this.main=main;
		setLayout(new GridLayout(4,1));
		setAlignmentX(LEFT_ALIGNMENT);
		aux=new JPanel();
		aux2=new JPanel();
		aux3=new JPanel();
		aux4=new JPanel();
		lblNick=new JLabel("NickName:" );
		txtNick=new JTextField();
		txtNick.setPreferredSize(new Dimension(100, 20));
		lblMail=new JLabel("Correo:");
		txtMail=new JTextField();
		txtMail.setPreferredSize(new Dimension(120, 20));
		lblPass=new JLabel("Password:");
		txtPass=new JTextField();
		txtPass.setPreferredSize(new Dimension(100, 20));
		btnStart=new JButton("Start");
		btnStart.addActionListener(this);
		//btnStart.setEnabled(false);
		btnStart.setActionCommand(START);
		aux.add(lblMail);
		aux.add(txtMail);
		aux2.add(lblPass);
		aux2.add(txtPass);
		aux3.add(new JLabel(" "));
		aux3.add(btnStart);
		aux3.add(new JLabel(" "));
		aux4.add(lblNick);
		aux4.add(txtNick);
		add(aux4);
		add(aux);
		add(aux2);
		add(aux3);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command=e.getActionCommand();
		if(command.equals(START)) {
			String resp=JOptionPane.showInputDialog("Ingrese la ip del servidor");
			main.register(resp, txtNick.getText()+" "+txtMail.getText()+" "+txtPass.getText());
			main.lobby();
		}
		
	}

}
