package paintex;

import java.io.File;

/**
 * 
 * Holds information of current file user is working on
 * 
 * @author 2033
 *
 */
public class ImageInstance {
	public File filePath;
	public boolean isModified;
	public static String lastUseDir;
	protected static final File DEFAULT_FILE = new File("[NOWHERE]/untitled.png");
	
	/**
	 * Default constructor
	 */
	public ImageInstance() {
		this(DEFAULT_FILE);
	}
	
	/**
	 * Change the folder of last used file
	 * @param lastUseDir
	 */
	public ImageInstance(String lastUseDir) {
		this(DEFAULT_FILE, lastUseDir);
	}
	
	/**
	 * Use given file path as working file
	 * @param filePath
	 */
	public ImageInstance(File filePath) {
		this.filePath = filePath;
		this.isModified = false;
	}
	
	/**
	 * Change both current working file and last used file path
	 * @param filePath
	 * @param lastUseDir
	 */
	public ImageInstance(File filePath, String lastUseDir) {
		this.filePath = filePath;
		ImageInstance.lastUseDir = lastUseDir;
		this.isModified = false;
	}
}