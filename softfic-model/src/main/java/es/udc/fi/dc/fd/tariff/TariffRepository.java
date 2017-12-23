package es.udc.fi.dc.fd.tariff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

	@Query("select t.idTariff from Tariff t WHERE t.raceId = :raceId AND t.minAge <= :age AND t.maxAge >= :age")
	public List<Long> findUserTariff(@Param("raceId") Long raceId, @Param("age") Long age);

}
