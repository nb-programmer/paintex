package paintex;

import java.io.File;

public class ImageInstance {
	public File filePath;
	public boolean isSaved;
	public boolean isModified;
	public static String lastUseDir;
	
	public ImageInstance() {
		this.filePath = new File("untitled.png");
		this.isSaved = false;
		this.isModified = false;
		ImageInstance.lastUseDir = ".";
	}
	
	public ImageInstance(File filePath) {
		this.filePath = filePath;
	}
	
	public ImageInstance(File filePath, String lastUseDir) {
		this.filePath = filePath;
		ImageInstance.lastUseDir = lastUseDir;
	}
}