package paintex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Simple dialog box for assigning dimension to image
 * @author 2004
 *
 */
public class ImageSizeDialog extends JDialog {
	protected boolean isConfirmed;
	protected Dimension newDimension;
	
	/**
	 * Constructor to generate the dialog box
	 * @param owner JFrame that is parent of this dialog box
	 * @param defaultCanvasSize Default value to fill-in the input boxes
	 */
	public ImageSizeDialog(JFrame owner, Dimension defaultCanvasSize) {
		super(owner, "Image size", JDialog.DEFAULT_MODALITY_TYPE);
		setBackground(Color.GRAY);
		setSize(400, 200);
		setPreferredSize(new Dimension(400, 200));
		
		newDimension = null;
		isConfirmed = false;
		
		JPanel container = new JPanel(new BorderLayout());
		setContentPane(container);
		
		JPanel sizeBox = new JPanel(new GridLayout(2, 2));

		JTextField width = new JTextField(Integer.toString(defaultCanvasSize.width));
		JLabel widthLabel = new JLabel("Width (px):");
		
		JTextField height = new JTextField(Integer.toString(defaultCanvasSize.height));
		JLabel heightLabel = new JLabel("Height (px):");

		sizeBox.add(widthLabel);
		sizeBox.add(width);
		sizeBox.add(heightLabel);
		sizeBox.add(height);
		
		JPanel confirmBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton okay = new JButton("OK");
		okay.setSize(75, 25);
		okay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					newDimension = new Dimension(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
					if (newDimension.width <= 0) throw new IllegalArgumentException("Width cannot be 0 (or lower).");
					if (newDimension.height <= 0) throw new IllegalArgumentException("Height cannot be 0 (or lower).");
					isConfirmed = true;
					dispose();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Please enter dimensions as integer numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.setSize(75, 25);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isConfirmed = false;
				dispose();
			}
		});
		
		confirmBox.add(okay);
		confirmBox.add(cancel);

		container.add(sizeBox, BorderLayout.NORTH);
		container.add(confirmBox, BorderLayout.SOUTH);
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	/**
	 * Get the dimensions given by the user
	 * @return Dimension (width and height), if applied
	 */
	public Dimension getNewDimension() {
		return newDimension;
	}
	
	/**
	 * Returns true if dialog box was applied, false if cancelled
	 * @return
	 */
	public boolean isConfirmed() {
		return isConfirmed;
	}
}
