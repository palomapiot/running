package es.udc.fi.dc.fd.ranking;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.RankingActivity;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceService;

@Service
public class RankingService {

	@Autowired
	RankingRepository rankingRepository;

	@Autowired
	RaceService raceService;

	@Autowired
	EnrollmentService enrollmentService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private ActivityRepository actRepository;

	private static final int PAGE_NUM_ELEMENTS = 10;

	/**
	 * Crea una nueva entidad {@link Ranking} asociada a la carrera indicada por
	 * parámetro
	 *
	 * @param raceId
	 *            id de la carrera a la que ese asocia el nuevo {@link Ranking}
	 * @param userEmail
	 *            inscripción del usuario identificativo del corredor que se quiere
	 *            clasificar en la carrera
	 * @param position
	 *            el puesto de clasificación
	 * @throws CantCreateRankingException
	 *             <br>
	 *             A - En caso de que el enrollment pasado por parámetro no
	 *             referencie a la carrera indicada.<br>
	 *             B - En caso de que la fecha de la entidad {@link Race}
	 *             referenciada no haya expirado aún.
	 */
	@Transactional(rollbackFor = CantCreateRankingException.class)
	public void addRunnerToRaceRanking(Long raceId, String userEmail, Integer position)
			throws CantCreateRankingException {
		// Si el enrollment no pertenece a la carrera a la que se pretende clasificar no
		// se debe permitir el ranking
		if (!enrollmentService.isEnroll(raceId, userEmail))
			throw new CantCreateRankingException(
					"User with email: '" + userEmail + "' isn't enrolled in the race with id = " + raceId);

		Race race = raceService.findById(raceId);
		// Si la carrera aún no ha ocurrido no se debe permitir añadir el ranking
		if (race.getDate().after(new Date()))
			throw new CantCreateRankingException("Race " + race.getId() + " didn't finish!");

		Account account = accountRepository.findOneByEmail(userEmail);
		Enrollment userEnrollment = enrollmentService.getEnroll(race, account);

		Ranking ranking = rankingRepository.save(new Ranking(userEnrollment, position, race));
		Activity activity = new RankingActivity(account, ranking);
		actRepository.save(activity);
	}

	/**
	 * Encuentra todos los objetos {@link Ranking} asociados a la carrera indicada.
	 *
	 * @param raceId
	 *            id de la carrera de la que buscamos las clasificaciones
	 * @param page
	 *            the page
	 * @return la lista de {@link Ranking} asociadas a la carrera
	 */
	@Transactional
	public Page<Ranking> findRankingByRace(Long raceId, int page) {
		return rankingRepository.findByRaceIdOrderByPosition(raceId, new PageRequest(page, PAGE_NUM_ELEMENTS));

	}

}
