package carnet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import carnet.Util;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.dao.ImagenDao;
import carnet.modelo.dao.InstructorDao;
import carnet.modelo.entidades.Instructor;

public class AbmInstructor extends JDialog {
	private static final Logger LOGGER = LogManager.getLogger();
	private ImagenDao imagenDao;
	private InstructorDao instructorDao;

	private static final long serialVersionUID = -1566439577443479682L;
	private JTextField txtApellido;
	private JTextField txtNombre;
	private JFormattedTextField txtDni;
	private JButton btnCancelar;
	private JButton btnGuardar; 
	private JButton btnSeleccionarImagen;
	private JButton btnEliminar; 
	private Instructor modelo;
	private PnlImagen pnlImagen; 
	private BufferedImage imagen;
	private ValidationPanel validador; 
	
	/**
	 * Create the panel.
	 */
	public AbmInstructor() {
		setTitle("Instuctor");
		setModal(true);
		setResizable(false);
		if (modelo == null) {
			modelo = new Instructor();
		}
		
		imagenDao = DaoManager.getInstance().getImagenDao();
		instructorDao = DaoManager.getInstance().getInstructorDao();
		InicializarVista();
		InicializarEventos();
	}

	private void InicializarEventos() {
		validador.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						btnGuardar.setEnabled(validador.getProblem() == null);
					}
				});
			}
		});
		
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guardarInstructor();
			}
		});
		
		btnSeleccionarImagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						seleccionarImagen();
					}
				});
			}
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		
		btnEliminar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void InicializarVista() {
		txtApellido = new JTextField();
		txtApellido.setName("Apellido");
		txtApellido.setColumns(10);
	    btnCancelar = new JButton("Cancelar");
		btnGuardar = new JButton("Guardar");
		
		pnlImagen = new PnlImagen();
		pnlImagen.setBackground(Color.WHITE);
		
		btnSeleccionarImagen = new JButton("Seleccionar Firma");
		JSeparator separator = new JSeparator();
		JLabel lblApellido = new JLabel("Apellido");
		JLabel lblNombre = new JLabel("Nombre");
		
		txtNombre = new JTextField();
		txtNombre.setColumns(10);
		txtNombre.setName("Nombre");
		
		txtDni = new JFormattedTextField(Util.getFormateadorDocumento());
		txtDni.setColumns(10);
		txtDni.setName("Dni");
				
		JLabel lblDni = new JLabel("Dni");
		validador = new ValidationPanel();
		
	    SwingValidationGroup group = (SwingValidationGroup) validador.getValidationGroup();
	    group.add(txtNombre, StringValidators.REQUIRE_NON_EMPTY_STRING);
	    group.add(txtApellido, StringValidators.REQUIRE_NON_EMPTY_STRING);
		group.add(txtDni, StringValidators.REQUIRE_NON_EMPTY_STRING, StringValidators.NO_WHITESPACE);
	    
		btnEliminar = new JButton("Eliminar");
		
	    
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtApellido, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
								.addComponent(txtNombre, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
								.addComponent(txtDni, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
								.addComponent(lblApellido)
								.addComponent(lblNombre)
								.addComponent(lblDni))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnSeleccionarImagen, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pnlImagen, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnEliminar)
							.addPreferredGap(ComponentPlacement.RELATED, 248, Short.MAX_VALUE)
							.addComponent(btnGuardar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancelar))
						.addComponent(separator, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(pnlImagen, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblDni, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(txtDni, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblApellido)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtApellido, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNombre)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSeleccionarImagen)
					.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancelar)
						.addComponent(btnGuardar)
						.addComponent(btnEliminar))
					.addContainerGap())
		);
		pnlImagen.setLayout(new BorderLayout(0, 0));
		getContentPane().setLayout(groupLayout);		
	}

	public ImagenDao getImagenDao() {
		return imagenDao;
	}

	public void setImagenDao(ImagenDao imagenDao) {
		this.imagenDao = imagenDao;
	}
	
	//Logica
	private boolean actualizarModelo() {
		modelo.setApellido(txtApellido.getText());
		modelo.setNombre(txtNombre.getText());
		modelo.setDni(txtDni.getText());
		return true;
	}
	
	private void actualizarVista() throws Exception {
		btnGuardar.setEnabled(false);
		btnEliminar.setVisible(modelo.getId() != 0);
		txtApellido.setText(modelo.getApellido());
		txtNombre.setText(modelo.getNombre());
		txtDni.setText(modelo.getDni());
		if (modelo.getFirmaId() != null) {
			imagen = imagenDao.getImage(modelo.getFirmaId());
		}
		else {
			imagen = null;
		}
		pnlImagen.setImage(imagen);
		pnlImagen.repaint();
	}
	
	public void setModelo(Instructor modelo) throws Exception {
		this.modelo = modelo;
		actualizarVista();
	}
	
	private void seleccionarImagen() {
		try {
			JFileChooser chooser = Util.getSeleccionadorImagenes();
			int seleccion = chooser.showDialog(AbmInstructor.this, "Abrir archivo");
			if (seleccion == JFileChooser.APPROVE_OPTION) {
				imagen = ImageIO.read(chooser.getSelectedFile());
				pnlImagen.setImage(imagen);
				pnlImagen.repaint();
			}
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error al leer el archivo", 
					"Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private void guardarInstructor() {
		UUID idFirma = null;
		try {
			if (actualizarModelo()) {
				if (validador.getProblem() == null) {
					if (imagen != null) {
						idFirma = imagenDao.guardarImagen(imagen);
						modelo.setFirmaId(idFirma);
					}
					if (modelo.getId() == 0) {
						instructorDao.agregar(modelo);
					}
					else {
						instructorDao.actualizar(modelo);
					}
					dispose();
				}
				else {
					validador.showOkCancelDialog("Error");
				}
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(AbmInstructor.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error(e.getMessage(), e);
		}
	}
		
	private void eliminar() {
		try {
			int seleccion = JOptionPane.showConfirmDialog(this, 
					"¿Está seguro que desea eliminar el instructor seleccionado?", 
					"Eliminar", JOptionPane.YES_NO_OPTION);
			if (seleccion == JOptionPane.YES_OPTION) {
				this.instructorDao.eliminar(modelo.getId());
				dispose();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error eliminar el instructor", 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
			LOGGER.error("A ocurrido un error eliminar el instructor", e);
		}
	}

}
