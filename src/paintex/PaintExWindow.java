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

public class PaintExWindow extends JFrame implements CanvasUpdateListener {
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
	
	//Event handlers
	protected ToolbarHandler toolbarAction;
	
	//Image to save on disk
	private ImageInstance currentImage;
	private FileFilter imageFileFilter;
	private FileFilter imageExportFileFilter;

	public PaintExWindow() {
		super(APPLICATION_TITLE);
		this.initWindow();
		this.initFileManager();
		this.initAllComponents();
		this.addAllComponents();
		reset();
	}
	
	public void reset() {
		this.paintCanvas.reset();
		this.toolBar.reset();
		this.colorChoosePanel.reset();
	}
	
	private boolean matchExtension(File f, String[] extensions) {
		if (f.isDirectory()) {
			return true;
		} else {
			String path = f.getAbsolutePath().toLowerCase();
			for (int i = 0; i < extensions.length; i++) {
				String extension = extensions[i];
				
				//Check file extension
				if (path.endsWith(extension) &&(path.charAt(path.length() - extension.length())) == '.')
					return true;
			}
		}
		return false;
	}
	
	public void initFileManager() {
		this.currentImage = new ImageInstance();
		this.imageFileFilter = new FileFilter() {
			private static String IMAGE_EXT[] = {".png", ".jpg", ".jpeg", ".bmp"};
			
			@Override
			public String getDescription() {
				return String.format("Image Files (%s)", String.join("; ", IMAGE_EXT));
			}
			
			@Override
			public boolean accept(File f) {
				return matchExtension(f, IMAGE_EXT);
			}
		};
		
		this.imageExportFileFilter = new FileFilter() {
			private static String EXPORT_EXT[] = {".pdf"};
			
			@Override
			public String getDescription() {
				return String.format("Document Export Files (%s)", String.join("; ", EXPORT_EXT));
			}
			
			@Override
			public boolean accept(File f) {
				return matchExtension(f, EXPORT_EXT);
			}
		};
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
		
		toolbarAction = new ToolbarHandler(this, paintCanvas, toolBar, colorChoosePanel);
		
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

	public void start() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * Save currently open image to the disk
	 * 
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
	}

	@Override
	public void canvasImageModify(CanvasUpdateEvent e) {
		this.currentImage.isModified = true;
	}
	
	/* Toolbar events */
	
	public class ToolbarHandler implements ToolbarListener {
		protected PaintExCanvas canvas;
		protected ToolBar toolBar;
		protected ColorPalettePanel colSelector;
		protected PaintExWindow owner;
		
		public ToolbarHandler(PaintExWindow owner, PaintExCanvas canvas, ToolBar toolBar, ColorPalettePanel colSelector) {
			this.owner = owner;
			this.canvas = canvas;
			this.toolBar = toolBar;
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
			if (!owner.saveModifiedConfirm()) return;
			
			ImageSizeDialog newImgSize = new ImageSizeDialog(owner, canvas.getPreferredSize());
			if (newImgSize.isConfirmed()) {
				this.canvas.setPreferredSize(newImgSize.getNewDimension());
				this.canvas.clearCanvas();
				owner.currentImage = new ImageInstance(ImageInstance.lastUseDir);
				owner.reset();
			}
		}

		@Override
		public void imageSave(ToolbarEvent e) {
			owner.saveModifiedConfirm(false);
		}

		@Override
		public void imageOpen(ToolbarEvent e) {
			owner.openNewImage();
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
