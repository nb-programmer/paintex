package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Eraser extends Shape {
	private ArrayList<Point> eraserPoints;
	
	public Eraser(int x1, int y1, int x2, int y2, Color strokeColor, BasicStroke stroke, Color fillColor,
			boolean isFilled) {
		super(x1, y1, x2, y2, strokeColor, stroke, fillColor, isFilled);
		this.eraserPoints = new ArrayList<Point>();
	}
	
	@Override
	public void updatePointer(int x, int y, boolean modifier) {
		super.updatePointer(x, y, modifier);
		this.eraserPoints.add(new Point(x, y));
	}
	
	@Override
	public boolean needsDraggingDraw() {
		return false;
	}

	@Override
	protected void render(BufferedImage img, Graphics2D g, int x, int y, int w, int h) {
		int eraserSize = (int) (10.0 + this.stroke.getLineWidth() * 3.0);
		//Draw each point as rectangle
		g.setColor(this.fillColor);
		for (Point p : eraserPoints)
		    g.fillRect(p.x - eraserSize/2, p.y - eraserSize/2, eraserSize, eraserSize);
	}
}
