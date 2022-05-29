package paintex.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;

/**
 * Event class for Canvas-related event such as mouse position change, resize, modify
 * @author 2004
 *
 */
public class CanvasUpdateEvent extends AWTEvent {
	public static final int CANVAS_POINTERPOSCHANGE = java.awt.AWTEvent.RESERVED_ID_MAX + 1;
	public static final int CANVAS_DIMENSIONCHANGE = java.awt.AWTEvent.RESERVED_ID_MAX + 2;
	public static final int CANVAS_MODIFICATION = java.awt.AWTEvent.RESERVED_ID_MAX + 3;
	
	public int x, y;
	
	public CanvasUpdateEvent(Component source, int id) {
		super(source, id);
	}
	public CanvasUpdateEvent(Component source, int id, int x, int y) {
		super(source, id);
		this.x = x;
		this.y = y;
	}
	public CanvasUpdateEvent(Component source, int id, Point p) {
		super(source, id);
		this.x = (int) p.getX();
		this.y = (int) p.getY();
	}
}