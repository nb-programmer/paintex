package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Straight line between starting and ending points
 * @author 2031
 *
 */
public class LineShape extends Shape {

	public LineShape(int x1, int y1, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled) {
		super(x1, y1, strokeColor, stroke, fillColor, isFilled);
	}

	@Override
	protected void render(BufferedImage img, Graphics2D g, int x, int y, int w, int h) {		
		g.setColor(this.strokeColor);
		g.drawLine(this.x1,this.y1,this.x2,this.y2);

	}

}
