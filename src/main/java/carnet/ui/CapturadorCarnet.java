package carnet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.batik.bridge.UpdateManagerAdapter;
import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import carnet.Util;
import carnet.modelo.dao.CarnetDao;
import carnet.modelo.dao.ConfiguracionDao;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.dao.EmpresaDao;
import carnet.modelo.dao.ImagenDao;
import carnet.modelo.dao.InstructorDao;
import carnet.modelo.dao.PeriodoVencimientoDao;
import carnet.modelo.entidades.Afiliado;
import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.ConfiguracionGeneral;
import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Instructor;
import carnet.modelo.entidades.PeriodoVencimiento;
import carnet.templates.TemplateBackHandler;
import carnet.templates.TemplateFrontHandler;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class CapturadorCarnet extends JFrame {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final long serialVersionUID = 6949727885067046587L;
	private JPanel contentPane;
	private JPanel pnlCaptura;
	private JPanel pnlResultado;
	private JPanel pnlSeleccionColor;
	private JFormattedTextField txtDni;
	private JTextField txtNombre;
	private JTextField txtApellido;
	private JTextField txtNota;
	private JComboBox<Instructor> cboInstructor;
	private JComboBox<Empresa> cboEmpresa;
	private JComboBox<PeriodoVencimiento> cboPeriodo;
	private JXDatePicker dpOtorgamiento;
	private JComboBox<Webcam> cboCamara;
	private Webcam camara;
	
	private TemplateBackHandler back;
	private TemplateFrontHandler front;

	//Daos
	private InstructorDao instructorDao;
	private EmpresaDao empresaDao;
	private ImagenDao imagenDao;
	private ConfiguracionDao configuracionDao;
	private PeriodoVencimientoDao periodoVencimientoDao;
	private CarnetDao carnetDao;
	
	//botones
	private JButton btnAgregarEmpresa;
	private JButton btnModificarInstructor;
	private JButton btnModificarEmpresa;
	private JButton btnCapturar;
	private JButton btnAgregarInstructor; 
	private JButton btnAgregarVencimiento;
	private JButton btnModificarVencimiento;
	private JButton btnConfiguracionGeneral; 
	private JButton btnAgregar;
	private JButton btnVerLista;
	private JButton btnAbrirImagen;
	
	//Validador
	private ValidationPanel validador;
	//Modelo
	private Carnet modelo;
	private JButton btnPlantillaDeCorreos;
	
	public CapturadorCarnet() throws Exception {
		setTitle("Generador de Carnets");
		modelo = new Carnet();
		this.setLocationRelativeTo(null);
		inicializarVista();
	}
	
	private void inicializarDaos() {
		instructorDao = DaoManager.getInstance().getInstructorDao();
		empresaDao = DaoManager.getInstance().getEmpresaDao();
		imagenDao = DaoManager.getInstance().getImagenDao();
		configuracionDao = DaoManager.getInstance().getConfiguracionDao();
		periodoVencimientoDao = DaoManager.getInstance().getPeriodoVencimientoDao();
		carnetDao = DaoManager.getInstance().getCarnetDao();
	}

	public void inicializar() throws Exception {
		inicializarVista();
		inicializarDaos();
		inicializarTemplates();
		inicializarWebCam();
		
		inicializarPanelesCarnet();
		inicializarEventos();
		inicializarModelos();
	}
	
	private void inicializarModelos() {
		refrescarListaInstructores();
		refrescarListaEmpresas();
		refrescarListaVencimientos();
	}

	private void inicializarTemplates() {
		try {
			back = new TemplateBackHandler();
			back.leerTemplate();
			front = new TemplateFrontHandler();
			front.leerTemplate();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void inicializarEventos() {
		validador.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				validar();
			}
		});
		
		btnVerLista.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarVerLista();
			}
		});
		
		btnAgregar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guardar();
			}
		});
		
		btnCapturar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)  {
				capturarImagen();
			}
		});
		
		btnAbrirImagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abrirImagen();
				
			}
		});
		
		btnAgregarInstructor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				agregarInstructor();
			}
		});
		
		btnModificarInstructor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				modificarInstructor();
			}
		});
		
		btnAgregarEmpresa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agregarEmpresa();
			}
		});
		
		btnModificarEmpresa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modificarEmpresa();
			}
		});
		
		btnAgregarVencimiento.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agregarVencimiento();
			}
		});
		
		btnModificarVencimiento.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modificarVencimiento();
			}
		});
		
		btnPlantillaDeCorreos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarPlantillaCorreo();
			}
		});
		
		cboEmpresa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarEmpresa();
				actualizarImagenInferior();
				validar();
			}
		});
		
		cboPeriodo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarPeriodoVencimiento();
				validar();
			}
		});
		
		cboInstructor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarInstructor();
				validar();
			}
		});
		
		btnConfiguracionGeneral.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modificarConfiguracion();
			}
		});
		
		pnlSeleccionColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seleccionarColor();
			}
		});
		
		dpOtorgamiento.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarPeriodoVencimiento();
				validar();
			}
		});
		
		txtDni.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateDni();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateDni();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateDni();
			}
			
			private void updateDni() {
				back.setDni(txtDni.getText());
			}
		});
		
		txtNombre.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateNombre();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateNombre();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateNombre();
			}
			
			public void updateNombre() {
				back.setNombre(txtNombre.getText());
			}
		});
		
		txtApellido.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateApellido();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateApellido();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateApellido();
			}
			
			public void updateApellido() {
				back.setApellido(txtApellido.getText());
			}
		});
		
		txtNota.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateNota();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateNota();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateNota();
			}
			
			public void updateNota() {
				front.setCalificacion(txtNota.getText().toUpperCase());
			}
		});
	}
	
	private void actualizarConfiguracionFront(ConfiguracionGeneral configuracion) {
		front.setColorNota(configuracion.getColor());
		front.setColorTitulo(configuracion.getColor());
		front.setTituloCarnet(configuracion.getTituloCarnet());
		front.setTituloEmpresa(configuracion.getNombreEmpresa());
		front.setTituloDescripcion(configuracion.getDescripcionEmpresa());
	}
	
	private void actualizarConfiguracionBack(ConfiguracionGeneral configuracion) {
		back.setColorRecuadro(configuracion.getColor());
		back.setTituloCarnet(configuracion.getTituloCarnet());
		if (configuracion.getPeriodoDefecto() != null) {
			cboPeriodo.setSelectedItem(configuracion.getPeriodoDefecto());
		}
	}
	
	private void actualizarConfiguracion() {
		ConfiguracionGeneral configuracion = configuracionDao.getConfiguracionGeneral();
		txtNota.setText(configuracion.getNotaDefecto());
		pnlSeleccionColor.setBackground(configuracion.getColor());
		actualizarConfiguracionBack(configuracion);
		actualizarConfiguracionFront(configuracion);
	}
	
	private void refrescarListaInstructores() {
		List<Instructor> obtenidos = instructorDao.getAll();
		Instructor[] instructores = obtenidos.toArray(new Instructor[obtenidos.size()]); 
		cboInstructor.setModel(new DefaultComboBoxModel<Instructor>(instructores));
	}
	
	private void refrescarListaEmpresas() {
		List<Empresa> obtenidos = empresaDao.getAll();
		Empresa[] empresas = obtenidos.toArray(new Empresa[obtenidos.size()]); 
		cboEmpresa.setModel(new DefaultComboBoxModel<Empresa>(empresas));
	}
	
	private void refrescarListaVencimientos() {
		List<PeriodoVencimiento> obtenidos = periodoVencimientoDao.getAll();
		PeriodoVencimiento[] vencimientos = obtenidos.toArray(new PeriodoVencimiento[obtenidos.size()]); 
		cboPeriodo.setModel(new DefaultComboBoxModel<PeriodoVencimiento>(vencimientos));		
	}
	
	private void capturarImagen() {
		try { 
			BufferedImage image = camara.getImage();
			UUID uuid = imagenDao.guardarImagen(image);
			File archivo = imagenDao.getFileImagen(uuid);
			back.setFoto(archivo);
			modelo.setFoto(uuid);
			validar();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error(e.getMessage(), e);
		}
	}
	

	private void abrirImagen() {
		try {
			JFileChooser chooser = Util.getSeleccionadorImagenes();
			int seleccion = chooser.showDialog(this, "Abrir archivo");
			if (seleccion == JFileChooser.APPROVE_OPTION) {
				BufferedImage imagen = ImageIO.read(chooser.getSelectedFile());
				UUID foto = imagenDao.guardarImagen(imagen);
				File archivo = imagenDao.getFileImagen(foto);
				back.setFoto(archivo);
				modelo.setFoto(foto);
				validar();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error(e.getMessage(), e);
		}
	}

	
	private void inicializarPanelesCarnet() throws Exception {
		pnlResultado.setLayout(new BoxLayout(pnlResultado, BoxLayout.Y_AXIS));
		pnlResultado.add(this.front.getCanvas());
		pnlResultado.add(this.back.getCanvas());
		final ConfiguracionGeneral configuracion = configuracionDao.getConfiguracionGeneral();
		
		this.front.getCanvas().addUpdateManagerListener(new UpdateManagerAdapter() {
			@Override
			public void managerStarted(UpdateManagerEvent arg0) {
				actualizarInstructor();
				actualizarEmpresa();
				actualizarConfiguracionFront(configuracion);
				actualizarPeriodoVencimiento();
			}
		});
		
		this.back.getCanvas().addUpdateManagerListener(new UpdateManagerAdapter() {
			@Override
			public void managerStarted(UpdateManagerEvent arg0) {
				actualizarConfiguracionBack(configuracion);
				actualizarImagenInferior();
			}
		});
	}

	private void inicializarWebCam() {
		if (Webcam.getDefault() != null) {
			camara = Webcam.getDefault();
			WebcamPanel pnl = new WebcamPanel(camara, WebcamResolution.QQVGA.getSize(), true);
			pnl.setBackground(Color.WHITE);
			pnl.setFillArea(true);
			pnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			pnl.setAlignmentX(CENTER_ALIGNMENT);
			pnl.setAlignmentY(CENTER_ALIGNMENT);
			pnlCaptura.add(pnl, BorderLayout.CENTER);
			
			List<Webcam> webCams = Webcam.getWebcams();
			ComboBoxModel<Webcam> model = new DefaultComboBoxModel<Webcam>(webCams.toArray(new Webcam[0]));
			cboCamara.setModel(model);
		}
	}

	@SuppressWarnings("unchecked")
	private void inicializarVista() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 876, 743);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel pnlInformacionPersonal = new JPanel();
		pnlInformacionPersonal.setBorder(new TitledBorder(null, "Informaci\u00F3n Personal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel pnlDatosCarnet = new JPanel();
		pnlDatosCarnet.setBorder(new TitledBorder(null, "Datos Carnet", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlResultado = new JPanel();
		pnlResultado.setBorder(new TitledBorder(null, "Resultado", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		btnAgregar = new JButton("Agregar a lista de impresi\u00F3n");
		btnAgregar.setEnabled(false);
		btnVerLista = new JButton("Ver Lista");
		
		btnConfiguracionGeneral = new JButton("Configuraci\u00F3n General");
		
		JSeparator separator = new JSeparator();
		
		btnPlantillaDeCorreos = new JButton("Plantilla de correos");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(pnlInformacionPersonal, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(pnlResultado, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
						.addComponent(pnlDatosCarnet, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
					.addGap(3))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(btnConfiguracionGeneral)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPlantillaDeCorreos)
					.addPreferredGap(ComponentPlacement.RELATED, 266, Short.MAX_VALUE)
					.addComponent(btnVerLista, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAgregar)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(separator, GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(pnlInformacionPersonal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(pnlDatosCarnet, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
							.addGap(2)
							.addComponent(pnlResultado, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAgregar)
						.addComponent(btnVerLista)
						.addComponent(btnConfiguracionGeneral)
						.addComponent(btnPlantillaDeCorreos))
					.addContainerGap())
		);
		
		JLabel lblInstructor = new JLabel("Instructor");
		
		cboInstructor = new JComboBox<Instructor>();
		JLabel lblEmpresa = new JLabel("Empresa");
		
		cboEmpresa = new JComboBox<Empresa>();
		
		JLabel lblOtorgamiento = new JLabel("Otorgamiento");
		
		dpOtorgamiento = new JXDatePicker();
		dpOtorgamiento.setDate(Calendar.getInstance().getTime());
		
		JLabel lblVencimiento = new JLabel("Vencimiento");
		
		btnAgregarInstructor = new JButton(Util.getCodigoMas());
		btnAgregarInstructor.setFont(Util.getFontAwsome());
		
		btnAgregarEmpresa = new JButton(Util.getCodigoMas());
		btnAgregarEmpresa.setFont(Util.getFontAwsome());
		
		btnModificarInstructor = new JButton(Util.getCodigoLapiz());
		btnModificarInstructor.setFont(Util.getFontAwsome());
		
		btnModificarEmpresa = new JButton(Util.getCodigoLapiz());
		btnModificarEmpresa.setFont(Util.getFontAwsome());
		
		cboPeriodo = new JComboBox<PeriodoVencimiento>();
		
		btnAgregarVencimiento = new JButton(Util.getCodigoMas());
		btnAgregarVencimiento.setFont(Util.getFontAwsome());
		
		btnModificarVencimiento = new JButton(Util.getCodigoLapiz());
		btnModificarVencimiento.setFont(Util.getFontAwsome());
		btnModificarVencimiento.setEnabled(false);
		GroupLayout gl_pnlDatosCarnet = new GroupLayout(pnlDatosCarnet);
		gl_pnlDatosCarnet.setHorizontalGroup(
			gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDatosCarnet.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblInstructor)
						.addComponent(lblEmpresa)
						.addComponent(cboInstructor, 0, 167, Short.MAX_VALUE)
						.addComponent(cboEmpresa, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnAgregarEmpresa, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnAgregarInstructor, GroupLayout.PREFERRED_SIZE, 38, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnModificarEmpresa, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnModificarInstructor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING)
						.addComponent(lblVencimiento)
						.addComponent(lblOtorgamiento)
						.addComponent(dpOtorgamiento, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_pnlDatosCarnet.createSequentialGroup()
							.addComponent(cboPeriodo, 0, 148, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAgregarVencimiento, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnModificarVencimiento, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_pnlDatosCarnet.setVerticalGroup(
			gl_pnlDatosCarnet.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDatosCarnet.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblInstructor)
						.addComponent(lblOtorgamiento))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.BASELINE)
						.addComponent(cboInstructor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dpOtorgamiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAgregarInstructor, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnModificarInstructor, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmpresa)
						.addComponent(lblVencimiento))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDatosCarnet.createParallelGroup(Alignment.BASELINE)
						.addComponent(cboEmpresa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAgregarEmpresa, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnModificarEmpresa, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(cboPeriodo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnModificarVencimiento, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAgregarVencimiento, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(21, Short.MAX_VALUE))
		);
		pnlDatosCarnet.setLayout(gl_pnlDatosCarnet);
		
		
		pnlCaptura = new JPanel();
		pnlCaptura.setBackground(Color.WHITE);
		
		btnCapturar = new JButton("Capturar Im\u00E1gen");
		
		JLabel lblDni = new JLabel("DNI");
		
		txtDni = new JFormattedTextField(Util.getFormateadorDocumento());;
		
		lblDni.setLabelFor(txtDni);
		txtDni.setColumns(10);
		
		JLabel lblNombre = new JLabel("Nombre");
		
		txtNombre = new JTextField();
		lblNombre.setLabelFor(txtNombre);
		txtNombre.setColumns(10);
		
		JLabel lblApellido = new JLabel("Apellidos");
		
		txtApellido = new JTextField();
		lblApellido.setLabelFor(txtApellido);
		txtApellido.setColumns(10);
		
		cboCamara = new JComboBox<Webcam>();
		
		JLabel lblNota = new JLabel("Nota");
		
		txtNota = new JTextField();
		txtNota.setText("A");
		txtNota.setColumns(10);
		
		pnlSeleccionColor = new JPanel();
		pnlSeleccionColor.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSeleccionColor.setBackground(new Color(66,174,129));
		
		btnAbrirImagen = new JButton("Abrir Imagen");
		
		GroupLayout gl_pnlInformacionPersonal = new GroupLayout(pnlInformacionPersonal);
		gl_pnlInformacionPersonal.setHorizontalGroup(
			gl_pnlInformacionPersonal.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlInformacionPersonal.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addGroup(gl_pnlInformacionPersonal.createParallelGroup(Alignment.LEADING)
								.addComponent(cboCamara, 0, 250, Short.MAX_VALUE)
								.addComponent(pnlCaptura, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
									.addComponent(btnCapturar)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnAbrirImagen, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
							.addGap(24))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(lblDni)
							.addContainerGap(256, Short.MAX_VALUE))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(txtDni, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
							.addGap(24))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(lblNombre)
							.addContainerGap(237, Short.MAX_VALUE))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(txtNombre, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
							.addGap(24))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(lblApellido)
							.addContainerGap(232, Short.MAX_VALUE))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(lblNota)
							.addContainerGap(251, Short.MAX_VALUE))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(txtNota, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pnlSeleccionColor, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
							.addGap(159))
						.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
							.addComponent(txtApellido, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
							.addGap(24))))
		);
		gl_pnlInformacionPersonal.setVerticalGroup(
			gl_pnlInformacionPersonal.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlInformacionPersonal.createSequentialGroup()
					.addGap(9)
					.addComponent(cboCamara, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlCaptura, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlInformacionPersonal.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCapturar)
						.addComponent(btnAbrirImagen))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDni)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtDni, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNombre)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblApellido)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtApellido, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNota)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlInformacionPersonal.createParallelGroup(Alignment.LEADING, false)
						.addComponent(pnlSeleccionColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(txtNota, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(202, Short.MAX_VALUE))
		);
		pnlCaptura.setLayout(new BorderLayout(0, 0));
		pnlInformacionPersonal.setLayout(gl_pnlInformacionPersonal);
		contentPane.setLayout(gl_contentPane);
		
		validador = new ValidationPanel();
		
	    SwingValidationGroup group = (SwingValidationGroup) validador.getValidationGroup();
	    group.add(txtNombre, StringValidators.REQUIRE_NON_EMPTY_STRING);
	    group.add(txtApellido, StringValidators.REQUIRE_NON_EMPTY_STRING);
		group.add(txtDni, StringValidators.REQUIRE_NON_EMPTY_STRING, StringValidators.NO_WHITESPACE);
		group.add(txtNota, StringValidators.REQUIRE_NON_EMPTY_STRING);
	}

	
	private void agregarInstructor() {
		try {
			mostrarAbmInstructor(new Instructor());
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void agregarEmpresa() {
		try {
			mostrarAbmEmpresa(new Empresa());
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void agregarVencimiento() {
		AbmPeriodo periodo = new AbmPeriodo();
		periodo.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				refrescarListaVencimientos();
				actualizarPeriodoVencimiento();
			}
		});
		periodo.setModelo(new PeriodoVencimiento());
		periodo.pack();
		periodo.setLocationRelativeTo(CapturadorCarnet.this);
		periodo.setVisible(true);
	}

	private void modificarVencimiento() {
		PeriodoVencimiento vencimiento = (PeriodoVencimiento) cboPeriodo.getSelectedItem();
		AbmPeriodo periodo = new AbmPeriodo();
		periodo.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				refrescarListaVencimientos();
				actualizarPeriodoVencimiento();
			}
		});
		periodo.setModelo(vencimiento);
		periodo.pack();
		periodo.setLocationRelativeTo(CapturadorCarnet.this);
		periodo.setVisible(true);
	}
	
	private void actualizarInstructor() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try { 
					if (cboInstructor.getSelectedItem() != null) {
						btnModificarInstructor.setEnabled(true);
						Instructor instructor = (Instructor) cboInstructor.getSelectedItem();
						if (instructor != null) {
							front.setInstructor(instructor.toString());
							if (instructor.getFirmaId() != null) {
								File imagen = imagenDao.getFileImagen(instructor.getFirmaId());
								if (imagen != null) {
									front.setImagenFirma(imagen);
								}
								else {
									LOGGER.error("Error, la imagen " + instructor.getFirmaId() + "no existe");
								}
							}
						}
					}
					else {
						btnModificarInstructor.setEnabled(false);
					}
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					JOptionPane.showMessageDialog(CapturadorCarnet.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private void actualizarImagenInferior() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try { 
					if (cboEmpresa.getSelectedItem() != null) {
						Empresa empresa = (Empresa)cboEmpresa.getSelectedItem();
						if (empresa.getImagenInferior() != null) {
							File fileImagen = imagenDao.getFileImagen(empresa.getImagenInferior());
							back.actualizarImagenInferior(fileImagen);
						}
					}
					else {
						back.actualizarImagenInferior(null);
					}
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					JOptionPane.showMessageDialog(CapturadorCarnet.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private void modificarConfiguracion() {
		ConfiguarionGeneralDialog configuracionGeneralDialog = new ConfiguarionGeneralDialog();
		configuracionGeneralDialog.pack();
		configuracionGeneralDialog.setLocationRelativeTo(CapturadorCarnet.this);
		configuracionGeneralDialog.setVisible(true);
		configuracionGeneralDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						actualizarConfiguracion();
					}
				});
			}
		});
	}
	
	private void actualizarEmpresa() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (cboEmpresa.getSelectedItem() != null) {
						btnModificarEmpresa.setEnabled(true);
						Empresa empresa = (Empresa) cboEmpresa.getSelectedItem();
						front.setEmpresa(empresa.getNombre());
					}
					else {
						btnModificarEmpresa.setEnabled(false);
					}
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					JOptionPane.showMessageDialog(CapturadorCarnet.this,
							ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void actualizarPeriodoVencimiento() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (cboPeriodo.getSelectedItem() != null) {
						btnModificarVencimiento.setEnabled(true);
						PeriodoVencimiento periodo = (PeriodoVencimiento) cboPeriodo.getSelectedItem();
						if (dpOtorgamiento.getDate() != null) {
							Calendar calendario = Calendar.getInstance();
							calendario.setTime(dpOtorgamiento.getDate());
							Integer anio = calendario.get(Calendar.YEAR);
							front.setAnio(anio.toString());
							front.setFechaOtorgamiento(dpOtorgamiento.getDate());
							front.setFechaVencimiento(periodo.obtenerVencimiento(dpOtorgamiento.getDate()));
						}
					}
					else {
						btnModificarVencimiento.setEnabled(false);
					}
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					JOptionPane.showMessageDialog(CapturadorCarnet.this,
							ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
	}
	
	private void modificarInstructor() {
		try {
			if (cboInstructor.getSelectedItem() != null) {
				Instructor instructor = (Instructor) cboInstructor.getSelectedItem();
				mostrarAbmInstructor(instructor);
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(CapturadorCarnet.this, 
					e.getMessage(),
					"Error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void modificarEmpresa() {
		try {
			if (cboEmpresa.getSelectedItem() != null) {
				Empresa empresa = (Empresa) cboEmpresa.getSelectedItem();
				mostrarAbmEmpresa(empresa);
				refrescarListaEmpresas();
				actualizarEmpresa();
				actualizarImagenInferior();
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(CapturadorCarnet.this, 
					e.getMessage(),
					"Error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void mostrarAbmEmpresa(Empresa empresa) throws Exception {
		AbmEmpresa abmEmpresaController = new AbmEmpresa();
		abmEmpresaController.setModelo(empresa);
		abmEmpresaController.pack();
		abmEmpresaController.setLocationRelativeTo(CapturadorCarnet.this);
		abmEmpresaController.setVisible(true);
		abmEmpresaController.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				refrescarListaEmpresas();
				actualizarEmpresa();
				actualizarImagenInferior();
			}
		});
	}
	
	private void mostrarAbmInstructor(Instructor instructor) throws Exception {
		AbmInstructor abmInstructorController = new AbmInstructor();
		abmInstructorController.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				refrescarListaInstructores();
				actualizarInstructor();
			}
		});
		abmInstructorController.setModelo(instructor);
		abmInstructorController.pack();
		abmInstructorController.setLocationRelativeTo(CapturadorCarnet.this);
		abmInstructorController.setVisible(true);
	}
	
	private void seleccionarColor() {
		Color nuevoColor = JColorChooser.showDialog(this, "Color carnet", pnlSeleccionColor.getBackground());
		if (nuevoColor != null) {
			actualizarColor(nuevoColor);
		}
	}

	private void actualizarColor(Color color) {
		pnlSeleccionColor.setBackground(color);
		front.setColorNota(color);
		front.setColorTitulo(color);
		back.setColorRecuadro(color);
	}
	
	private void guardar() {
		actualizarModelo();
		CarnetDao carnetDao = DaoManager.getInstance().getCarnetDao();
		if (modelo.getId() == 0) {
			carnetDao.agregar(modelo);
		}
		else {
			carnetDao.actualizar(modelo);
		}
		imagenDao.limpiarImagenes();
		limpiar();
	}
	
	private void actualizarModelo() {
		if (modelo == null) {
			modelo = new Carnet();
		}
		String dni = txtDni.getText();
		modelo.setAfiliado(carnetDao.getAfiliadoPorDni(dni));
		
		Afiliado afiliado = (modelo.getAfiliado() == null) ? new Afiliado() : modelo.getAfiliado();
		afiliado.setApellido(txtApellido.getText().trim());
		afiliado.setNombre(txtNombre.getText().trim());
		afiliado.setDni(dni);
		modelo.setAfiliado(afiliado);
		
		String rgb = Integer.toHexString(pnlSeleccionColor.getBackground().getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());
		modelo.setColor(rgb);
		modelo.setCalificacion(txtNota.getText());
		Date otorgamiento = dpOtorgamiento.getDate();
		modelo.setEmision(otorgamiento);
		PeriodoVencimiento periodo = (PeriodoVencimiento)cboPeriodo.getSelectedItem();
		modelo.setPeriodo(periodo);
		modelo.setVencimiento(periodo.obtenerVencimiento(otorgamiento));
		modelo.setInstructor((Instructor)cboInstructor.getSelectedItem());
		modelo.setEmpresa((Empresa)cboEmpresa.getSelectedItem());
	}
	
	private void actualizarVista() throws Exception {
		txtApellido.setText(modelo.getAfiliado().getApellido());
		txtNombre.setText(modelo.getAfiliado().getNombre());
		txtDni.setText(modelo.getAfiliado().getDni());
		txtNota.setText(modelo.getCalificacion());
		dpOtorgamiento.setDate(modelo.getEmision());
		cboEmpresa.setSelectedItem(modelo.getEmpresa());
		cboInstructor.setSelectedItem(modelo.getInstructor());
		cboPeriodo.setSelectedItem(modelo.getPeriodo());
		Color color = Color.decode(modelo.getColor());
		actualizarColor(color);
		
		File archivo = imagenDao.getFileImagen(modelo.getFoto());
		if (archivo == null) {
			modelo.setFoto(null);
		}
		back.setFoto(archivo);
		validar();
	}
	
	private void limpiar() {
		actualizarConfiguracion();
		modelo = new Carnet();
		txtDni.setText("");
		txtNombre.setText("");
		txtApellido.setText("");
		back.setFoto(null);
	}
	
	private void validar() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				boolean agregar = 
						(validador.getProblem() == null) &&
						cboInstructor.getSelectedItem() != null &&
						cboEmpresa.getSelectedItem() != null &&
						cboPeriodo.getSelectedItem() != null &&
						dpOtorgamiento.getDate() != null && 
						modelo.getFoto() != null;
				btnAgregar.setEnabled(agregar);
			}
		});
	}

	private void mostrarVerLista() {
		ImpresionCarnet impresionCarnet = new ImpresionCarnet(this);
		impresionCarnet.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				setFocusable(true);
				setEnabled(true);
				requestFocus();
			}
		});
		impresionCarnet.pack();
		this.setFocusable(false);
		this.setEnabled(false);
		impresionCarnet.setVisible(true);
	}

	private void mostrarPlantillaCorreo() {
		EditorPlantillaCorreo editorPlantilla = new EditorPlantillaCorreo();
		editorPlantilla.setLocationRelativeTo(null);
		editorPlantilla.pack();
		editorPlantilla.setModal(true);
		editorPlantilla.setVisible(true);
	}
	
	public Carnet getModelo() {
		return modelo;
	}
	
	public void setModelo(Carnet modelo) throws Exception {
		this.modelo = modelo;
		actualizarVista();
	}
}
