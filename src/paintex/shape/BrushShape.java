package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Brush which draws thick paint with primary color
 * @author 2031
 *
 */
public class BrushShape extends Shape {
	private ArrayList<Point> brushPoints;
	
	public BrushShape(int x1, int y1, int x2, int y2, Color strokeColor, BasicStroke stroke, Color fillColor,
			boolean isFilled) {
		super(x1, y1, x2, y2, strokeColor, stroke, fillColor, isFilled);
		this.brushPoints = new ArrayList<Point>();
	}
	
	@Override
	public void updatePointer(int x, int y, boolean modifier) {
		super.updatePointer(x, y, modifier);
		this.brushPoints.add(new Point(x, y));
	}

	@Override
	protected void render(BufferedImage img, Graphics2D g, int x, int y, int w, int h) {
		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

		//Start at first point
		polyline.moveTo(x1, y1);

		//Draw each point
		for (Point p : brushPoints)
		    polyline.lineTo(p.x, p.y);

		//Draw path to the image
		float width = 15.0f + this.stroke.getLineWidth() * 2.0f;
		g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g.draw(polyline);
	}
}
