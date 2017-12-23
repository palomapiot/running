package es.udc.fi.dc.fd.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

	Place findByPlaceNameLikeIgnoreCase(String placeName);
}
