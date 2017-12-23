package es.udc.fi.dc.fd.ranking;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceType;
import es.udc.fi.dc.fd.tariff.Tariff;

public class RankingControllerTest extends WebSecurityConfigurationAware {

	private RankingController rankingController;
	private RankingService rankingServiceMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		rankingServiceMock = org.mockito.Mockito.mock(RankingService.class);
		rankingController = new RankingController(rankingServiceMock);
		this.mockMvc = MockMvcBuilders.standaloneSetup(rankingController).build();
	}

	@Test
	@Transactional
	public void addRanking() throws CantCreateRankingException, Exception {
		String[] rankingEmails = { "email_1@email.com", "email_2@email.com" };

		// Simulamos la petición post
		mockMvc.perform(post("/addranking").param("rankingEmails", rankingEmails).param("raceId", "5"));

		// Comprobamos que se haya llamado al menos una vez por cada email, con la
		// raceId y posición correspondiente
		verify(rankingServiceMock, times(1)).addRunnerToRaceRanking(5L, "email_1@email.com", 1);
		verify(rankingServiceMock, times(1)).addRunnerToRaceRanking(5L, "email_2@email.com", 2);
	}

	@Test
	@Transactional
	public void addRankingWithDuplicateEmails() throws Exception {
		String userEmailDuplicado = "email_1@email.com";
		String[] rankingEmails = { userEmailDuplicado, "email_2@email.com", userEmailDuplicado };

		// Regla de excepción
		thrown.expect(NestedServletException.class);
		thrown.expectMessage(
				"Request processing failed; nested exception is es.udc.fi.dc.fd.ranking.CantCreateRankingException: "
						+ "Usuario duplicado en ranking: " + userEmailDuplicado);

		// Simulamos la petición post
		mockMvc.perform(post("/addranking").param("rankingEmails", rankingEmails).param("raceId", "5"));
	}

	@Test
	@Transactional
	public void redirectRankingTest() throws Exception {
		// Creamos una race
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));
		// Creamos un usuario, propietario de la carrera
		Account demoUser = new Account("user@example.com", "testpassword", "diego", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		demoUser.setId(1L);
		// Creamos la carrera
		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, demoUser);
		race.setId(1L);
		// Creamos los usuarios asistentes a la carrera
		Account acc1 = new Account("user1@example.com", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account acc2 = new Account("user2@example.com", "demo", "diego2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account acc3 = new Account("user3@example.com", "demo", "diego3", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account acc4 = new Account("user4@example.com", "demo", "diego4", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account acc5 = new Account("user5@example.com", "demo", "diego5", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		// Asistencias a la carrera
		Enrollment enroll1 = new Enrollment(acc1, race, false, false);
		Enrollment enroll2 = new Enrollment(acc2, race, false, false);
		Enrollment enroll3 = new Enrollment(acc3, race, false, false);
		Enrollment enroll4 = new Enrollment(acc4, race, false, false);
		Enrollment enroll5 = new Enrollment(acc5, race, false, false);
		// Creamos el ránking
		List<Ranking> ranking = new ArrayList<Ranking>();
		ranking.add(new Ranking(enroll1, 1, race));
		ranking.add(new Ranking(enroll2, 2, race));
		ranking.add(new Ranking(enroll3, 3, race));
		ranking.add(new Ranking(enroll4, 4, race));
		ranking.add(new Ranking(enroll5, 5, race));
		ranking.get(0).setRankingId(1L);
		ranking.get(1).setRankingId(2L);
		ranking.get(2).setRankingId(3L);
		ranking.get(3).setRankingId(4L);
		ranking.get(4).setRankingId(5L);

		// Creacion de la página de ránking
		Page<Ranking> rankingPage = new PageImpl<Ranking>(ranking);

		when(rankingServiceMock.findRankingByRace(race.getId(), 0)).thenReturn(rankingPage);
		mockMvc.perform(get("/redirectRanking").param("raceId", "1").param("page", "0"))
				.andExpect(view().name("races/ranking"));

	}

}