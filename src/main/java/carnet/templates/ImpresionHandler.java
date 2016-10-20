package carnet.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.batik.dom.svg.SVGOMTextElement;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import carnet.ModeloImpresionJasper;
import carnet.modelo.dao.CarnetDao;
import carnet.modelo.dao.ConfiguracionDao;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.dao.ImagenDao;
import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.ConfiguracionGeneral;

public class ImpresionHandler extends TemplateHandler {

	private static final String TEMPLATE = "Impresion.svg";
	private static final String REPORT = "carnet_A4.jasper";
	private	Queue<Carnet> imprimir;
	
	private ImagenDao imagenDao;
	private ConfiguracionDao configuracionDao;
	private CarnetDao carnetDao;
	private ConfiguracionGeneral configuracion;
	private SVGDocument documentoOriginal;
	private File file;
	private DateFormat fomatter = new SimpleDateFormat("dd/MM/yyyy");
		
	public ImpresionHandler(List<Long> seleccionados) throws IOException {
		imagenDao = DaoManager.getInstance().getImagenDao();
		carnetDao = DaoManager.getInstance().getCarnetDao();
		configuracionDao = DaoManager.getInstance().getConfiguracionDao();
		configuracion = configuracionDao.getConfiguracionGeneral();
		leerTemplateInterna(TEMPLATE);
		documentoOriginal = (SVGDocument) getTemplate().cloneNode(true);
		leerCarnets(seleccionados);
	}

	private void leerCarnets(List<Long> seleccionados) {
		this.imprimir = new LinkedList<Carnet>();
		for (long idSeleccionado : seleccionados) {
			imprimir.add(carnetDao.getById(idSeleccionado));
		}
	}

	public void imprimir() throws Exception {
		imprimir(true);
	}
	
	public void imprimir(File file) throws Exception {
		this.file = file;
		imprimir(false);
	}
	
	private List<ModeloImpresionJasper> gernerarListaCarnets() throws Exception {
		List<ModeloImpresionJasper> lista = new ArrayList<ModeloImpresionJasper>();
		Carnet carnet = null;
		while ((carnet = imprimir.poll()) != null) {
			File file = File.createTempFile("impresion_", ".svg");
			file.deleteOnExit();
			try (OutputStream fileOutputStream = new FileOutputStream(file);
				 Writer writer = new OutputStreamWriter(fileOutputStream, "UTF-8"))
			{
				procesarCarnet(carnet);
				DOMUtilities.writeDocument((SVGDocument)getTemplate().cloneNode(true), writer);
				lista.add(new ModeloImpresionJasper(file.getAbsolutePath()));
			}
		}
		return lista;
	}

	private void imprimir(boolean impresora) throws Exception {
		List<ModeloImpresionJasper> lista = gernerarListaCarnets();
		
		try (InputStream reporte = this.getClass().getResourceAsStream(REPORT)) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(lista);
			JasperPrint fileName = JasperFillManager.fillReport(
			         reporte,
			         parameters, 
			         datasource);
			if (impresora) {
				JasperPrintManager.printReport(fileName, true);
			}
			else {
				if (file.getParentFile().canWrite()) {
					JasperExportManager.exportReportToPdfFile(fileName, file.getAbsolutePath());	
				}
				else {
					throw new IllegalAccessError("Usted no tiene permisos para generar el archivo: " + file.getAbsolutePath());
				}
			}
			setTemplate((SVGDocument) documentoOriginal.cloneNode(true));
		}
	}
	
	private void procesarCarnet(Carnet carnet) throws Exception {
		//texto
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(carnet.getEmision());
		Integer anio = calendar.get(Calendar.YEAR);

		//actualizarTexto("EMPRESA_TITULO", carnet.getEmpresa().toString());
		//actualizarTexto("DESCRIPCION", carnet.getEmpresa().getDescripcion());
		
		actualizarTexto("EMPRESA_TITULO", configuracion.getNombreEmpresa());
		actualizarTexto("DESCRIPCION", configuracion.getDescripcionEmpresa());
		actualizarTexto("TITULO_CARNET",  configuracion.getTituloCarnet());
		actualizarTexto("TITULO_CARNET_BACK", configuracion.getTituloCarnet());
		actualizarTexto("INSTRUCTOR", "Instructor: " + carnet.getInstructor().toString());  
		actualizarTexto("EMPRESA", "Empresa: " + carnet.getEmpresa().toString());
		actualizarTexto("FECHA1", fomatter.format(carnet.getEmision()));
		actualizarTexto("FECHA2", fomatter.format(carnet.getVencimiento()));
		actualizarTexto("ANIO", anio.toString());
		actualizarTexto("CALIFICACION", carnet.getCalificacion().toUpperCase());
		actualizarTexto("NOMBRE_BACK",  "Nombre: " + carnet.getAfiliado().getNombre());
		actualizarTexto("APELLIDO_BACK", "Apellido: " +  carnet.getAfiliado().getApellido()); 
		actualizarTexto("DNI_BACK", "DNI: " + carnet.getAfiliado().getDni());
				
		//colores
		String color = carnet.getColor();
		actualizarColor("RECUADRO_NOTA", color);
		actualizarColor("RECUADRO_TITULO", color);
		actualizarColor("RECUADRO_TITULO_BACK", color);
	
		actualizarFoto("IMAGEN_INFERIOR_BACK", imagenDao.getFileImagen(
				carnet.getEmpresa().getImagenInferior()).toURI().toString());
		actualizarFoto("FOTO_BACK", imagenDao.getFileImagen(carnet.getFoto()).toURI().toString());
		actualizarFoto("FIRMA", imagenDao.getFileImagen(carnet.getInstructor().getFirmaId()).toURI().toString());
	}

	private void actualizarTexto(String id, String texto) {
		SVGOMTextElement element = ObtenerElementoTextual(id);
		element.setTextContent(texto);
	}	
	
	private void actualizarFoto(String id, String uri) {
		Element element = ObtenerImagen(id);
		element.getAttributeNodeNS(
				"http://www.w3.org/1999/xlink", "href")
				.setValue(uri);
	}
	
	private void actualizarColor(String id, String colorRgb) {
		Element element = getTemplate().getElementById(id);
		element.setAttribute("fill", colorRgb);
	}
}
