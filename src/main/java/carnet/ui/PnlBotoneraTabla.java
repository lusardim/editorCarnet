package carnet.ui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;

import carnet.Util;

public class PnlBotoneraTabla extends JPanel {
	
	private static final long serialVersionUID = -3559078703776713330L;
	private JButton btnEditar;

	public PnlBotoneraTabla() {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setVgap(0);
		
		btnEditar = new JButton(Util.getCodigoLapiz());
		btnEditar.setFont(Util.getFontAwsome().deriveFont(Font.PLAIN, 9f));
		btnEditar.setEnabled(true);
		add(btnEditar);
	}

	public JButton getBtnEditar() {
		return btnEditar;
	}
}

