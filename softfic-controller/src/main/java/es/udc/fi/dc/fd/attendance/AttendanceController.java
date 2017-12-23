package es.udc.fi.dc.fd.attendance;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.race.RaceService;

@Controller
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private RaceService raceService;

	/**
	 * Anota o borra al usuario que realiza la petición de la carrera con el id
	 * indicado por parámetro.
	 * 
	 * Devuelve el fragment "attendZone" localizado en "races/raceDetails"
	 *
	 * @param principal
	 *            the principal
	 * @param model
	 *            the model
	 * @param raceId
	 *            the race id
	 * @return the string
	 */
	@GetMapping("/attend")
	public String attendToRace(Principal principal, Model model, @RequestParam Long raceId) {
		// Anotar o borrar al usuario de la carrera
		attendanceService.attendToRace(raceId, principal.getName());

		// Carga en el modelo los datos necesitados por el fragment que se va a devolver
		model.addAttribute("attendance", attendanceService.isAttendant(raceId, principal.getName()));
		model.addAttribute("race", raceService.findById(raceId));
		model.addAttribute("attendants", attendanceService.numberOfAttendants(raceId));

		// Recarga el fragment "attendZone" localizado en "races/raceDetails.html"
		return "races/raceDetails :: attendZone";
	}

}
