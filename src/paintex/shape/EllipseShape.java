package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EllipseShape extends Shape {

	public EllipseShape(int x1, int y1, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled) {
		super(x1, y1, strokeColor, stroke, fillColor, isFilled);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void render(BufferedImage img, Graphics2D g, int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		if (w < 0) {
			x += w;
			w *= -1;
		}
		if (h < 0) {
			y += h;
			h *= -1;
		}
		//Draw the shape
				if (this.isFilled) {
					g.setColor(this.fillColor);
					g.fillRect(x, y, w, h);
				}
				
		g.setColor(this.strokeColor);
		g.drawOval(x, y, w, h);
	}

}
