package es.udc.fi.dc.fd.activity.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.udc.fi.dc.fd.account.Account;

/**
 * La Clase FollowActivity representa la actividad de un usuario que sigue a
 * otro
 */
@DiscriminatorValue("Follow")
@Entity(name = "FollowActivity")
public class FollowActivity extends Activity {

	@ManyToOne
	@JoinColumn(name = "followedId")
	private Account followed;

	public FollowActivity() {
		super();
	}

	public FollowActivity(Account account, Account followed) {
		super(account);
		this.followed = followed;
	}

	public Account getFollowed() {
		return followed;
	}

	public void setFollowed(Account followed) {
		this.followed = followed;
	}

	@Override
	public String toString(String context) {
		return "<i class=\"fa fa-user-plus\" aria-hidden=\"true\"></i> " + super.toString(context)
				+ " is now following " + "<a href=\"" + context + "myprofile?accId="
				+ followed.getId() + "\">" + followed.getName() + "</a>";
	}

}
