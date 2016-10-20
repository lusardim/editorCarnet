package carnet;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	private static final Logger LOGGER = LogManager.getLogger();
	private static Font fontAwsome;
		
	public static Font getFontAwsome() {
		if (fontAwsome == null) {
			  try (InputStream inputStream = Util.class.getResourceAsStream("fontawesome-webfont.ttf")) {
				  fontAwsome = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				  fontAwsome = fontAwsome.deriveFont(Font.PLAIN, 12f);
			  }
			  catch (IOException | FontFormatException exp) {
				  LOGGER.error(exp.getMessage(), exp);
				  throw new RuntimeException(exp);
			  }
		}
		return fontAwsome;
	}
	
	public static MaskFormatter getFormateadorDocumento() {
		try {
			MaskFormatter documentoFormat = new MaskFormatter("*#.###.###");
			documentoFormat.setValueContainsLiteralCharacters(false);
			
			return documentoFormat;
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static JFileChooser getSeleccionadorImagenes() {
		JFileChooser chooser = new JFileChooser();
		String[] tiposSoportados = ImageIO.getReaderFileSuffixes();
		for (String tipo : tiposSoportados) {
			FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos " + tipo, tipo);
			chooser.addChoosableFileFilter(filtro);
		}
		return chooser;
	}
	
	public static String getCodigoMas() {
		return "\uf067";
	}
	
	public static String getCodigoLapiz() {
		return "\uf040";
	}
}
