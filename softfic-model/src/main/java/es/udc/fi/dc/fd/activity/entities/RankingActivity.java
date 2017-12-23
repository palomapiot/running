package es.udc.fi.dc.fd.activity.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.ranking.Ranking;

/**
 * La Clase RankingActivity representa la actividad de un usuario que queda de
 * una posicion en una carrera
 */
@DiscriminatorValue("Ranking")
@Entity(name = "RankingActivity")
public class RankingActivity extends Activity {

	@ManyToOne
	@JoinColumn(name = "rankingId")
	private Ranking ranking;

	public RankingActivity() {
		super();
	}

	public RankingActivity(Account account, Ranking ranking) {
		super(account);
		this.ranking = ranking;
	}

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	@Override
	public String toString(String context) {
		return "<i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> " + super.toString(context) + " has reached the #"
				+ ranking.getPosition() + " position in <a href=\""
				+ context + "racedetails?raceId=" + ranking.getRace().getId() + "\">" + ranking.getRace().getName()
				+ "</a>";
	}
}
