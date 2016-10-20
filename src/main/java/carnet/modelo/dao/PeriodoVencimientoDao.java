package carnet.modelo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carnet.modelo.entidades.PeriodoVencimiento;
import carnet.modelo.entidades.PeriodoVencimiento_;

@Component("periodoVencimientoDao")
public class PeriodoVencimientoDao extends DaoGenerico<PeriodoVencimiento, Long>{
	@Override
	public void eliminar(PeriodoVencimiento periodoVencimiento) {
		periodoVencimiento.setEliminado(true);
		getEntityManager().merge(periodoVencimiento);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PeriodoVencimiento> getAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PeriodoVencimiento> query = builder.createQuery(PeriodoVencimiento.class);
		Root<PeriodoVencimiento> root = query.from(PeriodoVencimiento.class);
		query.select(root)
			 .where(builder.or(
					 builder.equal(root.get(PeriodoVencimiento_.eliminado), false),
					 builder.isNull(root.get(PeriodoVencimiento_.eliminado))
					));
		return getEntityManager()
					.createQuery(query)
					.getResultList();
	}
}
