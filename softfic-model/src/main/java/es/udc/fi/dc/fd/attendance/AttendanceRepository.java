package es.udc.fi.dc.fd.attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.race.Race;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	@Query("select count(a) > 0 from Attendance a where a.account.email= :email AND a.race.id= :raceId ")
	public boolean exists(@Param("email") String email, @Param("raceId") Long raceId);

	public Attendance findByAccountAndRace(Account account, Race race);

	@Query("select count(a) from Attendance a where a.race.id = :raceId")
	public Long numberOfAttendants(@Param("raceId") Long raceId);

	@Query("SELECT a FROM Attendance a WHERE a.account.email = :email")
	public List<Attendance> findByAccountEmail(@Param("email") String email);
}
