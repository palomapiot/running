package es.udc.fi.dc.fd.activity.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;

/**
 * La Clase GoActivity representa la actividad de un usuario que se apunta en
 * una carrera
 */
@DiscriminatorValue("Go")
@Entity(name = "GoActivity")
public class GoActivity extends Activity {

	@ManyToOne
	@JoinColumn(name = "goRaceId")
	private Race race;

	private boolean isInternal;

	public GoActivity() {
		super();
	}

	public GoActivity(Account account, Race race) {
		super(account);
		this.race = race;
		this.isInternal = race.getIsInternal();
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	@Override
	public String toString(String context) {
		String message;
		if (isInternal) {
			message = " has enrolled in ";
		} else {
			message = " will attend ";
		}
		return "<i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> " + super.toString(context) + message
				+ "<a href=\"" + context + "racedetails?raceId=" + race.getId()
				+ "\">"
				+ race.getName() + "</a>";
	}
}
