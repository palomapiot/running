package es.udc.fi.dc.fd.activity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;

public class ActivityControllerTest extends WebSecurityConfigurationAware {

	private ActivityService activityServiceMock;
	private ActivityController activityController;
	private AccountRepository accountRepositoryMock;

	@Before
	public void setUp() {
		activityServiceMock = mock(ActivityService.class);
		accountRepositoryMock = mock(AccountRepository.class);
		activityController = new ActivityController(activityServiceMock, accountRepositoryMock);
		this.mockMvc = MockMvcBuilders.standaloneSetup(activityController).build();
	}

	@Test
	public void timelineTest() throws Exception {

		List<Activity> list = new ArrayList<Activity>();
		Page<Activity> timeline = new PageImpl<Activity>(list, new PageRequest(0, 10), 0);
		when(activityServiceMock.findActivities(2L, 0)).thenReturn(timeline);
		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");
		mockMvc.perform(get("/timeline").param("accId", "2").param("page", "0").principal(mockPrincipal))
				.andExpect(view().name("profile/account :: timelineZone"))
				.andExpect(model().attribute("timeline", timeline));
	}

	@Test
	public void feedTest() throws Exception {
		List<Activity> list = new ArrayList<Activity>();
		Page<Activity> timeline = new PageImpl<Activity>(list, new PageRequest(0, 10), 0);
		when(activityServiceMock.findActivityFeed(2L, 0)).thenReturn(timeline);
		Principal mockPrincipal = mock(Principal.class);
		when(mockPrincipal.getName()).thenReturn("user@example.com");
		Account acc = new Account();
		acc.setId(2L);
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(acc);
		mockMvc.perform(get("/feed").param("accId", "2").param("page", "0").principal(mockPrincipal))
				.andExpect(view().name("profile/feed"))
				.andExpect(model().attribute("timeline", timeline));
	}
}
