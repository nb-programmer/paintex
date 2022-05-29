package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Rectangle shape to draw rectangle or square
 * @author 2004
 *
 */
public class RectangleShape extends Shape {
	public RectangleShape(int x1, int y1, int x2, int y2, Color strokeColor, BasicStroke stroke, Color fillColor,
			boolean isFilled) {
		super(x1, y1, x2, y2, strokeColor, stroke, fillColor, isFilled);
	}

	@Override
	protected void render(BufferedImage img, Graphics2D g, int x, int y, int w, int h) {
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
		g.drawRect(x, y, w, h);
	}
}
