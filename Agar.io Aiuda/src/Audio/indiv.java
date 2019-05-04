package Audio;

public class indiv {

		public static void main(String[] args) {
			AudioIndividualServidor s = new AudioIndividualServidor();
			AudioIndividualCliente c = new AudioIndividualCliente("RISE");
			s.start();
			c.start();
		}
	
}
