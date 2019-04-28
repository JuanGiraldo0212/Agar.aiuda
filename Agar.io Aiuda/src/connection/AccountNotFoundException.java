package connection;

public class AccountNotFoundException extends Exception{

	public AccountNotFoundException()
	{
		super("No se encontró ninguna cuenta de usuario nasociada al correo especificado");
	}
}
