package Audio;

public class PruebaAudioFinal {

	public static void main(String[] args) {
		AudioServidor s= new AudioServidor("RISE.wav");

		AudioCliente c= new AudioCliente("RISE.wav");
		s.start();
		c.start();
//		AudioCliente ac = new AudioCliente();
//		ac.start();
//		
	}
	
}
