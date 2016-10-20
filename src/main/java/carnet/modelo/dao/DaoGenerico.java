package carnet.modelo.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;
 
@Transactional
public abstract class DaoGenerico<T, ID extends Serializable> {

	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public DaoGenerico() {
	    this.clazz = (Class<T>) ((ParameterizedType) 
	    		this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
		
	//ABM
	public T agregar(T objeto) {
		entityManager.persist(objeto);
		entityManager.refresh(objeto);
		return objeto;
	}
	
	public void actualizar(T objeto) {
		entityManager.merge(objeto);
	}
		
	public void eliminar(T objeto) {
		entityManager.refresh(objeto);
		entityManager.remove(objeto);
	}
	
	public void eliminar(ID id) {
		T objeto = this.getById(id);
		eliminar(objeto);
	}
	
	//Búsquedas
	@Transactional
	public T getById(ID id) {
		return entityManager.find(clazz, id);
	}
	
	@Transactional(readOnly = true)
	public List<T> getAll() {
		CriteriaQuery<T> query = entityManager
				.getCriteriaBuilder()
				.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.select(root);
		return entityManager
					.createQuery(query)
					.getResultList();
	}
	
	//Injectados
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
