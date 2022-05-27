package paintex.event;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;

import paintex.ToolBar.ColorFillStyle;
import paintex.ToolBar.ImageActionType;
import paintex.ToolBar.PaintToolType;

public class ToolbarEvent extends AWTEvent {
	public static final String TARGET_FILL_TYPE = "FillType";
	public static final String TARGET_PRIMARY = "Primary";
	public static final String TARGET_SECONDARY = "Secondary";

	public static final int TOOLBAR_TOOLSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 11;
	public static final int TOOLBAR_BRUSHSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 12;
	public static final int TOOLBAR_COLORSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 13;
	public static final int TOOLBAR_IMAGEACTION		= java.awt.AWTEvent.RESERVED_ID_MAX + 14;

	public int selection_id;
	public ImageActionType actionType;
	public PaintToolType toolType;
	public ColorFillStyle fillType;
	public Color selectedColor;
	public String selectTarget;
	
	public ToolbarEvent(Component source, int id) {
		super(source, id);
	}
	public ToolbarEvent(Component source, int id, PaintToolType toolType) {
		super(source, id);
		this.toolType = toolType;
	}
	public ToolbarEvent(Component source, int id, ImageActionType actionType) {
		super(source, id);
		this.actionType = actionType;
	}
	public ToolbarEvent(Component source, int id, ColorFillStyle fillType) {
		super(source, id);
		this.fillType = fillType;
		this.selectTarget = TARGET_FILL_TYPE;
	}
	public ToolbarEvent(Component source, int id, Color color, String colorType) {
		super(source, id);
		this.selectedColor = color;
		this.selectTarget = colorType;
	}
}