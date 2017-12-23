package es.udc.fi.dc.fd.signin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SigninController {

	@GetMapping("signin")
	public String signin() {
		return "signin/signin";
	}

	@GetMapping("/redirectSignin")
	public String notAuthAttend(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		request.getSession().setAttribute("url_prior_login", referer);
		return "redirect:/signin";
	}
}
