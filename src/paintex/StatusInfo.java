package paintex;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * This class creates a status tool bar at bottom of the screen and shows common
 * information like mouse position
 * 
 * @author 2004
 *
 */
public class StatusInfo extends JToolBar {
	private JLabel coordinates;
	private JLabel frameSize;

	/**
	 * Default constructor to create the status bar
	 */
	public StatusInfo() {
		//Fixed toolbar at bottom
		this.setFloatable(false);
		this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

		this.add(new JLabel(new ImageIcon(getClass().getResource("/icons/coordinates.png"))));
		coordinates = new JLabel();
		setCoordinate(0, 0);
		this.add(coordinates);

		this.add(new Separator());

		this.add(new JLabel(new ImageIcon(getClass().getResource("/icons/size.png"))));
		frameSize = new JLabel();
		setCanvasSize(0, 0);
		this.add(frameSize);
	}
	
	/**
	 * Update status bar's mouse coordinate
	 * @param x Pointer x position
	 * @param y Pointer y position
	 */
	public void setCoordinate(int x, int y) {
		coordinates.setText(String.format("%d x %d", x, y));
	}
	
	/**
	 * Update status bar's image dimensions
	 * @param w Image width
	 * @param h Image height
	 */
	public void setCanvasSize(int w, int h) {
		frameSize.setText(String.format("%d x %d", w, h));
	}
}
