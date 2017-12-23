package es.udc.fi.dc.fd.attendance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;

/**
 * La clase {@link Attendance} representa la asistencia de un usuario
 * ({@link Account}) a una carrera externa ({@link Race}).<br>
 * Propiedades destacables:
 * <ul>
 * <li>{@link Attendance#account}</li>
 * <li>{@link Attendance#race}</li>
 * </ul>
 */
@Entity
@Table(name = "attendance")
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attendanceId;

	/** Cuenta de usuario ({@link Account}) asociada a esta entidad attendance */
	@ManyToOne
	@JoinColumn(name = "accId")
	private Account account;

	/** La {@link Race} asociada a esta entidad attendance */
	@ManyToOne
	@JoinColumn(name = "raceId")
	private Race race;

	public Attendance() {
	}

	public void setAttendanceId(Long attendanceId) {
		this.attendanceId = attendanceId;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Attendance(Account account, Race race) {
		this.account = account;
		this.race = race;
	}

	public Account getAccount() {
		return account;
	}

	public Race getRace() {
		return race;
	}

	public Long getAttendanceId() {
		return attendanceId;
	}

	@Override
	public String toString() {
		return "Attendance [account=" + account + ", race=" + race + "]";
	}

}
