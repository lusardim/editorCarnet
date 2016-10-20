package carnet.modelo.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carnet.modelo.entidades.ConfiguracionGeneral;
import carnet.modelo.entidades.ConfiguracionPlantillaMail;

@Component("configuracionDao")
@Transactional
public class ConfiguracionDao {

	private static final int ID = 1;

	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	
	public void guardar(ConfiguracionPlantillaMail plantilla) {
		if (plantilla.getId() == null) {
			ConfiguracionGeneral configuracionGeneral = getConfiguracionGeneral();
			configuracionGeneral.setTemplate(plantilla);
			guardar(configuracionGeneral);
		}
		else {
			entityManager.merge(plantilla);
		}
	}
	
	public void guardar(ConfiguracionGeneral configuracionGeneral) {
		entityManager.merge(configuracionGeneral);
	}
	
	public ConfiguracionGeneral getConfiguracionGeneral() {
		ConfiguracionGeneral configuracionGeneral = entityManager.find(ConfiguracionGeneral.class, ID );
		if (configuracionGeneral == null) {
			configuracionGeneral = new ConfiguracionGeneral();
			configuracionGeneral.setId(ID);
			configuracionGeneral.setDescripcionEmpresa("Breve descripción de la empresa");
			configuracionGeneral.setNombreEmpresa("Nombre empresa srl");
			configuracionGeneral.setTituloCarnet("Carnet de Manejo Defensivo");
			configuracionGeneral.setColorDefecto("#42ae81");
			configuracionGeneral.setNotaDefecto("A");
			guardar(configuracionGeneral);
		}
		return configuracionGeneral;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
