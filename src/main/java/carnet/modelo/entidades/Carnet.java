package carnet.modelo.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Carnet implements Serializable {
	
	private static final long serialVersionUID = 4744972179418323355L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String titulo = "Carnet de Manejo Defensivo";
	private UUID foto;
	private String calificacion;
	private String color;
	@Temporal(TemporalType.DATE)
	private Date emision;
	@Temporal(TemporalType.DATE)
	private Date vencimiento;
	private boolean impreso;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	private Afiliado afiliado;
	@ManyToOne(optional = false)
	private Instructor instructor;
	@ManyToOne(optional = false) 
	private Empresa empresa;
	@ManyToOne(optional = true)
	private PeriodoVencimiento periodo;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Afiliado getAfiliado() {
		return afiliado;
	}
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}
	public UUID getFoto() {
		return foto;
	}
	public void setFoto(UUID foto) {
		this.foto = foto;
	}
	public String getCalificacion() {
		return calificacion;
	}
	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Date getEmision() {
		return emision;
	}
	public void setEmision(Date emision) {
		this.emision = emision;
	}
	public Date getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	public boolean isImpreso() {
		return impreso;
	}
	public void setImpreso(boolean impreso) {
		this.impreso = impreso;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public Instructor getInstructor() {
		return instructor;
	}
	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public PeriodoVencimiento getPeriodo() {
		return periodo;
	}
	public void setPeriodo(PeriodoVencimiento periodo) {
		this.periodo = periodo;
	}
	
	@Override
	public String toString() {
		return String.format("[%1s] Nota: %1s [%2tF - %3tF]", 
				afiliado, calificacion, emision, vencimiento);
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
		Carnet other = (Carnet) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
