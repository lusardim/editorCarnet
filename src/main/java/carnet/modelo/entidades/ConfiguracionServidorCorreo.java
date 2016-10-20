package carnet.modelo.entidades;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;


@Entity
public class ConfiguracionServidorCorreo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@NotNull
	private String hostName;
	private int port;
	private String userName;
	@Convert(converter=CryptoConverter.class)
	private String password;
	@Enumerated(EnumType.STRING)
	private ProtocoloSeguridadEmail protocolo;
	
	public Email crearEmail() throws Exception {
		Email email = new SimpleEmail();
		email.setHostName(hostName);
		email.setSmtpPort(port);
		email.setAuthenticator(new DefaultAuthenticator(userName, password));
		switch (protocolo) {
			case STARTTLS: {
				email.setStartTLSEnabled(true);
				break;
			}
			case SSL: {
				email.setSSLOnConnect(true);
				break;
			}
			default:
		}
		return email;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProtocolo(ProtocoloSeguridadEmail protocolo) {
		this.protocolo = protocolo;
	}
	public ProtocoloSeguridadEmail getProtocolo() {
		return protocolo;
	}
	
}
