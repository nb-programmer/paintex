package paintex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import paintex.event.PaintExEventMulticaster;
import paintex.event.ToolbarEvent;
import paintex.event.ToolbarListener;

import javax.swing.JColorChooser;

public class ColorPalettePanel extends JToolBar {
	
	protected Color[] colorSwatch = {
		Color.BLACK,
		Color.WHITE,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.LIGHT_GRAY,
		Color.RED,
		Color.GREEN,
		Color.BLUE,
		Color.CYAN,
		Color.YELLOW,
		Color.MAGENTA
	};
	
	private ActionListener swatchListener;
	private ToolbarListener toolbarListener;

	private ColorSwatchButton primaryColor;
	private ColorSwatchButton secondaryColor;

	public ColorPalettePanel() {
		super(JToolBar.HORIZONTAL);
		swatchListener = new ColorSwatchAction(this);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
		setFloatable(false);
		initializeColorChooser();
		reset();
	}
	
	public void setPrimaryColor(Color col) {
		this.primaryColor.setBackground(col);
		if (toolbarListener != null)
			toolbarListener.colorSelect(new ToolbarEvent(this, ToolbarEvent.TOOLBAR_COLORSELECT, "Primary", col));
	}
	
	public void setSecondaryColor(Color col) {
		this.secondaryColor.setBackground(col);
		if (toolbarListener != null)
			toolbarListener.colorSelect(new ToolbarEvent(this, ToolbarEvent.TOOLBAR_COLORSELECT, "Secondary", col));
	}
	
	private class ColorSwatchButton extends JButton {
		public SwatchType type;
		public ColorSwatchButton(Color c, SwatchType type) {
			super("");
			this.type = type;
			setPreferredSize(new Dimension(25, 25));
			setBackground(c);
		}
		public ColorSwatchButton(Color c) {
			this(c, SwatchType.SWATCH_COLORPRESET);
		}
	}
	
	private class ColorSwatchAction implements ActionListener {
		ColorPalettePanel parent;
		public ColorSwatchAction(ColorPalettePanel parent) {
			super();
			this.parent = parent;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ColorSwatchButton btn = (ColorSwatchButton)e.getSource();
			Color selCol = null;
			String colType = null;
			switch (btn.type) {
			case SWATCH_CHOOSEWHEEL_PRIMARY:
				colType = ToolbarEvent.PROP_PRIMARY;
				selCol = getColorChooserColor(btn.getBackground(), colType);
				break;
			case SWATCH_CHOOSEWHEEL_SECONDARY:
				colType = ToolbarEvent.PROP_SECONDARY;
				selCol = getColorChooserColor(btn.getBackground(), colType);
				break;
			case SWATCH_COLORPRESET:
				selCol = btn.getBackground();
				//Choose secondary color if Shift-key is held
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0)
					colType = ToolbarEvent.PROP_PRIMARY;
				else
					colType = ToolbarEvent.PROP_SECONDARY;
				break;
			default:
				break;
			}
			
			if (selCol != null) {
				if (colType.compareTo(ToolbarEvent.PROP_PRIMARY) == 0)
					parent.setPrimaryColor(selCol);
				else if (colType.compareTo(ToolbarEvent.PROP_SECONDARY) == 0)
					parent.setSecondaryColor(selCol);
			}
		}
		
		private Color getColorChooserColor(Color initial, String type) {
			return JColorChooser.showDialog(parent, String.format("%s color", type), initial, true);
		}
	}

	private void initializeColorChooser() {
		for (Color c : colorSwatch) {
			JButton colBtn = new ColorSwatchButton(c);
			colBtn.addActionListener(this.swatchListener);
			add(colBtn);
		}
		
		addSeparator();
		
		primaryColor = new ColorSwatchButton(Color.BLACK, SwatchType.SWATCH_CHOOSEWHEEL_PRIMARY);
		secondaryColor = new ColorSwatchButton(Color.WHITE, SwatchType.SWATCH_CHOOSEWHEEL_SECONDARY);

		primaryColor.addActionListener(this.swatchListener);
		secondaryColor.addActionListener(this.swatchListener);

		add(new JLabel("Primary color:"));
		add(primaryColor);
		add(new JLabel("Secondary color:"));
		add(secondaryColor);
	}
	
	public void reset() {
		setPrimaryColor(Color.BLACK);
		setSecondaryColor(Color.WHITE);
	}
	
	public synchronized void addToolbarListener(ToolbarListener l) {
		toolbarListener = PaintExEventMulticaster.add(toolbarListener, l);
	}
	
	public synchronized void removeToolbarListener(ToolbarListener  l) {
		toolbarListener = PaintExEventMulticaster.remove(toolbarListener, l);
	}
}
