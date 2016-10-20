package carnet.modelo.entidades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CryptoConverterTest {

	private CryptoConverter cifrador;
	
	@Before
	public void init() throws Exception {
		cifrador = new CryptoConverter();
	}
	
	@Test
	public void testEncriptar() {
		String encriptado = "EsteTextoNoDeberíaSerLegible";
		String resultado = cifrador.convertToDatabaseColumn(encriptado);
		Assert.assertNotNull(resultado);
		Assert.assertNotEquals(encriptado, resultado);
	}
	
	@Test
	public void testDesencriptar() {
		String encriptadoOriginal = "Yix9QrkjXMebHH4IN3EZ3uCZJVv3wJ+09OJba48T9qo=";
		String esperado = "Encriptar todo esto";
		String resultado = cifrador.convertToDatabaseColumn(esperado);
		Assert.assertNotNull(resultado);
		Assert.assertNotEquals(esperado, resultado);
		
		String obtenido = cifrador.convertToEntityAttribute(resultado);
		Assert.assertNotNull(obtenido);
		Assert.assertEquals(esperado, obtenido);
		
		resultado = cifrador.convertToEntityAttribute(encriptadoOriginal);
		Assert.assertNotNull(resultado);
		Assert.assertEquals(esperado, resultado);
	}
	
}
