package paintex.event;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;

import paintex.ToolBar.ColorFillStyle;
import paintex.ToolBar.ImageActionType;
import paintex.ToolBar.PaintToolType;

/**
 * Event class for Toolbar event such as action (save, open) or tool select (pencil, brush)
 * @author 2004
 *
 */
public class ToolbarEvent extends AWTEvent {
	public static final String PROP_FILL_TYPE = "FillType";
	public static final String PROP_PRIMARY = "Primary";
	public static final String PROP_SECONDARY = "Secondary";
	public static final String PROP_THICKNESS = "lineThickness";

	public static final int TOOLBAR_TOOLSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 11;
	public static final int TOOLBAR_BRUSHSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 12;
	public static final int TOOLBAR_COLORSELECT		= java.awt.AWTEvent.RESERVED_ID_MAX + 13;
	public static final int TOOLBAR_IMAGEACTION		= java.awt.AWTEvent.RESERVED_ID_MAX + 14;
	public static final int TOOLBAR_TOOLPROPERTY	= java.awt.AWTEvent.RESERVED_ID_MAX + 15;

	public int selection_id;
	public ImageActionType actionType;
	public PaintToolType toolType;
	public ColorFillStyle fillType;
	public Color selectedColor;
	public String selectedProperty;
	public float thicknessSelected;
	
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
		this.selectedProperty = PROP_FILL_TYPE;
	}
	public ToolbarEvent(Component source, int id, String colorType, Color color) {
		super(source, id);
		this.selectedColor = color;
		this.selectedProperty = colorType;
	}
	public ToolbarEvent(Component source, int id, String propertyName, float thickness) {
		super(source, id);
		this.selectedProperty = propertyName;
		this.thicknessSelected = thickness;
	}
}