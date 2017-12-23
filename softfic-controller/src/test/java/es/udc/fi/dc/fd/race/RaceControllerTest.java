package es.udc.fi.dc.fd.race;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.attendance.AttendanceService;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.ranking.Ranking;
import es.udc.fi.dc.fd.ranking.RankingService;
import es.udc.fi.dc.fd.tariff.Tariff;

public class RaceControllerTest extends WebSecurityConfigurationAware {

	private RaceService raceServiceMock;
	private RaceController raceController;
	private EnrollmentService enrollmentServiceMock;
	private AttendanceService attendanceServiceMock;
	private RankingService rankingServiceMock;
	private AccountRepository accountRepositoryMock;
	private AccountService accountServiceMock;

	@Before
	public void setUp() {
		raceServiceMock = mock(RaceService.class);
		accountRepositoryMock = mock(AccountRepository.class);
		attendanceServiceMock = mock(AttendanceService.class);
		rankingServiceMock = mock(RankingService.class);
		enrollmentServiceMock = mock(EnrollmentService.class);
		accountServiceMock = mock(AccountService.class);
		raceController = new RaceController(raceServiceMock, attendanceServiceMock, rankingServiceMock,
				enrollmentServiceMock, accountRepositoryMock, accountServiceMock);
		this.mockMvc = MockMvcBuilders.standaloneSetup(raceController).build();
	}

	@Test
	@Transactional
	public void raceDetailsTest() throws Exception {

		// Creamos una carrera y la guardamos
		Race race = new Race("Maratón Atlántica 42", new Place("A Coruña", 40.4167754F, -3.7037902F),
				new GregorianCalendar(2017, Calendar.NOVEMBER, 5).getTime(),
				"https://runedia.mundodeportivo.com/carrera/maraton-coruna42-maraton-atlantica-2017/20171814/",
				"http://1.bp.blogspot.com/-Frdii3I0Wk0/U1dtoi5quWI/AAAAAAAAFO0/aYb8IBwYOxA/s1600/C42_50x50cm.png", null,
				null, null, null, null, null, false, null);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");

		Account acc = new Account("user@example.com", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);

		when(accountRepositoryMock.findOneByEmail(mockPrincipal.getName())).thenReturn(acc);

		when(rankingServiceMock.findRankingByRace(5L, 0)).thenReturn(new PageImpl<Ranking>(new ArrayList<>()));
		when(raceServiceMock.findById(5L)).thenReturn(race);
		when(accountServiceMock.numberOfFollowingAttendances(5L, 1L)).thenReturn(0);
		// Acccedemos a la página de detalles y comprobamos que sea la misma
		mockMvc.perform(get("/racedetails").param("raceId", "5").principal(mockPrincipal))
				.andExpect(model().attribute("race", race));

	}

	@Test
	public void createInternalRaceTest() throws Exception {
		// Creamos una race
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");
		when(rankingServiceMock.findRankingByRace(6L, 0)).thenReturn(new PageImpl<Ranking>(new ArrayList<>()));
		Account acc = new Account("user@example.com", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		race.setId(5L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);
		when(raceServiceMock.save(any(Race.class))).thenReturn(race);
		mockMvc.perform(post("/createrace").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.content("isInternal=true&_isInternal=on&name=La+carrera+del+ardor&place=Lugo&date=12-09-2017"
						+ "&website=&image=&medicalTest=true&_medicalTest=on&_chip=on&chipPrice=&raceType=226060"
						+ "&distance=226060&prices=0&minAges=0&maxAges=99")
				.principal(mockPrincipal)).andExpect(view().name("redirect:/racedetails?raceId=" + race.getId()));

	}

	@Test
	public void createRaceTest() throws Exception {
		// Creamos una race
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		Account acc = new Account("user@example.com", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		race.setId(6L);

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn(acc.getEmail());
		when(accountRepositoryMock.findOneByEmail(mockPrincipal.getName())).thenReturn(acc);

		when(raceServiceMock.save(any(Race.class))).thenReturn(race);
		when(rankingServiceMock.findRankingByRace(6L, 0)).thenReturn(new PageImpl<Ranking>(new ArrayList<>()));
		mockMvc.perform(post("/createrace").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.content("isInternal=false&_isInternal=on&name=La+carrera+del+ardor&place=Lugo&date=12-09-2017"
						+ "&website=&image=&medicalTest=true&_medicalTest=on&_chip=on&chipPrice=&raceType=226060"
						+ "&distance=226060&prices=0&minAges=0&maxAges=99"))
				.andExpect(view().name("redirect:/racedetails?raceId=" + race.getId()));

	}

	@Test
	@Transactional
	public void myraces() throws Exception {
		Integer page = 0;
		String userEmail = "user@example.com";

		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn(userEmail);

		// Simulamos la petición post
		mockMvc.perform(get("/myraces").param("page", page.toString()).principal(mockPrincipal));

		// Comprobamos que se haya llamado al menos una vez por cada email, con la
		// raceId y posición correspondiente
		verify(raceServiceMock, times(1)).findUserRacesByAccount(userEmail, page);
	}

}
