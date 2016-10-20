package carnet.modelo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carnet.modelo.entidades.Instructor;
import carnet.modelo.entidades.Instructor_;

@Component("instructorDao")
@Transactional
public class InstructorDao extends DaoGenerico<Instructor, Long> {
	@Override
	public void eliminar(Instructor instructor) {
		instructor.setEliminado(true);
		getEntityManager().merge(instructor);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Instructor> getAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Instructor> query = builder.createQuery(Instructor.class);
		Root<Instructor> root = query.from(Instructor.class);
		query.select(root)
			 .where(builder.or(
					 builder.equal(root.get(Instructor_.eliminado), false),
					 builder.isNull(root.get(Instructor_.eliminado))
					));
		return getEntityManager()
					.createQuery(query)
					.getResultList();
	}
}
