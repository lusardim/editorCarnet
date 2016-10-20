package carnet.modelo.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import carnet.modelo.entidades.Afiliado;
import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Instructor;
import carnet.vista.modelos.CarnetPorEmpresa;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/applicationContext.xml"})
@Transactional
public class CarnetDaoTest {
	
	@Autowired
	private CarnetDao carnetDao;
	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	
	@Test
	public void testAgregar() {
		Afiliado afiliado = crearAfiliadoPrueba();
		Carnet carnet = crearCarnetPrueba();
		carnet.setAfiliado(afiliado);
		Carnet obtenido = carnetDao.agregar(carnet);
		entityManager.flush();
		entityManager.clear();
		
		Assert.assertFalse(obtenido.getId() == 0);
		obtenido = entityManager.find(Carnet.class, carnet.getId());
		Assert.assertNotNull(obtenido);
	}

	@Test
	public void testGetCarnetsNoImpresos() {
		Afiliado afiliado = crearAfiliadoPrueba();
		
		Carnet carnetNoImpreso = crearCarnetPrueba();
		carnetNoImpreso.setAfiliado(afiliado);
		
		Carnet carnetImpreso = crearCarnetPrueba();
		carnetImpreso.setCalificacion("B");
		carnetImpreso.setAfiliado(afiliado);
		carnetImpreso.setImpreso(true);
		
		entityManager.persist(carnetNoImpreso);
		entityManager.persist(carnetImpreso);
		entityManager.flush();
		entityManager.clear();
		
		List<Carnet> impresos = carnetDao.getCarnetsNoImpresos(null, null);
		Assert.assertNotNull(impresos);
		Assert.assertTrue(impresos.size() == 1);
		Assert.assertEquals(carnetNoImpreso, impresos.get(0));
		
		impresos = carnetDao.getCarnetsNoImpresos(afiliado.getDni(), carnetNoImpreso.getEmpresa().getNombre());
		Assert.assertNotNull(impresos);
		Assert.assertTrue(impresos.size() == 1);
		Assert.assertEquals(carnetNoImpreso, impresos.get(0));
	}
	
	@Test
	public void testGetCarnetsImpresos() {
		Calendar calendario = Calendar.getInstance();
		calendario.set(2000, Calendar.JANUARY, 1);
		Date desde = calendario.getTime();
		
		Afiliado afiliado = crearAfiliadoPrueba();
		
		Carnet carnetNoImpreso = crearCarnetPrueba();
		carnetNoImpreso.setAfiliado(afiliado);
		
		Carnet carnetImpresoIncluido = crearCarnetPrueba();
		carnetImpresoIncluido.setCalificacion("B");
		carnetImpresoIncluido.setEmision(desde);
		carnetImpresoIncluido.setAfiliado(afiliado);
		carnetImpresoIncluido.setImpreso(true);
				
		Carnet carnetImpresoNoIncluido = crearCarnetPrueba();
		carnetImpresoNoIncluido.setCalificacion("A");
		calendario.add(Calendar.YEAR, -1);
		carnetImpresoNoIncluido.setEmision(calendario.getTime());
		carnetImpresoNoIncluido.setAfiliado(afiliado);
		carnetImpresoNoIncluido.setImpreso(true);
		
		entityManager.persist(carnetNoImpreso);
		entityManager.persist(carnetImpresoIncluido);
		entityManager.persist(carnetImpresoNoIncluido);
		entityManager.flush();
		entityManager.clear();
		 
		List<Carnet> impresos = carnetDao.getCarnetImpresos(null, null, desde, Calendar.getInstance().getTime());
		Assert.assertNotNull(impresos);
		Assert.assertEquals(1, impresos.size());
		Assert.assertEquals(carnetImpresoIncluido, impresos.get(0));
	}
	
	@Test
	public void testGetEmpresasConCarnetsPorVencer() {
		//Lote de pruebas:
		/*
		 * Empresa1: 
		 * 	-Afiliado1
		 *		+Carnet no vencido +10d
		 *		+Carnet vencido
		 *		+Carnet vencido
		 *	-Afiliado2
		 *		+Carnet vencido
		 *
		 * Empresa2:
		 * 	-Afiliado3
		 * 		+Carnet x vencer(2d)
		 * 	-Afiliado4
		 * 		+Carnet x vencer 20d
		 */
		Instructor instructor = crearInstructorPrueba();
		Empresa empresa1 = new Empresa();
		empresa1.setNombre("Empresa1");
		//---Afiliado1
		Afiliado afiliado1 = new Afiliado();
		afiliado1.setDni("dni");
		afiliado1.setNombre("Afiliado1");
		
		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.DAY_OF_MONTH, 10);
		
		Carnet carnet1 = new Carnet();
		carnet1.setAfiliado(afiliado1);
		carnet1.setEmpresa(empresa1);
		carnet1.setCalificacion("1");
		carnet1.setImpreso(true);
		carnet1.setVencimiento(calendario.getTime());
		carnet1.setInstructor(instructor);
		
		calendario.add(Calendar.MONTH, -5);
		Carnet carnet2 = new Carnet();
		carnet2.setAfiliado(afiliado1);
		carnet2.setEmpresa(empresa1);
		carnet2.setCalificacion("2");
		carnet2.setImpreso(true);
		carnet2.setVencimiento(calendario.getTime());
		carnet2.setInstructor(instructor);
		
		calendario.add(Calendar.MONTH, -10);
		Carnet carnet3 = new Carnet();
		carnet3.setAfiliado(afiliado1);
		carnet3.setEmpresa(empresa1);
		carnet3.setCalificacion("3");
		carnet3.setImpreso(true);
		carnet3.setVencimiento(calendario.getTime());
		carnet3.setInstructor(instructor);
		
		//--Afiliado 2
		Afiliado afiliado2 = new Afiliado();
		afiliado2.setDni("dni");
		afiliado2.setNombre("Afiliado2");
		
		Carnet carnetVencidoAfiliado2 = new Carnet();
		carnetVencidoAfiliado2.setAfiliado(afiliado2);
		carnetVencidoAfiliado2.setEmpresa(empresa1);
		carnetVencidoAfiliado2.setCalificacion("4");
		carnetVencidoAfiliado2.setImpreso(true);
		carnetVencidoAfiliado2.setVencimiento(calendario.getTime());
		carnetVencidoAfiliado2.setInstructor(instructor);
		
		//--Empresa 2
		calendario = Calendar.getInstance();
		Empresa empresa2 = new Empresa();
		empresa2.setNombre("Empresa2");
		
		//--Afiliado 3
		Afiliado afiliado3 = new Afiliado();
		afiliado3.setDni("dni");
		afiliado3.setNombre("Afiliado3");
		
		calendario.add(Calendar.DATE, 2);
		Carnet carnet5 = new Carnet();
		carnet5.setAfiliado(afiliado3);
		carnet5.setEmpresa(empresa2);
		carnet5.setCalificacion("5");
		carnet5.setImpreso(true);
		carnet5.setVencimiento(calendario.getTime());
		carnet5.setInstructor(instructor);
		
		//--Afiliado 4
		Afiliado afiliado4 = new Afiliado();
		afiliado4.setDni("dni");
		afiliado4.setNombre("Afiliado4");
		
		calendario.add(Calendar.DATE, 18);
		Carnet carnet6 = new Carnet();
		carnet6.setAfiliado(afiliado4);
		carnet6.setEmpresa(empresa2);
		carnet6.setCalificacion("6");
		carnet6.setImpreso(true);
		carnet6.setVencimiento(calendario.getTime());
		carnet6.setInstructor(instructor);
		
		//--Persist
		entityManager.persist(empresa1);
		entityManager.persist(empresa2);
		entityManager.persist(afiliado1);
		entityManager.persist(afiliado2);
		entityManager.persist(afiliado3);
		entityManager.persist(afiliado4);
		entityManager.persist(carnet1);
		entityManager.persist(carnet2);
		entityManager.persist(carnet3);
		entityManager.persist(carnetVencidoAfiliado2);
		entityManager.persist(carnet5);
		entityManager.persist(carnet6);
		entityManager.flush();
		entityManager.clear();

		/* 
		 * Resultado 15d
		 * 	Empresa1 : 1 x vencer + 1 Vencido
		 * 	Empresa2 : 1  vencer. 
		 */
		List<CarnetPorEmpresa> resultado = carnetDao.getEmpresasConCarnetAVencerOVencidos(15);
		Assert.assertNotNull(resultado);
		Assert.assertEquals(2, resultado.size());
		CarnetPorEmpresa carnetPorEmpresa = resultado.get(0);
		Assert.assertEquals(carnetPorEmpresa.getEmpresa(), empresa1);
		Assert.assertEquals(1, carnetPorEmpresa.getCantidadPorVencer().intValue());
		Assert.assertEquals(1, carnetPorEmpresa.getCantidadVencidos().intValue());
		
		carnetPorEmpresa = resultado.get(1);
		Assert.assertEquals(carnetPorEmpresa.getEmpresa(), empresa2);
		Assert.assertEquals(1, carnetPorEmpresa.getCantidadPorVencer().intValue());
		Assert.assertEquals(0, carnetPorEmpresa.getCantidadVencidos().intValue());
		Assert.assertEquals(carnet5, carnetPorEmpresa.getCarnets().get(0));
		
		 /* 
		 * Resultado 21d
		 * 	Empresa1 : 1 x vencer + 1 Vencido
		 * 	Empresa2 : 2 x vencer. 
		 */
		resultado = carnetDao.getEmpresasConCarnetAVencerOVencidos(21);
		Assert.assertNotNull(resultado);
		Assert.assertEquals(2, resultado.size());
		carnetPorEmpresa = resultado.get(0);
		Assert.assertEquals(carnetPorEmpresa.getEmpresa(), empresa1);
		Assert.assertEquals(1, carnetPorEmpresa.getCantidadPorVencer().intValue());
		Assert.assertEquals(1, carnetPorEmpresa.getCantidadVencidos().intValue());
		
		carnetPorEmpresa = resultado.get(1);
		Assert.assertEquals(carnetPorEmpresa.getEmpresa(), empresa2);
		Assert.assertEquals(2, carnetPorEmpresa.getCantidadPorVencer().intValue());
		Assert.assertEquals(0, carnetPorEmpresa.getCantidadVencidos().intValue());
		
		/*
		 * Resultado 1d
		 * Empresa 1: 1 carnet vencido
		 */
		resultado = carnetDao.getEmpresasConCarnetAVencerOVencidos(1);
		Assert.assertNotNull(resultado);
		Assert.assertEquals(1, resultado.size());
		carnetPorEmpresa = resultado.get(0);
		Assert.assertEquals(empresa1, carnetPorEmpresa.getEmpresa());
		Assert.assertEquals(carnetVencidoAfiliado2, carnetPorEmpresa.getCarnets().get(0));
	}
	
	private Carnet crearCarnetPrueba() {
		Carnet carnet = new Carnet();
		carnet.setCalificacion("A");
		carnet.setEmision(Calendar.getInstance().getTime());
		carnet.setEmpresa(crearEmpresaPrueba());
		carnet.setInstructor(crearInstructorPrueba());
		carnet.setImpreso(false);
		carnet.setTitulo("titulo");
		carnet.setVencimiento(carnet.getEmision());
		carnet.setColor("#FFFFFF");
		carnet.setFoto(UUID.randomUUID());
		return carnet;
	}
		
	private Afiliado crearAfiliadoPrueba() {
		Afiliado afiliado = new Afiliado();
		afiliado.setApellido("Apellido2");
		afiliado.setNombre("nombre");
		afiliado.setDni("20.202.202");
		return afiliado;
	}
	
	private Instructor crearInstructorPrueba() {
		Instructor instructor = new Instructor();
		instructor.setApellido("Apellido");
		instructor.setDni("20202020");
		instructor.setFirmaId(UUID.randomUUID());
		instructor.setNombre("Nombre");
		entityManager.persist(instructor);
		entityManager.flush();
		return instructor;
	}

	private Empresa crearEmpresaPrueba() {
		Empresa empresa = new Empresa();
		empresa.setNombre("test");
		empresa.setDescripcion("descripcion");
		entityManager.persist(empresa);
		entityManager.flush();
		return empresa;
	}

	public CarnetDao getCarnetDao() {
		return carnetDao;
	}
	public void setCarnetDao(CarnetDao carnetDao) {
		this.carnetDao = carnetDao;
	}
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
