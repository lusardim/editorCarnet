package carnet.modelo.dao;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import carnet.modelo.entidades.Instructor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext.xml"})
@Transactional
public class InstructorDaoTest {
	
	@Autowired
	private InstructorDao instructorDao;
	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	
	public InstructorDao getInstructorDao() {
		return instructorDao;
	}

	public void setInstructorDao(InstructorDao instructorDao) {
		this.instructorDao = instructorDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Test
	public void testGuardarInstructor() {
		Instructor instructor = new Instructor();
		instructor.setDni("1111111111");
		instructor.setNombre("nombre");
		instructor.setFirmaId(UUID.randomUUID());
		
		Instructor obtenido = instructorDao.agregar(instructor);
		entityManager.flush();
		entityManager.clear();
		
		obtenido = entityManager.find(Instructor.class, obtenido.getId());
		Assert.assertNotNull(obtenido);
		Assert.assertEquals(obtenido.getNombre(), instructor.getNombre());
	}
	
}
