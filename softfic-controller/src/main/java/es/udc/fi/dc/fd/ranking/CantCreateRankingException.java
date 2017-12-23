package es.udc.fi.dc.fd.ranking;

import es.udc.fi.dc.fd.race.Race;

/**
 * Debe ser lanzada cuando se realiza una acción sobre una {@link Race} cuya
 * fecha no haya expirado y se requiera esta circunstancia.
 */
public class CantCreateRankingException extends Exception {

	public CantCreateRankingException(String message) {
		super(message);
	}

}
