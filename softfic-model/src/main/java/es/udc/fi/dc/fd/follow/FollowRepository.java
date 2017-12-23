package es.udc.fi.dc.fd.follow;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

	/**
	 * Si sigues o has hecho una petición ya a followedId te devuelve la lista de
	 * Follow con un elemento, sino una lista vacía.
	 * 
	 * @param followerId
	 *            id follower
	 * @param followedId
	 *            id followed
	 * @return list of {@link Follow}
	 */
	List<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

	@Query("select a from Follow a where a.follower.id = :followerId and a.accepted = true")
	Page<Follow> findByFollowerId(@Param("followerId") Long followerId, Pageable page);

	/**
	 * Accepted = false indica que está pendiente de aceptación
	 * 
	 * @param followedId
	 *            id followed
	 * @param page
	 *            the page
	 * @return {@link Follow}
	 */
	@Query("select a from Follow a where a.followed.id = :followedId and a.accepted = false")
	Page<Follow> findByFollowedId(@Param("followedId") Long followedId, Pageable page);

}
