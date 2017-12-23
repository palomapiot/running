package es.udc.fi.dc.fd.race;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.tariff.Tariff;

/**
 * La clase {@link Race} representa una carrera externa dentro del sistema.<br>
 * Propiedades destacables:
 * <ul>
 * <li>{@link Race#place}</li>
 * </ul>
 */
@Entity
@Table(name = "race")
public class Race {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Date date;

	private String website;

	private String image;

	/** Entidad {@link Place} asociada a la carrera */
	@ManyToOne
	@JoinColumn(name = "placeId")
	private Place place;

	private Boolean medicalTest;

	private Boolean chip;

	private BigDecimal chipPrice;

	private Double distance;

	private RaceType raceType;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "raceId")
	private List<Tariff> prices = new ArrayList<>();

	private boolean isInternal;

	/**
	 * Usuario que ha creado la carrera. Será null si la carrera no es interna.
	 */
	@ManyToOne
	@JoinColumn(name = "createdById")
	private Account createdBy;

	public Race() {

	}

	public Race(String name, Place place, Date date, String website, String image, Boolean medicalTest, Boolean chip,
			BigDecimal chipPrice, Double distance, RaceType raceType, List<Tariff> prices, boolean isInternal,
			Account createdBy) {
		this.name = name;
		this.place = place;
		this.date = new Date(date.getTime());
		this.website = website;
		this.image = image;
		this.medicalTest = medicalTest;
		this.chip = chip;
		this.chipPrice = chipPrice;
		this.distance = distance;
		this.raceType = raceType;
		this.prices = prices;
		this.isInternal = isInternal;
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public Date getDate() {
		return new Date(date.getTime());
	}

	public void setDate(Date date) {
		this.date = new Date(date.getTime());
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

	public RaceType getRaceType() {
		return raceType;
	}

	public void setRaceType(RaceType raceType) {
		this.raceType = raceType;
	}

	public List<Tariff> getPrices() {
		return prices;
	}

	public void setPrices(List<Tariff> prices) {
		this.prices.clear();
		this.prices = prices;
	}

	public boolean getIsInternal() {
		return isInternal;
	}

	public void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}

	public Account getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Account createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "Race [id=" + id + ", name=" + name + ", date=" + date + ", website=" + website + ", image=" + image
				+ ", place=" + place + ", medicalTest=" + medicalTest + ", chip=" + chip + ", chipPrice=" + chipPrice
				+ ", distance=" + distance + ", raceType=" + raceType + ", prices=" + prices + ", isInternal="
				+ isInternal + ", createdBy=" + createdBy + "]";
	}

}
