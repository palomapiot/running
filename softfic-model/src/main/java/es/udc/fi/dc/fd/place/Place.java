package es.udc.fi.dc.fd.place;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import es.udc.fi.dc.fd.race.Race;

/**
 * La clase {@link Place} representa un lugar dentro del sistema. Contienen las
 * coordenadas reales del lugar. La entidad {@link Race} siempre se asocia a una
 * place.
 */
@Entity
@Table(name = "place")
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long placeId;

	@Column(unique = true)
	private String placeName;

	private Float lat;

	private Float lng;

	public Place() {

	}

	public Place(String placeName, Float lat, Float lng) {
		this.placeName = placeName;
		this.lat = lat;
		this.lng = lng;
	}

	public Long getPlaceId() {
		return placeId;
	}

	public String getName() {
		return placeName;
	}

	public void setName(String placeName) {
		this.placeName = placeName;
	}

	public Float getLat() {
		return lat;
	}

	public void setLat(Float lat) {
		this.lat = lat;
	}

	public Float getLng() {
		return lng;
	}

	public void setLng(Float lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "Place [id=" + placeId + ", place=" + placeName + ", lat=" + lat + ", lng=" + lng + "]";
	}

}
