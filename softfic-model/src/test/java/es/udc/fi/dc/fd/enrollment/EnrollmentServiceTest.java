package es.udc.fi.dc.fd.enrollment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.attendance.AttendanceService;
import es.udc.fi.dc.fd.enrollment.exceptions.NotPossiblePayException;
import es.udc.fi.dc.fd.follow.FollowRepository;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.race.RaceService;
import es.udc.fi.dc.fd.tariff.Tariff;
import es.udc.fi.dc.fd.tariff.TariffRepository;

@RunWith(MockitoJUnitRunner.class)
public class EnrollmentServiceTest {

	@InjectMocks
	private AccountService accountService = new AccountService();

	@InjectMocks
	private AttendanceService attendanceService = new AttendanceService();

	@InjectMocks
	private EnrollmentService enrollmentService = new EnrollmentService();

	@Mock
	private RaceService raceService;

	@Mock
	private EnrollmentRepository enrollmentRepositoryMock;

	@Mock
	private RaceRepository raceRepositoryMock;

	@Mock
	private AccountRepository accountRepositoryMock;

	@Mock
	private FollowRepository followRepositoryMock;

	@Mock
	private TariffRepository tariffRepositoryMock;

	@Test
	@Transactional
	public void shouldEnrollToRace() {
		// arrange
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);
		Enrollment assist = new Enrollment(demoUser, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(demoUser, race)).thenReturn(assist);

		enrollmentService.enrollToRace(race.getId(), demoUser.getEmail());
		assertThat(assist.isPaid()).isEqualTo(false);
		assertThat(assist.getChip()).isEqualTo(false);
		assertThat(assist.getRace().getId()).isEqualTo(race.getId());
		assertThat(assist.getAccount().getEmail()).isEqualTo(demoUser.getEmail());

	}

	@Test
	@Transactional
	public void isEnrollTest() {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		enrollmentService.enrollToRace(race.getId(), demoUser.getEmail());
		when(enrollmentRepositoryMock.exists(demoUser.getEmail(), race.getId())).thenReturn(true);

		assertThat(enrollmentService.isEnroll(race.getId(), demoUser.getEmail())).isEqualTo(true);

	}

	@Test
	@Transactional
	public void getEnrollTest() {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(demoUser, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(demoUser, race)).thenReturn(assist);

		Enrollment result = enrollmentService.getEnroll(race, demoUser);

		assertThat(result).isEqualTo(assist);

	}

	@Test
	@Transactional
	public void payRaceTest() throws NotPossiblePayException {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(demoUser, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(demoUser, race)).thenReturn(assist);

		enrollmentService.getEnroll(race, demoUser);

		Tariff tarifa = new Tariff(BigDecimal.valueOf(5), (long) 0, (long) 100);
		tarifa.setIdTariff(100L);
		List<Long> tariffList = new ArrayList<>();
		tariffList.add(tarifa.getIdTariff());
		when(tariffRepositoryMock.getOne(tarifa.getIdTariff())).thenReturn(tarifa);
		when(tariffRepositoryMock.findOne(tarifa.getIdTariff())).thenReturn(tarifa);
		when(tariffRepositoryMock.findUserTariff(race.getId(), demoUser.getAge())).thenReturn(tariffList);

		when(raceService.findById(race.getId())).thenReturn(race);
		when(accountRepositoryMock.getOne(demoUser.getId())).thenReturn(demoUser);

		enrollmentService.payRace(race.getId(), demoUser.getId(), false);
	}

	@Test(expected = NotPossiblePayException.class)
	@Transactional
	public void notPossiblePayRaceTest() throws NotPossiblePayException {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(demoUser, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(demoUser, race)).thenReturn(assist);

		enrollmentService.getEnroll(race, demoUser);

		Tariff tarifa = new Tariff(BigDecimal.valueOf(5), (long) 0, (long) 100);
		tarifa.setIdTariff(100L);
		when(tariffRepositoryMock.getOne(tarifa.getIdTariff())).thenReturn(tarifa);
		when(tariffRepositoryMock.findOne(tarifa.getIdTariff())).thenReturn(tarifa);
		when(tariffRepositoryMock.findUserTariff(race.getId(), demoUser.getAge())).thenReturn(new ArrayList<>());

		when(raceService.findById(race.getId())).thenReturn(race);
		when(accountRepositoryMock.getOne(demoUser.getId())).thenReturn(demoUser);

		enrollmentService.payRace(race.getId(), demoUser.getId(), false);
	}

	@Test
	@Transactional
	public void getnumberOfEnrollmentsTest() {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Account demoUser2 = new Account("prueba@festis.com", "demo", "prueba", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("prueba@festis.es")).thenReturn(demoUser2);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment assist = new Enrollment(demoUser, race, false, false);
		when(enrollmentRepositoryMock.findByAccountAndRace(demoUser, race)).thenReturn(assist);

		enrollmentService.enrollToRace(race.getId(), demoUser.getEmail());
		enrollmentService.enrollToRace(race.getId(), demoUser2.getEmail());
		when(enrollmentRepositoryMock.numberOfEnrollments(race.getId())).thenReturn(2L);
		Long result = enrollmentService.numberOfEnrollments(race.getId());
		assertThat(result).isEqualTo(2L);

	}

	@Test
	@Transactional
	public void getRacesPaymentsTest() {
		Account demoUser = new Account("piot@festis.com", "demo", "piot", "Paloma", "Piot", null,
				new GregorianCalendar(1996, Calendar.JUNE, 28).getTime(), "A Coruña", "España", "festis", true,
				"ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("piot@festis.es")).thenReturn(demoUser);

		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(race);

		Enrollment enroll = new Enrollment(demoUser, race, true, false);
		List<Enrollment> enrollList = new ArrayList<Enrollment>();
		enrollList.add(enroll);
		Page<Enrollment> enrollPage = new PageImpl<Enrollment>(enrollList);

		when(enrollmentRepositoryMock.findEnrollmentsPaidByAccount(demoUser.getId(), new PageRequest(0, 10)))
				.thenReturn(enrollPage);
		Page<Enrollment> result = enrollmentService.getRacesPayments(demoUser, 0);
		assertThat(result.getContent()).isEqualTo(enrollPage.getContent());

	}

}
