package es.udc.fi.dc.fd.enrollment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.GoActivity;
import es.udc.fi.dc.fd.enrollment.exceptions.NotPossiblePayException;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;
import es.udc.fi.dc.fd.race.RaceService;
import es.udc.fi.dc.fd.tariff.Tariff;
import es.udc.fi.dc.fd.tariff.TariffRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EnrollmentService {

	private static final int PAGE_NUM_ELEMENTS = 10;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private RaceRepository raceRepository;

	@Autowired
	private TariffRepository tariffRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RaceService raceService;

	@Autowired
	private ActivityRepository actRepository;

	/**
	 * Inscribirse a una carrera. El pago se inicia a false y se modificará una vez
	 * hecho. El chip se inicia a false y se modificará una vez hecho el pago.
	 *
	 * @param raceId
	 *            the race id
	 * @param email
	 *            the email
	 */
	@Transactional
	public void enrollToRace(Long raceId, String email) {
		Account account = accountRepository.findOneByEmail(email);
		Race race = raceRepository.findById(raceId);
		// Si la carrera ha sucedido no es posible alistarse
		if (race.getDate().before(new Date()))
			return;
		Enrollment assist = enrollmentRepository.findByAccountAndRace(account, race);
		if (assist == null) {
			enrollmentRepository.save(new Enrollment(account, race, false, false));
			Activity activity = new GoActivity(account, race);
			actRepository.save(activity);
		} else
			enrollmentRepository.delete(assist);
	}

	/**
	 * Devuelve si el usuario (email) está inscrito o no a la carrera (raceId).
	 *
	 * @param raceId
	 *            the race id
	 * @param email
	 *            the email
	 * @return true, if is enroll
	 */
	@Transactional(readOnly = true)
	public boolean isEnroll(Long raceId, String email) {
		return enrollmentRepository.exists(email, raceId);
	}

	/**
	 * Devuelve la inscripción a una carrera interna.
	 *
	 * @param race
	 *            the race
	 * @param account
	 *            the account
	 * @return the enroll
	 */
	@Transactional(readOnly = true)
	public Enrollment getEnroll(Race race, Account account) {
		return enrollmentRepository.findByAccountAndRace(account, race);
	}

	/**
	 * Devuelve el número de personas inscritas de forma oficial a la carrera.
	 *
	 * @param raceId
	 *            the race id
	 * @return the long
	 */
	@Transactional(readOnly = true)
	public Long numberOfEnrollments(Long raceId) {
		return enrollmentRepository.numberOfEnrollments(raceId);
	}

	/**
	 * Pone a true el atributo paid de enrollment.
	 *
	 * @param raceId
	 *            the race id
	 * @param accId
	 *            the acc id
	 * @param chip
	 *            id de la tarifa que se paga
	 * @throws NotPossiblePayException
	 *             the not possible pay exception
	 */
	@Transactional(rollbackFor = NotPossiblePayException.class)
	public void payRace(Long raceId, Long accId, Boolean chip) throws NotPossiblePayException {
		List<Long> tariffList = tariffRepository.findUserTariff(raceId, accountRepository.getOne(accId).getAge());
		if (!tariffList.isEmpty()) {
			Long tariffId = tariffList.get(0);
			sendMail(accountRepository.getOne(accId), raceId, tariffId);
			enrollmentRepository.payRace(raceId, accId, chip, tariffId);
		} else {
			throw new NotPossiblePayException("You can't pay this race, there is no tariff available!");
		}
	}

	/**
	 * Envía un email con los datos del pago.
	 *
	 * @param account
	 *            the account
	 * @param raceId
	 *            the race id
	 * @param trfId
	 *            the trf id
	 */
	@Async
	private void sendMail(Account account, Long raceId, Long trfId) {

		Race race = raceService.findById(raceId);
		String username = "fdmed.fic@gmail.com";
		String pd = "ferramentas";
		String to = account.getEmail();
		String subject = "Pago carrera " + race.getName();

		Tariff tarifa = tariffRepository.findOne(trfId);

		String emailBody = "<h1>Confirmación de compra</h1><br> " + "Hola " + account.getName()
				+ ":<br> Gracias por realizar el pago de la carrera <b>" + race.getName()
				+ "</b> en SOFTFIC RUNNING. <br>"
				+ "Te confirmamos que el pago se ha realizado satisfactoriamente. La carrera ha tenido un coste de <b>"
				+ tarifa.getPrice() + "€</b> que va asociado al rango de edades <b>" + tarifa.getMinAge() + " - "
				+ tarifa.getMaxAge() + "</b> y tendrá lugar el <b>"
				+ new SimpleDateFormat("dd-MM-yyyy").format(race.getDate()) + "</b> en <b>" + race.getPlace().getName()
				+ "</b>.<br>";
		if (race.getWebsite() != null) {
			emailBody = emailBody + "Puede consultar más información <a href=\"" + race.getWebsite() + "\">aquí</a>.";
		}

		emailBody = emailBody
				+ "<br> Aquí puede consultar las carreras que ya ha pagado: <a href=\"http://localhost:8080/payments?page=0\">Mis Carreras</a>";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "587");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, pd);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(emailBody, "text/html; charset=utf-8");
			Transport.send(message);

		} catch (Exception e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
		}
	}

	/**
	 * Recupera las carreras internas de un usuario que ha pagado.
	 *
	 * @param account
	 *            the account
	 * @param page
	 *            the page
	 * @return the races payments
	 */
	@Transactional(readOnly = true)
	public Page<Enrollment> getRacesPayments(Account account, int page) {
		return enrollmentRepository.findEnrollmentsPaidByAccount(account.getId(),
				new PageRequest(page, PAGE_NUM_ELEMENTS));
	}

	/**
	 * Recupera un {@link Enrollment} a partir de su id.
	 *
	 * @param enrollmentId
	 *            the enrollment id
	 * @return the enrollment
	 */
	@Transactional(readOnly = true)
	public Enrollment findById(Long enrollmentId) {
		return enrollmentRepository.findOne(enrollmentId);
	}

	/**
	 * Recupera todos los {@link Enrollment} de una carrera.
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @return Lista de enrollments
	 */
	@Transactional(readOnly = true)
	public List<Enrollment> findByRaceId(Long raceId) {
		return enrollmentRepository.findByRaceId(raceId);
	}

	/**
	 * Recupera todos los {@link Enrollment} de una carrera filtrando también por si
	 * su "email" contiene el término "search".
	 *
	 * @param raceId
	 *            identificador de la carrera
	 * @param search
	 *            término para filtrado por email
	 * @return lista no paginada de los enrollment que cumplen las condiciones
	 */
	@Transactional(readOnly = true)
	public List<Enrollment> findByRaceIdAndKeywords(Long raceId, String search) {
		return enrollmentRepository.findByRaceIdAndKeywords(raceId, search);
	}

}
