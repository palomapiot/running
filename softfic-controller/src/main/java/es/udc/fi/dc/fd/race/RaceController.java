package es.udc.fi.dc.fd.race;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.attendance.AttendanceService;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;
import es.udc.fi.dc.fd.race.exceptions.CantConvertToInternalException;
import es.udc.fi.dc.fd.ranking.RankingService;
import es.udc.fi.dc.fd.support.web.MessageHelper;
import es.udc.fi.dc.fd.tariff.Tariff;

@Controller
public class RaceController {

	@Autowired
	private RankingService rankingService;

	@Autowired
	private RaceService raceService;

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private EnrollmentService enrollmentService;

	/**
	 * Constante para la distancia en la búsqueda de carreras similares
	 */
	private static double distance = 50; // En kilómetros

	/**
	 * Instantiates a new race controller.
	 *
	 * @param raceServiceMock
	 *            the race service mock
	 * @param attendanceServiceMock
	 *            the attendance service mock
	 * @param rankingServiceMock
	 *            the ranking service mock
	 * @param enrollmentServiceMock
	 *            the enrollment service mock
	 * @param accountRepositoryMock
	 *            the account repository mock
	 * @param accountServiceMock
	 *            the account service mock
	 */
	public RaceController(RaceService raceServiceMock, AttendanceService attendanceServiceMock,
			RankingService rankingServiceMock, EnrollmentService enrollmentServiceMock,
			AccountRepository accountRepositoryMock, AccountService accountServiceMock) {
		this.raceService = raceServiceMock;
		this.attendanceService = attendanceServiceMock;
		this.rankingService = rankingServiceMock;
		this.enrollmentService = enrollmentServiceMock;
		this.accountRepository = accountRepositoryMock;
		this.accountService = accountServiceMock;
	}

	/**
	 * Races.
	 *
	 * @param model
	 *            the model
	 * @param place
	 *            the place
	 * @param minDate
	 *            the min date
	 * @param maxDate
	 *            the max date
	 * @param distance
	 *            the distance
	 * @param minPrice
	 *            the min price
	 * @param maxPrice
	 *            the max price
	 * @param medicalTest
	 *            the medical test
	 * @param minDistance
	 *            the min distance
	 * @param maxDistance
	 *            the max distance
	 * @param page
	 *            the page
	 * @param orderType
	 *            the order type
	 * @return the string
	 */
	/*
	 * Devuelve una página con la lista (paginada) de objetos Race, resultado de la
	 * búsqueda filtrada y ordenada según parámetros.
	 */
	@GetMapping("/races")
	public String races(Model model, @RequestParam String place,
			@RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") Date minDate,
			@RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") Date maxDate, @RequestParam Double distance,
			@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice, @RequestParam int medicalTest,
			@RequestParam Double minDistance, @RequestParam Double maxDistance, @RequestParam int page,
			@RequestParam int orderType) {
		// Si no se indica un Place, entonces anulamos el filtro por distancia radial
		// desde el Place

		model.addAttribute("racePage",
				raceService.find(place, minDate, maxDate, (place == null || place.length() == 0) ? null : distance,
						medicalTest, minPrice, maxPrice, minDistance, maxDistance, orderType, page)); // racePage

		return "races/races";
	}

	/**
	 * Race details.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @return the string
	 */
	/*
	 * Devuelve una página con los detalles de la carrera que se indica por
	 * parámetro
	 */
	@GetMapping("/racedetails")
	public String raceDetails(Principal principal, Model model, @RequestParam Long raceId) {

		// Null si el usuario no está logeado
		Boolean attendance = null;
		Boolean enrollment = null;
		Account account = null;

		model.addAttribute("race", raceService.findById(raceId)); // The race

		if (principal != null) {
			attendance = attendanceService.isAttendant(raceId, principal.getName());
			enrollment = enrollmentService.isEnroll(raceId, principal.getName());
			account = accountRepository.findOneByEmail(principal.getName());
		}

		Race race = raceService.findById(raceId);
		List<Tariff> tariffs = race.getPrices();

		model.addAttribute("tariffs", tariffs); // Bool | user attend to race
		model.addAttribute("attendance", attendance); // Bool | user attend to race
		model.addAttribute("attendants", attendanceService.numberOfAttendants(raceId)); // Number of attendants
		if (account != null) {
			model.addAttribute("followingAttendances",
					accountService.numberOfFollowingAttendances(raceId, account.getId())); // Number of following
			model.addAttribute("followingEnrollments",
					accountService.numberOfFollowingEnrollments(raceId, account.getId()));
		} // attendants
		model.addAttribute("enrollment", enrollment); // Bool | user enroll to race
		model.addAttribute("enrollments", enrollmentService.numberOfEnrollments(raceId));
		// Enrollments necesarios para el autocompletado.
		model.addAttribute("enrollmentList", enrollmentService.findByRaceId(raceId));
		model.addAttribute("enrollmentEnt", enrollmentService.getEnroll(race, account)); // Entity Enrollment
		model.addAttribute("rankingList", rankingService.findRankingByRace(raceId, 0).getContent());
		model.addAttribute("isOwner",
				(race.getIsInternal() && account != null) ? (account.getEmail().equals(race.getCreatedBy().getEmail()))
						: false);

		return "races/raceDetails";
	}

	/**
	 * Recupera los usuarios que van a asistir a una carrera externa.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param page
	 *            the page
	 * @return Devuelve una página con la lista (paginada) de los usuarios que
	 *         asisten a la carrera indicada por parámetro
	 */
	@GetMapping("racedetails/attendants")

	public String raceAttendants(Principal principal, Model model, @RequestParam Long raceId, @RequestParam int page) {
		Account account = null;
		Long id = -1L;
		if (principal != null) {
			account = accountRepository.findOneByEmail(principal.getName());
			id = account.getId();
		}

		model.addAttribute("attendantPage", accountService.findRaceAttendantsFollow(raceId, id, page));
		return "races/raceAttendants";
	}

	/**
	 * Recupera los usuarios inscritos a una carrera interna.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param page
	 *            the page
	 * @return the string
	 */
	@GetMapping("racedetails/enrollments")

	public String raceEnrollments(Principal principal, Model model, @RequestParam Long raceId, @RequestParam int page) {
		Account account = null;
		Long id = -1L;
		if (principal != null) {
			account = accountRepository.findOneByEmail(principal.getName());
			id = account.getId();
		}

		model.addAttribute("enrollmentPage", accountService.findRaceEnrollmentsFollow(raceId, id, page));
		return "races/raceEnrollments";
	}

	/**
	 * Recupera los usuarios que van a asistir a una carrera externa y que son
	 * amigos.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param page
	 *            the page
	 * @return Devuelve una página con la lista (paginada) de los usuarios que
	 *         asisten a la carrera indicada por parámetro
	 */
	@GetMapping("racedetails/friendattendants")

	public String raceFriendAttendants(Principal principal, Model model, @RequestParam Long raceId,
			@RequestParam int page) {
		Account account = null;
		Long id = -1L;
		if (principal != null) {
			account = accountRepository.findOneByEmail(principal.getName());
			id = account.getId();
		}

		model.addAttribute("attendantPage", accountService.findRaceFollowingAttendances(raceId, id, page));
		return "follow/raceFollowingAccounts";
	}

	/**
	 * Recupera los usuarios que van a asistir a una carrera externa y que son
	 * amigos.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param page
	 *            the page
	 * @return Devuelve una página con la lista (paginada) de los usuarios que
	 *         asisten a la carrera indicada por parámetro
	 */
	@GetMapping("racedetails/friendenrollments")

	public String raceFriendEnrollments(Principal principal, Model model, @RequestParam Long raceId,
			@RequestParam int page) {
		Account account = null;
		Long id = -1L;
		if (principal != null) {
			account = accountRepository.findOneByEmail(principal.getName());
			id = account.getId();
		}

		model.addAttribute("enrollmentPage", accountService.findRaceFollowingEnrollments(raceId, id, page));
		return "follow/raceFollowingAccountsEnrollment";
	}

	/**
	 * Myraces.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param page
	 *            the page
	 * @return the string
	 */
	/*
	 * Devuelve una página con la lista (paginada) de las carreras a las que asiste
	 * o está inscrito el usuario que realiza la petición.
	 */
	@GetMapping("/myraces")
	public String myraces(Principal principal, Model model, @RequestParam int page) {
		if (principal != null) {
			model.addAttribute("userRacesPage", raceService.findUserRacesByAccount(principal.getName(), page));
		}
		return "races/myraces";
	}

	/*
	 * Crea una nueva Race a partir de los datos del formulario raceForm, y redirige
	 * al usuario a la página de detalles de la carrera creada
	 */
	/**
	 * Createrace.
	 *
	 * @param principal
	 *            the principal
	 * @param prices
	 *            the prices
	 * @param minAges
	 *            the min ages
	 * @param maxAges
	 *            the max ages
	 * @param model
	 *            the model
	 * @param raceForm
	 *            the race form
	 * @param ra
	 *            the ra
	 * @return the string
	 */
	/*
	 * Para comprobar si la carrera existe se cogen 50km como distancia estándar
	 */
	@PostMapping("/createrace")
	public String createrace(Principal principal, @RequestParam(value = "prices") BigDecimal[] prices,
			@RequestParam(value = "minAges") Long[] minAges, @RequestParam(value = "maxAges") Long[] maxAges,
			Model model, @Valid @ModelAttribute RaceForm raceForm, RedirectAttributes ra) {
		// Creamos la carrera

		int i = 0;

		List<Tariff> tariffs = new ArrayList<>(prices.length);

		for (BigDecimal price : prices) {
			Tariff tariff = new Tariff();
			tariff.setPrice(price);
			tariff.setMinAge(minAges[i]);
			tariff.setMaxAge(maxAges[i]);
			tariffs.add(tariff);
			i++;
		}
		Race race = raceForm.createRace(tariffs);
		List<Race> possibleResults = raceService.findSimilarRaces(distance, race.getPlace().getName(), race.getDate(),
				race.getName());

		/**
		 * Si no existe ninguna carrera que pueda coincidir con la que se va a insertar
		 * se inserta
		 */
		if (possibleResults == null || possibleResults.isEmpty()) {
			if (race.getIsInternal())
				race.setCreatedBy(accountRepository.findOneByEmail(principal.getName()));
			race = raceService.save(race);

			MessageHelper.addSuccessAttribute(ra, "createrace.success");
			model.addAttribute("race", race);
			model.addAttribute("attendance", Boolean.FALSE);
			model.addAttribute("attendants", 0);

			return "redirect:/racedetails?raceId=" + race.getId();
		}
		/**
		 * Si existe alguna carrera que pueda coincidir con la que se va a insertar se
		 * comprueba si la que se va a insertar es la misma que alguna de las
		 * encontradas
		 */
		else {

			model.addAttribute("raceList", possibleResults);
			model.addAttribute("raceAdd", race);

			return "races/similarRaces";
		}
	}

	/**
	 * Inserta la carrera en base de datos después de comprobar si ya existe en la
	 * función createrace.
	 *
	 * @param principal
	 *            the principal
	 * @param prices
	 *            the prices
	 * @param minAges
	 *            the min ages
	 * @param maxAges
	 *            the max ages
	 * @param model
	 *            the model
	 * @param raceForm
	 *            the race form
	 * @param ra
	 *            the ra
	 * @return the string
	 */
	@PostMapping("/confirmcreaterace")
	public String confirmcreaterace(Principal principal, @RequestParam(value = "prices") BigDecimal[] prices,
			@RequestParam(value = "minAges") Long[] minAges, @RequestParam(value = "maxAges") Long[] maxAges,
			Model model, @Valid @ModelAttribute RaceForm raceForm, RedirectAttributes ra) {
		// Creamos la carrera

		int i = 0;

		List<Tariff> tariffs = new ArrayList<>(prices.length);

		for (BigDecimal price : prices) {
			Tariff tariff = new Tariff();
			tariff.setPrice(price);
			tariff.setMinAge(minAges[i]);
			tariff.setMaxAge(maxAges[i]);
			tariffs.add(tariff);
			i++;
		}
		Race race = raceForm.createRace(tariffs);
		if (race.getIsInternal())
			race.setCreatedBy(accountRepository.findOneByEmail(principal.getName()));
		race = raceService.save(race);

		MessageHelper.addSuccessAttribute(ra, "createrace.success");
		model.addAttribute("race", race);
		model.addAttribute("attendance", Boolean.FALSE);
		model.addAttribute("attendants", 0);

		return "redirect:/racedetails?raceId=" + race.getId();
	}

	/**
	 * Establece al usuario como organizador de la carrera indicada por parámetro
	 * 
	 * Devuelve el fragment "internalZone" localizado en "races/raceDetails".
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param prices
	 *            the prices
	 * @param minAges
	 *            the min ages
	 * @param maxAges
	 *            the max ages
	 * @return the string
	 */
	@PostMapping("/internal")
	public String convertRaceToInternal(Principal principal, Model model, @RequestParam Long raceId,
			@RequestParam(value = "prices") BigDecimal[] prices, @RequestParam(value = "minAges") Long[] minAges,
			@RequestParam(value = "maxAges") Long[] maxAges) {

		// Establecer, si es posible, el usuario como organizador de la carrera
		Account account = accountRepository.findOneByEmail(principal.getName());
		try {
			raceService.convertRaceToInternal(raceId, account.getId(), prices, minAges, maxAges);
		} catch (CantConvertToInternalException e) {
			return "/error";
		}

		model.addAttribute("race", raceService.findById(raceId));

		// Refresca la página con los detalles de la carrera
		return "redirect:/racedetails?raceId=" + raceId;
	}
}