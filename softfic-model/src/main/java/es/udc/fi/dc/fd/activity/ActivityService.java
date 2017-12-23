package es.udc.fi.dc.fd.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.activity.entities.Activity;

/**
 * The Class ActivityService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ActivityService {

	/** The activity repository. */
	@Autowired
	private ActivityRepository activityRepository;

	private static final int PAGE_NUM_ELEMENTS = 10;

	/**
	 * Busca actividades de un usuario, es la que se usa en el perfil de un usuario
	 *
	 * @param userId
	 *            El id de la cuenta en la que se está buscando
	 * @param page
	 *            Página actual de búsqueda
	 * @return La página de las {@link Activity}
	 */
	public Page<Activity> findActivities(Long userId, int page) {
		return activityRepository.findActivitiesByUserId(userId, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Busca las actividades de tus amigos (feed)
	 *
	 * @param userId
	 *            El id de la cuenta sobre la que se busca la actividad de sus
	 *            amigos
	 * @param page
	 *            Página actual de búsqueda
	 * @return La página de las {@link Activity}
	 */
	public Page<Activity> findActivityFeed(Long userId, int page) {
		return activityRepository.findActivitiesByUserFriends(userId, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

}
