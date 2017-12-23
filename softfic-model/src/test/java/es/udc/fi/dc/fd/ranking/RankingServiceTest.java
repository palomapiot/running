package es.udc.fi.dc.fd.ranking;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.race.RaceService;

public class RankingServiceTest extends WebSecurityConfigurationAware {

	@Autowired
	private RaceService raceService;

	@Autowired
	private RankingService rankingService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private RaceRepository raceRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	@Transactional
	public void testAddRunnerToRaceRanking() throws CantCreateRankingException {

		// Creación datos
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", null, null),
				new GregorianCalendar(2018, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		race = raceService.save(race);

		Account user1 = new Account("email_1@email.com", "password", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user2 = new Account("email_2@email.com", "password", "diego2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user3 = new Account("email_3@email.com", "password", "diego3", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(user1);
		accountService.save(user2);
		accountService.save(user3);

		enrollmentService.enrollToRace(race.getId(), user1.getEmail());
		enrollmentService.enrollToRace(race.getId(), user2.getEmail());
		enrollmentService.enrollToRace(race.getId(), user3.getEmail());

		List<Enrollment> foundEnrollments = enrollmentService.findByRaceId(race.getId());

		// Cambiamos la fecha de la carrera (para que permita meter el ranking) y
		// creamos el ranking.
		race.setDate(new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime());
		raceRepository.save(race);
		rankingService.addRunnerToRaceRanking(race.getId(), user1.getEmail(), 1);
		rankingService.addRunnerToRaceRanking(race.getId(), user2.getEmail(), 2);
		rankingService.addRunnerToRaceRanking(race.getId(), user3.getEmail(), 3);
		// Recuperamos los rankings
		Page<Ranking> foundRankings = rankingService.findRankingByRace(race.getId(), 0);

		// Revisamos todos los resultados
		for (Ranking ranking : foundRankings) {
			// Si uno de los enrollments no se ha insertado fallará
			if (!foundEnrollments.contains(ranking.getUserEnrollment()))
				Assert.fail();
			Assert.assertEquals(race, ranking.getRace());
		}

	}

	/*
	 * Debe fallar porque el usuario identificado con el email no esté enrolado en
	 * la carrera.
	 */
	@Test
	@Transactional
	public void rankUserNotEnrolled() throws CantCreateRankingException {

		// Creación datos
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", null, null),
				new GregorianCalendar(2018, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		race = raceService.save(race);

		Account user1 = new Account("email_1@email.com", "password", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user2 = new Account("email_2@email.com", "password", "diego2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user3 = new Account("email_3@email.com", "password", "diego3", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(user1);
		accountService.save(user2);
		accountService.save(user3);

		enrollmentService.enrollToRace(race.getId(), user1.getEmail());
		enrollmentService.enrollToRace(race.getId(), user2.getEmail());
		// NO enrolamos al usuario 3

		List<Enrollment> foundEnrollments = enrollmentService.findByRaceId(race.getId());

		// Regla de excepción
		thrown.expect(CantCreateRankingException.class);
		thrown.expectMessage(
				"User with email: '" + user3.getEmail() + "' isn't enrolled in the race with id = " + race.getId());

		// Cambiamos la fecha de la carrera (para que permita meter el ranking) y
		// creamos el ranking.
		race.setDate(new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime());
		raceRepository.save(race);
		rankingService.addRunnerToRaceRanking(race.getId(), user3.getEmail(), 3);
	}

	/*
	 * Debe fallar porque la fecha de la carrera no ha expirado aún al momento de
	 * añadir el ranking.
	 */
	@Test
	@Transactional
	public void rankInNotDoneRace() throws CantCreateRankingException {

		// Creación datos
		// La carrea aun no ha expirado
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", null, null),
				new GregorianCalendar(2018, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		race = raceService.save(race);

		Account user1 = new Account("email_1@email.com", "password", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(user1);

		enrollmentService.enrollToRace(race.getId(), user1.getEmail());

		// Regla de excepción
		thrown.expect(CantCreateRankingException.class);
		thrown.expectMessage("Race " + race.getId() + " didn't finish!");
		rankingService.addRunnerToRaceRanking(race.getId(), user1.getEmail(), 1);
	}

}
