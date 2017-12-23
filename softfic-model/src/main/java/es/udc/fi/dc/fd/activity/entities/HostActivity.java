package es.udc.fi.dc.fd.activity.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;

/**
 * La Clase HostActivity representa la actividad de un usuario que empieza a
 * organizar una carrera
 */
@DiscriminatorValue("Host")
@Entity(name = "HostActivity")
public class HostActivity extends Activity {

	@ManyToOne
	@JoinColumn(name = "hostRaceId")
	private Race race;

	public HostActivity() {
		super();
	}

	public HostActivity(Account account, Race race) {
		super(account);
		this.race = race;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	@Override
	public String toString(String context) {
		return "<i class=\"fa fa-users\" aria-hidden=\"true\"></i> " + super.toString(context) + " is now organizing "
				+ "<a href=\"" + context + "racedetails?raceId="
				+ race.getId() + "\">" + race.getName() + "</a>";
	}
}
