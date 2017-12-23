package es.udc.fi.dc.fd.race;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.validator.constraints.NotBlank;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.tariff.Tariff;

public class RaceForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";

	@NotBlank(message = RaceForm.NOT_BLANK_MESSAGE)
	private String name;

	@NotBlank(message = RaceForm.NOT_BLANK_MESSAGE)
	private String place;

	@NotBlank(message = RaceForm.NOT_BLANK_MESSAGE)
	private String date;

	private String website;

	private String image;

	private Boolean medicalTest;

	private Boolean chip;

	private BigDecimal chipPrice;

	private Double distance;

	/*
	 * En el form se trabaja como un int, para que en el html lo coja bien. Con
	 * getRaceType de RaceType.java conseguimos su "nombre" a través de la
	 * distancia.
	 */
	private int raceType;

	private List<BigDecimal> prices;

	private List<Long> minAges;

	private List<Long> maxAges;

	private boolean isInternal;

	private Account createdBy;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getMedicalTest() {
		return medicalTest;
	}

	public void setMedicalTest(Boolean medicalTest) {
		this.medicalTest = medicalTest;
	}

	public Boolean getChip() {
		return chip;
	}

	public void setChip(Boolean chip) {
		this.chip = chip;
	}

	public BigDecimal getChipPrice() {
		return chipPrice;
	}

	public void setChipPrice(BigDecimal chipPrice) {
		this.chipPrice = chipPrice;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public int getRaceType() {
		return raceType;
	}

	public void setRaceType(int raceType) {
		this.raceType = raceType;
	}

	public List<BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(List<BigDecimal> prices) {
		this.prices = prices;
	}

	public List<Long> getMinAges() {
		return minAges;
	}

	public void setMinAges(List<Long> minAges) {
		this.minAges = minAges;
	}

	public List<Long> getMaxAges() {
		return maxAges;
	}

	public void setMaxAges(List<Long> maxAges) {
		this.maxAges = maxAges;
	}

	public boolean getIsInternal() {
		return isInternal;
	}

	public void setIsInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}

	public Account getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Account createdBy) {
		this.createdBy = createdBy;
	}

	public Race createRace(List<Tariff> tariffs) {
		try {

			return new Race(getName(), new Place(getPlace(), null, null),
					new SimpleDateFormat("MM-dd-yyyy").parse(getDate()), getWebsite(), getImage(), getMedicalTest(),
					getChip(), getChipPrice(), getDistance(), RaceType.getRaceType(getRaceType()), tariffs,
					getIsInternal(), getCreatedBy());
		} catch (ParseException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
			return null;
		}
	}
}
