package exceptions;

@SuppressWarnings("serial")
public class InvalidPositionException extends Exception{

	public InvalidPositionException() {
	}


	public InvalidPositionException(String msg) {
		super( msg );
	}
	
}
