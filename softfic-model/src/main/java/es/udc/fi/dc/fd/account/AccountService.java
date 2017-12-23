package es.udc.fi.dc.fd.account;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.FollowActivity;
import es.udc.fi.dc.fd.follow.Follow;
import es.udc.fi.dc.fd.follow.FollowDTO;
import es.udc.fi.dc.fd.follow.FollowRepository;
import es.udc.fi.dc.fd.race.Race;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

	/** The Constant PAGE_NUM_ELEMENTS. */
	private static final int PAGE_NUM_ELEMENTS = 10;

	/** The account repository. */
	@Autowired
	private AccountRepository accountRepository;

	/** The password encoder. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/** The follow repository. */
	@Autowired
	private FollowRepository followRepository;

	/** The act repository. */
	@Autowired
	private ActivityRepository actRepository;

	/**
	 * Save.
	 *
	 * @param account
	 *            the account
	 */
	@Transactional
	public void save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findOneByEmail(username);
		if (account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	/**
	 * Signin.
	 *
	 * @param account
	 *            the account
	 */
	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	/**
	 * Authenticate.
	 *
	 * @param account
	 *            the account
	 * @return the authentication
	 */
	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null,
				Collections.singleton(createAuthority(account)));
	}

	/**
	 * Creates the user.
	 *
	 * @param account
	 *            the account
	 * @return the user
	 */
	private User createUser(Account account) {
		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority(account)));
	}

	/**
	 * Creates the authority.
	 *
	 * @param account
	 *            the account
	 * @return the granted authority
	 */
	private GrantedAuthority createAuthority(Account account) {
		return new SimpleGrantedAuthority(account.getRole());
	}

	/**
	 * Devuelve de forma paginada todos los usurios que asisten a una carrera.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param page
	 *            número de la página
	 * @return {@link Page} de los objetos {@link Account} que asisten a la
	 *         {@link Race} con el id indicado.
	 */
	@Transactional(readOnly = true)
	public Page<Account> findAccountsByRace(Long raceId, int page) {
		return accountRepository.findAccountsByRace(raceId, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Si no seguíamos al usuario, lo seguimos y se guarda en la base de datos, si
	 * lo dejamos de seguir, borramos la entrada de la bd.
	 *
	 * @param emailFollower
	 *            the email follower
	 * @param id
	 *            the id
	 * @return 0 si lo dejamos de seguir, 1 si lo empezamos a seguir, 2 si queda
	 *         pendiente
	 */
	@Transactional
	public int followUnfollowUser(String emailFollower, Long id) {
		Account follower = accountRepository.findOneByEmail(emailFollower);
		Account followed = accountRepository.findOne(id);
		// Buscamos si ya seguimos al usuario.
		List<Follow> following = followRepository.findByFollowerIdAndFollowedId(follower.getId(), followed.getId());
		if (following.isEmpty()) {
			// Empezar a seguir
			Follow follow = new Follow(follower, followed, followed.getPrivateAccount() ? false : true);
			followRepository.save(follow);

			if (followed.getPrivateAccount()) {
				return 2;
			}
			// Registramos el follow para la timeline
			Activity activity = new FollowActivity(follower, followed);
			actRepository.save(activity);

			return 1;

		} else {
			// Dejar de seguir
			followRepository.delete(following.get(0));
			return 0;
		}

	}

	/**
	 * Responde a una petición de seguimiento.
	 *
	 * @param emailPrincipal
	 *            the email principal
	 * @param id
	 *            the id
	 * @param accept
	 *            the accept
	 * @return 0 si no existe tal solicitud o se declina, 1 si se aprueba
	 */
	@Transactional
	public int answerRequest(String emailPrincipal, Long id, boolean accept) {
		Account principal = accountRepository.findOneByEmail(emailPrincipal);
		Account requester = accountRepository.findOne(id);
		// Buscamos la solicitud
		List<Follow> following = followRepository.findByFollowerIdAndFollowedId(requester.getId(), principal.getId());
		if (following.isEmpty()) {
			return 0;
		} else {
			if (accept) {
				Follow follow = following.get(0);
				follow.setAccepted(true);
				followRepository.save(follow);

				// Registramos el follow para la timeline
				Activity activity = new FollowActivity(requester, principal);
				actRepository.save(activity);

				return 1;
			} else {

				followRepository.delete(following.get(0));
				return 0;
			}
		}

	}

	/**
	 * Devuelve una lista de usuarios que coincidan con el patron introducido
	 * (parámetro email), junto a un flag que indica si el usuario
	 * (currentUserEmail) está actualmente siguiendo a los usuarios o no.
	 * 
	 * @param username
	 *            Patrón de búsqueda sobre el nick de usuario
	 * @param id
	 *            Id del usuario actual
	 * @param currentUserEmail
	 *            Email del usuario actual
	 * @param page
	 *            número de página
	 * @return Lista paginada de {@link FollowDTO} de usuarios que coinciden con el
	 *         patrón junto a si el usuario actual los sigue o no.
	 */
	@Transactional(readOnly = true)
	public Page<FollowDTO> findAccountsFollow(String username, Long id, String currentUserEmail, int page) {
		// Búsqueda de los usuarios que coinciden con el patrón
		Page<FollowDTO> pageFollowDTO = accountRepository.findByUsernameLikeIgnoreCaseOrderByUsername(
				"%" + username + "%", currentUserEmail, new PageRequest(page, PAGE_NUM_ELEMENTS));
		Iterator<FollowDTO> itr = pageFollowDTO.iterator();
		// Recorremos los resultados estableciendo si el usuario actual los sigue o no
		while (itr.hasNext()) {
			FollowDTO element = itr.next();
			List<Follow> follow = followRepository.findByFollowerIdAndFollowedId(id, element.getId());
			if (!follow.isEmpty()) {
				// Si lo sigue ponemos "following" a true, si no a null (SOLICITUD PENDIENTE)
				element.setFollowing(follow.get(0).isAccepted() ? Boolean.TRUE : null);
			} else {
				// Si no está, ni lo sigue ni ha hecho petición de seguimiento
				element.setFollowing(Boolean.FALSE);
			}
		}
		return pageFollowDTO;
	}

	/**
	 * Devuelve una lista paginada de cuentas de usuario a los que el usuario
	 * identificado por el id pasado por parámetro sigue.
	 *
	 * @param id
	 *            id del usuario actual
	 * @param page
	 *            número de página
	 * @return Lista paginada de objetos {@link Follow}
	 */
	@Transactional(readOnly = true)
	public Page<Follow> findAccountsFollowing(Long id, int page) {
		return followRepository.findByFollowerId(id, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Devuelve una lista paginada de cuentas de usuario a los que el usuario
	 * identificado por el id pasado por parámetro tiene pendiente de aceptar el
	 * seguimiento.
	 *
	 * @param id
	 *            id del usuario actual
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Transactional(readOnly = true)
	public Page<Follow> findAccountsPending(Long id, int page) {
		return followRepository.findByFollowedId(id, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Devuelve si el usuario autenticado está siguiendo o no al usuario (accId).
	 *
	 * @param principalId
	 *            the principal id
	 * @param accId
	 *            id del usuario del cual queremos comprobar si seguimos o no
	 * @return the boolean
	 */
	public FollowState following(Long principalId, Long accId) {
		List<Follow> follow = followRepository.findByFollowerIdAndFollowedId(principalId, accId);
		if (!follow.isEmpty()) {
			return (follow.get(0).isAccepted() ? FollowState.FOLLOWING : FollowState.PENDING);
		} else {
			// Si no está, ni lo sigue ni ha hecho petición de seguimiento
			return FollowState.NOT_FOLLOWING;
		}

	}

	/**
	 * Devuelve para una carrera y el usuario actual, una lista paginada de
	 * {@link FollowDTO} que representa la lista de usuarios que asisten a la
	 * carrera junto a si el usuario actual lo sigue o no.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param id
	 *            id de el usuario actual
	 * @param page
	 *            número de página
	 * @return Lista paginada de {@link FollowDTO}
	 */
	@Transactional(readOnly = true)
	public Page<FollowDTO> findRaceAttendantsFollow(Long raceId, Long id, int page) {
		// Recuperamos los usuarios que asisten a la carrera
		Page<Account> accounts = accountRepository.findAccountsByRace(raceId, new PageRequest(page, PAGE_NUM_ELEMENTS));

		// Convertmos la página de Accounts a páginas de FollowDTO
		return accounts.map(new Converter<Account, FollowDTO>() {
			@Override
			public FollowDTO convert(Account entity) {
				FollowDTO dto = new FollowDTO();
				dto.setEmail(entity.getEmail());
				dto.setId(entity.getId());
				dto.setUsername(entity.getUsername());
				List<Follow> follow = followRepository.findByFollowerIdAndFollowedId(id, entity.getId());
				if (!follow.isEmpty()) {
					// Si lo sigue ponemos "following" a true, si no a null (SOLICITUD PENDIENTE)
					dto.setFollowing(follow.get(0).isAccepted() ? Boolean.TRUE : null);
				} else {
					// Si no está, ni lo sigue ni ha hecho petición de seguimiento
					dto.setFollowing(Boolean.FALSE);
				}

				return dto;
			}
		});
	}

	/**
	 * Busca los usuarios inscritos a una carrera interna y si el usuario loggeado
	 * los sigue o no.
	 *
	 * @param raceId
	 *            the race id
	 * @param id
	 *            the id
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Transactional(readOnly = true)
	public Page<FollowDTO> findRaceEnrollmentsFollow(Long raceId, Long id, int page) {

		Page<Account> accounts = accountRepository.findEnrollAccountsByRace(raceId,
				new PageRequest(page, PAGE_NUM_ELEMENTS));

		return accounts.map(new Converter<Account, FollowDTO>() {
			@Override
			public FollowDTO convert(Account entity) {
				FollowDTO dto = new FollowDTO();
				dto.setEmail(entity.getEmail());
				dto.setId(entity.getId());
				dto.setUsername(entity.getUsername());
				List<Follow> follow = followRepository.findByFollowerIdAndFollowedId(id, entity.getId());
				if (!follow.isEmpty()) {
					// Si lo sigue ponemos "following" a true, si no a null (SOLICITUD PENDIENTE)
					dto.setFollowing(follow.get(0).isAccepted() ? Boolean.TRUE : null);
				} else {
					// Si no está, ni lo sigue ni ha hecho petición de seguimiento
					dto.setFollowing(Boolean.FALSE);
				}

				return dto;
			}
		});
	}

	/**
	 * Busca a los usuarios inscritos a una carrera y que siga el usuario.
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @param accId
	 *            identificador de la carrera
	 * @param page
	 *            pagina
	 * @return lista paginada de los usuarios
	 */
	@Transactional
	public Page<Account> findRaceFollowingAttendances(Long raceId, Long accId, int page) {
		return accountRepository.findRaceFollowingAttendance(raceId, accId, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Numero de usuarios inscritos a una carrera y que siga el usuario.
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @param accId
	 *            identificador de la carrera
	 * @return lista paginada de los usuarios
	 */
	@Transactional
	public int numberOfFollowingAttendances(Long raceId, Long accId) {
		return accountRepository.findNumberOfRaceFollowingAttendance(raceId, accId);
	}

	/**
	 * Busca a los usuarios inscritos a una carrera y que siga el usuario.
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @param accId
	 *            identificador de la carrera
	 * @param page
	 *            pagina
	 * @return lista paginada de los usuarios
	 */
	@Transactional
	public Page<Account> findRaceFollowingEnrollments(Long raceId, Long accId, int page) {
		return accountRepository.findRaceFollowingEnrollments(raceId, accId, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Numero de usuarios inscritos a una carrera y que siga el usuario.
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @param accId
	 *            identificador de la carrera
	 * @return lista paginada de los usuarios
	 */
	@Transactional
	public int numberOfFollowingEnrollments(Long raceId, Long accId) {
		return accountRepository.findNumberOfRaceFollowingEnrollments(raceId, accId);
	}

	/**
	 * Cambia el perfil de privado a público o viceversa.
	 *
	 * @param change
	 *            the change
	 * @param email
	 *            the email
	 */
	@Transactional
	public void setPrivacy(Boolean change, String email) {
		accountRepository.setPrivacy(change, email);
	}
}
