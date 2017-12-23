package es.udc.fi.dc.fd.activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.udc.fi.dc.fd.activity.entities.Activity;

/**
 * La interfaz ActivityRepository donde están las querys para conseguir la
 * información de las actividades de los diversos usuarios.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	/**
	 * Busca actividades de un usuario, es la que se usa en el perfil de un usuario
	 *
	 * @param accountId
	 *            el id de la cuenta en la que se está buscando
	 * @param page
	 *            Parámetro para decirle a la query como es la paginación
	 * @return La página de las {@link Activity}
	 */
	@Query("select a from Activity a where a.account.id = :id order by a.timestamp DESC")
	Page<Activity> findActivitiesByUserId(@Param("id") Long accountId, Pageable page);

	/**
	 * Busca las actividades de tus amigos (feed)
	 *
	 * @param accountId
	 *            el id de la cuenta sobre la que se busca la actividad de sus
	 *            amigos
	 * @param page
	 *            Parámetro para decirle a la query como es la paginación
	 * @return La página de las {@link Activity}
	 */
	@Query("select a from Activity a where a.account IN "
			+ "(Select f.followed from Follow f where f.follower.id = :id)"
			+ " order by a.timestamp DESC")
	Page<Activity> findActivitiesByUserFriends(@Param("id") Long accountId, Pageable page);

}
