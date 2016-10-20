package carnet.modelo.entidades;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.util.StringUtils;

import carnet.vista.modelos.CarnetPorEmpresa;

@Entity
public class ConfiguracionPlantillaMail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String origen;
	private String asunto;
	@Column(length = 10000)
	private String contenido;
	private int periodicidadDias;	
	private boolean enviarVencidos;
	private boolean enviarPorVencer;
	private int reenviarCada;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	/**
	 * @return Cantidad de días que faltan para el vencimiento de los carnets.
	 */
	public int getPeriodicidadDias() {
		return periodicidadDias;
	}
	public void setPeriodicidadDias(int periodicidadDias) {
		this.periodicidadDias = periodicidadDias;
	}
	
	/**
	 * @return true en caso de notificar a empresas con carnets vencidos
	 */
	public boolean isEnviarVencidos() {
		return enviarVencidos;
	}
	public void setEnviarVencidos(boolean enviarVencidos) {
		this.enviarVencidos = enviarVencidos;
	}
	
	/**
	 * @return true si se deben notificar las empresas con carnets por vencer
	 */
	public boolean isEnviarPorVencer() {
		return enviarPorVencer;
	}
	public void setEnviarPorVencer(boolean enviarPorVencer) {
		this.enviarPorVencer = enviarPorVencer;
	}
	/**
	 * @return cantidad de días entre notificaciones, por defecto 1 (todos los días)
	 */
	public int getReenviarCada() {
		return reenviarCada;
	}
	public void setReenviarCada(int reenviarCada) {
		this.reenviarCada = reenviarCada;
	}
	
	
	/**
	 * Obtiene el asunto parseado con los valores de un carnet por empresa
	 * @param carnetPorEmpresa
	 * @return
	 */
	public String getAsunto(CarnetPorEmpresa carnetPorEmpresa) {
		return parseTexto(asunto, carnetPorEmpresa);
	}

	/**
	 * Obtiene el contenido parseado con los valores de un carnet por empresa
	 * @param carnetPorEmpresa
	 * @return
	 */
	public String getContenido(CarnetPorEmpresa carnetPorEmpresa) {
		return parseTexto(contenido, carnetPorEmpresa);
	}
	
	private String parseTexto(String texto, CarnetPorEmpresa carnetPorEmpresa) {
		String resultado = texto;
		for (VariablesTemplate variable : VariablesTemplate.values()) {
			resultado = reemplazarVariable(variable, resultado, carnetPorEmpresa);
		}
		return resultado;
	}

	private String reemplazarVariable(VariablesTemplate variable,
			String cadena, CarnetPorEmpresa carnetPorEmpresa) {
		String reemplazo = ""; 
		switch (variable) {
			case CANTIDAD_POR_VENCER: 
				reemplazo = carnetPorEmpresa.getCantidadPorVencer().toString();
				break;
			case CANTIDAD_VENCIDOS: 
				reemplazo = carnetPorEmpresa.getCantidadVencidos().toString();
				break;
			case DIAS: 
				reemplazo = String.valueOf(this.getPeriodicidadDias());
				break;
			case NOMBRE_EMPRESA:
				reemplazo = carnetPorEmpresa.getEmpresa().getNombre();
				break;
			case LISTA_VENCIMIENTOS:
				reemplazo = String.format("%-10s  %-40s %-17s%n", "DNI", "Nombre y apellido", "Fecha Vencimiento");
				String formato = "%10s  %-40s %td/%tm/%tY%n";
				for (Carnet carnet : carnetPorEmpresa.getCarnets()) {
					String dni = carnet.getAfiliado().getDni();
					String nombreYApellido = carnet.getAfiliado().getApellido() + ", " + 
							carnet.getAfiliado().getNombre();
					Date fecha = carnet.getVencimiento();
					reemplazo += String.format(formato, dni, nombreYApellido, fecha, fecha, fecha);
				}
				break;
		}
		return StringUtils.replace(cadena, variable.getVariable(), reemplazo);
	}

}
