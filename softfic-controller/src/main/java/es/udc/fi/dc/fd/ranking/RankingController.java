package es.udc.fi.dc.fd.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentService;

@Controller
public class RankingController {

	@Autowired
	RankingService rankingService;

	@Autowired
	EnrollmentService enrollmentService;

	/**
	 * Necesario para las pruebas de unidad.
	 *
	 * @param rankingServiceMock
	 *            the ranking service mock
	 */
	public RankingController(RankingService rankingServiceMock) {
		this.rankingService = rankingServiceMock;
	}

	/**
	 * Crea los ranking para una carrera para los usuarios identificados por el
	 * email indicado. La posición viene determinada por el orden en el que llegan
	 * los emails
	 *
	 * @param userEmails
	 *            the user emails
	 * @param raceId
	 *            the race id
	 * @return the string
	 * @throws CantCreateRankingException
	 *             Cuando alguno de los emails de la lista aparece más de una vez.
	 */
	@PostMapping("/addranking")
	public String addRanking(@RequestParam(value = "rankingEmails") String[] userEmails,
			@RequestParam(value = "raceId") Long raceId) throws CantCreateRankingException {
		// Asignamos las posiciones en el ranking
		int position = 1;

		// Comprobamos que no haya duplicados
		List<String> tmpEmails = new ArrayList<>(userEmails.length);
		for (String userEmail : userEmails) {
			if (tmpEmails.contains(userEmail)) {
				throw new CantCreateRankingException("Usuario duplicado en ranking: " + userEmail);
			}
			tmpEmails.add(userEmail);
		}

		// Creamos los rankings
		for (String userEmail : userEmails) {
			rankingService.addRunnerToRaceRanking(raceId, userEmail, position);
			position++;
		}
		return "redirect:/racedetails?raceId=" + raceId;
	}

	/**
	 * Devuelve una página con el ránking completo de la carrera.
	 *
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @param page
	 *            the page
	 * @return the string
	 */
	@GetMapping("/redirectRanking")
	public String redirectRanking(Model model, @RequestParam Long raceId, @RequestParam int page) {
		model.addAttribute("rankingPage", rankingService.findRankingByRace(raceId, page));
		return "races/ranking";
	}

	/**
	 * Devuelve una lista de {@link RunnerDTO} asociado a una carrera.
	 *
	 * @param raceId
	 *            the race id
	 * @param term
	 *            the term
	 * @return the race runners
	 */
	@GetMapping("/raceRunners")
	public @ResponseBody List<RunnerDTO> getRaceRunners(@RequestParam Long raceId, @RequestParam String term) {
		List<Enrollment> enrollments = enrollmentService.findByRaceIdAndKeywords(raceId, term);
		List<RunnerDTO> runners = new ArrayList<>(enrollments.size());
		for (Enrollment e : enrollments)
			runners.add(new RunnerDTO(e.getAccount().getUsername(), e.getAccount().getEmail()));
		return runners;
	}

	/**
	 * Devuelve un mensaje que indica si el ranking que se va a subir es válido o
	 * no.
	 *
	 * @param userEmails
	 *            the user emails
	 * @param raceId
	 *            the race id
	 * @return "" si todo va bien Si hay un error de validación devuelve el mensaje
	 *         de error
	 */
	@GetMapping("/validateRanking")
	public @ResponseBody String validateRanking(@RequestParam(value = "rankingEmails[]") String[] userEmails,
			@RequestParam(value = "raceId") Long raceId) {
		List<String> userEmailsList = new ArrayList<>(Arrays.asList(userEmails));
		Set<String> conjuntoEmails = new HashSet<>(userEmailsList);

		// Validar que no hay emails duplicados
		if (userEmailsList.size() > conjuntoEmails.size())
			return "Hay usuarios duplicados en el ranking.";

		// Validar que todos los usuarios están enrolados
		for (String email : userEmailsList) {
			if (!enrollmentService.isEnroll(raceId, email))
				return "El usuario con mail " + email + " no está enrolado en la carrera.";
		}

		return "";
	}

}
