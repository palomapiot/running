package es.udc.fi.dc.fd.race;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

	/**
	 * Find races by account.
	 *
	 * @param email
	 *            the email
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Query("SELECT a.race FROM Attendance a WHERE a.account.email = :email ORDER BY a.race.date DESC, a.race.place.placeName,a.race.name")
	Page<Race> findRacesByAccount(@Param("email") String email, Pageable page);

	/**
	 * Devuelve las carreras internas a las que está inscrito un usuario.
	 *
	 * @param email
	 *            the email
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Query("SELECT e.race FROM Enrollment e WHERE e.account.email = :email ORDER BY e.race.date DESC, e.race.place, e.race.name")
	Page<Race> findEnrollRacesByAccount(@Param("email") String email, Pageable page);

	/**
	 * Find going races by account.
	 *
	 * @param email
	 *            the email
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Query("SELECT r FROM Race r WHERE r.id in (select a.race.id from Attendance a where a.account.email = :email) "
			+ "or r.id in (select e.race.id from Enrollment e where e.account.email = :email) order by r.date DESC")
	Page<Race> findGoingRacesByAccount(@Param("email") String email, Pageable page);

	/**
	 * Find by id.
	 *
	 * @param id
	 *            the id
	 * @return the race
	 */
	Race findById(Long id);

	/**
	 * Find all races.
	 *
	 * @return the list
	 */
	@Query("SELECT r FROM Race r")
	List<Race> findAllRaces();

	/**
	 * Busca carreras con datos similares a los pasados por parámetro.
	 *
	 * @param distance
	 *            distancia de la búsqueda de carreras similares
	 * @param lat
	 *            latitud en base de datos de dicha ubicación
	 * @param lng
	 *            longitud en base de datos
	 * @param date
	 *            fecha de la carrera
	 * @param name
	 *            nombre de la carrera
	 * @return the list
	 */

	@Query("SELECT r FROM Race r JOIN r.place p WHERE ((6371 * 2 * ASIN(SQRT("
			+ "            POWER(SIN((p.lat - abs(:lat)) * pi()/180 / 2),"
			+ "            2) + COS(p.lat * pi()/180 ) * COS(abs(:lat) *"
			+ "            pi()/180) * POWER(SIN((p.lng - :lng) *"
			+ "pi()/180 / 2), 2) ))) <= :distance) AND (r.date = :date) AND (r.name = :name)")
	List<Race> findSimilarRaces(@Param("distance") double distance, @Param("lat") Float lat, @Param("lng") Float lng,
			@Param("date") Date date, @Param("name") String name);

}
