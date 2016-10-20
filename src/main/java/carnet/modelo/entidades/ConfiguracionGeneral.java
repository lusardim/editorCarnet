package carnet.modelo.entidades;

import java.awt.Color;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ConfiguracionGeneral {
	
	@Id
	private int id;
	@Column(nullable = false)
	private String tituloCarnet;
	@Column(nullable = false)
	private String descripcionEmpresa;
	@Column(nullable = false)
	private String nombreEmpresa;
	@Column(nullable = false)
	private String colorDefecto;
	@Column(nullable = false)
	private String notaDefecto;
	@ManyToOne(fetch = FetchType.EAGER)
	private PeriodoVencimiento periodoDefecto;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private ConfiguracionServidorCorreo configuracionEmail;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private ConfiguracionPlantillaMail template;
		
	public String getTituloCarnet() {
		return tituloCarnet;
	}
	public void setTituloCarnet(String tituloCarnet) {
		this.tituloCarnet = tituloCarnet;
	}
	public String getDescripcionEmpresa() {
		return descripcionEmpresa;
	}
	public void setDescripcionEmpresa(String descripcionEmpresa) {
		this.descripcionEmpresa = descripcionEmpresa;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public String getColorDefecto() {
		return colorDefecto;
	}
	public void setColorDefecto(String colorDefecto) {
		this.colorDefecto = colorDefecto;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNotaDefecto() {
		return notaDefecto;
	}
	public void setNotaDefecto(String notaDefecto) {
		this.notaDefecto = notaDefecto;
	}
	public PeriodoVencimiento getPeriodoDefecto() {
		return periodoDefecto;
	}
	public void setPeriodoDefecto(PeriodoVencimiento periodoDefecto) {
		this.periodoDefecto = periodoDefecto;
	}
	public Color getColor() {
		return Color.decode(this.getColorDefecto());
	}
	public void setColorDefecto(Color color) {
		String rgb = Integer.toHexString(color.getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());
		setColorDefecto(rgb);
	}
	public ConfiguracionServidorCorreo getConfiguracionEmail() {
		return configuracionEmail;
	}
	public void setConfiguracionEmail(ConfiguracionServidorCorreo configuracionEmail) {
		this.configuracionEmail = configuracionEmail;
	}
	public ConfiguracionPlantillaMail getTemplate() {
		return template;
	}
	public void setTemplate(ConfiguracionPlantillaMail template) {
		this.template = template;
	}
}
