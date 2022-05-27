package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Eraser extends Shape {
private ArrayList<Point> brushPoints;
	

	public Eraser(int x1, int y1, int x2, int y2, Color strokeColor, BasicStroke stroke, Color fillColor,
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
		//Draw each point
		for (Point p : brushPoints)
		    g.fillRect(p.x, p.y,10,10);
		
	}

}
