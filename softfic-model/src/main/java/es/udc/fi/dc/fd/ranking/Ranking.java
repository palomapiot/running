package es.udc.fi.dc.fd.ranking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.race.Race;

/**
 * La clase {@link Ranking} representa una clasificación de un runner en una
 * carrera. El runner es un usuario de la aplicación que se ha 'enrolado' en la
 * carrera<br>
 * Propiedades destacables:
 * <ul>
 * <li>{@link Ranking#race}</li>
 * <li>{@link Ranking#userEnrollment}</li>
 * </ul>
 */
@Entity
@Table(name = "ranking")
public class Ranking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long rankingId;

	/* Inscripción de usuario a la carrera, de el runner que se clasifica */
	@OneToOne
	@JoinColumn(name = "userId")
	Enrollment userEnrollment;

	/* Posición en el ranking del runner para esta carrera */
	@Column
	Integer position;

	/** Entidad {@link Race} a la que se asocia la clasificación. */
	@ManyToOne
	@JoinColumn(name = "raceId")
	Race race;

	public Long getRankingId() {
		return rankingId;
	}

	public void setRankingId(Long rankingId) {
		this.rankingId = rankingId;
	}

	public Enrollment getUserEnrollment() {
		return userEnrollment;
	}

	public void setUserEnrollment(Enrollment userEnrollment) {
		this.userEnrollment = userEnrollment;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Ranking(Enrollment userEnrollment, Integer position, Race race) {
		super();
		this.userEnrollment = userEnrollment;
		this.position = position;
		this.race = race;
	}

	public Ranking() {

	}

}
