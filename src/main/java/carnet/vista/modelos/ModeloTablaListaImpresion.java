package carnet.vista.modelos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import carnet.modelo.dao.CarnetDao;
import carnet.modelo.dao.DaoManager;
import carnet.modelo.entidades.Carnet;
import carnet.ui.PnlBotoneraTabla;

public class ModeloTablaListaImpresion extends AbstractTableModel {

	private static final long serialVersionUID = -1029649432698713822L;
	private List<ModeloImpresion> modelo;
	private boolean verImpresos;
	private Date desde;
	private Date hasta;
	private String dni;
	private String empresa;
	private CarnetDao carnetDao;
	
	public ModeloTablaListaImpresion() {
		carnetDao = DaoManager.getInstance().getCarnetDao();
		actualizarModelo();
	}
	
	public void actualizarModelo() {
		if (verImpresos) {
			setModelo(carnetDao.getCarnetImpresos(dni, empresa, desde, hasta));
		}
		else {
			setModelo(carnetDao.getCarnetsNoImpresos(dni, empresa));
		}
		fireTableDataChanged();
	}
	
	public List<ModeloImpresion> getSeleccionados() {
		List<ModeloImpresion> seleccionados = new ArrayList<ModeloImpresion>();
		for (ModeloImpresion seleccion : modelo) {
			if (seleccion.isImprimir()) {
				seleccionados.add(seleccion);
			}
		}
		return seleccionados;
	}
	
	public Carnet getCarnet(int row) {
		ModeloImpresion modelo = this.modelo.get(row);
		return carnetDao.getById(modelo.getIdCarnet()); 
	}
	
	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return modelo.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return modelo.get(rowIndex).isImprimir();
			case 1: return modelo.get(rowIndex).getDni();
			case 2: return modelo.get(rowIndex).getNombre();
			case 3: return modelo.get(rowIndex).getApellido();
			case 4: return new PnlBotoneraTabla();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			modelo.get(rowIndex).setImprimir((boolean)aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 1: return "Dni";
			case 2: return "Nombre";
			case 3: return "Apellido";
		}
		return null;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0: return Boolean.class;
			case 4: return PnlBotoneraTabla.class;
		}
		return super.getColumnClass(columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
			case 4: return true;
			default: return super.isCellEditable(rowIndex, columnIndex);
		}
	}
	
	
	public boolean isVerImpresos() {
		return verImpresos;
	}

	public void setVerImpresos(boolean verImpresos) {
		this.verImpresos = verImpresos;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}
	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	private void setModelo(List<Carnet> carnets) {
		modelo = new ArrayList<ModeloImpresion>(carnets.size());
		for (Carnet carnet : carnets) {
			modelo.add(new ModeloImpresion(carnet));
		}
	}

}
