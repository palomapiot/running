package es.udc.fi.dc.fd.ranking;

/**
 * La clase runnerDTO representa la información más básica de un usuario: su
 * nombre y el email. Es utilizado para el autocompletado en
 */
public class RunnerDTO {

	private String username;

	private String email;

	public RunnerDTO() {
	}

	public RunnerDTO(String username, String email) {
		super();
		this.username = username;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
