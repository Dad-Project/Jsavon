package fr.rowlaxx.jsavon;

public class JsavonException extends RuntimeException {

	private static final long serialVersionUID = -7448336743350809376L;

	public JsavonException(String msg) {
		super(msg);
	}
	
	public JsavonException(Throwable cause) {
		super(cause);
	}
	
	public JsavonException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public JsavonException() {
		super();
	}
	
}
