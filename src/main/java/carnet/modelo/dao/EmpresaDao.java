package carnet.modelo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Empresa_;

@Component("empresaDao")
@Transactional
public class EmpresaDao extends DaoGenerico<Empresa, Long> {

	@Override
	public void eliminar(Empresa instructor) {
		instructor.setEliminada(true);
		getEntityManager().merge(instructor);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Empresa> getAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Empresa> query = builder.createQuery(Empresa.class);
		Root<Empresa> root = query.from(Empresa.class);
		query.select(root)
			 .where(builder.or(
					 builder.equal(root.get(Empresa_.eliminada), false),
					 builder.isNull(root.get(Empresa_.eliminada))
					));
		return getEntityManager()
					.createQuery(query)
					.getResultList();
	}
	
	public List<String> getNombres() {
		List<Empresa> empresas = getAll();
		List<String> nombres = new ArrayList<String>(empresas.size());
		for (Empresa empresa : empresas) {
			nombres.add(empresa.getNombre());
		}
		return nombres;
	}
	
	
}
