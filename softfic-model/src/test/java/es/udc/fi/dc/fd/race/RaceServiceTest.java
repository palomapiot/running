package es.udc.fi.dc.fd.race;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.attendance.Attendance;
import es.udc.fi.dc.fd.attendance.AttendanceRepository;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentRepository;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.place.PlaceRepository;
import es.udc.fi.dc.fd.race.exceptions.CantConvertToInternalException;
import es.udc.fi.dc.fd.ranking.Ranking;
import es.udc.fi.dc.fd.tariff.Tariff;

@RunWith(MockitoJUnitRunner.class)
public class RaceServiceTest {
	@InjectMocks
	private RaceService raceService = Mockito.spy(new RaceService());

	@Mock
	private RaceRepository raceRepositoryMock;
	@Mock
	private PlaceRepository placeRepositoryMock;
	@Mock
	private AccountRepository accountRepositoryMock;
	@Mock
	private AttendanceRepository attendanceRepositoryMock;
	@Mock
	private EnrollmentRepository enrollmentRepositoryMock;
	@Mock
	private ActivityRepository activityRepositoryMock;

	@Test
	@Transactional
	public void findWithNameAndDatesTest() {
		// arrange
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);

		List<Race> raceList = new ArrayList<Race>();
		raceList.add(race);
		Criteria criteriaMock = org.mockito.Mockito.mock(Criteria.class);

		when(criteriaMock.list()).thenReturn(raceList);
		when(criteriaMock.add(Restrictions.sqlRestriction("place like '%" + race.getPlace() + "%'")))
				.thenReturn(criteriaMock);
		when(criteriaMock.add(Restrictions.ge("date", new GregorianCalendar(2017, Calendar.SEPTEMBER, 25).getTime())))
				.thenReturn(criteriaMock);
		when(criteriaMock.add(Restrictions.le("date", new GregorianCalendar(2017, Calendar.SEPTEMBER, 25).getTime())))
				.thenReturn(criteriaMock);

		when(criteriaMock.addOrder(Order.asc("date"))).thenReturn(criteriaMock);
		when(criteriaMock.setFirstResult(0)).thenReturn(criteriaMock);
		when(criteriaMock.setMaxResults(10)).thenReturn(criteriaMock);
		when(criteriaMock.setProjection(Projections.rowCount())).thenReturn(criteriaMock);
		when(criteriaMock.uniqueResult()).thenReturn(1L);

		Mockito.doReturn(criteriaMock).when(raceService).createCriteriaSession();
		// act

		Page<Race> demoRacesPage = raceService.find("Coruña",
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 25).getTime(),
				new GregorianCalendar(2017, Calendar.OCTOBER, 02).getTime(), null, 0, null, null, null, null, 3, 0);

		// assert
		assertThat(demoRacesPage.getContent()).isEqualTo(raceList);
	}

	@Test
	@Transactional
	public void findWithPricesTest() {
		// arrange
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);

		List<Race> raceList = new ArrayList<Race>();
		raceList.add(race);

		Criteria criteriaMock = org.mockito.Mockito.mock(Criteria.class);

		when(criteriaMock.list()).thenReturn(raceList);
		when(criteriaMock.add(Restrictions.sqlRestriction("place like '%" + race.getPlace() + "%'")))
				.thenReturn(criteriaMock);
		when(criteriaMock.add(Restrictions.disjunction().add(Restrictions.ge("price1", BigDecimal.valueOf(20L)))
				.add(Restrictions.ge("price2", BigDecimal.valueOf(20L)))
				.add(Restrictions.ge("price3", BigDecimal.valueOf(20L)))
				.add(Restrictions.ge("price4", BigDecimal.valueOf(20L))))).thenReturn(criteriaMock);

		when(criteriaMock.add(Restrictions.disjunction().add(Restrictions.le("price1", BigDecimal.valueOf(40L)))
				.add(Restrictions.le("price2", BigDecimal.valueOf(40L)))
				.add(Restrictions.le("price3", BigDecimal.valueOf(40L)))
				.add(Restrictions.le("price4", BigDecimal.valueOf(40L))))).thenReturn(criteriaMock);

		when(criteriaMock.addOrder(Order.asc("date"))).thenReturn(criteriaMock);
		when(criteriaMock.setFirstResult(0)).thenReturn(criteriaMock);
		when(criteriaMock.setMaxResults(10)).thenReturn(criteriaMock);
		when(criteriaMock.setProjection(Projections.rowCount())).thenReturn(criteriaMock);
		when(criteriaMock.uniqueResult()).thenReturn(1L);

		Mockito.doReturn(criteriaMock).when(raceService).createCriteriaSession();
		// act
		Page<Race> demoRacesPage = raceService.find("Coruña", null, null, null, 0, BigDecimal.valueOf(20L),
				BigDecimal.valueOf(40L), null, null, 3, 0);

		// assert
		assertThat(demoRacesPage.getContent()).isEqualTo(raceList);
	}

	@Test
	@Transactional
	public void findWithDistancesTest() {
		// arrange
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);

		List<Race> raceList = new ArrayList<Race>();
		raceList.add(race);

		Criteria criteriaMock = org.mockito.Mockito.mock(Criteria.class);

		when(criteriaMock.list()).thenReturn(raceList);
		when(criteriaMock.add(Restrictions.sqlRestriction("place like '%" + race.getPlace() + "%'")))
				.thenReturn(criteriaMock);

		when(criteriaMock.add(Restrictions.ge("distance", 10000D))).thenReturn(criteriaMock);
		when(criteriaMock.add(Restrictions.le("distance", 40000D))).thenReturn(criteriaMock);

		when(criteriaMock.addOrder(Order.asc("date"))).thenReturn(criteriaMock);
		when(criteriaMock.setFirstResult(0)).thenReturn(criteriaMock);
		when(criteriaMock.setMaxResults(10)).thenReturn(criteriaMock);
		when(criteriaMock.setProjection(Projections.rowCount())).thenReturn(criteriaMock);
		when(criteriaMock.uniqueResult()).thenReturn(1L);

		Mockito.doReturn(criteriaMock).when(raceService).createCriteriaSession();
		// act
		Page<Race> demoRacesPage = raceService.find("Coruña", null, null, null, 0, null, null, 10000D, 40000D, 3, 0);

		// assert
		assertThat(demoRacesPage.getContent()).isEqualTo(raceList);
	}

	@Test
	@Transactional
	public void findWithRadiusTest() {
		// arrange
		Race race = new Race("Carrera de la Mujer XVII", new Place("A Coruña", 43.3623436F, -8.4115401F),
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);

		List<Race> raceList = new ArrayList<Race>();
		raceList.add(race);
		Criteria criteriaMock = org.mockito.Mockito.mock(Criteria.class);

		when(criteriaMock.list()).thenReturn(raceList);

		when(criteriaMock.add(Restrictions.sqlRestriction("(6371 * 2 * ASIN(SQRT(POWER(SIN((lat - abs(" + 43.3623436F
				+ ")) * pi()/180 / 2)," + " 2) + COS(lat * pi()/180 ) * COS(abs(" + 43.3623436F + ") *"
				+ " pi()/180) * POWER(SIN((lng - " + -8.4115401F + ") *" + "pi()/180 / 2), 2) )))" + " <= " + 300)))
						.thenReturn(criteriaMock);

		when(criteriaMock.addOrder(Order.asc("date"))).thenReturn(criteriaMock);
		when(criteriaMock.setFirstResult(0)).thenReturn(criteriaMock);
		when(criteriaMock.setMaxResults(10)).thenReturn(criteriaMock);
		when(criteriaMock.setProjection(Projections.rowCount())).thenReturn(criteriaMock);
		when(criteriaMock.uniqueResult()).thenReturn(1L);

		Mockito.doReturn(criteriaMock).when(raceService).createCriteriaSession();
		// act

		Page<Race> demoRacesPage = raceService.find("Coruña", null, null, 300D, 0, null, null, null, null, 3, 0);

		// assert
		assertThat(demoRacesPage.getContent()).isEqualTo(raceList);
	}

	@Test
	@Transactional
	public void createRaceTestWithExistingPlace() {
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));
		// arrange
		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		Place placeMock = new Place("Bilbao", 43.2630F, -2.9350F);

		Race raceMock = new Race("Carrera Popular Solidaria", placeMock,
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		raceMock.setId(10L);
		when(placeRepositoryMock.findByPlaceNameLikeIgnoreCase(race.getPlace().getName())).thenReturn(placeMock);
		when(raceRepositoryMock.save(race)).thenReturn(raceMock);

		// act
		Race raceResult = raceService.save(race);
		// assert
		// comprobamos que se ha añadido a la base de datos si tiene un id mayor que 0
		assertTrue(raceResult.getId() > 0);

	}

	@Test
	@Transactional
	public void createRaceTestWithoutExistingPlace() {
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));
		// arrange
		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		raceMock.setId(10L);
		when(placeRepositoryMock.findByPlaceNameLikeIgnoreCase(race.getPlace().getName())).thenReturn(null);
		when(raceRepositoryMock.save(race)).thenReturn(raceMock);

		// act
		Race raceResult = raceService.save(race);
		// assert
		// comprobamos que se ha añadido a la base de datos si tiene un id mayor que 0
		assertTrue(raceResult.getId() > 0);

	}

	@Test
	@Transactional
	public void convertRaceToInternalTest() throws CantConvertToInternalException {
		// arrange
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);
		Account userMock = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		userMock.setId(10000L);

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race.setId(2000L);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		raceMock.setId(2000L);

		BigDecimal[] prices = new BigDecimal[tariff.size()];
		Long[] minAges = new Long[tariff.size()];
		Long[] maxAges = new Long[tariff.size()];
		int i = 0;
		for (Tariff t : tariff) {
			prices[i] = t.getPrice();
			minAges[i] = t.getMinAge();
			maxAges[i] = t.getMaxAge();
			i++;
		}

		when(accountRepositoryMock.findOne(user.getId())).thenReturn(userMock);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(raceMock);
		when(activityRepositoryMock.save(any(Activity.class))).thenReturn(null);
		// act
		raceService.convertRaceToInternal(race.getId(), user.getId(), prices, minAges, maxAges);

		// assert
		assertEquals(userMock, raceMock.getCreatedBy());
		assertEquals(true, raceMock.getIsInternal());

		i = 0;
		for (BigDecimal price : prices) {
			assertEquals(price, raceMock.getPrices().get(i).getPrice());
			assertEquals(minAges[i], raceMock.getPrices().get(i).getMinAge());
			i++;
		}

	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	@Transactional
	public void convertPastRaceToInternalTest() throws CantConvertToInternalException {
		thrown.expect(CantConvertToInternalException.class);
		thrown.expectMessage("Race 2000 is already over.");
		// arrange
		// arrange
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);
		Account userMock = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		userMock.setId(10000L);

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race.setId(2000L);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		raceMock.setId(2000L);

		BigDecimal[] prices = new BigDecimal[tariff.size()];
		Long[] minAges = new Long[tariff.size()];
		Long[] maxAges = new Long[tariff.size()];
		int i = 0;
		for (Tariff t : tariff) {
			prices[i] = t.getPrice();
			minAges[i] = t.getMinAge();
			maxAges[i] = t.getMaxAge();
			i++;
		}

		when(accountRepositoryMock.findOne(user.getId())).thenReturn(userMock);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(raceMock);

		// act
		raceService.convertRaceToInternal(race.getId(), user.getId(), prices, minAges, maxAges);
	}

	@Test
	@Transactional
	public void convertInternalRaceToInternalTest() throws CantConvertToInternalException {
		thrown.expect(CantConvertToInternalException.class);
		thrown.expectMessage("Race 2000 is already internal.");
		// arrange
		// arrange
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);
		Account userMock = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		userMock.setId(10000L);

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2019, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, user);
		race.setId(2000L);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2019, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, userMock);
		raceMock.setId(2000L);

		BigDecimal[] prices = new BigDecimal[tariff.size()];
		Long[] minAges = new Long[tariff.size()];
		Long[] maxAges = new Long[tariff.size()];
		int i = 0;
		for (Tariff t : tariff) {
			prices[i] = t.getPrice();
			minAges[i] = t.getMinAge();
			maxAges[i] = t.getMaxAge();
			i++;
		}

		when(accountRepositoryMock.findOne(user.getId())).thenReturn(userMock);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(raceMock);

		// act
		raceService.convertRaceToInternal(race.getId(), user.getId(), prices, minAges, maxAges);

		// assert
		assertEquals(userMock, raceMock.getCreatedBy());
		assertEquals(true, raceMock.getIsInternal());

	}

	@Test
	@Transactional
	public void isOrganizerTest() {

		// arrange
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);
		Account userMock = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		userMock.setId(10000L);

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2019, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, user);
		race.setId(2000L);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, userMock);
		raceMock.setId(2000L);

		when(accountRepositoryMock.findOne(user.getId())).thenReturn(userMock);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(raceMock);

		// act
		Boolean result = raceService.isOrganizer(race.getId(), user.getId());

		// assert
		assertEquals(true, result);
	}

	@Test
	@Transactional
	public void isNotOrganizerTest() {

		// arrange
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);
		Account userMock = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		userMock.setId(10000L);

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race.setId(2000L);

		Race raceMock = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		raceMock.setId(2000L);

		when(accountRepositoryMock.findOne(user.getId())).thenReturn(userMock);
		when(raceRepositoryMock.findById(race.getId())).thenReturn(raceMock);

		// act
		Boolean result = raceService.isOrganizer(race.getId(), user.getId());

		// assert
		assertEquals(false, result);
	}

	@Test
	@Transactional
	public void getRaceTypeTest() {
		// arrange
		int validDistance = 42195;
		// act
		RaceType result = RaceType.getRaceType(validDistance);
		// assert
		assertEquals(result, RaceType.MARATHON);
		assertEquals(result.getDistance(), validDistance);
	}

	@Test(expected = IllegalArgumentException.class)
	@Transactional
	public void getUnexistingRaceTypeTest() {
		// arrange
		int invalidDistance = 0;
		// act
		RaceType.getRaceType(invalidDistance);
	}

	@Test
	@Transactional
	public void findUserRacesByAccount() {

		// Creación datos
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Account user = new Account("user@test.test", "test", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		user.setId(10000L);

		Race race1 = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2019, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race1.setId(1L);
		Race race2 = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race2.setId(2L);
		Race internalRace = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2018, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, true, null);
		internalRace.setId(3L);

		Attendance attendanceRace1 = new Attendance();
		attendanceRace1.setAccount(user);
		attendanceRace1.setRace(race1);
		attendanceRace1.setAttendanceId(1L);

		Attendance attendanceRace2 = new Attendance();
		attendanceRace2.setAccount(user);
		attendanceRace2.setRace(race2);
		attendanceRace2.setAttendanceId(2L);

		Enrollment enrollmentInternalRace = new Enrollment();
		enrollmentInternalRace.setTariff(tariff.get(2));
		enrollmentInternalRace.setAccount(user);
		enrollmentInternalRace.setRace(internalRace);
		enrollmentInternalRace.setChip(true);
		enrollmentInternalRace.setPaid(true);
		enrollmentInternalRace.setRanking(new Ranking(enrollmentInternalRace, 1, internalRace));

		// Mockeamos el attendanceRepository
		List<Attendance> attendanceList = new ArrayList<>();
		attendanceList.add(attendanceRace1);
		attendanceList.add(attendanceRace2);
		when(attendanceRepositoryMock.findByAccountEmail(user.getEmail())).thenReturn(attendanceList);

		// Mockeamos el enrollmentRepository
		List<Enrollment> enrollmentList = new ArrayList<>();
		enrollmentList.add(enrollmentInternalRace);
		when(enrollmentRepositoryMock.findByAccountEmail(user.getEmail())).thenReturn(enrollmentList);

		// Hacemos la llamada
		Page<UserRaceDTO> userRaces = raceService.findUserRacesByAccount(user.getEmail(), 0);

		// Comprobamos que construye bien el DTO y que ordena por fecha
		// Comprobar datos de carrera externa
		assertEquals(userRaces.getContent().get(0).getRaceId(), race1.getId());
		assertEquals(userRaces.getContent().get(0).getRacePlaceName(), race1.getPlace().getName());
		assertEquals(userRaces.getContent().get(0).getRaceDate(), race1.getDate());
		assertEquals(userRaces.getContent().get(0).getRaceIsInternal(), race1.getIsInternal());
		assertEquals(userRaces.getContent().get(0).getChip(), false);
		assertEquals(userRaces.getContent().get(0).getPaid(), false);
		assertEquals(userRaces.getContent().get(0).getUserPosition(), Integer.valueOf(0));
		// Comprobar datos de carrera interna
		assertEquals(userRaces.getContent().get(1).getRaceId(), internalRace.getId());
		assertEquals(userRaces.getContent().get(1).getRacePlaceName(), internalRace.getPlace().getName());
		assertEquals(userRaces.getContent().get(1).getRaceDate(), internalRace.getDate());
		assertEquals(userRaces.getContent().get(1).getRaceIsInternal(), internalRace.getIsInternal());
		assertEquals(userRaces.getContent().get(1).getChip(), enrollmentInternalRace.getChip());
		assertEquals(userRaces.getContent().get(1).getPaid(), enrollmentInternalRace.isPaid());
		assertEquals(userRaces.getContent().get(1).getUserPosition(),
				enrollmentInternalRace.getRanking().getPosition());

		assertEquals(userRaces.getContent().get(2).getRaceId(), race2.getId());
	}

	@Test
	@Transactional
	public void findSimilarRacesTest() {
		// Creación datos
		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55));
		tariff.add(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100));
		tariff.add(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17));

		Race race1 = new Race("Carrera Popular Solidaria", new Place("Bilbao", 43.2630F, -2.9350F),
				new GregorianCalendar(2019, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		race1.setId(1L);

		when(placeRepositoryMock.findByPlaceNameLikeIgnoreCase(race1.getPlace().getName()))
				.thenReturn(race1.getPlace());
		List<Race> raceList = new ArrayList<>();
		raceList.add(race1);
		when(raceRepositoryMock.findSimilarRaces(50000, race1.getPlace().getLat(), race1.getPlace().getLng(),
				race1.getDate(), race1.getName())).thenReturn(raceList);

		List<Race> result = raceService.findSimilarRaces(50000, race1.getPlace().getName(), race1.getDate(),
				race1.getName());
		List<Race> expectedRaceList = new ArrayList<>();
		expectedRaceList.add(race1);
		assertEquals(expectedRaceList, result);
	}
}
