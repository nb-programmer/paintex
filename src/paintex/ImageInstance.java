package paintex;

import java.io.File;

public class ImageInstance {
	public File filePath;
	public boolean isModified;
	public static String lastUseDir;
	protected static final File DEFAULT_FILE = new File("[NOWHERE]/untitled.png");
	
	public ImageInstance() {
		this(DEFAULT_FILE);
	}
	
	public ImageInstance(String lastUseDir) {
		this(DEFAULT_FILE, lastUseDir);
	}
	
	public ImageInstance(File filePath) {
		this.filePath = filePath;
	}
	
	public ImageInstance(File filePath, String lastUseDir) {
		this.filePath = filePath;
		ImageInstance.lastUseDir = lastUseDir;
		this.isModified = false;
	}
}