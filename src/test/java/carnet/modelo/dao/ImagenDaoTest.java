package carnet.modelo.dao;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

import carnet.modelo.entidades.Afiliado;
import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Instructor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext.xml"})
@Transactional
public class ImagenDaoTest {
	
	@Autowired
	private ImagenDao imagenDao;
	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	
	@Before
	public void initTest() throws Exception {
		String file = System.getProperty("java.io.tmpdir");
		String path = file + "/images";
		imagenDao.setPath(path);
	}
	
	@Test
	public void guardarImagenTest() throws Exception {
		
		BufferedImage image = ImageIO.read(
			this.getClass().getResourceAsStream("Firma1.png")		
		);
		Assert.assertNotNull(image);
		
		UUID id = imagenDao.guardarImagen(image);
		Path path = imagenDao.getPath().resolve(id.toString() + ".png");
		File file = path.toFile();
		
		Assert.assertTrue(file.exists());
		Assert.assertTrue(file.length() > 0);
		file.delete();
	}
	
	@Test
	public void limpiarTest() throws Exception {
		BufferedImage image = ImageIO.read(
				this.getClass().getResourceAsStream("Firma1.png")		
		);
		BufferedImage imagenEliminar = ImageIO.read(
				this.getClass().getResourceAsStream("Eliminar.png")		
		);
		
		UUID eliminarId = UUID.randomUUID();
		Path eliminarFilePath = imagenDao.getPath().resolve(eliminarId + ".png");
		ImageIO.write(imagenEliminar, "png", eliminarFilePath.toFile());
		UUID id = imagenDao.guardarImagen(image);
		Carnet carnet = crearCarnetPrueba();
		carnet.setFoto(id);
		entityManager.persist(carnet);
		
		entityManager.flush();
		imagenDao.limpiarImagenes();
		
		Path path = imagenDao.getPath().resolve(id.toString() + ".png");
		File file = path.toFile();
		
		Assert.assertTrue(file.exists());
		Assert.assertTrue(file.length() > 0);
		file.delete();
		
		File archivoEliminado = eliminarFilePath.toFile();
		Assert.assertFalse(archivoEliminado.exists());
	}

	private Carnet crearCarnetPrueba() {
		Carnet carnet = new Carnet();
		carnet.setCalificacion("A");
		carnet.setEmision(Calendar.getInstance().getTime());
		carnet.setEmpresa(getEmpresaPrueba());
		carnet.setInstructor(getInstructorPrueba());
		carnet.setAfiliado(crearAfiliadoPrueba());
		carnet.setImpreso(false);
		carnet.setTitulo("titulo");
		carnet.setVencimiento(carnet.getEmision());
		carnet.setColor("#FFFFFF");
		return carnet;
	}
	
	private Afiliado crearAfiliadoPrueba() {
		Afiliado afiliado = new Afiliado();
		afiliado.setApellido("Apellido2");
		afiliado.setNombre("nombre");
		afiliado.setDni("20.202.202");
		return afiliado;
	}
	
	private Instructor getInstructorPrueba() {
		Instructor instructor = new Instructor();
		instructor.setApellido("Apellido");
		instructor.setDni("20202020");
		instructor.setFirmaId(UUID.randomUUID());
		instructor.setNombre("Nombre");
		entityManager.persist(instructor);
		entityManager.flush();
		return instructor;
	}

	private Empresa getEmpresaPrueba() {
		Empresa empresa = new Empresa();
		empresa.setNombre("test");
		empresa.setDescripcion("descripcion");
		entityManager.persist(empresa);
		entityManager.flush();
		return empresa;
	}
	
	
	public void setImagenDao(ImagenDao imagenDao) {
		this.imagenDao = imagenDao;
	}
	public ImagenDao getImagenDao() {
		return imagenDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
