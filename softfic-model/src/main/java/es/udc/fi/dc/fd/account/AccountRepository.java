package es.udc.fi.dc.fd.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.follow.FollowDTO;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Account findOneByEmail(String email);

	@Query("select count(a) > 0 from Account a where a.email = :email")
	boolean exists(@Param("email") String email);

	@Query("select a.account from Attendance a where a.race.id = :raceId")
	Page<Account> findAccountsByRace(@Param("raceId") Long raceId, Pageable page);

	@Query("select e.account from Enrollment e where e.race.id = :raceId")
	Page<Account> findEnrollAccountsByRace(@Param("raceId") Long raceId, Pageable page);

	@Query("select new es.udc.fi.dc.fd.follow.FollowDTO (a.id, a.email, false, a.username) "
			+ " from Account a where UPPER(a.username) like UPPER(:username) and "
			+ " a.email not like :principal order by a.username")
	Page<FollowDTO> findByUsernameLikeIgnoreCaseOrderByUsername(@Param("username") String username,
			@Param("principal") String principal, Pageable page);

	/**
	 * Cambia el perfil de privado a público o viceversa
	 * 
	 * @param change
	 * @param email
	 */
	@Modifying
	@Query("update Account a set a.privateAccount=:change where a.email=:email")
	public void setPrivacy(@Param("change") Boolean change, @Param("email") String email);

	@Query("select a.account from Attendance a where a.race.id = :raceid and a.account in (select f.followed from Follow f where f.follower.id = :accountId and f.accepted = true)")
	public Page<Account> findRaceFollowingAttendance(@Param("raceid") Long raceId, @Param("accountId") Long accountId,
			Pageable page);

	@Query("select count(a) from Attendance a where a.race.id = :raceid and a.account in (select f.followed from Follow f where f.follower.id = :accountId and f.accepted = true)")
	public int findNumberOfRaceFollowingAttendance(@Param("raceid") Long raceId, @Param("accountId") Long accountId);

	@Query("select e.account from Enrollment e where e.race.id = :raceId and e.account in (select f.followed from Follow f where f.follower.id = :accountId and f.accepted = true)")
	public Page<Account> findRaceFollowingEnrollments(@Param("raceId") Long raceId, @Param("accountId") Long accountId,
			Pageable page);

	@Query("select count(e) from Enrollment e where e.race.id = :raceId and e.account in (select f.followed from Follow f where f.follower.id = :accountId and f.accepted = true)")
	public int findNumberOfRaceFollowingEnrollments(@Param("raceId") Long raceId, @Param("accountId") Long accountId);
}