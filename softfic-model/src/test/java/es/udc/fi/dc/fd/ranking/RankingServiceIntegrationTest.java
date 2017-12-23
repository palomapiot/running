package es.udc.fi.dc.fd.ranking;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceService;

public class RankingServiceIntegrationTest extends WebSecurityConfigurationAware {

	@Autowired
	RankingService rankingService;

	@Autowired
	AccountService accountService;

	@Autowired
	RaceService raceService;

	@Autowired
	EnrollmentService enrollmentService;

	@Test
	@Transactional
	public void findByRaceIdAndKeywordsTest() {

		// Creación datos
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", null, null),
				new GregorianCalendar(2018, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		race = raceService.save(race);

		Account user1 = new Account("ejemplo3@email.com", "password", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user2 = new Account("ejemplo@email.com", "password", "diego2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account user3 = new Account("nadaquever@email.com", "password", "diego3", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(user1);
		accountService.save(user2);
		accountService.save(user3);

		enrollmentService.enrollToRace(race.getId(), user1.getEmail());
		enrollmentService.enrollToRace(race.getId(), user2.getEmail());
		enrollmentService.enrollToRace(race.getId(), user3.getEmail());

		// LLamda
		List<Enrollment> foundEnrollments = enrollmentService.findByRaceIdAndKeywords(race.getId(), "ejemplo");

		// Asserciones
		assertEquals(2, foundEnrollments.size());

		assertEquals(user1.getEmail(), foundEnrollments.get(0).getAccount().getEmail());
		assertEquals(user2.getEmail(), foundEnrollments.get(1).getAccount().getEmail());
	}
}
