package es.udc.fi.dc.fd.attendance;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.account.Account;
import es.udc.fi.dc.fd.account.AccountRepository;
import es.udc.fi.dc.fd.activity.ActivityRepository;
import es.udc.fi.dc.fd.activity.entities.Activity;
import es.udc.fi.dc.fd.activity.entities.GoActivity;
import es.udc.fi.dc.fd.race.Race;
import es.udc.fi.dc.fd.race.RaceRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AttendanceService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private RaceRepository raceRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ActivityRepository actRepository;

	/**
	 * Anota al usuario a una carrera, creando una nueva entidad "Attendance". Si el
	 * usuario ya estaba anotado previamente, entonces borra la asistencia de ese
	 * usuario a esa carrera
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param email
	 *            email que identifica al usuario
	 */
	@Transactional
	public void attendToRace(Long raceId, String email) {
		Account account = accountRepository.findOneByEmail(email);
		Race race = raceRepository.findById(raceId);

		// Si la fecha ya ha ocurrido no es posible anotarse
		if (race.getDate().before(new Date()))
			return;

		// Dependiendo de si estamos anotados o no, creamos un nuevo Attendance o
		// borramos el que ya existe
		Attendance assist = attendanceRepository.findByAccountAndRace(account, race);
		if (assist == null) {
			attendanceRepository.save(new Attendance(account, race));
			Activity activity = new GoActivity(account, race);
			actRepository.save(activity);
		} else
			attendanceRepository.delete(assist);
	}

	/**
	 * Indica si el usuario identificado con ese email asiste o no a la carrera
	 * indicada.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @param email
	 *            email que identifica al usuario
	 * @return 'true', si el usuario asiste a la carrera. 'false' en caso contrario.
	 */
	@Transactional(readOnly = true)
	public boolean isAttendant(Long raceId, String email) {
		return attendanceRepository.exists(email, raceId);
	}

	/**
	 * Devuelve el número de asistentes a la carrera indicada por parámetro.
	 *
	 * @param raceId
	 *            id de la carrera
	 * @return el numero de asistentes
	 */
	@Transactional(readOnly = true)
	public Long numberOfAttendants(Long raceId) {
		return attendanceRepository.numberOfAttendants(raceId);
	}
}
