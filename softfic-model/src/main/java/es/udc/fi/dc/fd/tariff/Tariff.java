package es.udc.fi.dc.fd.tariff;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tariff")
public class Tariff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idTariff;

	private BigDecimal price;

	private Long minAge;

	private Long maxAge;

	private Long raceId;

	public Tariff() {

	}

	public Tariff(BigDecimal price, Long minAge, Long maxAge) {
		super();
		this.price = price;
		this.minAge = minAge;
		this.maxAge = maxAge;
	}

	public Long getIdTariff() {
		return idTariff;
	}

	public void setIdTariff(Long idTariff) {
		this.idTariff = idTariff;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getMinAge() {
		return minAge;
	}

	public void setMinAge(Long minAge) {
		this.minAge = minAge;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public Long getRaceId() {
		return raceId;
	}

	public void setRaceId(Long raceId) {
		this.raceId = raceId;
	}

	@Override
	public String toString() {
		return "Tariff [idTariff=" + idTariff + ", price=" + price + ", minAge=" + minAge + ", maxAge=" + maxAge
				+ ", raceId=" + raceId + "]";
	}

}
