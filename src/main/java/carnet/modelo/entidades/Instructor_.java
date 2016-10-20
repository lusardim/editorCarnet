package carnet.modelo.entidades;

import java.util.UUID;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Instructor.class)
public class Instructor_ {
	public static volatile SingularAttribute<Instructor, Boolean> eliminado;
	public static volatile SingularAttribute<Instructor, UUID> firmaId;
}
