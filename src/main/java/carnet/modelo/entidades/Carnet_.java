package carnet.modelo.entidades;

import java.util.Date;
import java.util.UUID;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Carnet.class)
public class Carnet_ {
	public static volatile SingularAttribute<Carnet, Boolean> impreso;
	public static volatile SingularAttribute<Carnet, Date> emision;
	public static volatile SingularAttribute<Carnet, Date> vencimiento;
	public static volatile SingularAttribute<Carnet, Afiliado> afiliado;
	public static volatile SingularAttribute<Carnet, Empresa> empresa;
	public static volatile SingularAttribute<Carnet, UUID> foto;
}
