package es.udc.fi.dc.fd.activity.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.udc.fi.dc.fd.account.Account;;

/**
 * La Clase Activity representa una actividad cualquiera que forma parte de la
 * timeline de usuario.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_type")
@Table(name = "activity")
public abstract class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "actId", updatable = false, nullable = false)
	private Long actId;

	private Date timestamp;

	@ManyToOne
	@JoinColumn(name = "accountId")
	private Account account;

	public Activity() {
		this.timestamp = new Date();
	}

	public Activity(Account account) {
		this.timestamp = new Date();
		this.account = account;
	}

	public Long getActId() {
		return actId;
	}

	public void setActId(Long actId) {
		this.actId = actId;
	}

	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = new Date(timestamp.getTime());
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getFormattedTime() {
		SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm dd-MM-yyyy");
		return dt1.format(timestamp);
	}

	public String toString(String context) {
		return "<a href=\"" + context + "myprofile?accId=" + account.getId() + "\">" + account.getName() + "</a>";
	}

}
