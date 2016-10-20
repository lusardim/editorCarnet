package carnet.templates;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMImageElement;
import org.apache.batik.dom.svg.SVGOMPathElement;
import org.apache.batik.dom.svg.SVGOMTextElement;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTextElement;

public abstract class TemplateHandler {

	private SVGDocument template;
	private JSVGCanvas canvas;

	public void leerTemplateInterna(String nombreTemplate) throws IOException {
		try (InputStream resource = this.getClass().getResourceAsStream(
				nombreTemplate)) {
			if (resource == null) {
				throw new IllegalArgumentException(
						"No se pudo encontrar el template " + nombreTemplate);
			}
			// Fuerza que se cargue en memoria el template
			String templateString = IOUtils.toString(resource);
			StringReader reader = new StringReader(templateString);
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

			template = factory.createSVGDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, reader);
		}
	}

	protected SVGOMTextElement ObtenerElementoTextual(String id) {
		Element element = template.getElementById(id);
		if (element != null) {
			if (element instanceof SVGOMTextElement) {
				return (SVGOMTextElement) element;
			}

			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item instanceof SVGOMTextElement) {
					return (SVGOMTextElement) item;
				}
			}
		}
		throw new IllegalArgumentException(
				"Template incompatible, elemento faltante: " + id);
	}

	protected SVGOMImageElement ObtenerImagen(String id) {
		Element element = template.getElementById(id);
		if (element != null) {
			if (element instanceof SVGOMImageElement) {
				return (SVGOMImageElement) element;
			}
			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item instanceof SVGOMImageElement) {
					return (SVGOMImageElement) item;
				}
			}
		}
		throw new IllegalArgumentException(
				"Template incompatible, elemento faltante: " + id);
	}

	protected SVGRectElement ObtenerElementoRect(String id) {
		Element element = template.getElementById(id);
		if (element != null) {
			if (element instanceof SVGRectElement) {
				return (SVGRectElement) element;
			}

			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item instanceof SVGRectElement) {
					return (SVGRectElement) item;
				}
			}
		}
		throw new IllegalArgumentException(
				"Template incompatible, elemento faltante: " + id);
	}
	
	public SVGOMPathElement ObtenerElementoPath(String id) {
		Element element = template.getElementById(id);
		if (element != null) {
			if (element instanceof SVGOMPathElement) {
				return (SVGOMPathElement) element;
			}

			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item instanceof SVGRectElement) {
					return (SVGOMPathElement) item;
				}
			}
		}
		throw new IllegalArgumentException(
				"Template incompatible, elemento faltante: " + id);
	}

	protected void actualizarTexto(final SVGTextElement elemento,
			final String texto) {
		canvas.getUpdateManager().getUpdateRunnableQueue()
				.invokeLater(new Runnable() {
					@Override
					public void run() {
						elemento.setTextContent(texto);
					}
				});
	}

	protected void actualizarImagen(final SVGOMImageElement imagen,
			final String href) 
	{
		canvas.getUpdateManager().getUpdateRunnableQueue()
				.invokeLater(new Runnable() {
					@Override
					public void run() {
						imagen.getAttributeNodeNS(
								"http://www.w3.org/1999/xlink", "href")
								.setValue(href);
					}
				});
	}

	protected void actualizarColor(final SVGRectElement recuadro, 
			final Color color) 
	{
		canvas.getUpdateManager().getUpdateRunnableQueue()
				.invokeLater(new Runnable() {
					@Override
					public void run() {
						String rgb = Integer.toHexString(color.getRGB());
						rgb = "#" + rgb.substring(2, rgb.length());
						recuadro.setAttribute("fill", rgb);
					}
				});
	}

	protected void actualizarColor(final SVGOMPathElement path, 
			final Color color) 
	{
		canvas.getUpdateManager().getUpdateRunnableQueue()
				.invokeLater(new Runnable() {
					@Override
					public void run() {
						String rgb = Integer.toHexString(color.getRGB());
						rgb = "#" + rgb.substring(2, rgb.length());
						path.setAttribute("fill", rgb);
					}
				});
	}
	
	public JSVGCanvas getCanvas() {
		if (canvas == null) {
			canvas = new JSVGCanvas();
			canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
			canvas.setDocument(this.getTemplate());
		}
		return canvas;
	}

	public SVGDocument getTemplate() {
		return template;
	}

	public void setTemplate(SVGDocument template) {
		this.template = template;
	}
}
