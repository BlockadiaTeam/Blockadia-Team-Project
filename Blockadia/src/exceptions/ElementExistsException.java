package exceptions;

/**
 * @author alex.yang
 * */
@SuppressWarnings("serial")
public class ElementExistsException extends Exception {

	public ElementExistsException() {
	}


	public ElementExistsException(String msg) {
		super( msg );
	}
}
