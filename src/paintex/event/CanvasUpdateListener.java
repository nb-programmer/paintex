package paintex.event;

import java.util.EventListener;

/**
 * Event listener interface for Canvas update events such as resize, modify, etc.
 * @author 2004
 * @see paintex.event.CanvasUpdateEvent
 * @see paintex.PaintExCanvas
 * @see paintex.StatusInfo
 *
 */
public interface CanvasUpdateListener extends EventListener {
    public abstract void canvasPointerPosition(CanvasUpdateEvent e);
    public abstract void canvasDimension(CanvasUpdateEvent e);
    public abstract void canvasImageModify(CanvasUpdateEvent e);
}

