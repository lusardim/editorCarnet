package carnet.modelo.dao;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import carnet.servicios.ServicioMail;

@Component("daoManager")
public class DaoManager {
	
	private static final Logger LOGGER = Logger.getLogger(DaoManager.class);
	private static DaoManager instance;
	@Autowired
	private ImagenDao imagenDao;
	@Autowired
	private EmpresaDao empresaDao;
	@Autowired
	private InstructorDao instructorDao;
	@Autowired
	private ConfiguracionDao configuracionDao;
	@Autowired
	private PeriodoVencimientoDao periodoVencimientoDao;
	@Autowired
	private CarnetDao carnetDao;
	@Autowired
	private ServicioMail servicioMail;
		
	@PostConstruct
	public void init() throws Exception {
		instance = this;
		try {
			servicioMail.enviarEmails();
		}
		catch (Exception e) {
			LOGGER.error("Ha ocurrido un error al envíar los mails", e);
		}
		
	}
	
	public static DaoManager getInstance() {
		return instance;
	}

	public ImagenDao getImagenDao() {
		return imagenDao;
	}

	public void setImagenDao(ImagenDao imagenDao) {
		this.imagenDao = imagenDao;
	}

	public EmpresaDao getEmpresaDao() {
		return empresaDao;
	}

	public void setEmpresaDao(EmpresaDao empresaDao) {
		this.empresaDao = empresaDao;
	}

	public InstructorDao getInstructorDao() {
		return instructorDao;
	}

	public void setInstructorDao(InstructorDao instructorDao) {
		this.instructorDao = instructorDao;
	}

	public ConfiguracionDao getConfiguracionDao() {
		return configuracionDao;
	}

	public void setConfiguracionDao(ConfiguracionDao configuracionDao) {
		this.configuracionDao = configuracionDao;
	}

	public PeriodoVencimientoDao getPeriodoVencimientoDao() {
		return periodoVencimientoDao;
	}

	public void setPeriodoVencimientoDao(PeriodoVencimientoDao periodoVencimientoDao) {
		this.periodoVencimientoDao = periodoVencimientoDao;
	}

	public CarnetDao getCarnetDao() {
		return carnetDao;
	}

	public void setCarnetDao(CarnetDao carnetDao) {
		this.carnetDao = carnetDao;
	}

	public ServicioMail getServicioMail() {
		return servicioMail;
	}

	public void setServicioMail(ServicioMail servicioMail) {
		this.servicioMail = servicioMail;
	}
}
