package carnet.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import carnet.modelo.dao.DaoManager;
import carnet.modelo.dao.PeriodoVencimientoDao;
import carnet.modelo.entidades.PeriodoVencimiento;
import carnet.modelo.entidades.PeriodoVencimiento.Periodicidad;

public class AbmPeriodo extends JDialog {
	private static final long serialVersionUID = 3904667154060559970L;
	private JTextField txtDescripcion;
	private JComboBox<Periodicidad> cboPeriodicidad;
	private JSpinner spCantidad; 
	private JButton btnCancelar;
	private JButton btnGuardar;
	private JButton btnEliminar;
	private ValidationPanel validador;
	private PeriodoVencimiento modelo;
	
	/**
	 * Create the dialog.
	 */
	public AbmPeriodo() {
		inicializarVista();
		inicializarEventos();
	}

	private void inicializarEventos() {
		validador.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						verificarValidez();
					}
				});
			}
		});
		
		cboPeriodicidad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				verificarValidez();
			}
		});
		
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				guardar();
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
	
	private void guardar() {
		actualizarModelo();
		PeriodoVencimientoDao dao = DaoManager.getInstance().getPeriodoVencimientoDao();
		if (modelo.getId() == 0) {
			dao.agregar(modelo);
		}
		else {
			dao.actualizar(modelo);
		}
		dispose();
	}
	
	private void eliminar() {
		int seleccion = JOptionPane.showConfirmDialog(this, 
				"¿Está seguro que desea eliminar el período seleccionado?", 
				"Eliminar", JOptionPane.YES_NO_OPTION);
		if (seleccion == JOptionPane.YES_OPTION) {
			DaoManager
				.getInstance()
				.getPeriodoVencimientoDao()
				.eliminar(modelo);
			dispose();
		}
	}
	
	public void setModelo(PeriodoVencimiento modelo) {
		if (modelo == null) {
			modelo = new PeriodoVencimiento();
		}
		this.modelo = modelo;
		actualizarVista();
	}

	private void actualizarVista() {
		if (modelo.getId() != 0) {
			btnEliminar.setVisible(true);
		}
		txtDescripcion.setText(modelo.getDescripcion());
		spCantidad.setValue(modelo.getCantidad());
		cboPeriodicidad.setSelectedItem(modelo.getPeriodicidad());
	}
	
	private void actualizarModelo() {
		modelo.setDescripcion(txtDescripcion.getText().trim());
		modelo.setCantidad((Integer)spCantidad.getValue());
		modelo.setPeriodicidad((Periodicidad) cboPeriodicidad.getSelectedItem());
	}

	private void verificarValidez() {
		btnGuardar.setEnabled(
				validador.getProblem() == null &&
				cboPeriodicidad.getSelectedItem() != null);
	}

	private void inicializarVista() {
		setTitle("Periodos");
		setBounds(100, 100, 473, 277);
		JLabel lblCantidad = new JLabel("Cantidad");
		JLabel lblPeriodicidad = new JLabel("Periodicidad");
		
		cboPeriodicidad = new JComboBox<Periodicidad>();
		cboPeriodicidad.setModel(new DefaultComboBoxModel<Periodicidad>(Periodicidad.values()));
		
		spCantidad = new JSpinner();
		spCantidad.setModel(new SpinnerNumberModel(1, 1, 365, 1));
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setVisible(false);
		
		JLabel lblDescripcion = new JLabel("Descripcion");
		
		txtDescripcion = new JTextField();
		txtDescripcion.setColumns(10);
		
		validador = new ValidationPanel();
		
	    SwingValidationGroup group = (SwingValidationGroup) validador.getValidationGroup();
	    group.add(txtDescripcion, StringValidators.REQUIRE_NON_EMPTY_STRING);
		
		btnCancelar = new JButton("Cancelar");
		btnGuardar = new JButton("Guardar");
		btnGuardar.setEnabled(false);
		JSeparator separator = new JSeparator();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDescripcion)
						.addComponent(txtDescripcion, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
						.addComponent(lblCantidad)
						.addComponent(spCantidad, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
						.addComponent(lblPeriodicidad)
						.addComponent(cboPeriodicidad, 0, 440, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnEliminar)
							.addPreferredGap(ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
							.addComponent(btnGuardar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancelar))
						.addComponent(separator, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDescripcion)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCantidad)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spCantidad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPeriodicidad)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cboPeriodicidad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnEliminar)
						.addComponent(btnCancelar)
						.addComponent(btnGuardar))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
	}
}
