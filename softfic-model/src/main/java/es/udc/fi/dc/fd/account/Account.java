package es.udc.fi.dc.fd.account;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * La clase {@link Account} representa una cuenta de usuario en el sistema.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "account")
public class Account implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	@JsonIgnore
	private String password;

	@Column(unique = true)
	private String username;

	private String name;

	private String surname;

	@Size(max = 2000000000)
	private String image;

	private Date birthday;

	private String city;

	private String country;

	private String bio;

	/**
	 * Boolean que representa si una cuenta es privada (true) o pública (false). Si
	 * es privada, el perfil sólo se podrá ver cuando se esté siguiendo a ese
	 * usuario.
	 */
	private boolean privateAccount;

	private String role = "ROLE_USER";

	/**
	 * Momento en el que se crea el perfil.
	 */
	private Instant created;

	public Account() {

	}

	public Account(String email, String password, String username, String name, String surname, String image,
			Date birthday, String city, String country, String bio, boolean privateAccount, String role) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.image = image;
		this.birthday = new Date(birthday.getTime());
		this.city = city;
		this.country = country;
		this.bio = bio;
		this.privateAccount = privateAccount;
		this.role = role;
		this.created = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return new Date(birthday.getTime());
	}

	public Long getAge() {
		LocalDate now = LocalDate.now();
		LocalDate birth = this.birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Period period = Period.between(birth, now);
		return (long) period.getYears();
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = new Date(birthday.getTime());
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
	 * @return the privateAccount
	 */
	public boolean getPrivateAccount() {
		return privateAccount;
	}

	/**
	 * @param privateAccount
	 *            the privateAccount to set
	 */
	public void setPrivateAccount(boolean privateAccount) {
		this.privateAccount = privateAccount;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Instant getCreated() {
		return created;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", email=" + email + ", password=" + password + ", role=" + role + ", created="
				+ created + "]";
	}
}
