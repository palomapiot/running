package es.udc.fi.dc.fd.account;

import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.attendance.AttendanceService;
import es.udc.fi.dc.fd.follow.Follow;
import es.udc.fi.dc.fd.follow.FollowDTO;
import es.udc.fi.dc.fd.follow.FollowRepository;
import es.udc.fi.dc.fd.race.RaceRepository;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	@InjectMocks
	private AccountService accountService = new AccountService();

	@InjectMocks
	private AttendanceService attendanceService = new AttendanceService();

	@Mock
	private RaceRepository raceRepositoryMock;

	@Mock
	private ActivityRepository activityRepositoryMock;

	@Mock
	private AccountRepository accountRepositoryMock;

	@Mock
	private FollowRepository followRepositoryMock;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldThrowExceptionWhenUserNotFound() {
		// arrange
		thrown.expect(UsernameNotFoundException.class);
		thrown.expectMessage("user not found");

		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(null);
		// act
		accountService.loadUserByUsername("user@example.com");
	}

	@Test
	@Transactional
	public void shouldReturnUserDetails() {
		// arrange
		Account demoUser = new Account("user@example.com", "demo", "admin", "admin", "admin", null,
				new GregorianCalendar(1996, Calendar.SEPTEMBER, 30).getTime(), "A Coruña", "España", "I'm the admin",
				true, "ROLE_USER");
		when(accountRepositoryMock.findOneByEmail("user@example.com")).thenReturn(demoUser);

		// act
		UserDetails userDetails = accountService.loadUserByUsername("user@example.com");

		// assert
		assertThat(demoUser.getEmail()).isEqualTo(userDetails.getUsername());
		assertThat(demoUser.getPassword()).isEqualTo(userDetails.getPassword());
		assertThat(hasAuthority(userDetails, demoUser.getRole())).isTrue();
	}

	@Test
	@Transactional
	public void shouldFollowUser() {
		// arrange
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", false,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);
		List<Follow> following = new ArrayList<Follow>();
		when(accountRepositoryMock.findOneByEmail(currentUser.getEmail())).thenReturn(currentUser);
		when(accountRepositoryMock.findOne(followedUser.getId())).thenReturn(followedUser);
		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);
		when(activityRepositoryMock.save(any(Activity.class))).thenReturn(null);
		// act
		int follow = accountService.followUnfollowUser(currentUser.getEmail(), followedUser.getId());

		// assert
		assertThat(follow).isEqualTo(1);

	}

	@Test
	@Transactional
	public void shouldUnFollowUser() {
		// arrange
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", false,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);
		List<Follow> following = new ArrayList<Follow>();
		following.add(new Follow(currentUser, followedUser, true));
		when(accountRepositoryMock.findOneByEmail(currentUser.getEmail())).thenReturn(currentUser);
		when(accountRepositoryMock.findOne(followedUser.getId())).thenReturn(followedUser);
		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);

		// act
		int follow = accountService.followUnfollowUser(currentUser.getEmail(), followedUser.getId());

		// assert
		assertThat(follow).isEqualTo(0);

	}

	@Test
	@Transactional
	public void shouldReturnAllAccounts() {
		// arrange
		int PAGE_NUM_ELEMENTS = 10;
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", false,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);

		List<FollowDTO> followDTOList = new ArrayList<FollowDTO>();
		followDTOList.add(new FollowDTO(currentUser.getId(), followedUser.getEmail(), true, followedUser.getName()));
		followDTOList.add(new FollowDTO(followedUser.getId(), currentUser.getEmail(), false, followedUser.getName()));
		Page<FollowDTO> page = new PageImpl<FollowDTO>(followDTOList);

		when(accountRepositoryMock.findByUsernameLikeIgnoreCaseOrderByUsername("%" + followedUser.getUsername() + "%",
				currentUser.getEmail(), new PageRequest(0, PAGE_NUM_ELEMENTS))).thenReturn(page);

		List<Follow> following = new ArrayList<Follow>();
		following.add(new Follow(currentUser, followedUser, true));

		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);
		// act
		Page<FollowDTO> actual = accountService.findAccountsFollow(followedUser.getUsername(), currentUser.getId(),
				currentUser.getEmail(), 0);

		// assert
		assertThat(actual.getNumberOfElements()).isEqualTo(2);
	}

	@Test
	@Transactional
	public void shouldReturnFollowingAccounts() {
		// arrange
		int PAGE_NUM_ELEMENTS = 10;
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", false,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);

		List<FollowDTO> followDTOList = new ArrayList<FollowDTO>();
		followDTOList.add(new FollowDTO(currentUser.getId(), followedUser.getEmail(), true, followedUser.getName()));
		followDTOList.add(new FollowDTO(followedUser.getId(), currentUser.getEmail(), false, followedUser.getName()));
		Page<FollowDTO> page = new PageImpl<FollowDTO>(followDTOList);
		when(accountRepositoryMock.findByUsernameLikeIgnoreCaseOrderByUsername("%" + followedUser.getUsername() + "%",
				currentUser.getEmail(), new PageRequest(0, PAGE_NUM_ELEMENTS))).thenReturn(page);
		List<Follow> following = new ArrayList<Follow>();
		following.add(new Follow(currentUser, followedUser, true));
		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);
		// act
		List<Follow> followList = new ArrayList<Follow>();
		followList.add(new Follow(currentUser, followedUser, true));
		Page<Follow> devolver = new PageImpl<Follow>(followList);
		when(followRepositoryMock.findByFollowerId(currentUser.getId(), new PageRequest(0, PAGE_NUM_ELEMENTS)))
				.thenReturn(devolver);
		Page<Follow> actual = accountService.findAccountsFollowing(currentUser.getId(), 0);

		// assert
		assertThat(actual.getNumberOfElements()).isEqualTo(1);
	}

	@Test
	@Transactional
	public void shouldReturnIfFollowing() {
		// arrange
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", false,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);

		List<Follow> following = new ArrayList<Follow>();
		following.add(new Follow(currentUser, followedUser, true));
		when(accountRepositoryMock.findOneByEmail(currentUser.getEmail())).thenReturn(currentUser);
		when(accountRepositoryMock.findOne(followedUser.getId())).thenReturn(followedUser);
		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);

		// act
		FollowState follow = accountService.following(currentUser.getId(), followedUser.getId());

		// assert
		assertThat(follow).isEqualTo(FollowState.FOLLOWING);
	}

	@Test
	@Transactional
	public void shouldReturnPendingAccounts() {
		// arrange
		int PAGE_NUM_ELEMENTS = 10;
		Account currentUser = new Account("diego@festis.es", "demo", "diegosl", "Diego", "Sánchez", null,
				new GregorianCalendar(1992, Calendar.AUGUST, 17).getTime(), "Lugo", "España", "festis", true,
				"ROLE_USER");
		Account followedUser = new Account("mig@festis.es", "demo", "migguis", "Miguel", "Guisantes", null,
				new GregorianCalendar(1991, Calendar.SEPTEMBER, 9).getTime(), "A Guarda", "España", "miggg", true,
				"ROLE_USER");
		currentUser.setId(11L);
		followedUser.setId(12L);

		List<FollowDTO> followDTOList = new ArrayList<FollowDTO>();
		followDTOList.add(new FollowDTO(currentUser.getId(), followedUser.getEmail(), true, followedUser.getName()));
		followDTOList.add(new FollowDTO(followedUser.getId(), currentUser.getEmail(), false, followedUser.getName()));
		List<Follow> following = new ArrayList<Follow>();
		following.add(new Follow(currentUser, followedUser, false)); // false porque está pendiente
		when(followRepositoryMock.findByFollowerIdAndFollowedId(currentUser.getId(), followedUser.getId()))
				.thenReturn(following);
		// act
		List<Follow> followList = new ArrayList<Follow>();
		followList.add(new Follow(currentUser, followedUser, false)); // false porque está pendiente
		Page<Follow> devolver = new PageImpl<Follow>(followList);
		when(followRepositoryMock.findByFollowedId(currentUser.getId(), new PageRequest(0, PAGE_NUM_ELEMENTS)))
				.thenReturn(devolver);
		Page<Follow> actual = accountService.findAccountsPending(currentUser.getId(), 0);

		// assert
		assertThat(actual.getNumberOfElements()).isEqualTo(1);
	}

	private boolean hasAuthority(UserDetails userDetails, String role) {
		return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(isEqual(role));
	}

}
