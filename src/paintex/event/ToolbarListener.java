package paintex.event;

import java.util.EventListener;

public interface ToolbarListener extends EventListener {
    public abstract void toolSelect(ToolbarEvent e);
    public abstract void brushSelect(ToolbarEvent e);
    public abstract void colorSelect(ToolbarEvent e);
    public abstract void imageNew(ToolbarEvent e);
    public abstract void imageOpen(ToolbarEvent e);
    public abstract void imageSave(ToolbarEvent e);
    public abstract void imagePrint(ToolbarEvent e);
    public abstract void imageResize(ToolbarEvent e);
}

