package carnet.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.undo.UndoManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import carnet.modelo.dao.DaoManager;
import carnet.modelo.entidades.ConfiguracionGeneral;
import carnet.modelo.entidades.ConfiguracionPlantillaMail;
import carnet.modelo.entidades.VariablesTemplate;

public class EditorPlantillaCorreo extends JDialog {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final long serialVersionUID = 8674209371050177747L;
	private ConfiguracionPlantillaMail plantilla;
	private UndoManager undoManager;
	private JTextField txtAsunto;
	private JButton btnCancelar;
	private JButton btnGuardar;
	private JList<VariablesTemplate> lvVariables;
	private JButton btnAgregarAsunto;
	private JTextPane txtContenido;
	private JButton btnAgregarContenido;
	private JSpinner spDiasReenvio;
	private JCheckBox chkEnviarVencidos;
	private JCheckBox chkEnviarPorVencer;
	private JLabel lblDias;
	private JSpinner spDiasFaltantes;
	private JLabel lblDiasFaltantes;
	private JLabel lblDiasVerificacion;
	
	public EditorPlantillaCorreo() {
		inicializarVista();
		inicializarEventos();
		cargarPlantilla();
	}

	private void cargarPlantilla() {
		ConfiguracionGeneral config = DaoManager.getInstance().getConfiguracionDao().getConfiguracionGeneral();
		if (config != null && config.getTemplate() != null) {
			this.setPlantilla(config.getTemplate());
		}
	}

	private void inicializarEventos() {
		btnAgregarContenido.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertVariableContenido();
			}
		});
		
		lvVariables.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
					insertVariableContenido();
				}
			}
		});
		
		btnAgregarAsunto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertarVariableAsunto();
				
			}
		});
		
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guardar();
			}
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelar();
			}
		});
		
		
		//deshacer soporte
		txtContenido.getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent evento) {
				undoManager.addEdit(evento.getEdit());
			}
		});
		
		InputMap map = txtContenido.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = txtContenido.getActionMap();
		
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");
		
		actionMap.put("Undo", new AbstractAction() {
			private static final long serialVersionUID = 4728177714733376310L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deshacer();
			}
		});
		
		actionMap.put("Redo", new AbstractAction() {
			private static final long serialVersionUID = -3180012566643422305L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rehacer();
			}
		});
	}

	
	private void inicializarVista() {
		setTitle("Configuraci\u00F3n de plantilla de correo");
		setBounds(100, 100, 802, 644);
		this.undoManager = new UndoManager();
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblReenviarCorreoCada = new JLabel("Reenviar correo cada");
		
		spDiasReenvio = new JSpinner();
		spDiasReenvio.setToolTipText("Este valor representa la cantidad de d\u00EDas entre notificaciones que debe recibir una empresa, en caso de ser 1 todos los d\u00EDas se reenviar\u00E1 un correo con el n\u00FAmero de carnets por vencer");
		spDiasReenvio.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
		
		chkEnviarVencidos = new JCheckBox("Enviar correo a empresas con carnets vencidos");
		chkEnviarVencidos.setToolTipText("Si esta opci\u00F3n est\u00E1 habilitada, tambi\u00E9n se notificar\u00E1 de los carnets vencidos.");
		
		chkEnviarPorVencer = new JCheckBox("Enviar correo a empresas con carnets por vencer");
		chkEnviarPorVencer.setToolTipText("Cuando esta opci\u00F3n est\u00E1 seleccionada, se enviar\u00E1 un correo cuando al menos haya un carnet por vencer en los proximos X d\u00EDas, considerando la cantidad de d\u00EDas configurada en \"Cantidad de d\u00EDas hasta el vencimiento\"");
		
		JLabel lblAsunto = new JLabel("Asunto");
		
		txtAsunto = new JTextField();
		txtAsunto.setColumns(10);
		btnCancelar = new JButton("Cancelar");
		btnGuardar = new JButton("Guardar");
		lvVariables = new JList<VariablesTemplate>(VariablesTemplate.values());
		lvVariables.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = -876703745214448721L;
			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				if (value instanceof VariablesTemplate) {
					value = ((VariablesTemplate) value).getDescripcion();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
			}
		});
		btnAgregarAsunto = new JButton("<");
		JLabel lblContenido = new JLabel("Contenido");
		txtContenido = new JTextPane();
		txtContenido.setEditorKit(new WrapEditorKit());
		
		JScrollPane scroll = new JScrollPane(txtContenido);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		btnAgregarContenido = new JButton("<");
		
		JSeparator separator = new JSeparator();
		
		lblDias = new JLabel("Dias");
		
		spDiasFaltantes = new JSpinner();
		spDiasFaltantes.setToolTipText("Este valor representa cuantos d\u00EDas antes del vencimiento debe notificarse a las empresas. ");
		spDiasFaltantes.setModel(new SpinnerNumberModel(1, 0, 10000, 1));
		
		lblDiasFaltantes = new JLabel("Cantidad de d\u00EDas hasta el vencimiento");
		
		lblDiasVerificacion = new JLabel("D\u00EDas");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(624)
							.addComponent(btnGuardar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancelar))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator, GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblAsunto))
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panel.createSequentialGroup()
											.addContainerGap()
											.addComponent(lblContenido))
										.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
											.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
												.addGroup(gl_panel.createSequentialGroup()
													.addContainerGap()
													.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
														.addComponent(chkEnviarPorVencer, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
														.addComponent(chkEnviarVencidos)
														.addComponent(spDiasReenvio, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)))
												.addGroup(gl_panel.createSequentialGroup()
													.addGap(10)
													.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
												.addGroup(gl_panel.createSequentialGroup()
													.addContainerGap()
													.addComponent(txtAsunto, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
													.addComponent(btnAgregarAsunto, Alignment.TRAILING)
													.addComponent(btnAgregarContenido, Alignment.TRAILING))
												.addComponent(lblDias))))
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(gl_panel.createSequentialGroup()
									.addContainerGap()
									.addComponent(lblReenviarCorreoCada, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
									.addGap(242)))
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblDiasFaltantes)
								.addComponent(lvVariables, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
								.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
									.addComponent(spDiasFaltantes)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblDiasVerificacion)))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblReenviarCorreoCada)
						.addComponent(lblDiasFaltantes))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spDiasReenvio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(spDiasFaltantes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDiasVerificacion)
						.addComponent(lblDias))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chkEnviarVencidos)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chkEnviarPorVencer)
					.addGap(18)
					.addComponent(lblAsunto)
					.addGap(3)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAgregarAsunto)
								.addComponent(txtAsunto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblContenido)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
								.addComponent(btnAgregarContenido)))
						.addComponent(lvVariables, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 5, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancelar)
						.addComponent(btnGuardar))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
	}
	
	private void insertVariableContenido() {
		VariablesTemplate selectedValue = lvVariables.getSelectedValue();
		if (selectedValue != null) {
			try {
				int position = txtContenido.getCaretPosition();
				txtContenido.getDocument().insertString(position, selectedValue.getVariable(), null);
			} 
			catch (BadLocationException e) {
				LOGGER.info("No se pudo insertar variable en el contenido", e);
			}
		}
	}

	private void insertarVariableAsunto() {
		VariablesTemplate selectedValue = lvVariables.getSelectedValue();
		if (selectedValue != null) {
			try {
				int position = txtAsunto.getCaretPosition();
				txtAsunto.getDocument().insertString(position, selectedValue.getVariable(), null);
			} 
			catch (BadLocationException e) {
				LOGGER.info("No se pudo insertar variable en el asunto", e);
			}
		}
	}
	
	private void deshacer() {
		try {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}
		catch (Exception e) {
			LOGGER.error("No se pudo deshacer", e);
		}
	}
	
	private void rehacer() {
		try {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
		catch (Exception e) {
			LOGGER.error("No se pudo rehacer", e);
		}
	}
	
	private void actualizarModelo() {
		if (plantilla == null) {
			plantilla = new ConfiguracionPlantillaMail();
		}
		plantilla.setAsunto(txtAsunto.getText().trim());
		plantilla.setContenido(txtContenido.getText().trim());
		plantilla.setEnviarPorVencer(chkEnviarPorVencer.isSelected());
		plantilla.setEnviarVencidos(chkEnviarVencidos.isSelected());
		plantilla.setPeriodicidadDias((int) spDiasFaltantes.getValue());
		plantilla.setReenviarCada((int) spDiasReenvio.getValue());
	}
	
	private void actualizarVista() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (plantilla == null) {
					plantilla = new ConfiguracionPlantillaMail();
				}
				txtAsunto.setText(plantilla.getAsunto());
				txtContenido.setText(plantilla.getContenido());
				chkEnviarPorVencer.setSelected(plantilla.isEnviarPorVencer());
				chkEnviarVencidos.setSelected(plantilla.isEnviarVencidos());
				spDiasFaltantes.setValue(plantilla.getPeriodicidadDias());
				spDiasReenvio.setValue(plantilla.getReenviarCada());
			}
		});
	}
	
	private void guardar() {
		try {
			actualizarModelo();
			DaoManager.getInstance().getConfiguracionDao().guardar(plantilla);
			dispose();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Ha ocurrido un error guardar la plantilla", 
					"Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Ha ocurrido un error al guardar la plantilla", e);
		}
	}
	
	private void cancelar() {
		dispose();
	}
	
	public void setPlantilla(ConfiguracionPlantillaMail plantilla) {
		this.plantilla = plantilla;
		actualizarVista();
	}
}

//--- Editor del txt
class WrapEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = -985018283895597163L;
	ViewFactory defaultFactory = new WrapColumnFactory();

	public ViewFactory getViewFactory() {
		return defaultFactory;
	}

}

class WrapColumnFactory implements ViewFactory {
	public View create(Element elem) {
		String kind = elem.getName();
		if (kind != null) {
			if (kind.equals(AbstractDocument.ContentElementName)) {
				return new WrapLabelView(elem);
			} 
			else if (kind.equals(AbstractDocument.ParagraphElementName)) {
				return new ParagraphView(elem);
			} 
			else if (kind.equals(AbstractDocument.SectionElementName)) {
				return new BoxView(elem, View.Y_AXIS);
			} 
			else if (kind.equals(StyleConstants.ComponentElementName)) {
				return new ComponentView(elem);
			} 
			else if (kind.equals(StyleConstants.IconElementName)) {
				return new IconView(elem);
			}
		}
		return new LabelView(elem);
	}
}

class WrapLabelView extends LabelView {
	public WrapLabelView(Element elem) {
		super(elem);
	}

	public float getMinimumSpan(int axis) {
		switch (axis) {
		case View.X_AXIS:
			return 0;
		case View.Y_AXIS:
			return super.getMinimumSpan(axis);
		default:
			throw new IllegalArgumentException("Invalid axis: " + axis);
		}
	}

}