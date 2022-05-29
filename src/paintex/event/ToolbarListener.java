package paintex.event;

import java.util.EventListener;

/**
 * Event listener for ToolbarEvent called by various toolbar buttons
 * @author 2004
 * @see paintex.event.ToolbarEvent
 * @see paintex.ToolBar
 * @see paintex.ColorPaletteToolbar
 */
public interface ToolbarListener extends EventListener {
    public abstract void toolSelect(ToolbarEvent e);
    public abstract void brushSelect(ToolbarEvent e);
    public abstract void colorSelect(ToolbarEvent e);
    public abstract void toolProperty(ToolbarEvent e);
    public abstract void imageNew(ToolbarEvent e);
    public abstract void imageOpen(ToolbarEvent e);
    public abstract void imageSave(ToolbarEvent e);
    public abstract void imagePrint(ToolbarEvent e);
    public abstract void imageResize(ToolbarEvent e);
    public abstract void imageRotate(ToolbarEvent e);
}

