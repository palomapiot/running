package es.udc.fi.dc.fd.signup;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.activity.ActivityService;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;

public class SignupControllerTest extends WebSecurityConfigurationAware {

	private AccountService accountServiceMock;

	private AccountRepository accountRepositoryMock;

	private ActivityService activityServiceMock;

	private SignupController signupControllerMock;

	@Before
	public void setUp() {
		accountServiceMock = mock(AccountService.class);
		accountRepositoryMock = mock(AccountRepository.class);
		activityServiceMock = mock(ActivityService.class);
		signupControllerMock = new SignupController(accountServiceMock, activityServiceMock, accountRepositoryMock);
	}

	@Test
	public void displaysSignupForm() throws Exception {
		mockMvc.perform(get("/signup")).andExpect(model().attributeExists("signupForm"))
				.andExpect(view().name("signup/signup"))
				.andExpect(content().string(allOf(containsString("<title>Signup</title>"),
						containsString("<legend>Please Sign Up</legend>"))));
	}

	@Test
	public void profile() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(signupControllerMock).build();
		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");

		Account account = new Account("user@example.com", "demo", "admin", "admin", "admin", null,
				new GregorianCalendar(1996, Calendar.SEPTEMBER, 30).getTime(), "A Coruña", "España", "I'm the admin",
				true, "ROLE_USER");
		account.setId(2L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(account);
		when(accountRepositoryMock.findOne(2L)).thenReturn(account);
		List<Activity> list = new ArrayList<Activity>();
		Page<Activity> timeline = new PageImpl<Activity>(list, new PageRequest(0, 10), 0);
		when(activityServiceMock.findActivities(2L, 0))
				.thenReturn(timeline);
		mockMvc.perform(get("/myprofile").param("accId", "2").principal(mockPrincipal))
				.andExpect(view().name("profile/account")).andExpect(model().attribute("profile", account))
				.andExpect(model().attribute("timeline", timeline));

	}
}