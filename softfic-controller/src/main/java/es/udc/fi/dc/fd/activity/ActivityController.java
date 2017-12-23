package es.udc.fi.dc.fd.activity;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;

/**
 * La clase ActivityController controla los endpoints de la timeline en el
 * perfil de un usuario, usando ajax, y el feed del usuario usando paginación
 * clásica.
 */
@Controller
public class ActivityController {

	/** The activity service. */
	@Autowired
	private ActivityService activityService;

	/** The account repository. */
	@Autowired
	private AccountRepository accountRepository;

	/**
	 * Instantiates a new activity controller for mock tests.
	 *
	 * @param activityServiceMock
	 *            the activity service mock
	 * @param accountRepositoryMock
	 *            the account repository mock
	 */
	public ActivityController(ActivityService activityServiceMock, AccountRepository accountRepositoryMock) {
		this.activityService = activityServiceMock;
		this.accountRepository = accountRepositoryMock;
	}

	/**
	 * Recarga en el perfil de usuario la timeline con la pagina que se pase por
	 * parámetro.
	 *
	 * @param principal
	 *            información del usuario que pide la timeline
	 * @param model
	 *            el modelo donde se cargan los datos para la plantilla html
	 * @param accId
	 *            el id de la cuenta que quieres recargar los datos
	 * @param page
	 *            la pagina de la lista de la timeline
	 * @return la señal para recargar la pagina con los nuevos datos
	 */
	@GetMapping("/timeline")
	public String timeline(Principal principal, Model model, @RequestParam Long accId, @RequestParam int page) {
		Page<Activity> actPage = activityService.findActivities(accId, page);
		model.addAttribute("timeline", actPage);
		return "profile/account :: timelineZone";
	}

	/**
	 * Controla el Feed del usuario, donde se muestra la actividad de todos sus
	 * amigos.
	 *
	 * @param principal
	 *            El usuario que quiere ver la actividad reciente
	 * @param model
	 *            El modelo donde se guardan los datos
	 * @param page
	 *            Numero de pagina
	 * @return La pagina donde se encuentra la plantilla para el feed
	 */
	@GetMapping("/feed")
	public String feed(Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
		Long accId = accountRepository.findOneByEmail(principal.getName()).getId();
		Page<Activity> actPage = activityService.findActivityFeed(accId, page);
		model.addAttribute("timeline", actPage);
		return "profile/feed";
	}
}
