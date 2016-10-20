package carnet.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import carnet.modelo.entidades.Carnet;
import carnet.vista.modelos.ModeloTablaListaImpresion;

public class PnlBotoneraTablaCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final long serialVersionUID = 2733385107063740028L;
	private JFrame dialogo;
	private CapturadorCarnet capturador;
	
	public PnlBotoneraTablaCellEditor(CapturadorCarnet capturador, JFrame dialogo) {
		this.dialogo = dialogo;
		this.capturador = capturador;
	}
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, final int row, int column) {
		final ModeloTablaListaImpresion modelo = (ModeloTablaListaImpresion) table.getModel();
		PnlBotoneraTabla pnlBotonera = (PnlBotoneraTabla)value;
		pnlBotonera.setEnabled(true);
		pnlBotonera.getBtnEditar().setEnabled(true);
		pnlBotonera.getBtnEditar().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Carnet carnet = modelo.getCarnet(row);
					capturador.setModelo(carnet);
					dialogo.dispose();
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					JOptionPane.showMessageDialog(
							dialogo, 
							ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		return pnlBotonera;
	}


}
