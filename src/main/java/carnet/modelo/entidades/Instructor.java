package carnet.modelo.entidades;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "INSTRUCTORES")
public class Instructor implements Serializable {

	private static final long serialVersionUID = -365442528506278607L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@NotEmpty
	private String dni;
	@NotEmpty
	@NotBlank
	private String nombre;
	@NotEmpty
	@NotBlank
	private String apellido;
	private UUID firmaId;
	private Boolean eliminado = false;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public UUID getFirmaId() {
		return firmaId;
	}
	public void setFirmaId(UUID firmaId) {
		this.firmaId = firmaId;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	@Override
	public String toString() {
		return String.format("%1$s, %2$s", this.apellido, this.nombre);
	}

	public Boolean getEliminado() {
		return eliminado;
	}
	
	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
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
		Instructor other = (Instructor) obj;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
