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
import javax.swing.filechooser.FileNameExtensionFilter;

import paintex.event.CanvasUpdateEvent;
import paintex.event.CanvasUpdateListener;
import paintex.event.ToolbarEvent;
import paintex.event.ToolbarListener;

/**
 * Main window that contains tools, menu, canvas status bar, etc.
 * 
 * @author 2004 2031 2033
 *
 */
public class PaintExWindow extends JFrame implements CanvasUpdateListener {
	private static final String APPLICATION_TITLE = "PaintEx";

	private JPanel contentPane;
	private JScrollPane canvasScroll;
	private PaintExCanvas paintCanvas;

	//Toolbars
	private JMenuBar menuBar;
	private ToolBar toolBar;
	private ColorPaletteToolbar colorChoosePanel;
	private StatusInfo statusBar;

	//Default canvas size for new image
	private final int CANVAS_DEFAULT_WIDTH = 1280;
	private final int CANVAS_DEFAULT_HEIGHT = 720;
	
	//Event handlers
	protected ToolbarHandler toolbarAction;
	
	//Image to save on disk
	private static String IMAGE_SUPPORTED_EXT[] = {"png", "jpg", "jpeg", "bmp"};
	private static String IMAGE_EXPORT_EXT[] = {"pdf"};
	
	private ImageInstance currentImage;
	private FileFilter imageFileFilter;
	private FileFilter imageExportFileFilter;
	
	/**
	 * Default constructor to initialize the window
	 */
	public PaintExWindow() {
		super(APPLICATION_TITLE);
		this.initWindow();
		this.initFileManager();
		this.initAllComponents();
		this.addAllComponents();
		reset();
	}

	/**
	 * Reset state of whole application
	 */
	public void reset() {
		this.paintCanvas.reset();
		this.toolBar.reset();
		this.colorChoosePanel.reset();
	}
	
	
	/**
	 * Create a filter for File open and save boxes with description
	 * @param description
	 * @param extensions
	 * @return
	 */
	private FileNameExtensionFilter getFileNameFilter(String description, String[] extensions) {
		return new FileNameExtensionFilter(String.format("%s (%s)", description, String.join("; ", extensions)), extensions);
	}


	public void initFileManager() {
		this.currentImage = new ImageInstance();
		this.imageFileFilter = getFileNameFilter("Image Files", IMAGE_SUPPORTED_EXT);
		this.imageExportFileFilter = getFileNameFilter("Document Export Files", IMAGE_EXPORT_EXT);
	}

	/**
	 * Prepare the window to present to the user
	 */
	protected void initWindow() {
		this.setResizable(true);
		this.setLayout(new BorderLayout());

		// Add event listeners to control buttons
		this.addWindowListener(new WindowCloser());

		// Fit window to the canvas and tools
		this.setSize(CANVAS_DEFAULT_WIDTH, CANVAS_DEFAULT_HEIGHT);
		this.setPreferredSize(new Dimension(CANVAS_DEFAULT_WIDTH, CANVAS_DEFAULT_HEIGHT));
	}

	/**
	 * Create all components and add event listeners
	 */
	public void initAllComponents() {
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		toolBar = new ToolBar();
		statusBar = new StatusInfo();
		colorChoosePanel = new ColorPaletteToolbar();
		paintCanvas = new PaintExCanvas(CANVAS_DEFAULT_WIDTH, CANVAS_DEFAULT_HEIGHT);

		canvasScroll = new JScrollPane(paintCanvas);
		canvasScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		canvasScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(canvasScroll);
		
		toolbarAction = new ToolbarHandler(this, paintCanvas, colorChoosePanel);
		
		//Register Event listeners
		paintCanvas.addCanvasListener(this);
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
	
	/**
	 * Update title if image is modified
	 */
	public void refreshWindowTitle() { 
		setTitle(String.format("%s%s - %s", this.currentImage.isModified ? "*" : "", this.currentImage.filePath.getName(), APPLICATION_TITLE));
	}

	/**
	 * Start the application, show the window
	 */
	public void start() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.refreshWindowTitle();
		this.setVisible(true);
	}
	
	/**
	 * Save currently open image to the disk
	 * @return Whether to continue after dialog closes. false if user cancels saving or saving fails
	 */
	public boolean saveModifiedConfirm(boolean askConfirm) {
		int saveDialogAnswer;
		if (askConfirm) {
			if (this.currentImage.isModified) {
				saveDialogAnswer = JOptionPane.showConfirmDialog(
					this,
					String.format(
						"Would you like to save changes to \"%s\"?",
						this.currentImage.filePath.getName()),
					"Save modified image",
					JOptionPane.YES_NO_CANCEL_OPTION);
			} else {
				saveDialogAnswer = JOptionPane.NO_OPTION;
			}
		} else {
			saveDialogAnswer = JOptionPane.YES_OPTION;
		}
		
		//Check user's answer
		switch (saveDialogAnswer) {
		case JOptionPane.YES_OPTION:
			if (!setSaveFilePathUnsaved()) break;
			
			if (this.paintCanvas.saveImageToFile(this.currentImage.filePath)) {
				this.currentImage.isModified = false;
				return true;
			}
			
			//Failed to save
			//TODO: Show message
			break;

		case JOptionPane.NO_OPTION:
			return true;

			//Cancelled saving, return back to image
		case JOptionPane.CANCEL_OPTION:
		case JOptionPane.CLOSED_OPTION:
			break;
		}
		return false;
	}
	
	/**
	 * Open a save dialog box if an unsaved file needs to be saved to disk
	 * @return true if not cancelled
	 */
	private boolean setSaveFilePathUnsaved() {
		if (this.currentImage.filePath.exists()) return true;
		
		JFileChooser fc = new JFileChooser(ImageInstance.lastUseDir);
		fc.setFileFilter(this.imageFileFilter);
		int response = fc.showSaveDialog(this);
		switch (response) {
		case JFileChooser.APPROVE_OPTION:
			File imgFile = fc.getSelectedFile();
			String imgPath = imgFile.getParentFile().getAbsolutePath();
			this.currentImage = new ImageInstance(imgFile, imgPath);
			return true;
		case JFileChooser.CANCEL_OPTION:
		default:
			break;
		}
		return false;
	}

	/**
	 * Shortcut to not prompt user to save modified file
	 * @return
	 */
	public boolean saveModifiedConfirm() {
		return saveModifiedConfirm(true);
	}

	/**
	 * Open a new File dialog to browse an image file
	 */
	public void openNewImage() {
		//Check if image was modified
		if (!saveModifiedConfirm()) return;
		
		//Find an image file using the Open file window
		JFileChooser fc = new JFileChooser(ImageInstance.lastUseDir);
		fc.setFileFilter(this.imageFileFilter);
		int response = fc.showOpenDialog(this);
		switch (response) {
		case JFileChooser.APPROVE_OPTION:
			File imgFile = fc.getSelectedFile();
			if (this.paintCanvas.loadImageFromFile(imgFile)) {
				//Update image instance
				String imgPath = imgFile.getParentFile().getAbsolutePath();
				this.currentImage = new ImageInstance(imgFile, imgPath);
				this.reset();
			} else {
				//Failed to load
				//TODO: Show message
			}
			break;
		
		case JFileChooser.CANCEL_OPTION:
		default:
			break;
		}
	}

	/**
	 * Terminate program on window close
	 */
	private class WindowCloser extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {
			if (!saveModifiedConfirm(true)) return;
			System.exit(0);
		}
	}
	
	
	/* Canvas events */
	
	@Override
	public void canvasPointerPosition(CanvasUpdateEvent e) {
		this.statusBar.setCoordinate(e.x, e.y);
	}

	@Override
	public void canvasDimension(CanvasUpdateEvent e) {
		this.statusBar.setCanvasSize(e.x, e.y);
		this.canvasScroll.invalidate();
		this.currentImage.isModified = true;
		this.refreshWindowTitle();
	}

	@Override
	public void canvasImageModify(CanvasUpdateEvent e) {
		this.currentImage.isModified = true;
		this.refreshWindowTitle();
	}
	
	/* Toolbar events */
	
	public class ToolbarHandler implements ToolbarListener {
		protected PaintExCanvas canvas;
		protected ColorPaletteToolbar colSelector;
		protected PaintExWindow owner;
		
		public ToolbarHandler(PaintExWindow owner, PaintExCanvas canvas, ColorPaletteToolbar colSelector) {
			this.owner = owner;
			this.canvas = canvas;
			this.colSelector = colSelector;
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
		public void toolProperty(ToolbarEvent e) {
			if (e.selectedProperty.compareTo(ToolbarEvent.PROP_THICKNESS) == 0)
				canvas.setLineThickness(e.thicknessSelected);
		}

		@Override
		public void colorSelect(ToolbarEvent e) {
			if (e.selectedProperty.compareTo(ToolbarEvent.PROP_PRIMARY) == 0)
				this.canvas.setStrokeColor(e.selectedColor);
			else if (e.selectedProperty.compareTo(ToolbarEvent.PROP_SECONDARY) == 0)
				this.canvas.setFillColor(e.selectedColor);
			else if (e.selectedProperty.compareTo(ToolbarEvent.PROP_FILL_TYPE) == 0) {
				this.canvas.setFillStyle(e.fillType);
			}
		}

		@Override
		public void imageNew(ToolbarEvent e) {
			if (!owner.saveModifiedConfirm()) return;
			
			ImageSizeDialog newImgSize = new ImageSizeDialog(owner, canvas.getPreferredSize());
			if (newImgSize.isConfirmed()) {
				this.canvas.setPreferredSize(newImgSize.getNewDimension());
				this.canvas.clearCanvas();
				owner.currentImage = new ImageInstance(ImageInstance.lastUseDir);
				owner.reset();
				owner.refreshWindowTitle();
			}
		}

		@Override
		public void imageSave(ToolbarEvent e) {
			owner.saveModifiedConfirm(false);
			owner.refreshWindowTitle();
		}

		@Override
		public void imageOpen(ToolbarEvent e) {
			owner.openNewImage();
			owner.refreshWindowTitle();
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

		@Override
		public void imageRotate(ToolbarEvent e) {
			switch (e.actionType) {
			case ACTION_RLEFT:
				canvas.rotateCCW();
				break;
			case ACTION_RRIGHT:
				canvas.rotateCW();
				break;
			default:
				break;
			}
		}
	}
}
