package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Shape {
	protected int x1;
	protected int x2;
	protected int y1;
	protected int y2;
	protected Color strokeColor;
	protected BasicStroke stroke;
	protected Color fillColor;
	protected boolean isFilled;
	protected boolean isModifier;

	public Shape(int x1, int y1, int x2, int y2, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.strokeColor = strokeColor;
		this.stroke = stroke;
		this.fillColor = fillColor;
		this.isFilled = isFilled;
		this.isModifier = false;
	}
	
	public Shape(int x1, int y1, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled) {
		this(x1, y1, x1, y1, strokeColor, stroke, fillColor, isFilled);
	}
	
	public void setX1(int x) {
		this.x1 = x;
	}

	public void setX2(int x) {
		this.x2 = x;
	}

	public void setY1(int y) {
		this.y1 = y;
	}

	public void setY2(int y) {
		this.y2 = y;
	}
	
	public boolean needsDraggingDraw() {
		return true;
	}
	
	/**
	 * What action to perform when mouse moves dragging the shape
	 * @param x
	 * @param y
	 */
	public void updatePointer(int x, int y, boolean modifier) {
		this.x2 = x;
		this.y2 = y;
		this.isModifier = modifier;
	}

	/**
	 * Draw this shape to the given image
	 * @param img
	 */
	public void renderToImage(BufferedImage img) {
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		int	px = x1,
			py = y1,
			w = x2 - x1,
			h = y2 - y1;
		
		//Shift-key to make shape equal size
		if (this.isModifier) {
			int bigDim = Math.max(Math.abs(w), Math.abs(h));
			int w2 = (int) (bigDim * Math.signum(w));
			int h2 = (int) (bigDim * Math.signum(h));
			w = w2;
			h = h2;
		}
		
		g.setStroke(this.stroke);
		g.setColor(this.strokeColor);
		render(img, g, px, py, w, h);
		g.dispose();
	}
	
	protected abstract void render(BufferedImage img, Graphics2D graphics, int x, int y, int w, int h);
}
