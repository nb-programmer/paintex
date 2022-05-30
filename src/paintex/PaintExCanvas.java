package paintex;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import paintex.event.PaintExEventMulticaster;
import paintex.shape.Shape;
import paintex.shape.ShapeFactory;
import paintex.ToolBar.ColorFillStyle;
import paintex.ToolBar.PaintToolType;
import paintex.event.CanvasUpdateEvent;
import paintex.event.CanvasUpdateListener;

/**
 * The white canvas to draw the image with various shapes and brushes
 * 
 * @author 2004
 *
 */
public class PaintExCanvas extends JPanel implements MouseListener, MouseMotionListener {
	//Image to draw onto
	private BufferedImage canvas = null, preview = null;
	
	//Properties of drawing a shape
	private PaintToolType activeTool;
	private boolean isShapeDragged = false;
	private Shape drawShape;
	private Color strokeColor;
	private Color fillColor;
	private BasicStroke strokeType;
	private boolean isFilled;
	private boolean isStroked;
	
	private CanvasUpdateListener canvasListener;

	public PaintExCanvas(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(null);
		setDoubleBuffered(true);
		setBackground(Color.GRAY);
		
		reset();
		
		clearCanvas();
		
		setFocusable(true);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.emitCanvasSizeEvent();
	}
	
	public void reset() {
		this.activeTool = PaintToolType.TOOL_PENCIL;
		this.drawShape = null;
		this.strokeColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.strokeType = new BasicStroke(1.0f);
		this.isFilled = false;
		this.isStroked = true;
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		
		//Create new image with same content
		BufferedImage newCanvas = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_INT_ARGB);
		
		//If there is an existing image
		if (canvas != null) {
			Graphics2D g = (Graphics2D) newCanvas.getGraphics();
			//Outside area will be white if new image is bigger
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, newCanvas.getWidth(), newCanvas.getHeight());
			int dw = canvas.getWidth(), dh = canvas.getHeight();
			g.drawImage(canvas, 0, 0, dw, dh, 0, 0, dw, dh, null);
		}
		
		canvas = newCanvas;
		
		//Canvas for preview while dragging
		preview = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		this.emitCanvasSizeEvent();
		repaint();
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	/**
	 * Rotate the image 90 degrees, swapping the two dimensions and retaining the image's content
	 * @param direction Positive if clockwise, negative if counter-clockwise
	 */
	private void rotateImageRightAngle(double direction) {
		double angle = 0.0;
		
		if (direction > 0.0) {
			//Rotate clockwise 90 degrees
			angle = Math.PI / 2.0;
		}
		else if (direction < 0.0) {
			//Rotate counter-clockwise 90 degrees
			angle = -Math.PI / 2.0;
		}
		else {
			//Don't rotate, just exit
			return;
		}
		//Keep a copy of the image
		BufferedImage copy = deepCopy(canvas);
		//Switch x and y axes
		setPreferredSize(new Dimension(canvas.getHeight(), canvas.getWidth()));
		//Erase image which isn't rotated yet
		clearCanvas();
		//Draw old copy but rotated clockwise 90 degrees
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		AffineTransform oldTfm = g.getTransform();
		//Translate image origin to center so rotation can be performed from the center
		g.translate(canvas.getWidth() / 2.0, canvas.getHeight() / 2.0);
		//Rotate to given angle
		g.rotate(angle);
		//Translate back to the origin after rotation
		g.translate(-copy.getWidth() / 2.0, -copy.getHeight() / 2.0);
		//Draw the original image, now with rotation
		g.drawImage(copy, 0, 0, null);
		g.setTransform(oldTfm);
	}
	
	/**
	 * Rotate the image clockwise 90 degrees
	 */
	public void rotateCW() {
		rotateImageRightAngle(1.0);
	}
	
	/**
	 * Rotate the image counter-clockwise 90 degrees
	 */
	public void rotateCCW() {
		rotateImageRightAngle(-1.0);
	}
	
	/**
	 * Erase the image with pure white color
	 */
	public void clearCanvas() {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		repaint();
	}
	
	protected void clearPreview() {
		Graphics2D g = (Graphics2D) preview.getGraphics();
		g.setBackground(new Color(0,0,0,0));
		g.clearRect(0, 0, preview.getWidth(), preview.getHeight());
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		//Anti-aliasing off to preserve the image on scaling
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		//Draw the current image
		if (canvas != null)
			g2.drawImage(canvas, 0, 0, null);

		//Draw preview image if being drawn over it
		if (preview != null && isShapeDragged)
			g2.drawImage(preview, 0, 0, null);
	}

	public void setTool(PaintToolType tool) {
		this.activeTool = tool;
	}

	public void setStrokeColor(Color c) {
		this.strokeColor = c;
	}

	public void setFillColor(Color c) {
		this.fillColor = c;
	}

	public void setLineThickness(float f) {
		this.strokeType = new BasicStroke(f);
	}
	
	public void setFillStyle(ColorFillStyle style) {
		switch (style) {
		case FILL_ONLY:
			this.isFilled = true;
			break;
		case FILL_OUTLINE_BOTH:
			this.isFilled = true;
			break;
		case OUTLINE_ONLY:
			this.isFilled = false;
			break;
		default:
			break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		emitPointerPositionEvent(e.getPoint());
		
		//Right-click to switch fill and stroke color
		Color primary = strokeColor;
		Color secondary = fillColor;
		if (SwingUtilities.isRightMouseButton(e)) {
			primary = secondary;
			secondary = strokeColor;
		}
		
		int x1 = e.getX();
		int y1 = e.getY();
		
		//Get shape selected
		drawShape = ShapeFactory.getShape(activeTool, x1, y1, primary, strokeType, secondary, isFilled, isStroked);
		clearPreview();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		emitPointerPositionEvent(e.getPoint());
		isShapeDragged = true;
		int x2 = e.getX();
		int y2 = e.getY();
		boolean mod = e.isShiftDown();
		if (drawShape != null) {
			drawShape.updatePointer(x2, y2, mod);
			//Draw preview
			clearPreview();
			drawShape.renderToImage(preview);
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		emitPointerPositionEvent(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) {
		//Draw only if mouse was dragged a bit
		if (drawShape != null) {
			boolean shouldDraw = false;
			
			if (drawShape.needsDraggingDraw() && isShapeDragged || !drawShape.needsDraggingDraw()) {
				isShapeDragged = false;
				shouldDraw = true;
			}
			
			if (shouldDraw) {
				int x2 = e.getX();
				int y2 = e.getY();
				boolean mod = e.isShiftDown();
				drawShape.updatePointer(x2, y2, mod);
				//Draw to the canvas
				drawShape.renderToImage(canvas);
				//Dispatch image modified event
				emitCanvasModifiedEvent();
			}
		}

		drawShape = null;
		clearPreview();
		repaint();
	}
	
	/**
	 * Add new CanvasUpdateListener
	 * @param l Event handler object
	 */
	public synchronized void addCanvasListener(CanvasUpdateListener l) {
		canvasListener = PaintExEventMulticaster.add(canvasListener, l);
	}
	
	/**
	 * Remove existing CanvasUpdateListener
	 * @param l Registered event handler object
	 */
	public synchronized void removeCanvasListener(CanvasUpdateListener l) {
		canvasListener = PaintExEventMulticaster.remove(canvasListener, l);
	}

	/**
	 * Update mouse position in status bar when mouse motion occurs
	 * @param e
	 */
	public void emitPointerPositionEvent(Point pt) {
		if (canvasListener == null) return;
		canvasListener.canvasPointerPosition(new CanvasUpdateEvent(this, CanvasUpdateEvent.CANVAS_POINTERPOSCHANGE, pt));
	}
	
	/**
	 * Update canvas size in status bar when resizing
	 * @param width
	 * @param height
	 */
	public void emitCanvasSizeEvent() {
		if (canvasListener == null) return;
		int width = this.canvas.getWidth();
		int height = this.canvas.getHeight();
		canvasListener.canvasDimension(new CanvasUpdateEvent(this, CanvasUpdateEvent.CANVAS_DIMENSIONCHANGE, width, height));
	}

	/**
	 * User modifies the canvas by drawing
	 */
	public void emitCanvasModifiedEvent() {
		if (canvasListener == null) return;
		canvasListener.canvasImageModify(new CanvasUpdateEvent(this, CanvasUpdateEvent.CANVAS_MODIFICATION));
	}

	/**
	 * Try to load image from file and draw it on the canvas
	 * @param imgFile
	 * @return true if successful
	 */
	public boolean loadImageFromFile(File imgFile) {
		try {
			BufferedImage img_loaded = ImageIO.read(imgFile);
			if (img_loaded == null) return false;
			this.setPreferredSize(new Dimension(img_loaded.getWidth(), img_loaded.getHeight()));
			clearCanvas();
			this.canvas.getGraphics().drawImage(img_loaded, 0, 0, null);
			return true;
		} catch (IOException e) { }
		return false;
	}

	/**
	 * Convert the image to an image format and write to the given file path
	 * @param filePath
	 * @return true if successful
	 */
	public boolean saveImageToFile(File filePath) {
		try {
			ImageIO.write(this.canvas, "png", filePath);
			return true;
		} catch (IOException e) { }
		return false;
	}
	
	/**
	 * Return a copy of the image drawn on the canvas
	 * @return BufferedImage instance
	 */
	public BufferedImage getImage() {
		return PaintExCanvas.deepCopy(canvas);
	}
}