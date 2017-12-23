package es.udc.fi.dc.fd.race.exceptions;

import es.udc.fi.dc.fd.race.Race;

/**
 * Debe ser lanzada cuando se realiza una acción de convertir a interna sobre
 * una {@link Race} cuyo estado ya sea el mencionado anteriormente.
 */
@SuppressWarnings("serial")
public class CantConvertToInternalException extends Exception {

	public CantConvertToInternalException(String message) {
		super(message);
	}

}
