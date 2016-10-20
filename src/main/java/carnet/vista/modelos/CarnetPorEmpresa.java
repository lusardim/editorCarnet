package carnet.vista.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.Empresa;

public class CarnetPorEmpresa implements Serializable {

	private static final long serialVersionUID = 4254927629268054143L;
	private Empresa empresa;
	private List<Carnet> carnets = new ArrayList<Carnet>();
	private Integer cantidadVencidos = 0;
	private Integer cantidadPorVencer = 0;
	
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	public Integer getCantidadVencidos() {
		return cantidadVencidos;
	}
	public void setCantidadVencidos(Integer cantidadVencidos) {
		this.cantidadVencidos = cantidadVencidos;
	}
	public Integer getCantidadPorVencer() {
		return cantidadPorVencer;
	}
	public void setCantidadPorVencer(Integer cantidadPorVencer) {
		this.cantidadPorVencer = cantidadPorVencer;
	}
	public List<Carnet> getCarnets() {
		return carnets;
	}
	public void setCarnets(List<Carnet> carnets) {
		this.carnets = carnets;
	}
}
