package fr.rowlaxx.jsavon.exceptions;

public class JSavONException extends RuntimeException {

	private static final long serialVersionUID = -7448336743350809376L;

	public JSavONException(String msg) {
		super(msg);
	}
	
	public JSavONException(Throwable cause) {
		super(cause);
	}
	
	public JSavONException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public JSavONException() {
		super();
	}
	
}
