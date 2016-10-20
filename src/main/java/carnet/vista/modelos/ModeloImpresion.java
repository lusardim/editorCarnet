package carnet.vista.modelos;

import carnet.modelo.entidades.Carnet;

public class ModeloImpresion {
	
	private long idCarnet;
	private boolean imprimir;
	private String dni;
	private String nombre;
	private String apellido;

	public ModeloImpresion() {}
	public ModeloImpresion(Carnet carnet) {
		this.idCarnet = carnet.getId();
		this.dni = carnet.getAfiliado().getDni();
		this.nombre = carnet.getAfiliado().getNombre();
		this.apellido = carnet.getAfiliado().getApellido();
	}
		
	public long getIdCarnet() {
		return idCarnet;
	}
	public void setIdCarnet(long idCarnet) {
		this.idCarnet = idCarnet;
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
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public boolean isImprimir() {
		return imprimir;
	}
	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}
}
