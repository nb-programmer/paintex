package paintex;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import paintex.event.PaintExEventMulticaster;
import paintex.event.ToolbarEvent;
import paintex.event.ToolbarListener;

public class ToolBar extends JToolBar implements ActionListener {
	
	/**
	 * Action to perform on image
	 */
	public enum ImageActionType {
		ACTION_NEW("New"),
		ACTION_OPEN("Open"),
		ACTION_SAVE("Save"),
		ACTION_PRINT("Print"),
		ACTION_RESIZE("Resize", "/icons/size.png");
		
		public String text;
		public String ico_uri;
		
		ImageActionType(String text, String icon) {
			this.text = text;
			this.ico_uri = icon;
		}
		ImageActionType(String text) {
			this(text, null);
		}
	}
	
	/**
	 * Buttons for various tools
	 */
	public enum PaintToolType {
		TOOL_PENCIL("Pencil", "/icons/PencilToolIcon.192.png"),
		TOOL_LINE("Line", "/icons/Line-24.png"),
		TOOL_RECT("Rectangle", "/icons/Rectangle-24.png"),
		TOOL_ELLIPSE("Ellipse", "/icons/Ellipse-24.png"),
		TOOL_BRUSH("Brush", "/icons/PaintBrushToolIcon.192.png"),
		TOOL_ERASER("Eraser", "/icons/EraserToolIcon.192.png");
		
		public String text;
		public String ico_uri;
		
		PaintToolType(String text, String icon) {
			this.text = text;
			this.ico_uri = icon;
		}
		PaintToolType(String text) {
			this(text, null);
		}
	}
	
	/**
	 *  Custom button class
	 *
	 */
	public class ToolActionButton extends JButton {
		ImageActionType type;
		public ToolActionButton(String text, ImageActionType type) {
			super(text);
			this.type = type;
		}
		public ToolActionButton(String text, ImageIcon icon, ImageActionType type) {
			super(text, icon);
			this.type = type;
		}
	}
	public class ToolSelectButton extends JButton {
		PaintToolType type;
		public ToolSelectButton(String text, PaintToolType type) {
			super(text);
			this.type = type;
		}
		public ToolSelectButton(String text, ImageIcon icon, PaintToolType type) {
			super(text, icon);
			this.type = type;
		}
	}

	private Map<ImageActionType, ToolActionButton> tool_action_buttons;
	private Map<PaintToolType, ToolSelectButton> tool_select_buttons;
	
	private JComboBox<String> selectLineThickness;
	private JToggleButton toolIsFillBox;
	
	private JFileChooser fc;
	private ToolbarListener toolbarListener;

	public ToolBar(PaintExWindow window) {
		super("Tools", JToolBar.VERTICAL);
		
		this.tool_action_buttons = new HashMap<ImageActionType,ToolActionButton>();
		this.tool_select_buttons = new HashMap<PaintToolType,ToolSelectButton>(); 
		
		fc = new JFileChooser(new File("."));
		fc.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));
		
		initializeToolBar();
		reset();
	}

	private void initializeToolBar() {
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		this.setFloatable(false);
		this.setLayout(new GridLayout(18, 0));
		
		//Open, save, etc.
		for (ImageActionType action : ImageActionType.values()) {
			ToolActionButton tool_btn = new ToolActionButton(action.text, action);
			if (action.ico_uri != null)
				tool_btn.setIcon(new ImageIcon(this.getClass().getResource(action.ico_uri)));
			tool_action_buttons.put(action, tool_btn);
			this.add(tool_btn);
			tool_btn.addActionListener(this);
		}

		this.addSeparator();
		
		//Pencil, rectangle, etc.
		for (PaintToolType tool : PaintToolType.values()) {
			ToolSelectButton tool_btn = new ToolSelectButton(tool.text, tool);
			if (tool.ico_uri != null)
				tool_btn.setIcon(new ImageIcon(this.getClass().getResource(tool.ico_uri)));
			tool_select_buttons.put(tool, tool_btn);
			this.add(tool_btn);
			tool_btn.addActionListener(this);
		}

		String[] items = {"1", "2", "3", "4", "5", "6", "7", "8"};

		selectLineThickness = new JComboBox<String>(items);
		this.addSeparator();
		this.add(selectLineThickness);
		
		toolIsFillBox = new JToggleButton("Fill");
		toolIsFillBox.setSelected(false);
		this.add(toolIsFillBox);
	}
	
	public void reset() {
		selectLineThickness.setSelectedIndex(0);
	}
	
	public synchronized void addToolbarListener(ToolbarListener l) {
		toolbarListener = PaintExEventMulticaster.add(toolbarListener, l);
	}
	
	public synchronized void removeToolbarListener(ToolbarListener  l) {
		toolbarListener = PaintExEventMulticaster.remove(toolbarListener, l);
	}

	/* Toolbar click handler */
	public void actionPerformed(ActionEvent ae) {
		if (tool_action_buttons.containsValue(ae.getSource())) {
			//Tool action
			ToolActionButton source = (ToolActionButton) ae.getSource();
			if (toolbarListener == null) return;
			ToolbarEvent ev = new ToolbarEvent(source, ToolbarEvent.TOOLBAR_IMAGEACTION, source.type);
			//Which button was clicked
			switch (source.type) {
			case ACTION_NEW:
				toolbarListener.imageNew(ev);
				break;
			case ACTION_OPEN:
				toolbarListener.imageOpen(ev);
				break;
			case ACTION_SAVE:
				toolbarListener.imageSave(ev);
				break;
			case ACTION_PRINT:
				toolbarListener.imagePrint(ev);
				break;
			case ACTION_RESIZE:
				toolbarListener.imageResize(ev);
				break;
			default:
				break;
			}
		} else if (tool_select_buttons.containsValue(ae.getSource())) {
			//Tool select
			ToolSelectButton source = (ToolSelectButton) ae.getSource();
			if (toolbarListener == null) return;
			toolbarListener.toolSelect(new ToolbarEvent(source, ToolbarEvent.TOOLBAR_TOOLSELECT, source.type));
		}
	}
}