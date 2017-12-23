package es.udc.fi.dc.fd.enrollment.exceptions;

/**
 * Debe ser lanzada cuando no es posible realizar el pago de una carrera
 */
@SuppressWarnings("serial")
public class NotPossiblePayException extends Exception {

	public NotPossiblePayException(String message) {
		super(message);
	}

}
