package carnet.servicios;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.mail.Email;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import carnet.modelo.dao.CarnetDao;
import carnet.modelo.dao.ConfiguracionDao;
import carnet.modelo.dao.EmpresaDao;
import carnet.modelo.entidades.ConfiguracionGeneral;
import carnet.modelo.entidades.ConfiguracionPlantillaMail;
import carnet.modelo.entidades.ConfiguracionServidorCorreo;
import carnet.modelo.entidades.Empresa;
import carnet.vista.modelos.CarnetPorEmpresa;

@Component("servicioMail")
public class ServicioMail {
	
	@Autowired
	private CarnetDao carnetDao;
	@Autowired
	private ConfiguracionDao configuracionDao;
	@Autowired
	private EmpresaDao empresaDao;
	
	@Transactional
	public void enviarEmails() throws Exception {
		ConfiguracionGeneral configuracion = configuracionDao.getConfiguracionGeneral();
		ConfiguracionServidorCorreo configuracionEmail = configuracion.getConfiguracionEmail();
		ConfiguracionPlantillaMail plantilla = configuracion.getTemplate();
				
		List<CarnetPorEmpresa> carnets = carnetDao.getEmpresasConCarnetAVencerOVencidos(plantilla.getPeriodicidadDias());
		
		LocalDate fechaHoy = LocalDate.now();
		Date hoy = Calendar.getInstance().getTime();
		
		for (CarnetPorEmpresa carnetPorEmpresa : carnets) {
			Empresa empresa = carnetPorEmpresa.getEmpresa(); 
			int cantidadDias = 0;
			if (empresa.getFechaUltimaNoficiacion() != null) {
				LocalDate diaUltimaNoficacion = LocalDate.fromDateFields(
						empresa.getFechaUltimaNoficiacion());
				cantidadDias = Days.daysBetween(diaUltimaNoficacion, fechaHoy).getDays();
			}
			
			if ((empresa.getFechaUltimaNoficiacion() == null || cantidadDias > plantilla.getReenviarCada()) && 	
				(
					(plantilla.isEnviarPorVencer() && carnetPorEmpresa.getCantidadPorVencer() > 0) ||
					(plantilla.isEnviarVencidos() && carnetPorEmpresa.getCantidadVencidos() > 0)
				)
			) 
			{
				Email email = configuracionEmail.crearEmail();
				email.setFrom(plantilla.getOrigen());
				email.setSubject(plantilla.getAsunto(carnetPorEmpresa));
				email.setMsg(plantilla.getContenido(carnetPorEmpresa));
				email.addTo(carnetPorEmpresa.getEmpresa().getEmail());
				email.send();
				
				//Actualiza la fecha de última notificación
				empresa.setFechaUltimaNoficiacion(hoy);
				empresaDao.actualizar(empresa);
			}
		}
	}
	
	public void enviarMailPrueba(String direccion, ConfiguracionServidorCorreo configuracionPrueba) throws Exception {
		if (configuracionPrueba == null)
			throw new NullPointerException("La configuración no puede ser nula");
		
		Email emailPrueba = configuracionPrueba.crearEmail();
		emailPrueba.setFrom("test@carnet.org");
		emailPrueba.setSubject("Test configuración de correo");
		emailPrueba.setMsg("Si recibió este correo la configuración ha sido exitosa");
		emailPrueba.addTo(direccion);
		emailPrueba.send();
	}
	
	public CarnetDao getCarnetDao() {
		return carnetDao;
	}
	public void setCarnetDao(CarnetDao carnetDao) {
		this.carnetDao = carnetDao;
	}

	public ConfiguracionDao getConfiguracionDao() {
		return configuracionDao;
	}

	public void setConfiguracionDao(ConfiguracionDao configuracionDao) {
		this.configuracionDao = configuracionDao;
	}

	public EmpresaDao getEmpresaDao() {
		return empresaDao;
	}

	public void setEmpresaDao(EmpresaDao empresaDao) {
		this.empresaDao = empresaDao;
	}
	
}
