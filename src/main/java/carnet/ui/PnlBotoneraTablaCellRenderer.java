package carnet.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PnlBotoneraTablaCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		PnlBotoneraTabla botonera = (PnlBotoneraTabla)value;
		return botonera;
	}

}
