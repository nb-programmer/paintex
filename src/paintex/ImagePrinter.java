package paintex;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Draws the given image onto a printable, that can be sent to a printer as a print job
 * @author 2004
 *
 */
public class ImagePrinter implements Printable {
	private BufferedImage image;

	/**
	 * Default constructor
	 * @param image BufferedImage to be printed
	 */
	public ImagePrinter(BufferedImage image) {
		this.image = image;
	}

	@Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        if (pageIndex == 0) {
            int pWidth = 0;
            int pHeight = 0;
            pWidth = (int) Math.min(pageFormat.getImageableWidth(), (double) image.getWidth());
            pHeight = pWidth * image.getHeight() / image.getWidth();
            g.drawImage(image, (int) pageFormat.getImageableX(), (int) pageFormat.getImageableY(), pWidth, pHeight, null);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }
	
	/**
	 * Create a print job and start it in a thread
	 * @param image The BufferedImage to print
	 * @return Thread object if printing is not cancelled
	 */
	public static Thread printImage(BufferedImage image) {
		Thread printThread = null;
		PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new ImagePrinter(image));
        if (printJob.printDialog()) {
        	printThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
		                printJob.print();
		            } catch (PrinterException prt) {
		                prt.printStackTrace();
		            }
				}
			});
        	printThread.start();
        }
		return printThread;
	}
}
