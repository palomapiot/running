package es.udc.fi.dc.fd.signup;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.multipart.MultipartFile;

import es.udc.fi.dc.fd.account.Account;

public class EditProfileForm {

	private String password;

	private String name;

	private String surname;

	private MultipartFile image;

	private String birthday;

	private String city;

	private String country;

	private String bio;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname
	 *            the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the image
	 */
	public MultipartFile getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(MultipartFile image) {
		this.image = image;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the bio
	 */
	public String getBio() {
		return bio;
	}

	/**
	 * @param bio
	 *            the bio to set
	 */
	public void setBio(String bio) {
		this.bio = bio;
	}

	/**
	 * Crea un Account con el boolean de perfil privado a false por defecto.
	 * 
	 * @return la cuenta creada
	 */
	public Account createAccount() {
		try {
			byte[] bytes = null;
			String base64Encoded = null;
			if (getImage() != null) {
				try {
					bytes = getImage().getBytes();
					base64Encoded = Base64.getEncoder().encodeToString(bytes);
				} catch (IOException e) {
					Logger.getGlobal().log(Level.WARNING, e.toString());
				}
			}
			return new Account(null, getPassword(), null, getName(), getSurname(), base64Encoded,
					new SimpleDateFormat("MM-dd-yyyy").parse(getBirthday()), getCity(), getCountry(), getBio(), false,
					"ROLE_USER");
		} catch (ParseException e) {
			Logger.getGlobal().log(Level.WARNING, e.toString());
			return null;
		}
	}
}
