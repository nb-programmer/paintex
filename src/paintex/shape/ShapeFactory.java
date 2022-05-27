package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;

import paintex.ToolBar.PaintToolType;

public class ShapeFactory {
	public static Shape getShape(PaintToolType activeTool, int x1, int y1, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled) {
		switch (activeTool) {
		case TOOL_BRUSH:
			break;
		case TOOL_ELLIPSE:
			break;
		case TOOL_ERASER:
			break;
		case TOOL_LINE:
			break;
		case TOOL_PENCIL:
			return new PencilShape(x1, y1, x1, y1, strokeColor, stroke, fillColor, isFilled);
		case TOOL_RECT:
			return new RectangleShape(x1, y1, x1, y1, strokeColor, stroke, fillColor, isFilled);
		}
		return null;
	}
}
