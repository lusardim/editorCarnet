package carnet.modelo.entidades;

import java.util.UUID;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Empresa.class)
public class Empresa_ {
	public static volatile SingularAttribute<Empresa, Boolean> eliminada;
	public static volatile SingularAttribute<Empresa, String> nombre;
	public static volatile SingularAttribute<Empresa, UUID> imagenInferior;
}
