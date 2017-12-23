package es.udc.fi.dc.fd.account;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

	private final AccountRepository accountRepository;

	private final AccountService accountService;

	/**
	 * Instantiates a new account controller.
	 *
	 * @param accountRepository
	 *            the account repository
	 * @param accountService
	 *            the account service
	 */
	public AccountController(AccountRepository accountRepository, AccountService accountService) {
		this.accountRepository = accountRepository;
		this.accountService = accountService;
	}

	/**
	 * Current account.
	 *
	 * @param principal
	 *            the principal
	 * @return the account
	 */
	@GetMapping("account/current")
	@ResponseStatus(value = HttpStatus.OK)
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	public Account currentAccount(Principal principal) {
		Assert.notNull(principal);
		return accountRepository.findOneByEmail(principal.getName());
	}

	/**
	 * Account.
	 *
	 * @param id
	 *            the id
	 * @return the account
	 */
	@GetMapping("account/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	@Secured("ROLE_ADMIN")
	public Account account(@PathVariable("id") Long id) {
		return accountRepository.findOne(id);
	}

	/**
	 * Follow user.
	 *
	 * @param principal
	 *            the principal
	 * @param id
	 *            the id
	 * @return the int
	 */
	@GetMapping("account/{id}/follow")
	@ResponseStatus(value = HttpStatus.OK)
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	public int followUser(Principal principal, @PathVariable("id") Long id) {
		return accountService.followUnfollowUser(principal.getName(), id);
	}

	/**
	 * Answer request.
	 *
	 * @param principal
	 *            the principal
	 * @param id
	 *            the id
	 * @param accept
	 *            the accept
	 * @return the int
	 */
	@GetMapping("account/{id}/answerrequest/{accept}")
	@ResponseStatus(value = HttpStatus.OK)
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	public int answerRequest(Principal principal, @PathVariable("id") Long id, @PathVariable("accept") boolean accept) {
		return accountService.answerRequest(principal.getName(), id, accept);
	}

	/**
	 * Cambia el perfil de privado a público o viceversa.
	 *
	 * @param principal
	 *            the principal
	 * @param change
	 *            the change
	 */
	@GetMapping("myprofile/{change}")
	@ResponseStatus(value = HttpStatus.OK)
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	public void changePrivacy(Principal principal, @PathVariable("change") boolean change) {
		if (principal != null) {
			Account acc = accountRepository.findOneByEmail(principal.getName());
			accountService.setPrivacy(change, acc.getEmail());
			// devolvemos el estado de acc, que es el contrario al que entra
		}
	}

}
