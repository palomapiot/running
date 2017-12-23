package es.udc.fi.dc.fd.signup;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.activity.ActivityService;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.support.web.Ajax;
import es.udc.fi.dc.fd.support.web.MessageHelper;

@Controller
class SignupController {

	private static final String SIGNUP_VIEW_NAME = "signup/signup";

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ActivityService activityService;

	public SignupController(AccountService accountServiceMock, ActivityService activityServiceMock,
			AccountRepository accountRepositoryMock) {
		this.accountRepository = accountRepositoryMock;
		this.accountService = accountServiceMock;
		this.activityService = activityServiceMock;
	}

	@GetMapping("signup")
	String signup(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		model.addAttribute(new SignupForm());
		if (Ajax.isAjaxRequest(requestedWith)) {
			return SIGNUP_VIEW_NAME.concat(" :: signupForm");
		}
		return SIGNUP_VIEW_NAME;
	}

	@PostMapping("signup")
	String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {

		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
		Account account = signupForm.createAccount();
		accountService.save(account);
		accountService.signin(account);
		// see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/";
	}

	/**
	 * Muestra el perfil del usuario (principal) o el del usuario con id accountId
	 * 
	 * @param principal
	 * @param model
	 * @param accountId
	 *            id de la cuenta de otro usuario
	 * @return
	 */
	@GetMapping("myprofile")
	String profile(Principal principal, Model model,
			@RequestParam(value = "accId", defaultValue = "-1") Long accountId) {

		Account account = null;
		// Si accountId es -1 --> ver nuestro propio perfil
		if (principal != null && accountId.equals(-1L)) {
			account = accountRepository.findOneByEmail(principal.getName());
			model.addAttribute("profile", account);
			model.addAttribute("following", null);
		} else {
			account = accountRepository.findOne(accountId);
			model.addAttribute("profile", account);
			if (principal != null) {
				Account prin = accountRepository.findOneByEmail(principal.getName());
				model.addAttribute("following", accountService.following(prin.getId(), accountId));
			} else {
				model.addAttribute("following", null);
			}
		}

		Page<Activity> activities = activityService.findActivities(account.getId(), 0);
		model.addAttribute("timeline", activities);
		return "profile/account";
	}

	@GetMapping("editProfile")
	public String editProfilePage(Principal principal, Model model) throws Exception {
		try {
			Account acc = accountRepository.findOneByEmail(principal.getName());
			EditProfileForm form = new EditProfileForm();
			form.setName(acc.getName());
			form.setSurname(acc.getSurname());
			form.setBirthday(new SimpleDateFormat("MM-dd-yyyy").format(acc.getBirthday()));
			form.setCity(acc.getCity());
			form.setCountry(acc.getCountry());
			model.addAttribute(form);
			return "profile/editProfilePage";
		} catch (NullPointerException e) {
			throw new Exception("You are not logged in", e);
		}
	}

	@PostMapping("editProfileForm")
	String editProfileForm(Principal principal, @Valid @ModelAttribute EditProfileForm editProfileForm, Errors errors,
			RedirectAttributes ra) throws ParseException {

		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
		Account account = accountRepository.findOneByEmail(principal.getName());
		// Actualizamos los datos
		if (editProfileForm.getPassword() != null)
			account.setPassword(editProfileForm.getPassword());
		account.setName(editProfileForm.getName());
		account.setSurname(editProfileForm.getSurname());
		account.setBirthday(new SimpleDateFormat("MM-dd-yyyy").parse(editProfileForm.getBirthday()));
		account.setCity(editProfileForm.getCity());
		account.setCountry(editProfileForm.getCountry());
		accountService.save(account);
		// see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/myprofile";
	}

}
