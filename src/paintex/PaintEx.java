/**
 * PaintEx application main class
 * 
 * @author 2004 2031 2033
 *
 */

package paintex;


/**
 * This class creates a new PaintEx application window
 * and begins the application.
 * 
 * @author 2031
 *
 */
public class PaintEx {
	PaintExWindow paintEx;

	/**
	 * Begin PaintEx GUI
	 */
	public PaintEx() {
		paintEx = new PaintExWindow();
		paintEx.start();
	}

	public static void main(String[] args) {
		new PaintEx();
	}
}
