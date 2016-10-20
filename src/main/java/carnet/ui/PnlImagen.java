package carnet.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PnlImagen extends JPanel{

	private static final long serialVersionUID = -6072040976189122437L;
	private BufferedImage image;

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    if (image != null) {
	    	Graphics2D g2 = image.createGraphics();
	    	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    	g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null); 
	    }
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
