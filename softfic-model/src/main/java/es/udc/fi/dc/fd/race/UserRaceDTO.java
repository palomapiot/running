package es.udc.fi.dc.fd.race;

import java.util.Date;

import es.udc.fi.dc.fd.attendance.Attendance;

/**
 * La clase UserRaceDTO contiene la información que es interesante para el
 * usuario sobre las carreras a las que asistirá ({@link Attendance}) o en las
 * que se ha enrolado.
 */
public class UserRaceDTO {

	private Long raceId;
	private String raceName;

	private String racePlaceName;

	private Date raceDate;

	private Boolean raceIsInternal;

	private Boolean paid;

	private Boolean chip;

	private Integer userPosition;

	public Integer getUserPosition() {
		return userPosition;
	}

	public void setUserPosition(Integer userPosition) {
		this.userPosition = userPosition;
	}

	public UserRaceDTO() {
		super();
	}

	public Long getRaceId() {
		return raceId;
	}

	public void setRaceId(Long raceId) {
		this.raceId = raceId;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public Date getRaceDate() {
		return new Date(raceDate.getTime());
	}

	public void setRaceDate(Date raceDate) {
		this.raceDate = new Date(raceDate.getTime());
	}

	public Boolean getRaceIsInternal() {
		return raceIsInternal;
	}

	public void setRaceIsInternal(Boolean raceIsInternal) {
		this.raceIsInternal = raceIsInternal;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Boolean getChip() {
		return chip;
	}

	public void setChip(Boolean chip) {
		this.chip = chip;
	}

	public String getRacePlaceName() {
		return racePlaceName;
	}

	public void setRacePlaceName(String racePlaceName) {
		this.racePlaceName = racePlaceName;
	}

}
