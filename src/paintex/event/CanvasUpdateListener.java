package paintex.event;

import java.util.EventListener;

public interface CanvasUpdateListener extends EventListener {
    public abstract void canvasPointerPosition(CanvasUpdateEvent e);
    public abstract void canvasDimension(CanvasUpdateEvent e);
    public abstract void canvasImageModify(CanvasUpdateEvent e);
}

