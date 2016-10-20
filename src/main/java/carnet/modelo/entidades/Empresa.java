package carnet.modelo.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "EMPRESAS")
public class Empresa implements Serializable {
	private static final long serialVersionUID = 6300111972629552587L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String nombre;
	private String descripcion;
	private Boolean eliminada = false;
	private UUID imagenInferior;
	
	@Email
	private String email;
	@Temporal(TemporalType.DATE)
	private Date fechaUltimaNoficiacion;
	
	@OneToMany(mappedBy="empresa", cascade=CascadeType.REMOVE)
	private List<Carnet> carnets;

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Boolean getEliminada() {
		return eliminada;
	}
	
	public void setEliminada(Boolean eliminada) {
		this.eliminada = eliminada;
	}
	public UUID getImagenInferior() {
		return imagenInferior;
	}
	public void setImagenInferior(UUID imagenInferior) {
		this.imagenInferior = imagenInferior;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empresa other = (Empresa) obj;
		if (id != other.id)
			return false;
		return true;
	}
	public List<Carnet> getCarnets() {
		return carnets;
	}
	public void setCarnets(List<Carnet> carnets) {
		this.carnets = carnets;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getFechaUltimaNoficiacion() {
		return fechaUltimaNoficiacion;
	}
	public void setFechaUltimaNoficiacion(Date fechaUltimaNoficiacion) {
		this.fechaUltimaNoficiacion = fechaUltimaNoficiacion;
	}
}
