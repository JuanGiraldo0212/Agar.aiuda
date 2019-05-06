package Audio;

public class prieba {

	
	public static void main(String[] args) {
		AudioServidor as = new AudioServidor("pumped");
		System.out.println("corriendo server");
		as.start();
	}
	
}
