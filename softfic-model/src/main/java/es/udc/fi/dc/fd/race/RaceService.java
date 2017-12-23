package es.udc.fi.dc.fd.race;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.HostActivity;
import es.udc.fi.dc.fd.attendance.Attendance;
import es.udc.fi.dc.fd.attendance.AttendanceRepository;
import es.udc.fi.dc.fd.enrollment.Enrollment;
import es.udc.fi.dc.fd.enrollment.EnrollmentRepository;
import es.udc.fi.dc.fd.place.Place;
import es.udc.fi.dc.fd.place.PlaceRepository;
import es.udc.fi.dc.fd.race.exceptions.CantConvertToInternalException;
import es.udc.fi.dc.fd.tariff.Tariff;
import es.udc.fi.dc.fd.tariff.TariffRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RaceService {

	@Autowired
	private RaceRepository raceRepository;

	@Autowired
	private TariffRepository tariffRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private AttendanceRepository attendanceRepository;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ActivityRepository actRepository;

	private static final int PAGE_NUM_ELEMENTS = 10;

	private static final String GOOGLE_MAPS_API = "AIzaSyBPD4z1YS-H_Xm2zaHWYTTsE8pDM5rNp6U";

	/**
	 * Crea una nueva {@link Race} inicializando el place.
	 *
	 * @param race
	 *            {@link Race} sin inicializar
	 * @return {@link Race} con place e id inicializados
	 */
	@Transactional
	public Race save(Race race) {
		Place location;
		Place existingPlace = placeRepository.findByPlaceNameLikeIgnoreCase(race.getPlace().getName());
		if (race.getIsInternal()) {
			Activity activity = new HostActivity(race.getCreatedBy(), race);
			actRepository.save(activity);
		}
		if (existingPlace != null) {
			race.setPlace(existingPlace);
			return raceRepository.save(race);
		} else {
			try {
				location = retrieveLocation(race.getPlace().getName());
				race.setPlace(location);
				placeRepository.save(race.getPlace());
				return raceRepository.save(race);
			} catch (Exception e) {
				return raceRepository.save(race);
			}
		}

	}

	/**
	 * Recupera una lista de {@link Race} que cumplan los requisitos indicados por
	 * parámetro, ordenadas por el criterio de ordenación indicado.
	 *
	 * @param place
	 *            {@link String} que representa el nombre de la entidad
	 *            {@link Place}
	 * @param minDate
	 *            {@link Date} mínima del rango de fechas en el que buscamos
	 * @param maxDate
	 *            {@link Date} máximo del rango de fechas en el que buscamos
	 * @param distance
	 *            ({@link Double}) radio del área (alrededor del {Place indicado) en
	 *            la que buscamos
	 * @param medicalTest
	 *            ({@link int}) 1 si buscamos carreras con prueba médica, 0 en caso
	 *            contrario
	 * @param minPrice
	 *            ({@link BigDecimal}) indica el precio mínimo del rango de precios
	 *            en el que buscamos
	 * @param maxPrice
	 *            ({@link BigDecimal}) indica el precio máximo del rango de precios
	 *            en el que buscamos
	 * @param minDistance
	 *            ({@link Double}) indica la longitud mínima del recorrido de la
	 *            carrera en el rango de longitudes en el que buscamos
	 * @param maxDistance
	 *            ({@link Double}) indica la longitud máxima del recorrido de la
	 *            carrera en el rango de longitudes en el que buscamos
	 * @param orderType
	 *            (@link int) que indica el criterio de ordenación: - 0 : por precio
	 *            - 1 : por place - 2 : por nombre - 3 : por fecha - 4 : por
	 *            distancia
	 * @param page
	 *            ({@link int}) indica el número de página pedido
	 * @return {@link Page} con las entidades {@link Race} correspondientes a la
	 *         página solicitada
	 */
	@Transactional(readOnly = true)
	public Page<Race> find(String place, Date minDate, Date maxDate, Double distance, int medicalTest,
			BigDecimal minPrice, BigDecimal maxPrice, Double minDistance, Double maxDistance, int orderType, int page) {
		Page<Race> resultPage = null;
		// Criteria para obtención de los datos filtrados
		Criteria criteria = createCriteriaSession();
		// Para la paginación
		Criteria countCriteria = createCriteriaSession();

		// Si no se especifica un radio entonces se filtra por nombre del lugar.

		if (distance == null) {
			criteria.add(Restrictions.sqlRestriction("placeName like '%" + place + "%'"));
			countCriteria.add(Restrictions.sqlRestriction("placeName like '%" + place + "%'"));
		}

		// Restricciones de rango de fecha
		if (minDate != null) {
			criteria.add(Restrictions.ge("race.date", minDate));
			countCriteria.add(Restrictions.ge("race.date", minDate));
		}
		if (maxDate != null) {
			criteria.add(Restrictions.le("race.date", maxDate));
			countCriteria.add(Restrictions.le("race.date", maxDate));
		}

		// Restricción de búsqueda por radio
		if (distance != null) {
			Place requestedLocation;
			// Llamada a la api de google
			try {
				requestedLocation = retrieveLocation(place);
			} catch (Exception e) {
				// Si la petición provoca un error, devuelve una página vacía.
				return new PageImpl<Race>(new ArrayList<Race>(), new PageRequest(page, PAGE_NUM_ELEMENTS), 0);

			}
			// Coordenadas de el Place
			Float lat = requestedLocation.getLat();
			Float lng = requestedLocation.getLng();

			// Consulta SQL cuyo resultado son las carreras que entran dentro del radio
			// especificado. La fórmula es la distancia euclídea
			criteria.add(Restrictions.sqlRestriction("(6371 * 2 * ASIN(SQRT(POWER(SIN((lat - abs(" + lat
					+ ")) * pi()/180 / 2)," + " 2) + COS(lat * pi()/180 ) * COS(abs(" + lat + ") *"
					+ " pi()/180) * POWER(SIN((lng - " + lng + ") *" + "pi()/180 / 2), 2) )))" + " <= " + distance));
			countCriteria.add(Restrictions.sqlRestriction("(6371 * 2 * ASIN(SQRT(POWER(SIN((lat - abs(" + lat
					+ ")) * pi()/180 / 2)," + " 2) + COS(lat * pi()/180 ) * COS(abs(" + lat + ") *"
					+ " pi()/180) * POWER(SIN((lng - " + lng + ") *" + "pi()/180 / 2), 2) )))" + " <= " + distance));
		}

		// Restricción de prueba médica
		if (medicalTest != 0) {
			boolean test = medicalTest == 2;
			criteria.add(Restrictions.eq("race.medicalTest", test));
			countCriteria.add(Restrictions.eq("race.medicalTest", test));
		}

		// Restricción de rango de precios
		if (minPrice != null) {
			criteria.add(Restrictions.disjunction().add(Restrictions.ge("price.price", minPrice)));
			countCriteria.add(Restrictions.disjunction().add(Restrictions.ge("price.price", minPrice)));
		}
		if (maxPrice != null) {

			criteria.add(Restrictions.disjunction().add(Restrictions.le("price.price", maxPrice)));
			countCriteria.add(Restrictions.disjunction().add(Restrictions.le("price.price", maxPrice)));
		}

		// Restricción de rango de la longitud del recorrido
		if (minDistance != null) {
			criteria.add(Restrictions.ge("race.distance", minDistance));
			countCriteria.add(Restrictions.ge("race.distance", minDistance));
		}
		if (maxDistance != null) {
			criteria.add(Restrictions.le("race.distance", maxDistance));
			countCriteria.add(Restrictions.le("race.distance", maxDistance));
		}

		switch (orderType) {

		case 0:
			criteria.addOrder(Order.asc("price.price"));
			break;
		case 1:
			criteria.addOrder(Order.asc("place.placeName"));
			break;
		case 2:
			criteria.addOrder(Order.asc("race.name"));
			break;
		case 3:
			criteria.addOrder(Order.asc("race.date"));
			break;
		case 4:
			criteria.addOrder(Order.asc("race.distance"));
			break;
		case 5:
			criteria.addOrder(Order.desc("price.price"));
			break;
		case 6:
			criteria.addOrder(Order.desc("place.placeName"));
			break;
		case 7:
			criteria.addOrder(Order.desc("race.name"));
			break;
		case 8:
			criteria.addOrder(Order.desc("race.date"));
			break;
		case 9:
			criteria.addOrder(Order.desc("race.distance"));
			break;
		default:

		}
		criteria.setFirstResult(page * PAGE_NUM_ELEMENTS).setMaxResults(PAGE_NUM_ELEMENTS);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		@SuppressWarnings("unchecked")
		List<Race> list = Collections.checkedList(criteria.list(), Race.class);

		countCriteria.setProjection(Projections.rowCount());
		Long totalCount = (Long) countCriteria.uniqueResult();

		return new PageImpl<Race>(list, new PageRequest(page, PAGE_NUM_ELEMENTS), totalCount);

	}

	/**
	 * Crea una nueva {@link Criteria} para el objeto {@link Race} cuyo atributo
	 * {@link Place} está inicializado.
	 *
	 * @return el objeto {@link Criteria} creado
	 */
	protected Criteria createCriteriaSession() {
		return em.unwrap(Session.class).createCriteria(Race.class, "race")
				.createAlias("prices", "price", JoinType.LEFT_OUTER_JOIN)
				.createAlias("race.place", "place", JoinType.LEFT_OUTER_JOIN);
	}

	/**
	 * Recupera el objeto {@link Race} con el id indicado por parámetro.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @return el objeto {@link Race} encontrado
	 */
	@Transactional(readOnly = true)
	public Race findById(Long raceId) {
		return raceRepository.findOne(raceId);
	}

	/**
	 * Usa la API de Google para encontrar la latitud y la longitud de un place que
	 * se le pasa por parámetro.
	 *
	 * @param place
	 *            El lugar del que se quieren obtener las coordenadas.
	 * @return Un array de {@link String} que contiene: en el índice 0 la latitud y
	 *         en el índice 1 la longitud. Un lugar con su latitud y longitud
	 * @throws Exception
	 *             Si el api falla
	 */
	private Place retrieveLocation(String place) throws Exception {
		int responseCode = 0;
		try {
			// Necesario para el correcto funcionamiento de la API
			String encoderplace = URLEncoder.encode(place, StandardCharsets.UTF_8.name());

			// Creando la petición HTTP al api
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/xml?address=" + encoderplace + "&key="
					+ GOOGLE_MAPS_API);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.connect();
			responseCode = httpConnection.getResponseCode();
			if (responseCode == 200) {
				// Contruímos un documento xml para recibir los datos del api de goolge
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = builder.parse(httpConnection.getInputStream());
				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression expr = xpath.compile("/GeocodeResponse/status");
				String status = (String) expr.evaluate(document, XPathConstants.STRING);
				if ("OK".equals(status)) {
					// Si el estado es OK
					// Solo cogemos la longitud y la latitud
					expr = xpath.compile("//geometry/location/lat");
					Float latitude = Float.parseFloat((String) expr.evaluate(document, XPathConstants.STRING));
					expr = xpath.compile("//geometry/location/lng");
					Float longitude = Float.parseFloat((String) expr.evaluate(document, XPathConstants.STRING));
					return new Place(place, latitude, longitude);
				} else {
					// Si el estado no es OK
					throw new Exception("Error from the API - response status: " + status);
				}
			}
		} catch (MalformedURLException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		} catch (XPathExpressionException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		} catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		} catch (ParserConfigurationException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		} catch (SAXException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		}
		return null;
	}

	/**
	 * Recupera de forma paginada la lista de {@link Race} ordenadas a las que
	 * asiste el usuario identificado por el email pasado por parámetro.
	 *
	 * @param email
	 *            email que identifica al usuario
	 * @param page
	 *            número de página que queremos recuperar
	 * @return {@link Page} con las entidades {@link Race} correspondientes a la
	 *         página solicitada
	 */
	@Transactional(readOnly = true)
	public Page<Race> findRacesByAccount(String email, int page) {
		return raceRepository.findRacesByAccount(email, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Recupera tanto las carreras de attendance como las de enrollment.
	 *
	 * @param email
	 *            email que identifica al usuario
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Transactional(readOnly = true)
	public Page<Race> findGoingRacesByAccount(String email, int page) {
		return raceRepository.findGoingRacesByAccount(email, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Recupera de forma paginada la lista de {@link Race} ordenadas a las que
	 * asiste el usuario identificado por el email pasado por parámetro.
	 *
	 * @param email
	 *            email que identifica al usuario
	 * @param page
	 *            número de página que queremos recuperar
	 * @return {@link Page} con las entidades {@link Race} correspondientes a la
	 *         página solicitada
	 */
	@Transactional(readOnly = true)
	public Page<UserRaceDTO> findUserRacesByAccount(String email, int page) {
		// Recuperamos los attendances y los enrollments
		List<Attendance> attendanceList = attendanceRepository.findByAccountEmail(email);
		List<Enrollment> enrollmentList = enrollmentRepository.findByAccountEmail(email);

		// Construimos los DTO
		List<UserRaceDTO> userRaces = new ArrayList<>((attendanceList.size()) + (enrollmentList.size()));
		int i;
		for (Attendance attendance : attendanceList) {
			UserRaceDTO userRace = new UserRaceDTO();
			userRace.setRaceId(attendance.getRace().getId());
			userRace.setRaceName(attendance.getRace().getName());
			userRace.setRacePlaceName(attendance.getRace().getPlace().getName());
			userRace.setRaceDate(attendance.getRace().getDate());
			userRace.setRaceIsInternal(Boolean.FALSE);
			userRace.setPaid(Boolean.FALSE);
			userRace.setChip(Boolean.FALSE);
			userRace.setUserPosition(0);
			userRaces.add(userRace);
		}
		for (Enrollment enrollment : enrollmentList) {
			UserRaceDTO userRace = new UserRaceDTO();
			userRace.setRaceId(enrollment.getRace().getId());
			userRace.setRaceName(enrollment.getRace().getName());
			userRace.setRaceDate(enrollment.getRace().getDate());
			userRace.setRacePlaceName(enrollment.getRace().getPlace().getName());
			userRace.setRaceIsInternal(Boolean.TRUE);
			userRace.setPaid(enrollment.isPaid());
			userRace.setChip(enrollment.getChip());
			if (enrollment.getRanking() != null)
				userRace.setUserPosition(enrollment.getRanking().getPosition());
			else
				userRace.setUserPosition(0);
			userRaces.add(userRace);
		}

		// Ordenamos la lista por fecha
		Collections.sort(userRaces, new Comparator<UserRaceDTO>() {
			@Override
			public int compare(UserRaceDTO object1, UserRaceDTO object2) {
				return object1.getRaceDate().compareTo(object2.getRaceDate());
			}
		}.reversed());

		int start = page * PAGE_NUM_ELEMENTS;
		int end = (start + PAGE_NUM_ELEMENTS > userRaces.size()) ? userRaces.size() : start + PAGE_NUM_ELEMENTS;

		List<UserRaceDTO> userRacesSublist = userRaces.subList(start, end);

		return new PageImpl<>(userRacesSublist, new PageRequest(page, PAGE_NUM_ELEMENTS), userRaces.size());
	}

	/**
	 * Find enroll races by account.
	 *
	 * @param email
	 *            the email
	 * @param page
	 *            the page
	 * @return the page
	 */
	@Transactional(readOnly = true)
	public Page<Race> findEnrollRacesByAccount(String email, int page) {
		return raceRepository.findEnrollRacesByAccount(email, new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Adds the tariff.
	 *
	 * @param tariff
	 *            the tariff
	 * @return the tariff
	 */
	@Transactional
	public Tariff addTariff(Tariff tariff) {
		return tariffRepository.save(tariff);
	}

	/**
	 * Establece al usuario como organizador de la carrera. Si la carrera ya era
	 * interna lanza {@link CantConvertToInternalException}
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param userId
	 *            id que identifica al usuario organizador
	 * @param prices
	 *            los precios para la carrera
	 * @param minAges
	 *            the min ages
	 * @param maxAges
	 *            the max ages
	 * @throws CantConvertToInternalException
	 *             cuando la fecha de la carrera ya ha expirado o si la carrera ya
	 *             es interna
	 */
	@Transactional(rollbackFor = CantConvertToInternalException.class)
	public void convertRaceToInternal(Long raceId, Long userId, BigDecimal[] prices, Long[] minAges, Long[] maxAges)
			throws CantConvertToInternalException {
		Account account = accountRepository.findOne(userId);
		Race race = raceRepository.findById(raceId);

		// Si la fecha ya ha ocurrido no es posible organizar la carrera
		if (race.getDate().before(new Date()))
			throw new CantConvertToInternalException("Race " + race.getId() + " is already over.");

		// Dependiendo de si es interna o no, rechazamos la operación
		// o la establecemos como interna y establecemos su organizador
		if (!race.getIsInternal()) {
			race.setCreatedBy(account);
			race.setInternal(true);
		} else
			throw new CantConvertToInternalException("Race " + race.getId() + " is already internal.");

		// Creamos los precios para la carrera
		int i = 0;
		List<Tariff> tariffs = new ArrayList<>(prices.length);
		for (BigDecimal price : prices) {
			tariffs.add(new Tariff(price, minAges[i], maxAges[i]));
			i++;
		}
		race.setPrices(tariffs);

		save(race);
	}

	/**
	 * Indica si el usuario identificado con ese id es el organizador de la carrera.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param userId
	 *            id que identifica al usuario
	 * @return 'true', si el usuario organiza la carrera. 'false' en caso contrario.
	 */
	@Transactional(readOnly = true)
	public boolean isOrganizer(Long raceId, Long userId) {
		Account account = accountRepository.findOne(userId);
		Race race = raceRepository.findById(raceId);

		return (race.getCreatedBy() == account);
	}

	/**
	 * La función busca carreras en la base de datos que sean similares a la que se
	 * va a insertar para comprobar si son la misma.
	 *
	 * @param distance
	 *            distancia de la búsqueda de carreras similares
	 * @param placeName
	 *            ubicación de la carrera que se va a buscar
	 * @param date
	 *            fecha de la carrera
	 * @param raceName
	 *            nombre de la carrera
	 * @return the list
	 */

	@Transactional(readOnly = true)
	public List<Race> findSimilarRaces(double distance, String placeName, Date date, String raceName) {
		Place place = placeRepository.findByPlaceNameLikeIgnoreCase(placeName);
		try {
			if (place == null)
				place = retrieveLocation(placeName);
		} catch (Exception e) {
			return null;
		}
		return raceRepository.findSimilarRaces(distance, place.getLat(), place.getLng(), date, raceName);
	}

}