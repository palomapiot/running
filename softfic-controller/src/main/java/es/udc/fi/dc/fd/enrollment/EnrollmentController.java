package es.udc.fi.dc.fd.enrollment;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.enrollment.exceptions.NotPossiblePayException;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.race.RaceService;
import es.udc.fi.dc.fd.tariff.TariffRepository;

@Controller
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private RaceService raceService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RaceRepository raceRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private TariffRepository tariffRepository;

	public EnrollmentController(RaceService raceServiceMock, EnrollmentService enrollmentServiceMock,
			AccountRepository accountRepositoryMock, RaceRepository raceRepositoryMock,
			EnrollmentRepository enrollmentRepositoryMock, TariffRepository tariffRepositoryMock) {
		this.raceService = raceServiceMock;
		this.enrollmentService = enrollmentServiceMock;
		this.accountRepository = accountRepositoryMock;
		this.raceRepository = raceRepositoryMock;
		this.enrollmentRepository = enrollmentRepositoryMock;
		this.tariffRepository = tariffRepositoryMock;

	}

	/**
	 * Inscribirse a una carrera interna.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @return the string
	 */
	@GetMapping("/enroll")
	public String enrollToRace(Principal principal, Model model, @RequestParam Long raceId) {
		// Enroll or not enroll to the race
		enrollmentService.enrollToRace(raceId, principal.getName());
		Account account = accountRepository.findOneByEmail(principal.getName());
		Race race = raceService.findById(raceId);

		// Load in the model the data needed by the fragment
		model.addAttribute("enrollment", enrollmentService.isEnroll(raceId, principal.getName()));
		model.addAttribute("enrollmentEnt", enrollmentService.getEnroll(race, account));
		model.addAttribute("race", raceService.findById(raceId));
		model.addAttribute("enrollments", enrollmentService.numberOfEnrollments(raceId));

		// Reload fragment "enrollZone" located in "races/raceDetails.html"
		return "races/raceDetails :: enrollZone";
	}

	/**
	 * Recibe el raceId y el usuario que paga para realizar el pago y el envío del
	 * email.
	 *
	 * @param principal
	 *            usuario que hace el pago
	 * @param raceId
	 *            identificador de la carrera a pagar
	 * @param chip
	 *            the chip
	 * @return the string
	 * @throws NotPossiblePayException
	 *             the not possible pay exception
	 */
	@PostMapping("/payrace")
	public String payRace(Principal principal, @RequestParam(value = "raceId") Long raceId,
			@RequestParam(value = "chip", defaultValue = "false") Boolean chip) throws NotPossiblePayException {
		Long accId = null;

		if (principal != null) {
			accId = accountRepository.findOneByEmail(principal.getName()).getId();
		}

		enrollmentService.payRace(raceId, accId, chip);
		return "redirect:/racedetails?raceId=" + raceId;
	}

	/**
	 * Carreras internas pagadas por un usuario.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param page
	 *            the page
	 * @return the string
	 */
	@GetMapping("/payments")
	public String payments(Principal principal, Model model, @RequestParam int page) {
		Account account = accountRepository.findOneByEmail(principal.getName());
		model.addAttribute("paymentsPage", enrollmentService.getRacesPayments(account, page));
		return "races/payments";
	}

}
