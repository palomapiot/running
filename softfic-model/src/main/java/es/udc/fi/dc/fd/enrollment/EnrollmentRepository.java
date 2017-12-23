package es.udc.fi.dc.fd.enrollment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@Query("select count(e) > 0 from Enrollment e where e.account.email= :email AND e.race.id= :raceId ")
	public boolean exists(@Param("email") String email, @Param("raceId") Long raceId);

	public Enrollment findByAccountAndRace(Account account, Race race);

	@Modifying
	@Query("update Enrollment e set e.paid=true, e.chip=:chip, e.tariff.id=:trfId where e.race.id=:raceId and e.account.id=:accId")
	public void payRace(@Param("raceId") Long raceId, @Param("accId") Long accId, @Param("chip") Boolean chip,
			@Param("trfId") Long trfId);

	@Query("select count(e) from Enrollment e where e.race.id = :raceId")
	public Long numberOfEnrollments(@Param("raceId") Long raceId);

	@Query("select e from Enrollment e where e.account.id = :accId and e.paid=true")
	public Page<Enrollment> findEnrollmentsPaidByAccount(@Param("accId") Long accId, Pageable page);

	public List<Enrollment> findByRaceId(Long raceId);

	@Query("SELECT e FROM Enrollment e WHERE e.account.email = :accEmail")
	public List<Enrollment> findByAccountEmail(@Param("accEmail") String email);

	@Query("SELECT e FROM Enrollment e WHERE e.race.id = :raceId AND e.account.email LIKE %" + ":search" + "%")
	public List<Enrollment> findByRaceIdAndKeywords(@Param("raceId") Long raceId, @Param("search") String search);
}
