package carnet;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import carnet.ui.CapturadorCarnet;

public class Main {
	
	private static final String APP_CONTEXT = "META-INF/applicationContext.xml";
	private ClassPathXmlApplicationContext context;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try { 
					Main main = new Main();
					main.init();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"A ocurrido un error al inicializar la aplicación", 
							"Error", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		});
	}
	
	public Main() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
			
		} 
		catch (Exception e) {
			LOGGER.error("Error al iniciar el LaF", e);
		} 
	}
	
	public void init() throws Exception {
		context = new ClassPathXmlApplicationContext(APP_CONTEXT);
		CapturadorCarnet frame = new CapturadorCarnet();
		frame.inicializar();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				context.close();
			}
		});
		frame.setVisible(true);
	}
}
