package carnet.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import carnet.modelo.dao.DaoManager;
import carnet.templates.ImpresionHandler;
import carnet.vista.modelos.ModeloImpresion;
import carnet.vista.modelos.ModeloTablaListaImpresion;

public class ImpresionCarnet extends JFrame {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final long serialVersionUID = 9066411189865477051L;
	private JTable tblCarnets;
	private ModeloTablaListaImpresion modelo;
	private CapturadorCarnet capturadorCarnet;
	private JXDatePicker dpFechaOtorgamiento;
	private JXDatePicker dpFechaVencimiento;
	private JCheckBox chkVerCarnetsImpresos;
	private JComboBox<String> cboEmpresa;
	private JComboBox<String> cboDni;
	private JButton btnImprimir;
	private JButton btnLimpiarEmpresa;
	private JButton btnLimpiarDni;
	private JButton btnGuardarPdf;
	
	public ImpresionCarnet() {
		inicializarVista();
	}

	public ImpresionCarnet(CapturadorCarnet capturadorCarnet) {
		//super(capturadorCarnet);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.capturadorCarnet = capturadorCarnet;
		inicializarVista();
		inicializarModelo();
		inicializarEventos();
	}

	private void inicializarEventos() {
		ActionListener actualizarModelo = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarSeleccion();
				actualizarModelo();
			}
		};
		
		chkVerCarnetsImpresos.addActionListener(actualizarModelo);
		dpFechaOtorgamiento.addActionListener(actualizarModelo);
		dpFechaVencimiento.addActionListener(actualizarModelo);
		cboEmpresa.addActionListener(actualizarModelo);
		cboDni.addActionListener(actualizarModelo);
		btnImprimir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImprimirSeleccionados();
			}
		});
		
		btnGuardarPdf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imprimirSeleccionadosAPdf();
			}
		});
		
		btnLimpiarEmpresa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cboEmpresa.setSelectedItem(null);
				actualizarModelo();
			}
		});
		
		btnLimpiarDni.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cboDni.setSelectedItem(null);
				actualizarModelo();
			}
		});
	}

	private void imprimirSeleccionadosAPdf() {
		List<ModeloImpresion> seleccionados = modelo.getSeleccionados();
		if (!seleccionados.isEmpty()) {
			List<Long> ids = getIds(seleccionados);
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filtro = new FileNameExtensionFilter("Documentos PDF", "pdf");
			fileChooser.addChoosableFileFilter(filtro);
			fileChooser.setFileFilter(filtro);
			
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File archivo = agregarExtension(fileChooser.getSelectedFile());
				imprimir(ids, archivo);
			}
		}
	}

	private void ImprimirSeleccionados() {
		List<ModeloImpresion> seleccionados = modelo.getSeleccionados();
		if (!seleccionados.isEmpty()) {
			List<Long> ids = getIds(seleccionados);
			imprimir(ids);
		}
	}
	
	private File agregarExtension(File selectedFile) {
		String nombre = selectedFile.getName();
	    if(nombre.lastIndexOf('.') == -1) {
	        nombre += ".pdf";
	    }
	    return new File(selectedFile.getParent(), nombre);
	}
	
	private void imprimir(final List<Long> ids, final File file) {
		final JDialog dlg = new DialogoImpresion(this);
	    
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ImpresionHandler handler = new ImpresionHandler(ids);
					handler.imprimir(file);
					DaoManager.getInstance().getCarnetDao().marcarImpresos(ids);
					modelo.actualizarModelo();
				}				
				catch (Exception e) {
					LOGGER.error("A ocurrido un error al intentar imprimir los carnets seleccionados.", e);
					JOptionPane.showMessageDialog(
							ImpresionCarnet.this, 
							e.getMessage(), 
							"Error", 
							JOptionPane.ERROR_MESSAGE);
				}
				finally {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							dlg.dispose();
						}
					});
				}
			}
		});
		thread.start();
		dlg.setVisible(true);
	}
	
	private void imprimir(final List<Long> ids) {
		final JDialog dlg = new DialogoImpresion(this);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ImpresionHandler handler = new ImpresionHandler(ids);
					// ImpresionCarnet.this.setModal(false);
					handler.imprimir();
					DaoManager.getInstance().getCarnetDao().marcarImpresos(ids);
					modelo.actualizarModelo();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(ImpresionCarnet.this,
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							// ImpresionCarnet.this.setModal(true);
							dlg.dispose();
						}
					});
				}
			}
		});
		thread.start();
		dlg.setVisible(true);
	}

	private List<Long> getIds(List<ModeloImpresion> seleccionados) {
		List<Long> ids = new ArrayList<Long>(seleccionados.size());
		for (ModeloImpresion modelo : seleccionados) {
			ids.add(modelo.getIdCarnet());
		}
		return ids;
	}

	private void actualizarSeleccion() {
		boolean habilitar = chkVerCarnetsImpresos.isSelected();
		dpFechaOtorgamiento.setEnabled(habilitar);
		dpFechaVencimiento.setEnabled(habilitar);
	}
	
	private void actualizarModelo() {
		modelo.setVerImpresos(chkVerCarnetsImpresos.isSelected());
		modelo.setDesde(dpFechaOtorgamiento.getDate());
		modelo.setHasta(dpFechaVencimiento.getDate());
		String dni = (String) cboDni.getSelectedItem();
		String empresa = (String) cboEmpresa.getSelectedItem();
		if (dni != null && !dni.trim().equals("")) {
			modelo.setDni(cboDni.getSelectedItem().toString());
		}
		else {
			modelo.setDni(null);
		}
		
		if (empresa != null && !empresa.trim().equals("")) {
			modelo.setEmpresa(cboEmpresa.getSelectedItem().toString());
		}
		else {
			modelo.setEmpresa(null);
		}
		modelo.actualizarModelo();
	}
	
	private void inicializarModelo() {
		modelo = new ModeloTablaListaImpresion();
		tblCarnets.setModel(modelo);
		List<String> dnis = DaoManager.getInstance().getCarnetDao().getAfiliados();
		List<String> empresas = DaoManager.getInstance().getEmpresaDao().getNombres();
		
		cboDni.setModel(new DefaultComboBoxModel<String>(dnis.toArray(new String[dnis.size()])));
		cboEmpresa.setModel(new DefaultComboBoxModel<String>(empresas.toArray(new String[empresas.size()])));
		cboDni.setSelectedItem(null);
		cboEmpresa.setSelectedItem(null);
		AutoCompleteDecorator.decorate(cboDni);
		AutoCompleteDecorator.decorate(cboEmpresa);
				
		TableColumn botonera = tblCarnets.getColumnModel().getColumn(4);
		botonera.setCellRenderer(new PnlBotoneraTablaCellRenderer());
		botonera.setCellEditor(new PnlBotoneraTablaCellEditor(capturadorCarnet, this));
		
		TableColumn seleccion = tblCarnets.getColumnModel().getColumn(0);
		seleccion.setHeaderRenderer(new SelectAllHeader(tblCarnets, 0));
	}

	private void inicializarVista() {
		//setModal(true);
		setTitle("Impresi\u00F3n de carnets");
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Filtros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		tblCarnets = new JTable();
		tblCarnets.setRowHeight(20);
		tblCarnets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(tblCarnets);
		btnImprimir = new JButton("Imprimir Seleccionados");
		
		btnGuardarPdf = new JButton("Guardar a PDF");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnImprimir)
								.addComponent(btnGuardarPdf, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(btnGuardarPdf)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnImprimir))
						.addComponent(panel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		chkVerCarnetsImpresos = new JCheckBox("Ver carnets ya impresos");
		
		JLabel lblFechaOtorgamiento = new JLabel("Fecha otorgamiento desde");
		
		Date fechaHoy = Calendar.getInstance().getTime();
		dpFechaOtorgamiento = new JXDatePicker(fechaHoy);
		lblFechaOtorgamiento.setLabelFor(dpFechaOtorgamiento);
		
		dpFechaVencimiento = new JXDatePicker(fechaHoy);
		
		JLabel lblFechaVencimiento = new JLabel("Fecha otorgamiento hasta");
		lblFechaVencimiento.setLabelFor(dpFechaVencimiento);
		
		cboEmpresa = new JComboBox<String>();
		cboEmpresa.setEditable(true);
		
		JLabel lblEmpresa = new JLabel("Empresa");
		
		cboDni = new JComboBox<String>();
		cboDni.setEditable(true);
		
		JLabel lblDni = new JLabel("Dni");
		
		btnLimpiarEmpresa = new JButton("X");
		btnLimpiarDni = new JButton("X");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panel.createSequentialGroup()
										.addComponent(lblFechaOtorgamiento)
										.addPreferredGap(ComponentPlacement.RELATED))
									.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
										.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
											.addComponent(dpFechaOtorgamiento, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
											.addGroup(gl_panel.createSequentialGroup()
												.addComponent(cboEmpresa, 0, 231, Short.MAX_VALUE)
												.addGap(2)
												.addComponent(btnLimpiarEmpresa)))
										.addGap(4)))
								.addGap(2))
							.addGroup(gl_panel.createSequentialGroup()
								.addComponent(chkVerCarnetsImpresos)
								.addGap(122)))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblEmpresa)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFechaVencimiento)
						.addComponent(dpFechaVencimiento, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(cboDni, 0, 247, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnLimpiarDni, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblDni))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmpresa)
						.addComponent(lblDni))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cboEmpresa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cboDni, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLimpiarEmpresa, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLimpiarDni, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chkVerCarnetsImpresos)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFechaOtorgamiento)
						.addComponent(lblFechaVencimiento))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(dpFechaOtorgamiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dpFechaVencimiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(48, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);
		chkVerCarnetsImpresos.setSelected(false);
		dpFechaOtorgamiento.setEnabled(false);
		dpFechaVencimiento.setEnabled(false);
	}
	

	private class DialogoImpresion extends JDialog {
		private static final long serialVersionUID = 1890061197900734710L;
	
		public DialogoImpresion(ImpresionCarnet padre) {
			super(padre, "Imprimiendo carnets", true);
		    JProgressBar dpb = new JProgressBar(0, 500);
		    dpb.setIndeterminate(true);
		    getContentPane().add(BorderLayout.CENTER, dpb);
		    getContentPane().add(BorderLayout.NORTH, new JLabel("Procesando..."));
		    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		    setSize(300, 75);
		    setLocationRelativeTo(padre);
		}
	}
}

class SelectAllHeader extends JToggleButton implements TableCellRenderer {

    private static final long serialVersionUID = -1259618354803488287L;
	private static final String ALL = "\u2713 Seleccionar";
    private static final String NONE = "\u00D7 Deseleccionar";
    private JTable table;
    private TableModel tableModel;
    private JTableHeader header;
    private TableColumnModel tcm;
    private int targetColumn;
    private int viewColumn;

    public SelectAllHeader(JTable table, int targetColumn) {
        super(ALL);
        this.table = table;
        this.tableModel = table.getModel();
        if (tableModel.getColumnClass(targetColumn) != Boolean.class) {
            throw new IllegalArgumentException("Boolean column required.");
        }
        this.targetColumn = targetColumn;
        this.header = table.getTableHeader();
        this.tcm = table.getColumnModel();
        this.applyUI();
        this.addItemListener(new ItemHandler());
        header.addMouseListener(new MouseHandler());
        tableModel.addTableModelListener(new ModelHandler());
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
        return this;
    }

    private class ItemHandler implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            boolean state = e.getStateChange() == ItemEvent.SELECTED;
            setText((state) ? NONE : ALL);
            for (int r = 0; r < table.getRowCount(); r++) {
                table.setValueAt(state, r, viewColumn);
            }
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        applyUI();
    }

    private void applyUI() {
        this.setFont(UIManager.getFont("TableHeader.font"));
        this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        this.setBackground(UIManager.getColor("TableHeader.background"));
        this.setForeground(UIManager.getColor("TableHeader.foreground"));
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            viewColumn = header.columnAtPoint(e.getPoint());
            int modelColumn = tcm.getColumn(viewColumn).getModelIndex();
            if (modelColumn == targetColumn) {
                doClick();
            }
        }
    }

    private class ModelHandler implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            if (needsToggle()) {
                doClick();
                header.repaint();
            }
        }
    }

    private boolean needsToggle() {
        boolean allTrue = true;
        boolean allFalse = true;
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            boolean b = (Boolean) tableModel.getValueAt(r, targetColumn);
            allTrue &= b;
            allFalse &= !b;
        }
        return allTrue && !isSelected() || allFalse && isSelected();
    }
}

