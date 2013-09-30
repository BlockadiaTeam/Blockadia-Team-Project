package exceptions;

/**
 * @author alex.yang
 * */
@SuppressWarnings("serial")
public class ElementNotExistException extends Exception {
	
	public ElementNotExistException() {
	}


	public ElementNotExistException(String msg) {
		super( msg );
	}
}
