package carnet.modelo.entidades;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PERIODOS")
public class PeriodoVencimiento implements Serializable {
	
	public enum Periodicidad {
		DIAS(Calendar.DAY_OF_YEAR, "Dias"),
		MESES(Calendar.MONTH, "Meses"),
		ANIOS(Calendar.YEAR, "Años");
		
		private int constante;
		private String descipcion;
		
		private Periodicidad(int constante, String descipcion) {
			this.constante = constante;
			this.descipcion = descipcion;
		}
		
		public int getConstante() {
			return constante;
		}
		
		public String toString() {
			return descipcion;
		}
	}

	private static final long serialVersionUID = -8068953908544719772L;
	
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private long id;
	@Basic(optional = false)
	private String descripcion;
	@Min(1)
	private int cantidad;
	@NotNull
	@Enumerated(EnumType.STRING)
	private Periodicidad periodicidad;
	private Boolean eliminado;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public Periodicidad getPeriodicidad() {
		return periodicidad;
	}
	public void setPeriodicidad(Periodicidad periodicidad) {
		this.periodicidad = periodicidad;
	}
	
	public Date obtenerVencimiento(Date origen) {
		if (origen == null) {
			throw new NullPointerException("El origen no puede ser nulo");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(origen);
		
		calendar.add(periodicidad.constante, cantidad);
		return calendar.getTime();
	}
	
	public Boolean getEliminado() {
		return eliminado;
	}
	
	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
		PeriodoVencimiento other = (PeriodoVencimiento) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
 