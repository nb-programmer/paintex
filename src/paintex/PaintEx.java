package paintex;

/**
 * PaintEx application
 * 
 * @author 2004 2031 2033
 *
 */


/**
 * This class creates a new PaintEx application window
 * and begins the application.
 *
 */
public class PaintEx {
	PaintExWindow paintEx;

	public PaintEx() {
		paintEx = new PaintExWindow();
		paintEx.start();
	}

	public static void main(String[] args) {
		new PaintEx();
	}
}
