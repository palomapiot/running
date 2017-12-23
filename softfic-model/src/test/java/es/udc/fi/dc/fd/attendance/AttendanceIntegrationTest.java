package es.udc.fi.dc.fd.attendance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceService;
import es.udc.fi.dc.fd.race.RaceType;
import es.udc.fi.dc.fd.tariff.Tariff;

public class AttendanceIntegrationTest extends WebSecurityConfigurationAware {

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private RaceService raceService;

	@Autowired
	private AccountService accountService;

	@Test
	@Transactional
	public void addRaceAndLookForAttendants() {

		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55)));
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100)));
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17)));

		Race race = new Race("Carrera Popular Solidaria", new Place("Bilbao", null, null),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		raceService.save(race);
		Account demoUser = new Account("addRaceAndLookForAttendants", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(demoUser);

		// Probar que devuelve una lista vacía si no hay nadie apuntado
		assertTrue(accountService.findAccountsByRace(race.getId(), 0).getContent().isEmpty());

		attendanceService.attendToRace(race.getId(), "addRaceAndLookForAttendants");

		// Probar que despues de apuntarse devuelve un usuario y es el que se anotó
		assertTrue(accountService.findAccountsByRace(race.getId(), 0).getContent().size() == 1);
		assertTrue(accountService.findAccountsByRace(race.getId(), 0).getContent().get(0).equals(demoUser));

		attendanceService.attendToRace(race.getId(), "addRaceAndLookForAttendants");

		// Probar que al volver a realizar la operación de desapunta
		assertTrue(accountService.findAccountsByRace(race.getId(), 0).getContent().isEmpty());

	}

	@Test
	@Transactional
	public void attendToRacesAndLookForMyRaces() {

		List<Tariff> tariff = new ArrayList<Tariff>();
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(10), (long) 18, (long) 55)));
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(5), (long) 56, (long) 100)));
		tariff.add(raceService.addTariff(new Tariff(BigDecimal.valueOf(7), (long) 0, (long) 17)));

		Race race1 = new Race("Carrera1", new Place("Bilbao", null, null),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		Race race2 = new Race("Carrera2", new Place("Bilbao", null, null),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);
		Race race3 = new Race("Carrera3", new Place("Bilbao", null, null),
				new GregorianCalendar(3017, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		// Carrera ya pasada
		Race race4 = new Race("Carrera4", new Place("Bilbao", null, null),
				new GregorianCalendar(2010, Calendar.OCTOBER, 12).getTime(), "http://acambibizkaia.org/",
				"http://acambibizkaia.org/images/apuntateAqui", false, true, BigDecimal.valueOf(2), 5000.0,
				RaceType.POPULAR_RACE, tariff, false, null);

		raceService.save(race1);
		raceService.save(race2);
		raceService.save(race3);
		raceService.save(race4);

		Account demoUser = new Account("attendToRacesAndLookForMyRaces", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		accountService.save(demoUser);

		assertTrue(raceService.findRacesByAccount("attendToRacesAndLookForMyRaces", 0).getContent().isEmpty());
		attendanceService.attendToRace(race1.getId(), "attendToRacesAndLookForMyRaces");
		attendanceService.attendToRace(race2.getId(), "attendToRacesAndLookForMyRaces");
		attendanceService.attendToRace(race3.getId(), "attendToRacesAndLookForMyRaces");
		attendanceService.attendToRace(race4.getId(), "attendToRacesAndLookForMyRaces");

		List<Race> raceList = raceService.findRacesByAccount("attendToRacesAndLookForMyRaces", 0).getContent();

		// Probar que despues de apuntarse devuelve las 3 carreras a las que se anoto, y
		// no a más
		assertTrue(raceList.size() == 3);
		assertTrue(raceList.contains(race1));
		assertTrue(raceList.contains(race2));
		assertTrue(raceList.contains(race3));
		assertFalse(raceList.contains(race4));

		attendanceService.attendToRace(race1.getId(), "attendToRacesAndLookForMyRaces");

		// Probar que al volver a realizar la operación de desapunta
		assertFalse(raceService.findRacesByAccount("attendToRacesAndLookForMyRaces", 0).getContent().contains(race1));

	}
}
