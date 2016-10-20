package carnet.modelo.entidades;

public enum ProtocoloSeguridadEmail {
	STARTTLS(25),
	SSL(465),
	PLANO(0);
	
	private int puertoDefecto;
	
	private ProtocoloSeguridadEmail(int puerto) {
		this.puertoDefecto = puerto;
	}

	public int getPuertoDefecto() {
		return puertoDefecto;
	}
	
}
