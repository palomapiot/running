package es.udc.fi.dc.fd.enrollment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.race.RaceService;
import es.udc.fi.dc.fd.race.RaceType;
import es.udc.fi.dc.fd.tariff.Tariff;
import es.udc.fi.dc.fd.tariff.TariffRepository;

public class EnrollmentControllerTest extends WebSecurityConfigurationAware {

	private RaceService raceServiceMock;
	private RaceRepository raceRepositoryMock;
	private EnrollmentController enrollmentController;
	private EnrollmentService enrollmentServiceMock;
	private EnrollmentRepository enrollmentRepositoryMock;
	private AccountRepository accountRepositoryMock;
	private TariffRepository tariffRepositoryMock;

	@Before
	public void setUp() {
		raceServiceMock = mock(RaceService.class);
		enrollmentServiceMock = mock(EnrollmentService.class);
		accountRepositoryMock = mock(AccountRepository.class);
		raceRepositoryMock = mock(RaceRepository.class);
		enrollmentRepositoryMock = mock(EnrollmentRepository.class);
		tariffRepositoryMock = mock(TariffRepository.class);
		enrollmentController = new EnrollmentController(raceServiceMock, enrollmentServiceMock, accountRepositoryMock,
				raceRepositoryMock, enrollmentRepositoryMock, tariffRepositoryMock);

		this.mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
	}

	@Test
	@Transactional
	public void enrollToRaceTest() throws Exception {
		// Creamos una race

		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55)));
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100)));
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17)));

		Race race = new Race("Carrera interna de festis", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.DECEMBER, 27).getTime(), "https://festis.es/",
				"'https://festis.es/wp-content/uploads/2016/08/cropped-cropped-cropped-agua-negro-1-1-e1472125120868-1.png'",
				false, true, BigDecimal.valueOf(2), 5000.0, RaceType.POPULAR_RACE, tariff, true, null);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");

		Account acc = new Account("user@example.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		race.setId(5L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(acc, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(acc, race)).thenReturn(assist);

		// Comprobamos que nos lleva a los detalles de la carrera

		mockMvc.perform(get("/enroll").param("raceId", "5").principal(mockPrincipal))
				.andExpect(view().name("races/raceDetails :: enrollZone"));

	}

	@Test
	public void payRaceTest() throws Exception {
		// Creamos una race

		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add((new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55)));
		tariff.add((new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100)));
		tariff.add((new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17)));

		Race race = new Race("Carrera interna de festis", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.DECEMBER, 27).getTime(), "https://festis.es/",
				"'https://festis.es/wp-content/uploads/2016/08/cropped-cropped-cropped-agua-negro-1-1-e1472125120868-1.png'",
				false, true, BigDecimal.valueOf(2), 5000.0, RaceType.POPULAR_RACE, tariff, true, null);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");

		Account acc = new Account("user@example.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		race.setId(5L);
		Tariff tariff1 = tariff.get(1);
		tariff1.setIdTariff(2L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);
		Mockito.doNothing().when(enrollmentServiceMock).payRace(race.getId(), acc.getId(), true);

		// Comprobamos que nos lleva a los detalles de la carrera

		mockMvc.perform(post("/payrace").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.content("tariffRadio=11000&Account+Number=832749&raceId=5&chip=on").principal(mockPrincipal))
				.andExpect(view().name("redirect:/racedetails?raceId=" + race.getId()));

	}

	@Test
	@Transactional
	public void gePaymentsTest() throws Exception {
		// Creamos una race

		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55)));
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100)));
		tariff.add(raceServiceMock.addTariff(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17)));

		Race race = new Race("Carrera interna de festis", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.DECEMBER, 27).getTime(), "https://festis.es/",
				"'https://festis.es/wp-content/uploads/2016/08/cropped-cropped-cropped-agua-negro-1-1-e1472125120868-1.png'",
				false, true, BigDecimal.valueOf(2), 5000.0, RaceType.POPULAR_RACE, tariff, true, null);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");

		Account acc = new Account("user@example.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		race.setId(5L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(acc, race, true, true);
		when(enrollmentRepositoryMock.findByAccountAndRace(acc, race)).thenReturn(assist);

		List<Enrollment> enrollList = new ArrayList<Enrollment>();
		enrollList.add(assist);
		Page<Enrollment> enrollPage = new PageImpl<Enrollment>(enrollList);

		when(enrollmentRepositoryMock.findEnrollmentsPaidByAccount(acc.getId(), new PageRequest(0, 10)))
				.thenReturn(enrollPage);

		// Comprobamos que nos lleva a los detalles de la carrera

		mockMvc.perform(get("/payments").param("page", "0").principal(mockPrincipal))
				.andExpect(view().name("races/payments"));

	}
}
