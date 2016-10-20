package carnet.modelo.dao;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import carnet.modelo.entidades.Carnet;
import carnet.modelo.entidades.Carnet_;
import carnet.modelo.entidades.Empresa;
import carnet.modelo.entidades.Empresa_;
import carnet.modelo.entidades.Instructor;
import carnet.modelo.entidades.Instructor_;

public class ImagenDao {
	private static final Logger LOGGER = LogManager.getLogger();
	private Path path;
	@PersistenceContext(name="carnetDb")
	private EntityManager entityManager;
	
	public UUID guardarImagen(BufferedImage image) throws Exception {
		UUID id = UUID.randomUUID();
		Path filePath = path.resolve(id.toString() + ".png");
		try {
			ImageIO.write(image, "png", filePath.toFile());
			return id;
		}
		catch (Exception e) {
			LOGGER.error("A ocurrido un error al guardar la imagen", e);
			throw e;
		}
	}
	
	public BufferedImage getImage(UUID id) throws Exception {
		Path filePath = path.resolve(id.toString() + ".png");
		try {
			File file = filePath.toFile();
			if (!file.exists()) {
				throw new IllegalArgumentException(
						"La imagen no se encuentra disponible o ha sido eliminada " + id.toString());
			}
			return ImageIO.read(file);
		}
		catch (Exception e) {
			LOGGER.error("A ocurrido un error al leer la imagen", e);
			throw e;
		}
	}
	
	public File getFileImagen(UUID id) throws Exception {
		if (id == null) {
			throw new NullPointerException("El id no de la imagen no puede ser nulo");
		}
		Path filePath = path.resolve(id.toString() + ".png");
		File file = filePath.toFile();
		if (file.exists()) {
			return file;
		}
		return null;
	}
	
	public boolean eliminarImagen(UUID id) {
		Path filePath = path.resolve(id.toString() + ".png");
		File file = filePath.toFile();
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
	
	@Transactional
	public void limpiarImagenes() {
		Set<UUID> imagenes = new HashSet<UUID>();
		imagenes.addAll(getListaFirmas());
		imagenes.addAll(getListaFotosCarnet());
		imagenes.addAll(getListaImagenesEmpresa());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path))
		{
			for (Path entry : stream) {
				if (Files.exists(entry)) 
				{
					String name = entry.getFileName().toString();
					name = name.substring(0, name.lastIndexOf("."));
					try {
						UUID idOfFile = UUID.fromString(name);
						if (!imagenes.contains(idOfFile)) {
							Files.delete(entry);
						}
					}
					catch (IllegalArgumentException e){
						LOGGER.info("Ignoring file : " + entry.getFileName());
					}
				}
			}
		}
		catch (IOException e) {
			LOGGER.error("Error limpieza imagenes", e);
		}
	}
	
	//FIXME esto puede ser reemplazado por una clase imagen y agregarla como relación
	private List<UUID> getListaFotosCarnet() 
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UUID> query = builder.createQuery(UUID.class);
		Root<Carnet> root = query.from(Carnet.class);
		
		query.select(root.get(Carnet_.foto))
			  .distinct(true);
		List<UUID> imagenes = entityManager.createQuery(query).getResultList();
		return imagenes;
	}
	
	private List<UUID> getListaFirmas() 
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UUID> query = builder.createQuery(UUID.class);
		Root<Instructor> root = query.from(Instructor.class);
		
		query.select(root.get(Instructor_.firmaId))
			  .distinct(true);
		List<UUID> imagenes = entityManager.createQuery(query).getResultList();
		return imagenes;
	}
	
	private List<UUID> getListaImagenesEmpresa() 
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UUID> query = builder.createQuery(UUID.class);
		Root<Empresa> root = query.from(Empresa.class);
		
		query.select(root.get(Empresa_.imagenInferior))
			  .distinct(true);
		List<UUID> imagenes = entityManager.createQuery(query).getResultList();
		return imagenes;
	}
	
	public void setPath(String pathUri) throws Exception {
		path = Paths.get(pathUri);
		if (!Files.exists(path)) {
			Files.createDirectory(path);
		}
	}
	
	public Path getPath() {
		return path;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
