package es.udc.fi.dc.fd.follow;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.udc.fi.dc.fd.account.Account;

/**
 * La clase Follow representa el seguimiento de un usuario a otro dentro del
 * sistema. Ambos de tipo {@link Account}.<br>
 * Propiedades destacables:
 * <ul>
 * <li>{@link Follow#follower}</li>
 * <li>{@link Follow#followed}</li>
 * </ul>
 */
@Entity
@Table(name = "follow")
public class Follow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Usuario que sigue a {@link Follow.followed}
	 */
	@ManyToOne
	@JoinColumn(name = "followerId")
	private Account follower;

	/**
	 * Usuario que es seguido por {@link Follow.follower}
	 */
	@ManyToOne
	@JoinColumn(name = "followedId")
	private Account followed;

	/**
	 * Indica si se está siguiendo o no
	 */
	private boolean accepted;

	public Follow() {

	}

	public Follow(Account follower, Account followed, boolean accepted) {
		this.follower = follower;
		this.followed = followed;
		this.accepted = accepted;
	}

	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @param accepted
	 *            the accepted to set
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public Account getFollower() {
		return follower;
	}

	public void setFollower(Account follower) {
		this.follower = follower;
	}

	public Account getFollowed() {
		return followed;
	}

	public void setFollowed(Account followed) {
		this.followed = followed;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Follow [id=" + id + ", follower=" + follower + ", followed=" + followed + "]";
	}

}
