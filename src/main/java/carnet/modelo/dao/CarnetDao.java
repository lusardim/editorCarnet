package carnet.modelo.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import carnet.modelo.entidades.Afiliado;
import carnet.modelo.entidades.Afiliado_;
import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.Carnet_;
import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Empresa_;
import carnet.vista.modelos.CarnetPorEmpresa;

@Component("carnetDao")
@Transactional
public class CarnetDao extends DaoGenerico<Carnet, Long> {
	
	@Override
	public Carnet agregar(Carnet objeto) {
		if (objeto.getAfiliado().getId() != 0) {
			objeto.setAfiliado(getEntityManager().merge(objeto.getAfiliado()));
		}
		return super.agregar(objeto);
	}
	
	@Override
	public void actualizar(Carnet objeto) {
		if (objeto.getAfiliado().getId() != 0) {
			objeto.setAfiliado(getEntityManager().merge(objeto.getAfiliado()));
		}
		super.actualizar(objeto);
	}
	
	@Override
	public Carnet getById(Long id) {
		Carnet carnet = super.getById(id);
		Hibernate.initialize(carnet.getAfiliado());
		Hibernate.initialize(carnet.getInstructor());
		Hibernate.initialize(carnet.getEmpresa());
		return carnet;
	}
	
	public void marcarImpresos(List<Long> carnets) {
		for (long carnetId : carnets) {
			Carnet carnet = getEntityManager().find(Carnet.class, carnetId);
			carnet.setImpreso(true);
			getEntityManager().merge(carnet);
		}
	}

	public List<Carnet> getCarnetsNoImpresos(String dni, String empresa) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Carnet> query = builder.createQuery(Carnet.class);
		
		Root<Carnet> root = query.from(Carnet.class);
		Predicate predicate = builder.equal(root.get(Carnet_.impreso), false);
		
		if (dni != null) {
			Path<String> path = root.join(Carnet_.afiliado).get(Afiliado_.dni);
			predicate = builder.and(predicate, builder.equal(path, dni));
		}
		
		if (empresa != null) {
			Path<String> path = root.join(Carnet_.empresa).get(Empresa_.nombre);
			predicate = builder.and(predicate, builder.equal(path, empresa));
		}
		
		root.fetch(Carnet_.afiliado);
		query.select(root)
		  	 .where(predicate)
		  	 .orderBy(builder.asc(root.get(Carnet_.emision)));
		
		return getEntityManager()
				.createQuery(query)
				.getResultList();
	}
	
	public List<Carnet> getCarnetImpresos(String dni, String empresa, Date desde, Date hasta) {
		if (desde == null) {
			desde = Calendar.getInstance().getTime();
		}
		if (hasta == null) {
			hasta = desde;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(desde);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		desde = calendar.getTime();
		
		calendar.setTime(hasta);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		hasta = calendar.getTime();
		
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Carnet> query = builder.createQuery(Carnet.class);
		
		Root<Carnet> root = query.from(Carnet.class);
		root.fetch(Carnet_.afiliado);
		
		Predicate predicate =  builder.and(
					 builder.equal(root.get(Carnet_.impreso), true),
					 builder.greaterThanOrEqualTo(root.get(Carnet_.emision), desde),
					 builder.lessThanOrEqualTo(root.get(Carnet_.emision), hasta));
		
		if (dni != null) {
			Path<String> path = root.join(Carnet_.afiliado).get(Afiliado_.dni);
			predicate = builder.and(predicate, builder.equal(path, dni));
		}
		
		if (empresa != null) {
			Path<String> path = root.join(Carnet_.empresa).get(Empresa_.nombre);
			predicate = builder.and(predicate, builder.equal(path, empresa));
		}
		
		query.select(root)
			 .where(predicate)
		  	 .orderBy(builder.asc(root.get(Carnet_.emision)));
		
		return getEntityManager()
				.createQuery(query)
				.getResultList();
	}
	
	public List<CarnetPorEmpresa> getEmpresasConCarnetAVencerOVencidos(int cantidadDias) {
		Calendar calendario = Calendar.getInstance();
		Date hoy = calendario.getTime();
		
		calendario.add(Calendar.DATE, cantidadDias);
		
		String carnetsPorAfiliado = "SELECT c.empresa, c FROM Carnet c "
				+ " WHERE "
				+ "	c.vencimiento <= :vencimiento AND"
				+ "	c.vencimiento = (SELECT MAX(carn.vencimiento) FROM Carnet carn"
				+ "	where carn.afiliado = c.afiliado)";
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>) getEntityManager().createQuery(carnetsPorAfiliado)
				.setParameter("vencimiento", calendario.getTime(), TemporalType.DATE)
			.getResultList();
		
		Map<Long, CarnetPorEmpresa> cantidades = new HashMap<Long, CarnetPorEmpresa>();
		for (Object[] resultado : resultList) {
			Empresa empresa = (Empresa)resultado[0];
			Carnet carnet = (Carnet)resultado[1]; 

			CarnetPorEmpresa contador = cantidades.get(empresa.getId());
			if (contador == null) {
				contador = new CarnetPorEmpresa();
				contador.setEmpresa(empresa);
				cantidades.put(empresa.getId(), contador);
			}
			
			if (carnet.getVencimiento().after(hoy)) {
				Integer cantidad = contador.getCantidadPorVencer() == null ? 0 : contador.getCantidadPorVencer();
				contador.setCantidadPorVencer(1 + cantidad );
			}
			else {
				Integer cantidad = contador.getCantidadVencidos() == null ? 0 : contador.getCantidadVencidos();
				contador.setCantidadVencidos(1 + cantidad);
			}
			contador.getCarnets().add(carnet);
		}
		return new ArrayList<CarnetPorEmpresa>(cantidades.values());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAfiliados() {
		return this.getEntityManager()
				.createQuery("select a.dni from Afiliado a")
				.getResultList();
	}
	
	public Afiliado getAfiliadoPorDni(String dni) {
		try {
			CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Afiliado> query = builder.createQuery(Afiliado.class);
			Root<Afiliado> root = query.from(Afiliado.class);
			query.select(root)
				.where(builder.equal(root.get(Afiliado_.dni), dni));
				
			return getEntityManager()
					.createQuery(query)
					.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}
}
