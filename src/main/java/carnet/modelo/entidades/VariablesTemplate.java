package carnet.modelo.entidades;

public enum VariablesTemplate {
	CANTIDAD_POR_VENCER("Cantidad de carnets por vencer", "${cantidadPorVencer}"),
	NOMBRE_EMPRESA("Nombre de la empresa", "${nombreEmpresa}"),
	CANTIDAD_VENCIDOS("Cantidad de carnets vencidos", "${cantidadVencidos}"),
	DIAS("Perído", "${dias}"),
	LISTA_VENCIMIENTOS("Listado de vencimientos","${listaVencidos}");
	
	private String descripcion;
	private String variable;
	
	private VariablesTemplate(String descripcion, String variable) {
		this.descripcion = descripcion;
		this.variable = variable;
	}
	
	public String getVariable() {
		return variable;
	}
	public String getDescripcion() {
		return descripcion;
	}
}
