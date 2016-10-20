package carnet.templates;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.batik.dom.svg.SVGOMImageElement;
import org.apache.batik.dom.svg.SVGOMPathElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTextElement;

public class TemplateFrontHandler extends TemplateHandler {

	private static final String DEFAULT_TEMPLATE_NAME = "TemplateFront.svg";
	private static final String EMPRESA_TITULO_ID = "EMPRESA_TITULO";
	private static final String DESCRIPCION_TITULO_ID = "DESCRIPCION";
	private static final String INSTRUCTOR_ID = "INSTRUCTOR";
	private static final String EMPRESA_ID = "EMPRESA";
	private static final String FECHA_OTORGAMIENTO_ID = "FECHA1";
	private static final String FECHA_VENCIMIENTO_ID = "FECHA2";
	private static final String FIRMA_ID = "FIRMA";
	private static final String CALIFICACION_ID = "CALIFICACION";
	private static final String ANIO_ID = "ANIO";
	private static final String RECUADRO_NOTA_ID = "RECUADRO_NOTA";
	private static final String RECUADRO_TITULO = "RECUADRO_TITULO";
	private static final String TITULO_CARNET = "TITULO_CARNET";
	
	
	private DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
	private SVGTextElement tituloEmpresa;
	private SVGTextElement tituloDescripcion;
	private SVGTextElement instructor;
	private SVGTextElement empresa;
	private SVGTextElement fechaOtorgamiento;
	private SVGTextElement fechaVencimiento;
	private SVGOMImageElement imagenFirma;
	private SVGTextElement calificacion;
	private SVGTextElement anio;
	private SVGRectElement recuadroNota;
	private SVGOMPathElement recuadroTitulo;
	private SVGTextElement tituloCarnet;
	
	public void leerTemplate() throws IOException {
		super.leerTemplateInterna(DEFAULT_TEMPLATE_NAME);
		this.tituloEmpresa = ObtenerElementoTextual(EMPRESA_TITULO_ID);
		this.tituloDescripcion = ObtenerElementoTextual(DESCRIPCION_TITULO_ID);
		this.instructor = ObtenerElementoTextual(INSTRUCTOR_ID);
		this.empresa = ObtenerElementoTextual(EMPRESA_ID);
		this.fechaOtorgamiento = ObtenerElementoTextual(FECHA_OTORGAMIENTO_ID);
		this.fechaVencimiento = ObtenerElementoTextual(FECHA_VENCIMIENTO_ID);
		this.imagenFirma = ObtenerImagen(FIRMA_ID);
		this.calificacion = ObtenerElementoTextual(CALIFICACION_ID);
		this.anio = ObtenerElementoTextual(ANIO_ID);
		this.recuadroNota = ObtenerElementoRect(RECUADRO_NOTA_ID);
		this.recuadroTitulo = ObtenerElementoPath(RECUADRO_TITULO);
		this.tituloCarnet = ObtenerElementoTextual(TITULO_CARNET);
	}
	
	public void setTituloEmpresa(String tituloEmpresa) {
		actualizarTexto(this.tituloEmpresa, tituloEmpresa);
	}

	public void setTituloDescripcion(String tituloDescripcion) {
		actualizarTexto(this.tituloDescripcion, tituloDescripcion);
	}

	public void setInstructor(String instructor) {
		actualizarTexto(this.instructor, "Instructor: " + instructor);
	}

	public void setEmpresa(String empresa) {
		actualizarTexto(this.empresa, "Empresa: " + empresa);
	}

	public void setFechaOtorgamiento(Date fechaOtorgamiento) {
		actualizarTexto(this.fechaOtorgamiento, formatoFecha.format(fechaOtorgamiento));
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		actualizarTexto(this.fechaVencimiento, formatoFecha.format(fechaVencimiento));
	}

	public void setImagenFirma(File imagenFirma) {
		this.actualizarImagen(this.imagenFirma, imagenFirma.toURI().toString());
	}

	public void setCalificacion(String calificacion) {
		actualizarTexto(this.calificacion, calificacion);
	}

	public void setAnio(String anio) {
		actualizarTexto(this.anio, anio);
	}
	
	public void setColorNota(Color color) {
		actualizarColor(this.recuadroNota, color);
	}
	
	public void setColorTitulo(Color color) {
		actualizarColor(this.recuadroTitulo, color);
	}

	public void setTituloCarnet(String tituloCarnet) {
		actualizarTexto(this.tituloCarnet, tituloCarnet);
	}
	
}
