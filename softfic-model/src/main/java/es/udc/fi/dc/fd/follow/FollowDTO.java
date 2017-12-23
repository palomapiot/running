package es.udc.fi.dc.fd.follow;

/**
 * La clase FollowDTO representa la existencia o no existencia de una relación
 * de "Following" entre dos usuarios: "follower" y "followed". No contiene
 * relaciones directas con otras entidades, pero contiene el id del supuesto
 * follower y el email del followed.<br>
 * Recomendado mirar la descripción de los atributos:<br>
 * <ul>
 * <li>{@link FollowDTO#id}</li>
 * <li>{@link FollowDTO#email}</li>
 * </ul>
 */
public class FollowDTO {

	/**
	 * Id de el usuario "follower" que sigue al otro: "followed"
	 */
	Long id;

	/**
	 * Email que identifica al usuario que es seguido
	 */
	String email;

	/**
	 * Indica si el follower sigue al followed o no, si es nulo es que está
	 * pendiente de aceptación
	 */
	Boolean following;

	/**
	 * Nombre del usuario que es seguido
	 */
	String username;

	public FollowDTO() {
	}

	public FollowDTO(Long id, String email, Boolean following, String username) {
		this.id = id;
		this.email = email;
		this.following = following;
		this.username = username;
	}

	public FollowDTO(Long id, String email, Long following, String username) {
		this.id = id;
		this.email = email;
		this.following = following == 1;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getFollowing() {
		return following;
	}

	public void setFollowing(Boolean following) {
		this.following = following;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "FollowDTO [id=" + id + ", email=" + email + ", following=" + following + ", username=" + username + "]";
	}

}
