package paintex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import paintex.event.CanvasUpdateEvent;
import paintex.event.CanvasUpdateListener;
import paintex.event.ToolbarEvent;
import paintex.event.ToolbarListener;

public class PaintExWindow extends JFrame {
	private static final String APPLICATION_TITLE = "PaintEx";

	private JPanel contentPane;
	private JScrollPane canvasScroll;
	private PaintExCanvas paintCanvas;

	//Toolbars
	private JMenuBar menuBar;
	private ToolBar toolBar;
	private ColorPalettePanel colorChoosePanel;
	private StatusInfo statusBar;

	//Default canvas size for new image
	private final int CONTENT_PANE_WIDTH = 1280;
	private final int CONTENT_PANE_HEIGHT = 720;
	
	//Event listeners
	protected StatusBarUpdater statusBarUpdate;
	protected ToolbarHandler toolbarAction;
	
	//Image to save on disk
	protected ImageInstance currentImage;

	public PaintExWindow() {
		super(APPLICATION_TITLE);
		this.initWindow();
		this.initAllComponents();
		this.addAllComponents();
		currentImage = new ImageInstance();
		reset();
	}
	
	public void reset() {
		this.paintCanvas.reset();
		this.toolBar.reset();
		this.colorChoosePanel.reset();
	}

	protected void initWindow() {
		this.setResizable(true);
		this.setLayout(new BorderLayout());

		// Add event listeners to control buttons
		this.addWindowListener(new WindowCloser());

		// Fit window to the canvas and tools
		this.setSize(CONTENT_PANE_WIDTH, CONTENT_PANE_HEIGHT);
		this.setPreferredSize(new Dimension(CONTENT_PANE_WIDTH, CONTENT_PANE_HEIGHT));
	}

	public void initAllComponents() {
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		toolBar = new ToolBar(this);
		statusBar = new StatusInfo();
		colorChoosePanel = new ColorPalettePanel();
		paintCanvas = new PaintExCanvas(CONTENT_PANE_WIDTH, CONTENT_PANE_HEIGHT);

		canvasScroll = new JScrollPane(paintCanvas);
		canvasScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		canvasScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(canvasScroll);
		
		statusBarUpdate = new StatusBarUpdater(statusBar, canvasScroll);
		toolbarAction = new ToolbarHandler(this, paintCanvas, toolBar, colorChoosePanel);
		
		//Register Event listeners
		paintCanvas.addCanvasListener(statusBarUpdate);
		toolBar.addToolbarListener(toolbarAction);
		colorChoosePanel.addToolbarListener(toolbarAction);
	}

	/**
	 * Add all components to the PaintEx window
	 */
	public void addAllComponents() {
		this.setJMenuBar(menuBar);
		this.add(colorChoosePanel, BorderLayout.PAGE_START);
		this.add(statusBar, BorderLayout.PAGE_END);
		this.add(toolBar, BorderLayout.WEST);
		this.add(contentPane, BorderLayout.CENTER);
		this.pack();
		
		//Update status bar
		statusBar.setCanvasSize(paintCanvas.getWidth(), paintCanvas.getHeight());
	}

	public void start() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * Terminate program on window close
	 */
	private class WindowCloser extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}
	
	public class StatusBarUpdater implements CanvasUpdateListener {
		protected StatusInfo statusBar;
		protected JScrollPane canvasScroll;
		
		public StatusBarUpdater(StatusInfo statusBar, JScrollPane canvasScroll) {
			this.statusBar = statusBar;
			this.canvasScroll = canvasScroll;
		}

		@Override
		public void canvasPointerPosition(CanvasUpdateEvent e) {
			this.statusBar.setCoordinate(e.x, e.y);
		}

		@Override
		public void canvasDimension(CanvasUpdateEvent e) {
			this.statusBar.setCanvasSize(e.x, e.y);
			this.canvasScroll.invalidate();
		}
	}
	
	public class ToolbarHandler implements ToolbarListener {
		protected PaintExCanvas canvas;
		protected ToolBar toolBar;
		protected ColorPalettePanel colSelector;
		protected PaintExWindow owner;
		private FileFilter imageFilter;
		
		public ToolbarHandler(PaintExWindow owner, PaintExCanvas canvas, ToolBar toolBar, ColorPalettePanel colSelector) {
			this.owner = owner;
			this.canvas = canvas;
			this.toolBar = toolBar;
			this.colSelector = colSelector;
			
			this.imageFilter = new FileFilter() {
				@Override
				public String getDescription() {
					return "Image Files";
				}
				
				@Override
				public boolean accept(File f) {
					return true;
				}
			};
		}

		@Override
		public void toolSelect(ToolbarEvent e) {
			canvas.setTool(e.toolType);
		}

		@Override
		public void brushSelect(ToolbarEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void colorSelect(ToolbarEvent e) {
			if (e.selectTarget.compareTo(ToolbarEvent.TARGET_PRIMARY) == 0)
				this.canvas.setStrokeColor(e.selectedColor);
			else if (e.selectTarget.compareTo(ToolbarEvent.TARGET_SECONDARY) == 0)
				this.canvas.setFillColor(e.selectedColor);
			else if (e.selectTarget.compareTo(ToolbarEvent.TARGET_FILL_TYPE) == 0) {
				this.canvas.setFillStyle(e.fillType);
			}
		}

		@Override
		public void imageNew(ToolbarEvent e) {
			ImageSizeDialog newImgSize = new ImageSizeDialog(owner, canvas.getPreferredSize());
			if (newImgSize.isConfirmed()) {
				this.canvas.setPreferredSize(newImgSize.getNewDimension());
				this.canvas.clearCanvas();
				owner.currentImage = new ImageInstance();
				owner.reset();
			}
		}

		@Override
		public void imageSave(ToolbarEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void imageOpen(ToolbarEvent e) {
			if (owner.currentImage.isModified) {
				int saveDialogAnswer = JOptionPane.showConfirmDialog(owner, String.format("Would you like to save changes to \"%s\"?", owner.currentImage.filePath.getName()), "Save modified image", JOptionPane.YES_NO_CANCEL_OPTION);
				//Cancelled saving, return back to image
				switch (saveDialogAnswer) {
				case JOptionPane.YES_OPTION:
					canvas.saveImageToFile(owner.currentImage.filePath);
					break;
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.CLOSED_OPTION:
					return;
				}
			}
			
			//Find an image file
			JFileChooser fc = new JFileChooser(ImageInstance.lastUseDir);
			fc.setFileFilter(this.imageFilter);
			
			int response = fc.showOpenDialog(owner);
			switch (response) {
			case JFileChooser.APPROVE_OPTION:
				File imgFile = fc.getSelectedFile();
				if (this.canvas.loadImageFromFile(imgFile)) {
					//Update image instance
					String imgPath = imgFile.getParentFile().getAbsolutePath();
					owner.currentImage = new ImageInstance(imgFile, imgPath);
					reset();
				}
				break;
			
			case JFileChooser.CANCEL_OPTION:
			default:
				break;
			}
		}

		@Override
		public void imagePrint(ToolbarEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void imageResize(ToolbarEvent e) {
			ImageSizeDialog newImgSize = new ImageSizeDialog(owner, canvas.getPreferredSize());
			if (newImgSize.isConfirmed())
				canvas.setPreferredSize(newImgSize.getNewDimension());
		}
	}
}
