package es.udc.fi.dc.fd.follow;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceService;

@Controller
public class FollowController {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	RaceService raceService;

	/**
	 * Instantiates a new follow controller.
	 *
	 * @param accountRepositoryMock
	 *            the account repository mock
	 * @param accountServiceMock
	 *            the account service mock
	 */
	public FollowController(AccountRepository accountRepositoryMock, AccountService accountServiceMock) {
		this.accountService = accountServiceMock;
		this.accountRepository = accountRepositoryMock;
	}

	/**
	 * Profile.
	 *
	 * @return the string
	 */
	@GetMapping("/follow")
	String profile() {
		return "follow/follow";
	}

	/**
	 * Accounts.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param email
	 *            the email
	 * @param page
	 *            the page
	 * @return the string
	 */
	/*
	 * Devuelve una página con una lista (paginada) de usuarios cuyo email
	 * coindicida con el email de búsqueda por parámetro.
	 */
	@GetMapping("/accounts")
	public String accounts(Principal principal, Model model, @RequestParam String email, @RequestParam int page) {
		// Recuperamos la cuenta actual
		Account account = accountRepository.findOneByEmail(principal.getName());
		Long id = account.getId();

		// Conseguimos la página de Accounts
		model.addAttribute("accountPage", accountService.findAccountsFollow(email, id, principal.getName(), page));

		return "follow/accounts";
	}

	/**
	 * Following.
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
	 * Devuelve una página con una lista (paginada) con los usuarios a los que sigue
	 * el usuario que realiza la petición.
	 */
	@GetMapping("/following")
	public String following(Principal principal, Model model, @RequestParam int page) {
		// Recuperamos la cuenta actual
		Account account = accountRepository.findOneByEmail(principal.getName());
		Long id = account.getId();

		// Conseguimos la página de Follow
		model.addAttribute("followPage", accountService.findAccountsFollowing(id, page));

		return "profile/following";
	}

	/**
	 * Devuelve una página con una lista (paginada) con los usuarios que han
	 * solicitado el seguimiento del usuario actual.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param page
	 *            the page
	 * @return the string
	 */
	@GetMapping("/requests")
	public String followrequests(Principal principal, Model model, @RequestParam int page) {
		// Recuperamos la cuenta actual
		Account account = accountRepository.findOneByEmail(principal.getName());
		Long id = account.getId();

		// Conseguimos la página de Pending
		model.addAttribute("pendingPage", accountService.findAccountsPending(id, page));

		return "profile/requests";
	}

	/**
	 * Accounts.
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param id
	 *            the id
	 * @param page
	 *            the page
	 * @return the string
	 */
	/*
	 * Devuelve una página con una lista (paginada) de las carreras a las que asiste
	 * el usuario que realiza la petición.
	 */
	@GetMapping("/profile")
	public String accounts(Principal principal, Model model, @RequestParam Long id, @RequestParam int page) {
		// Recuperamos el usuario actual
		Account account = accountRepository.findOne(id);
		String email = account.getEmail();
		String username = account.getUsername();

		// Conseguimos la página de Race
		Page<Race> racePage = raceService.findGoingRacesByAccount(email, page);
		model.addAttribute("racePage", racePage);
		model.addAttribute("email", username);

		return "profile/profile";
	}
}
