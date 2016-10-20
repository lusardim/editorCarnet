package carnet.templates;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.batik.dom.svg.SVGOMImageElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTextElement;

public class TemplateBackHandler extends TemplateHandler {

	private static final String DEFAULT_TEMPLATE_NAME = "TemplateBack.svg";
	private static final String NOMBRE_ID = "NOMBRE";
	private static final String APELLIDO_ID = "APELLIDO";
	private static final String DNI_ID = "DNI";
	private static final String FOTO_ID = "FOTO";
	private static final String IMAGEN_INFERIOR_ID = "IMAGEN_INFERIOR";
	private static final String TITULO_CARNET = "TITULO_CARNET"; 
	private static final String RECUADRO_TITULO = "RECUADRO_TITULO";
	
	private SVGTextElement nombre;
	private SVGTextElement apellido;
	private SVGTextElement dni;
	private SVGTextElement tituloCarnet;
	private SVGOMImageElement foto;
	private SVGOMImageElement imagenInferior;
	private SVGRectElement recuadroTitulo;
	
	public void leerTemplate() throws IOException {
		leerTemplateInterna(DEFAULT_TEMPLATE_NAME);
		nombre = ObtenerElementoTextual(NOMBRE_ID);
		tituloCarnet = ObtenerElementoTextual(TITULO_CARNET);
		apellido = ObtenerElementoTextual(APELLIDO_ID);
		dni = ObtenerElementoTextual(DNI_ID);
		foto = ObtenerImagen(FOTO_ID);
		imagenInferior = ObtenerImagen(IMAGEN_INFERIOR_ID);
		recuadroTitulo = ObtenerElementoRect(RECUADRO_TITULO);
	}
	
	public void setNombre(String nombre) {
		this.actualizarTexto(this.nombre, "Nombre: " + nombre);
	}
	
	public void setApellido(String apellido) {
		actualizarTexto(this.apellido, "Apellido: " + apellido);
	}
	
	public void setDni(String dni) {
		actualizarTexto(this.dni, "DNI: " + dni);
	}
	
	public void setTituloCarnet(String tituloCarnet) {
		actualizarTexto(this.tituloCarnet, tituloCarnet);
	}
	
	public void setFoto(File foto) {
		if (foto != null && foto.exists()) {
			actualizarImagen(this.foto, foto.toURI().toString());
		}
		else {
			actualizarImagen(this.foto, "IMAGEN");
		}
	}
	
	public void actualizarImagenInferior(File imagen) {
		if (imagen != null && imagen.exists()) {
			actualizarImagen(this.imagenInferior, imagen.toURI().toString());
		}
		else {
			actualizarImagen(this.imagenInferior, "IMAGEN");
		}
	}
	
	public void setColorRecuadro(Color color) {
		this.actualizarColor(recuadroTitulo, color);
	}
	
}
