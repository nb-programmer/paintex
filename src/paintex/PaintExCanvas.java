package paintex;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import paintex.event.PaintExEventMulticaster;
import paintex.shape.Shape;
import paintex.shape.ShapeFactory;
import paintex.ToolBar.PaintToolType;
import paintex.event.CanvasUpdateEvent;
import paintex.event.CanvasUpdateListener;

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
		this.emitCanvasSizeEvent(width, height);
	}
	
	public void reset() {
		this.activeTool = PaintToolType.TOOL_PENCIL;
		this.drawShape = null;
		this.strokeColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.strokeType = new BasicStroke(1.0f);
		this.isFilled = false;
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
		
		this.emitCanvasSizeEvent(preferredSize.width, preferredSize.height);
		repaint();
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

	@Override
	public void mousePressed(MouseEvent e) {
		emitPointerPositionEvent(e);
		
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
		drawShape = ShapeFactory.getShape(activeTool, x1, y1, primary, strokeType, secondary, isFilled);
		clearPreview();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		emitPointerPositionEvent(e);
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
		emitPointerPositionEvent(e);
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
		if (isShapeDragged) {
			isShapeDragged = false;
			if (drawShape != null) {
				int x2 = e.getX();
				int y2 = e.getY();
				boolean mod = e.isShiftDown();
				drawShape.updatePointer(x2, y2, mod);
				//Draw to the canvas
				drawShape.renderToImage(canvas);
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
	public void emitPointerPositionEvent(MouseEvent e) {
		if (canvasListener == null) return;
		canvasListener.canvasPointerPosition(new CanvasUpdateEvent(this, CanvasUpdateEvent.CANVAS_POINTERPOSCHANGE, e.getPoint()));
	}

	/**
	 * Update canvas size in status bar when resizing
	 * @param width
	 * @param height
	 */
	public void emitCanvasSizeEvent(int width, int height) {
		if (canvasListener == null) return;
		canvasListener.canvasDimension(new CanvasUpdateEvent(this, CanvasUpdateEvent.CANVAS_DIMENSIONCHANGE, width, height));
	}
}