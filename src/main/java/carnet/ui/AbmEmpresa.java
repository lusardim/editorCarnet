package carnet.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import carnet.Util;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.dao.EmpresaDao;
import carnet.modelo.dao.ImagenDao;
import carnet.modelo.entidades.Empresa;

public class AbmEmpresa extends JDialog {
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6259206101181767632L;
	private JTextField txtNombre;
	private JTextField txtDescripcion;
	private JButton btnCancelar;
	private JButton btnGuardar;
	private JButton btnEliminar; 
	private ImagenDao imagenDao;
	private ValidationPanel validador;
	private Empresa modelo;
	private EmpresaDao empresaDao;
	private PnlImagen pnlImagen;
	private BufferedImage imagen;
	private JTextField txtEmail;
	
	public AbmEmpresa() throws Exception {
		setResizable(false);
		modelo = new Empresa();
		empresaDao = DaoManager.getInstance().getEmpresaDao();
		imagenDao = DaoManager.getInstance().getImagenDao();
		inicializarVista();
		inicializarEventos();
		actualizarVista();
	}
	
	private void inicializarEventos() {
		validador.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						validar();
					}
				});
			}
		});
		
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guardar();
			}
		});
		
		btnEliminar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();		
			}
		});
		
		pnlImagen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				seleccionarImagen();
			}
		});
		
	}

	private void inicializarVista() {
		setTitle("Empresa");
		
		JLabel lblNombre = new JLabel("Nombre");
		
		txtNombre = new JTextField();
		txtNombre.setColumns(10);
		
		JLabel lblDescripcion = new JLabel("Descripci\u00F3n");
		
		txtDescripcion = new JTextField();
		txtDescripcion.setColumns(10);
		
		JSeparator separator = new JSeparator();
		btnCancelar = new JButton("Cancelar");
		btnGuardar = new JButton("Guardar");
		btnGuardar.setEnabled(false);
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setVisible(false);
		txtEmail = new JTextField();
		validador = new ValidationPanel();
		
	    SwingValidationGroup group = (SwingValidationGroup) validador.getValidationGroup();
	    group.add(txtNombre, StringValidators.REQUIRE_NON_EMPTY_STRING);
	    group.add(txtDescripcion, StringValidators.REQUIRE_NON_EMPTY_STRING);
	    group.add(txtEmail, StringValidators.EMAIL_ADDRESS);
		
		pnlImagen = new PnlImagen();
		pnlImagen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlImagen.setBackground(Color.WHITE);
		
		JLabel lblImagen = new JLabel("Imagen Inferior");
		
		JLabel lblEmail = new JLabel("Email");
		
		
				
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(separator, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(lblDescripcion)
						.addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblImagen)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnEliminar)
							.addPreferredGap(ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
							.addComponent(btnGuardar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancelar))
						.addComponent(lblNombre)
						.addComponent(txtNombre, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(txtDescripcion, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(txtEmail, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(pnlImagen, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNombre)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDescripcion)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblEmail)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblImagen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlImagen, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnEliminar)
						.addComponent(btnCancelar)
						.addComponent(btnGuardar))
					.addGap(13))
		);
		getContentPane().setLayout(groupLayout);
	}

	public void setModelo(Empresa empresa) {
		this.modelo = empresa;
		actualizarVista();
	}

	private void actualizarVista() {
		try {
			btnEliminar.setVisible(modelo.getId() != 0);
			txtDescripcion.setText(modelo.getDescripcion());
			txtNombre.setText(modelo.getNombre());
			txtEmail.setText(modelo.getEmail());
			if (modelo.getImagenInferior() != null) {
				this.imagen = imagenDao.getImage(modelo.getImagenInferior());
				this.pnlImagen.setImage(this.imagen);
				this.pnlImagen.repaint();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error al leer la imagen", 
					"Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error("A ocurrido un error al leer el archivo", e);
		}
	}

	private void actualizarModelo() {
		modelo.setNombre(txtNombre.getText().trim());
		modelo.setDescripcion(txtDescripcion.getText().trim());
		modelo.setEmail(txtEmail.getText().trim());
	}
	
	private void guardar() {
		try {
			actualizarModelo();
			UUID imagen = imagenDao.guardarImagen(this.imagen);
			modelo.setImagenInferior(imagen);
			if (modelo.getId() == 0) {
				empresaDao.agregar(modelo);
			}
			else {
				empresaDao.actualizar(modelo);
			}
			dispose();
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error al guardar la empresa", 
					"Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Ha ocurrido un error al guardar la empresa", ex);
		}	
	}

	private void eliminar() {
		int seleccion = JOptionPane.showConfirmDialog(this, 
				"¿Está seguro que desea eliminar la empresa seleccionada?", 
				"Eliminar", JOptionPane.YES_NO_OPTION);
		if (seleccion == JOptionPane.YES_OPTION) {
			if (modelo.getId() != 0) {
				empresaDao.eliminar(modelo);
			}
			dispose();
		}
	}

	private void seleccionarImagen() {
		try {
			JFileChooser chooser = Util.getSeleccionadorImagenes();
			int seleccion = chooser.showDialog(this, "Abrir archivo");
			if (seleccion == JFileChooser.APPROVE_OPTION) {
				imagen = ImageIO.read(chooser.getSelectedFile());
				pnlImagen.setImage(imagen);
				pnlImagen.repaint();
			}
			validar();
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error al leer el archivo", 
					"Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Ha ocurrido un error al leer el archivo", ex);
		}		
	}
	
	private void validar() {
		btnGuardar.setEnabled(
				validador.getProblem() == null && 
				pnlImagen.getImage() != null);
	}
	
	public EmpresaDao getEmpresaDao() {
		return empresaDao;
	}

	public void setEmpresaDao(EmpresaDao empresaDao) {
		this.empresaDao = empresaDao;
	}
}
