package paintex.shape;

import java.awt.BasicStroke;
import java.awt.Color;

import paintex.ToolBar.PaintToolType;

public class ShapeFactory {
	public static Shape getShape(PaintToolType activeTool, int x1, int y1, Color strokeColor, BasicStroke stroke, Color fillColor, boolean isFilled, boolean isStroked) {
		switch (activeTool) {
		case TOOL_ERASER:
			return new Eraser(x1, y1,x1, y1, strokeColor, stroke, fillColor, isFilled);
		case TOOL_ELLIPSE:
			return new EllipseShape(x1, y1, strokeColor, stroke, fillColor, isFilled);
		case TOOL_BRUSH:
			return new BrushShape(x1, y1,x1,y1,strokeColor, stroke, fillColor, isFilled);
		case TOOL_LINE:
			return new LineShape(x1, y1, strokeColor, stroke, fillColor, isFilled);
		case TOOL_PENCIL:
			return new PencilShape(x1, y1, x1, y1, strokeColor, stroke, fillColor, isFilled);
		case TOOL_RECT:
			return new RectangleShape(x1, y1, x1, y1, strokeColor, stroke, fillColor, isFilled);
		}
		return null;
	}
}
