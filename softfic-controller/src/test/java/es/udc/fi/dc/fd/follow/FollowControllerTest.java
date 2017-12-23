package es.udc.fi.dc.fd.follow;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.account.AccountService;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;

public class FollowControllerTest extends WebSecurityConfigurationAware {

	private FollowController followController;
	private AccountRepository accountRepositoryMock;
	private AccountService accountServiceMock;

	@Before
	public void setUp() {
		accountServiceMock = org.mockito.Mockito.mock(AccountService.class);
		accountRepositoryMock = org.mockito.Mockito.mock(AccountRepository.class);
		followController = new FollowController(accountRepositoryMock, accountServiceMock);
		this.mockMvc = MockMvcBuilders.standaloneSetup(followController).build();
	}

	@Test
	@Transactional
	public void shouldReturnAccounts() throws Exception {
		Account acc = new Account("user@example.com", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);

		User user = new User("user@example.com", "demo", AuthorityUtils.createAuthorityList("ROLE_USER"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

		FollowDTO fDto1 = new FollowDTO(1L, "follower1@gmail.com", true, "follower1");
		FollowDTO fDto2 = new FollowDTO(2L, "follower2@gmail.com", false, "follower2");
		FollowDTO fDto3 = new FollowDTO(3L, "follower3@gmail.com", true, "follower3");

		List<FollowDTO> folList = new ArrayList<FollowDTO>();
		folList.add(fDto1);
		folList.add(fDto2);
		folList.add(fDto3);
		Page<FollowDTO> followPage = new PageImpl<FollowDTO>(folList);
		when(accountServiceMock.findAccountsFollow("", acc.getId(), acc.getEmail(), 0)).thenReturn(followPage);

		mockMvc.perform(get("/accounts").param("email", "").param("page", "0").principal(testingAuthenticationToken))
				.andExpect(view().name("follow/accounts"));

	}

	@Test
	@Transactional
	public void shouldReturnFOllowRequests() throws Exception {
		Account acc = new Account("user@example.com", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		acc.setId(1L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);

		Account f1 = new Account("f2@example.com", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		f1.setId(1L);
		Account f2 = new Account("f2@example.com", "demo", "diego1", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		f2.setId(2L);

		User user = new User("user@example.com", "demo", AuthorityUtils.createAuthorityList("ROLE_USER"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

		Follow foll1 = new Follow(f1, acc, false);
		Follow foll2 = new Follow(f2, acc, false);

		List<Follow> folList = new ArrayList<Follow>();
		folList.add(foll1);
		folList.add(foll2);
		Page<Follow> pendingPage = new PageImpl<Follow>(folList);
		when(accountServiceMock.findAccountsPending(acc.getId(), 0)).thenReturn(pendingPage);

		mockMvc.perform(get("/requests").param("page", "0").principal(testingAuthenticationToken))
				.andExpect(view().name("profile/requests"));

	}

}
