package es.udc.fi.dc.fd.home;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.udc.fi.dc.fd.race.RaceForm;

@Controller
class HomeController {

	@ModelAttribute("module")
	String module() {
		return "home";
	}

	@GetMapping("/")
	String index(Model model, Principal principal) {
		// createRace modal, para que estando logeados llegue el formulario
		model.addAttribute(new RaceForm());
		model.addAttribute("orderType", 3);
		return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}
}
