
/**
 * Laplacien_.java 
 *
 * D�tection de points contours par approches du second ordre.
 * Fichier �tudiant � compl�ter
 *
 * @author 
 *
 */

//pour classes AWTEvent, CheckBox, TextField
import java.awt.AWTEvent;
import java.awt.Rectangle;

import DCT.DCT2D;
// pour classes ImagePlus et IJ
import ij.ImagePlus;
// pour classes GenericDialog et DialogListener
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
// pour interface PlugInFilter et Convolver
import ij.plugin.filter.PlugInFilter;
// pour classe ImageProcessor et sous-classes
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Plug_DCT2D implements PlugInFilter, DialogListener {

	private ImagePlus imp;

	private final static int BLOC_SIZE = 8;
	
	// ---------------------------------------------------------------------------------
	// M�thodes de l'interface PlugInFilter

	// Initialisation du plugin
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	public void run(ImageProcessor ip) {
		final FloatProcessor fp = new FloatProcessor(ip.getFloatArray());
		fp.add(-128);
		
		final Rectangle block = new Rectangle(0, 0, BLOC_SIZE, BLOC_SIZE);
		
		for (int j = 0; j < fp.getHeight(); j += BLOC_SIZE) {
			for (int i = 0; i < fp.getWidth(); i += BLOC_SIZE) {
				block.setLocation(i, j);
				fp.setRoi(block);
				DCT2D.forwardDCT(fp);
			}
		}

		// Afficher l’image si possible
		final ImagePlus imgPlus = new ImagePlus(imp.getTitle() + "_dct2d", fp);
		imgPlus.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.gui.DialogListener#dialogItemChanged(ij.gui.GenericDialog,
	 * java.awt.AWTEvent)
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}