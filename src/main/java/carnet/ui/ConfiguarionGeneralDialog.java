package carnet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import carnet.modelo.dao.ConfiguracionDao;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.entidades.ConfiguracionGeneral;
import carnet.modelo.entidades.ConfiguracionPlantillaMail;
import carnet.modelo.entidades.ConfiguracionServidorCorreo;
import carnet.modelo.entidades.PeriodoVencimiento;
import carnet.modelo.entidades.ProtocoloSeguridadEmail;
import carnet.servicios.ServicioMail;
public class ConfiguarionGeneralDialog extends JDialog {
	private static final Logger LOGGER = LogManager.getLogger(ConfiguarionGeneralDialog.class);
	private static final long serialVersionUID = -961198409870809692L;
	
	private ConfiguracionDao configuracionDao;
	private ServicioMail servicioMail;
	private JTextField txtTituloCarnet;
	private JTextField txtNota;
	private JComboBox<PeriodoVencimiento> cboPeriodoDefecto;
	private JPanel pnlSeleccionColor;
	private JButton btnGuardar;
	private JButton btnCancelar;
	private ValidationPanel validador; 
	private ConfiguracionGeneral configuracion;
	private JTextField txtNombreEmpresa;
	private JTextField txtDescripcion;

	//Email
	private JComboBox<ProtocoloSeguridadEmail> cboTipoAutenticacion;
	private JTextField txtNombreCuenta;
	private JTextField txtNombreUsuario;
	private JPasswordField txtClave;
	private JSpinner spPuerto;
	private JTextField txtServidor;
	private JButton btnTest;
	private JTextField txtUrl;
	/**
	 * Create the dialog.
	 */
	public ConfiguarionGeneralDialog() {
		configuracionDao = DaoManager.getInstance().getConfiguracionDao();
		servicioMail = DaoManager.getInstance().getServicioMail();
		inicializarVista();
		inicializarValidadores();
		inicializarEventos();
		actualizarVista();
		refrescarListaVencimientos();
	}
	
	private void inicializarEventos() {
		
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

		btnTest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enviarEmailPrueba();
			}
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guardar();
			}
		});
		
		pnlSeleccionColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seleccionarColor();
			}
		});
		
		cboTipoAutenticacion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cambiarTipoAutenticacion();
			}
		});
	}

	private void cambiarTipoAutenticacion() {
		ProtocoloSeguridadEmail tipoAutenticacion = (ProtocoloSeguridadEmail) cboTipoAutenticacion.getSelectedItem();
		spPuerto.setValue(tipoAutenticacion == null ? 0 : tipoAutenticacion.getPuertoDefecto());
	}
	
	private void actualizarModelo() {
		configuracion.setTituloCarnet(txtTituloCarnet.getText());
		configuracion.setNotaDefecto(txtNota.getText());
		configuracion.setPeriodoDefecto((PeriodoVencimiento) cboPeriodoDefecto.getSelectedItem());
		Color color = pnlSeleccionColor.getBackground();
		configuracion.setColorDefecto(color);
		configuracion.setNombreEmpresa(txtNombreEmpresa.getText().trim());
		configuracion.setDescripcionEmpresa(txtDescripcion.getText().trim());
		
		ConfiguracionServidorCorreo configuracionEmail = configuracion.getConfiguracionEmail();
		if (configuracionEmail == null) {
			configuracionEmail = new ConfiguracionServidorCorreo();
			configuracion.setConfiguracionEmail(configuracionEmail);
		}
		configuracionEmail.setHostName(txtServidor.getText().trim());
		configuracionEmail.setPassword(new String(txtClave.getPassword()));
		configuracionEmail.setProtocolo((ProtocoloSeguridadEmail) cboTipoAutenticacion.getSelectedItem());
		configuracionEmail.setPort((int) spPuerto.getValue());
		configuracionEmail.setUserName(txtNombreUsuario.getText().trim());
		
		ConfiguracionPlantillaMail template = configuracion.getTemplate();
		if (template == null) {
			template = new ConfiguracionPlantillaMail();
			configuracion.setTemplate(template);
		}
		template.setOrigen(txtNombreCuenta.getText().trim());
	}

	private void actualizarVista() {
		this.configuracion = configuracionDao.getConfiguracionGeneral();
		txtTituloCarnet.setText(configuracion.getTituloCarnet());
		txtNota.setText(configuracion.getNotaDefecto());
		pnlSeleccionColor.setBackground(configuracion.getColor());
		cboPeriodoDefecto.setSelectedItem(configuracion.getPeriodoDefecto());
		txtNombreEmpresa.setText(configuracion.getNombreEmpresa());
		txtDescripcion.setText(configuracion.getDescripcionEmpresa());

		//Configuracion mail
		ConfiguracionServidorCorreo configuracionEmail = configuracion.getConfiguracionEmail();
		ConfiguracionPlantillaMail template = configuracion.getTemplate();
		if (configuracionEmail != null) {
			cboTipoAutenticacion.setSelectedItem(configuracionEmail.getProtocolo());
			txtClave.setText(configuracionEmail.getPassword());
			txtNombreUsuario.setText(configuracionEmail.getUserName());
			spPuerto.setValue(configuracionEmail.getPort());
			txtServidor.setText(configuracionEmail.getHostName());
		}
		
		if (template != null) {
			txtNombreCuenta.setText(template.getOrigen());
		}
	}
	
	private void guardar() {
		actualizarModelo();
		configuracionDao.guardar(configuracion);
		dispose();
	}

	private void seleccionarColor() {
		Color nuevoColor = JColorChooser.showDialog(this, 
				"Color carnet",
				pnlSeleccionColor.getBackground());
		if (nuevoColor != null) {
			pnlSeleccionColor.setBackground(nuevoColor);
		}
	}

	private void inicializarVista() {
		setTitle("Configuracion General");
		setModal(true);
		setBounds(100, 100, 478, 526);
		
		validador = new ValidationPanel();
		
		JTabbedPane tp = new JTabbedPane();
		getContentPane().add(tp, BorderLayout.CENTER);		
		JLabel lblTituloCarnet = new JLabel("Titulo Carnet");
		txtTituloCarnet = new JTextField();
		lblTituloCarnet.setLabelFor(txtTituloCarnet);
		txtTituloCarnet.setColumns(10);
		
		JLabel lblNota = new JLabel("Nota por defecto");
		
		txtNota = new JTextField();
		lblNota.setLabelFor(txtNota);
		txtNota.setColumns(10);
		
		JLabel label = new JLabel("Color por defecto");
		
		pnlSeleccionColor = new JPanel();
		pnlSeleccionColor.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JLabel lblPeriodoVencimientoPor = new JLabel("Periodo vencimiento por defecto");
		
		cboPeriodoDefecto = new JComboBox<PeriodoVencimiento>();
		lblPeriodoVencimientoPor.setLabelFor(cboPeriodoDefecto);
		
		JLabel lblNombreDeLa = new JLabel("Nombre de la empresa");
		
		txtNombreEmpresa = new JTextField();
		lblNombreDeLa.setLabelFor(txtNombreEmpresa);
		txtNombreEmpresa.setColumns(10);
		
		JLabel lblDescripcinDeLa = new JLabel("Descripci\u00F3n de la empresa");
		
		txtDescripcion = new JTextField();
		lblDescripcinDeLa.setLabelFor(txtDescripcion);
		txtDescripcion.setColumns(10);
		JPanel pnlConfiguracionGeneral = new JPanel();
		
		GroupLayout groupLayout = new GroupLayout(pnlConfiguracionGeneral);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNombreDeLa)
							.addContainerGap(340, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblDescripcinDeLa)
							.addContainerGap(323, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtNombreEmpresa, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtDescripcion, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTituloCarnet)
							.addContainerGap(385, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtTituloCarnet, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNota)
							.addContainerGap(365, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtNota, 96, 96, 96)
							.addContainerGap(351, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(label, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(363, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(pnlSeleccionColor, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(417, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(cboPeriodoDefecto, Alignment.LEADING, 0, 155, Short.MAX_VALUE)
								.addComponent(lblPeriodoVencimientoPor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(307))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNombreDeLa)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNombreEmpresa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDescripcinDeLa)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTituloCarnet)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtTituloCarnet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNota)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNota, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPeriodoVencimientoPor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cboPeriodoDefecto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlSeleccionColor, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(139, Short.MAX_VALUE))
		);
		
		pnlConfiguracionGeneral.setLayout(groupLayout);
		tp.add(pnlConfiguracionGeneral);
		tp.setTitleAt(0, "Configuraci\u00F3n General");
		tp.setEnabledAt(0, true);
		
		JPanel panel = new JPanel();
		tp.addTab("Configuraci\u00F3n Email", null, panel, null);
		
		JLabel lblNombreCuenta = new JLabel("Nombre de la cuenta");
		
		txtNombreCuenta = new JTextField();
		txtNombreCuenta.setColumns(10);
		
		JLabel lblAutenticacion = new JLabel("Tipo de Autenticaci\u00F3n");
		
		cboTipoAutenticacion = new JComboBox<ProtocoloSeguridadEmail>();
		cboTipoAutenticacion.setModel(new DefaultComboBoxModel<ProtocoloSeguridadEmail>(ProtocoloSeguridadEmail.values()));
		
		JLabel lblPuerto = new JLabel("Puerto");
		
		spPuerto = new JSpinner();
		spPuerto.setModel(new SpinnerNumberModel(0, 0, 65535, 1));
		
		JLabel lblUsuario = new JLabel("Nombre de usuario");
		
		txtNombreUsuario = new JTextField();
		txtNombreUsuario.setColumns(10);
		
		JLabel lblClave = new JLabel("Clave");
		
		txtClave = new JPasswordField();
		
		JLabel lblServidor = new JLabel("Servidor");
		
		txtServidor = new JTextField();
		txtServidor.setColumns(10);
		
		JPanel pnlTest = new JPanel();
		pnlTest.setBorder(new TitledBorder(null, "Test de configuraci\u00F3n de correo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(pnlTest, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addComponent(lblServidor, Alignment.LEADING)
						.addComponent(lblNombreCuenta, Alignment.LEADING)
						.addComponent(lblAutenticacion, Alignment.LEADING)
						.addComponent(cboTipoAutenticacion, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPuerto, Alignment.LEADING)
						.addComponent(spPuerto, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUsuario, Alignment.LEADING)
						.addComponent(lblClave, Alignment.LEADING)
						.addComponent(txtNombreUsuario, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addComponent(txtNombreCuenta, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addComponent(txtServidor, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addComponent(txtClave, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblServidor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtServidor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNombreCuenta)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNombreCuenta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblAutenticacion)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cboTipoAutenticacion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addComponent(lblPuerto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spPuerto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblUsuario)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNombreUsuario, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblClave)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtClave, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlTest, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JLabel lblDireccion = new JLabel("Direcci\u00F3n");
		
		txtUrl = new JTextField();
		txtUrl.setColumns(10);
		
		btnTest = new JButton("Verificar configuraci\u00F3n de correo");
		GroupLayout gl_pnlTest = new GroupLayout(pnlTest);
		gl_pnlTest.setHorizontalGroup(
			gl_pnlTest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTest.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlTest.createParallelGroup(Alignment.LEADING)
						.addComponent(txtUrl, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
						.addComponent(lblDireccion)
						.addComponent(btnTest))
					.addContainerGap())
		);
		gl_pnlTest.setVerticalGroup(
			gl_pnlTest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTest.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDireccion)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTest)
					.addContainerGap(22, Short.MAX_VALUE))
		);
		pnlTest.setLayout(gl_pnlTest);
		panel.setLayout(gl_panel);
		tp.setEnabledAt(1, true);
		
		JPanel pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		btnCancelar = new JButton("Cancelar");
		pnlBottom.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		btnGuardar = new JButton("Guardar");
		btnGuardar.setEnabled(false);
		pnlBottom.add(btnGuardar);
		pnlBottom.add(btnCancelar);
	}

	@SuppressWarnings("unchecked")
	private void inicializarValidadores() {
		SwingValidationGroup group = (SwingValidationGroup) validador.getValidationGroup();
		group.add(txtTituloCarnet, StringValidators.REQUIRE_NON_EMPTY_STRING);
		group.add(txtNota, StringValidators.REQUIRE_NON_EMPTY_STRING, StringValidators.NO_WHITESPACE);
		group.add(txtDescripcion, StringValidators.REQUIRE_NON_EMPTY_STRING);
		group.add(txtTituloCarnet, StringValidators.REQUIRE_NON_EMPTY_STRING);
		group.add(txtNombreEmpresa, StringValidators.REQUIRE_NON_EMPTY_STRING);
	}
	
	private void refrescarListaVencimientos() {
		List<PeriodoVencimiento> obtenidos = DaoManager.getInstance().getPeriodoVencimientoDao().getAll();
		PeriodoVencimiento[] vencimientos = obtenidos.toArray(new PeriodoVencimiento[obtenidos.size()]); 
		cboPeriodoDefecto.setModel(new DefaultComboBoxModel<PeriodoVencimiento>(vencimientos));
		cboPeriodoDefecto.setSelectedItem(configuracion.getPeriodoDefecto());
	}
	
	private void enviarEmailPrueba() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					actualizarModelo();
					String direccion = txtUrl.getText();
					servicioMail.enviarMailPrueba(direccion, configuracion.getConfiguracionEmail());
				} 
				catch (Exception e) {
					JOptionPane.showMessageDialog(ConfiguarionGeneralDialog.this, 
							"Ha ocurrido un error al intentar enviar el correo", 
							"Error", JOptionPane.ERROR_MESSAGE);
					LOGGER.error("A ocurrido un error al intentar enviar el correo de prueba", e);
					e.printStackTrace();
				}
			}
		});
	}
}
