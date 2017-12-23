package es.udc.fi.dc.fd.activity;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.FollowActivity;
import es.udc.fi.dc.fd.activity.entities.GoActivity;
import es.udc.fi.dc.fd.activity.entities.HostActivity;
import es.udc.fi.dc.fd.activity.entities.RankingActivity;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.place.PlaceRepository;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.ranking.Ranking;
import es.udc.fi.dc.fd.ranking.RankingRepository;

public class ActivityIntegrationTest extends WebSecurityConfigurationAware {

	@Autowired
	private ActivityRepository actRepository;

	@Autowired
	private ActivityService actService;

	@Autowired
	private AccountRepository accRepository;

	@Autowired
	private RaceRepository raceRepository;

	@Autowired
	private RankingRepository rankingRepository;

	@Autowired
	private PlaceRepository placeRepository;

	/*
	 * Prueba el funcionamiento de las clases activity, sus constructores y el
	 * toString
	 */
	@Test
	public void activityEntityTest() {
		Account account = new Account("testActivityCreateAndRetrieveTest@example.com", "demo",
				"testActivityCreateAndRetrieveTest", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followed = new Account("testActivityCreateAndRetrieveTest2@example.com", "demo",
				"testActivityCreateAndRetrieveTest2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Place place = new Place("A Coruña", 43.3623436F, -8.4115401F);
		Race race1 = new Race("testActivityCreateAndRetrieveTest", place,
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		Race race2 = new Race("testActivityCreateAndRetrieveTest2", place,
				new GregorianCalendar(2016, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, true, account);
		Enrollment enroll = new Enrollment(account, race1, Boolean.FALSE, Boolean.FALSE);
		Ranking ranking = new Ranking(enroll, 2, race1);

		Activity activity1 = new FollowActivity(account, followed);
		Activity activity2 = new GoActivity(account, race1);
		Activity activity3 = new HostActivity(account, race2);
		Activity activity4 = new RankingActivity(account, ranking);
		Activity activity5 = new GoActivity(account, race2);
		String parent = "<a href=\"/myprofile?accId=" + account.getId() + "\">" + account.getName() + "</a>";

		assertEquals(
				"<i class=\"fa fa-user-plus\" aria-hidden=\"true\"></i> " + parent + " is now following "
						+ "<a href=\"/myprofile?accId=" + followed.getId() + "\">" + followed.getName() + "</a>",
				activity1.toString("/"));
		assertEquals(
				"<i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> " + parent + " will attend "
						+ "<a href=\"/racedetails?raceId=" + race1.getId() + "\">" + race1.getName() + "</a>",
				activity2.toString("/"));
		assertEquals(
				"<i class=\"fa fa-users\" aria-hidden=\"true\"></i> " + parent + " is now organizing "
						+ "<a href=\"/racedetails?raceId=" + race2.getId() + "\">" + race2.getName() + "</a>",
				activity3.toString("/"));
		assertEquals(
				"<i class=\"fa fa-trophy\" aria-hidden=\"true\"></i> " + parent + " has reached the #"
						+ ranking.getPosition() + " position in <a href=\"" + "/racedetails?raceId="
						+ ranking.getRace().getId() + "\">" + ranking.getRace().getName() + "</a>",
				activity4.toString("/"));
		assertEquals(
				"<i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> " + parent + " has enrolled in "
						+ "<a href=\"/racedetails?raceId=" + race2.getId() + "\">" + race2.getName() + "</a>",
				activity5.toString("/"));
		SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm dd-MM-yyyy");
		assertEquals(dt1.format(activity4.getTimestamp()), activity4.getFormattedTime());
	}

	@Test
	@Transactional
	public void activityCreateAndRetrieveTest() throws InterruptedException {
		Account account = new Account("testActivityCreateAndRetrieveTest@example.com", "demo",
				"testActivityCreateAndRetrieveTest", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followed = new Account("testActivityCreateAndRetrieveTest2@example.com", "demo",
				"testActivityCreateAndRetrieveTest2", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Place place = new Place("activityCreateAndRetrieveTest", 43.3623436F, -8.4115401F);
		placeRepository.save(place);
		Race race1 = new Race("testActivityCreateAndRetrieveTest", place,
				new GregorianCalendar(2017, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		Race race2 = new Race("testActivityCreateAndRetrieveTest2", place,
				new GregorianCalendar(2016, Calendar.SEPTEMBER, 30).getTime(), "http://www.carreradelamujer.com/coruna",
				"http://www.carreradelamujer.com/img/index/logo-carrera-mujer-index2.svg", null, null, null, null, null,
				null, false, null);
		Ranking ranking = rankingRepository.findOne(1L);
		accRepository.save(account);
		accRepository.save(followed);
		raceRepository.save(race1);
		raceRepository.save(race2);

		Activity activity1 = new FollowActivity(account, followed);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2017, 10, 5, 10, 12, 32);
		activity1.setTimestamp(cal.getTime());
		Activity activity2 = new GoActivity(account, race1);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2017, 11, 5, 10, 21, 32);
		activity2.setTimestamp(cal.getTime());
		Activity activity3 = new HostActivity(account, race2);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2017, 11, 6, 10, 21, 32);
		activity3.setTimestamp(cal.getTime());
		Activity activity4 = new RankingActivity(account, ranking);

		actRepository.save(activity3);
		actRepository.save(activity1);
		actRepository.save(activity2);
		actRepository.save(activity4);

		Page<Activity> page = actService.findActivities(account.getId(), 0);

		// check order
		assertEquals(activity4, page.getContent().get(0));
		assertEquals(activity3, page.getContent().get(1));
		assertEquals(activity2, page.getContent().get(2));
		assertEquals(activity1, page.getContent().get(3));
	}

}
