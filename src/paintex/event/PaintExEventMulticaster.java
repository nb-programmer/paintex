package paintex.event;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

public class PaintExEventMulticaster extends AWTEventMulticaster
		implements CanvasUpdateListener, ToolbarListener {
	protected PaintExEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public static CanvasUpdateListener add(CanvasUpdateListener a, CanvasUpdateListener b) {
		return (CanvasUpdateListener) addInternal(a, b);
	}

	public static CanvasUpdateListener remove(CanvasUpdateListener l, CanvasUpdateListener oldl) {
		return (CanvasUpdateListener) removeInternal(l, oldl);
	}
	
	public static ToolbarListener  add(ToolbarListener a, ToolbarListener  b) {
		return (ToolbarListener) addInternal(a, b);
	}

	public static ToolbarListener remove(ToolbarListener l, ToolbarListener oldl) {
		return (ToolbarListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null) return b;
		if (b == null) return a;
		return new PaintExEventMulticaster(a, b);
	}

	protected EventListener remove(EventListener oldl) {
		if (oldl == a) return b;
		if (oldl == b) return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) return this;
		return addInternal(a2, b2);
	}

	@Override
	public void canvasPointerPosition(CanvasUpdateEvent e) {
        if (a != null) ((CanvasUpdateListener) a).canvasPointerPosition(e);
        if (b != null) ((CanvasUpdateListener) b).canvasPointerPosition(e);
	}

	@Override
	public void canvasDimension(CanvasUpdateEvent e) {
		if (a != null) ((CanvasUpdateListener) a).canvasDimension(e);
        if (b != null) ((CanvasUpdateListener) b).canvasDimension(e);
	}

	@Override
	public void toolSelect(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).toolSelect(e);
        if (b != null) ((ToolbarListener) b).toolSelect(e);
	}

	@Override
	public void brushSelect(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).brushSelect(e);
        if (b != null) ((ToolbarListener) b).brushSelect(e);
	}

	@Override
	public void colorSelect(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).colorSelect(e);
        if (b != null) ((ToolbarListener) b).colorSelect(e);
	}

	@Override
	public void imageSave(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).imageSave(e);
        if (b != null) ((ToolbarListener) b).imageSave(e);
	}

	@Override
	public void imageOpen(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).imageOpen(e);
        if (b != null) ((ToolbarListener) b).imageOpen(e);
	}

	@Override
	public void imageNew(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).imageNew(e);
        if (b != null) ((ToolbarListener) b).imageNew(e);
	}

	@Override
	public void imagePrint(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).imagePrint(e);
        if (b != null) ((ToolbarListener) b).imagePrint(e);
	}

	@Override
	public void imageResize(ToolbarEvent e) {
		if (a != null) ((ToolbarListener) a).imageResize(e);
        if (b != null) ((ToolbarListener) b).imageResize(e);
	}
}
