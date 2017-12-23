package es.udc.fi.dc.fd.enrollment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.ranking.Ranking;
import es.udc.fi.dc.fd.tariff.Tariff;

@Entity
@Table(name = "enrollment")
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long enrollmentId;

	@ManyToOne
	@JoinColumn(name = "accId")
	private Account account;

	@ManyToOne
	@JoinColumn(name = "raceId")
	private Race race;

	@ManyToOne
	@JoinColumn(name = "trfId")
	private Tariff tariff;

	@OneToOne(mappedBy = "userEnrollment")
	private Ranking ranking;

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	private boolean paid;

	private boolean chip;

	public Enrollment() {

	}

	public Enrollment(Account account, Race race, boolean paid, boolean chip) {
		this.account = account;
		this.race = race;
		this.paid = paid;
		this.chip = chip;
	}

	/**
	 * @return the enrollmentId
	 */
	public Long getEnrollmentId() {
		return enrollmentId;
	}

	/**
	 * @param enrollmentId
	 *            the enrollmentId to set
	 */
	public void setEnrollmentId(Long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @return the race
	 */
	public Race getRace() {
		return race;
	}

	/**
	 * @param race
	 *            the race to set
	 */
	public void setRace(Race race) {
		this.race = race;
	}

	/**
	 * @return the paid
	 */
	public boolean isPaid() {
		return paid;
	}

	/**
	 * @param paid
	 *            the paid to set
	 */
	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	/**
	 * @return the chip
	 */
	public boolean getChip() {
		return chip;
	}

	/**
	 * @param chip
	 *            the chip to set
	 */
	public void setChip(boolean chip) {
		this.chip = chip;
	}

	/**
	 * @return the tariff
	 */
	public Tariff getTariff() {
		return tariff;
	}

	/**
	 * @param tariff
	 *            the tariff to set
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	@Override
	public String toString() {
		return "Enrollment [enrollmentId=" + enrollmentId + ", account=" + account + ", race=" + race + ", paid=" + paid
				+ ", chip=" + chip + "]";
	}

}
