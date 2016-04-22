/**
 * Gradient_.java 
 *
 * D�tection de points contours par approche du premier ordre :
 * calcul de la norme et de la direction du gradient, des maxima locaux 
 * de la norme du gradient dans sa direction, et seuillage par hyst�r�sis.
 *
 * @author Olivier.Losson at univ-lille1.fr
 *
 * Commentaires et am�liorations bienvenus !
 */

import ij.*;	// pour classes ImagePlus et IJ
import ij.plugin.filter.*;	// pour interface PlugInFilter et Convolver
import ij.process.*;	// pour classe ImageProcessor
import ij.gui.GenericDialog;	// pour classe GenericDialog
import ij.gui.DialogListener;	// pour interface DialogListener
import java.awt.*;		//pour classe AWTEvent
import java.util.Vector;	// pour classes Vector, ArrayList, List
import java.util.List;
import java.util.ArrayList;


public class Gradient_ implements PlugInFilter, DialogListener {
 
	// Les variables sont statiques, ce qui interdit de faire tourner le plugin dans des threads parall�les avec diff�rents param�tres
	private final static String[] FILTERS = {"Roberts", "Prewitt", "Sobel"};
	private static int filtre=2;
	private final static float[][] KERNELSX = {
			{0,0,0, -1.0f/2,0,1.0f/2, 0,0,0},
			{-1.0f/6,0,1.0f/6, -1.0f/6,0,1.0f/6, -1.0f/6,0,1.0f/6},
			{-1.0f/8,0,1.0f/8, -2.0f/8,0,2.0f/8, -1.0f/8,0,1.0f/8}
		};
	private final static float[][] KERNELSY = {
			{0,-1.0f/2,0, 0,0,0, 0,1.0f/2,0},
			{-1.0f/6,-1.0f/6,-1.0f/6, 0,0,0, 1.0f/6,1.0f/6,1.0f/6},
			{-1.0f/8,-2.0f/8,-1.0f/8, 0,0,0, 1.0f/8,2.0f/8,1.0f/8}
		};
	private static boolean deriveesPartielles=false;
	private static boolean direction=false;
	private static boolean maximaLocaux=true;
	private static boolean seuillageHyst=true;
	private static int seuilBas=8;
	private static int seuilHaut=20;
	
	private ImagePlus imp;

	// ---------------------------------------------------------------------------------
	// M�thodes de l'interface PlugInFilter

	// Initialisation du plugin
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

	// M�thode principale du plugin
	public void run(ImageProcessor ip) {
 
		// Affichage de la fen�tre de config
		if (!showDialog())
			return;
       
		// Titre et extension de l'image source
		String titre = imp.getTitle();
		String extension="";
		int index = titre.lastIndexOf('.');
		if (index>0)
			extension = titre.substring(index);
		else
			index = titre.length();
		titre = titre.substring(0,index);

		// D�riv�es partielles suivant X et Y
		FloatProcessor fpDX = (FloatProcessor)ip.duplicate().convertToFloat();
		FloatProcessor fpDY = (FloatProcessor)ip.duplicate().convertToFloat();
		Convolver conv = new Convolver();
		conv.setNormalize(false);	// SANS normalisation (par d�faut, la m�thode convolve() normalise)
		conv.convolve(fpDX,KERNELSX[filtre],3,3);
		conv.convolve(fpDY,KERNELSY[filtre],3,3);
		if (deriveesPartielles) {
			// Repr�sentation des d�riv�es
			ImagePlus dXImg = new ImagePlus(titre+"_DX ("+FILTERS[filtre]+")", fpDX);
			dXImg.show();
			ImagePlus dYImg = new ImagePlus(titre+"_DY ("+FILTERS[filtre]+")", fpDY);
			dYImg.show();
		}	
		
		// Norme et direction du gradient
		FloatProcessor fpGradNorm = new FloatProcessor (ip.getWidth(), ip.getHeight());
		FloatProcessor fpGradDir = new FloatProcessor (ip.getWidth(), ip.getHeight());
		computeGradient (fpDX, fpDY, fpGradNorm, fpGradDir);
		// Repr�sentation de la norme du gradient
		ImagePlus gradNormImg = new ImagePlus(titre+"_G norm ("+FILTERS[filtre]+")", fpGradNorm);
		gradNormImg.show();
		if (direction) {
			// Repr�sentation de la direction du gradient
			ImagePlus gradDirImg = new ImagePlus(titre+"_G dir ("+FILTERS[filtre]+")", fpGradDir);
			gradDirImg.show();
		}			
		
		if (maximaLocaux || seuillageHyst) {
			// D�tection des maxima locaux
			FloatProcessor fpMaxLoc = maxLoc( fpGradNorm, fpGradDir );

			if (maximaLocaux) {
				// Repr�sentation des maxima locaux
				ImagePlus maxLocImg = new ImagePlus(titre+"_maxima locaux", fpMaxLoc);
				maxLocImg.show();
			}		
		
			if (seuillageHyst) {
				// Seuillage par hyst�r�sis
				ByteProcessor fpMaxLocHyst = hystRec( fpMaxLoc, seuilBas, seuilHaut );
				// Repr�sentation du r�sultat
				ImagePlus maxLocHystImg = new ImagePlus(titre+"_maxima locaux seuill�s ("+seuilBas+","+seuilHaut+")", fpMaxLocHyst);
				maxLocHystImg.show();
			}
		}
	}
	
	// ---------------------------------------------------------------------------------
	/**
	 * M�thode d'affichage de la fen�tre de config et de lecture des valeurs saisies, appel�e dans run()
	 * 
	 * @return false si la fen�tre a �t� ferm�e en cliquant sur Cancel, true sinon (boolean)
	 */ 
    
	public boolean showDialog() {
		// Description de la fen�tre de config
		GenericDialog gd = new GenericDialog("Gradient parameters");
		gd.addChoice("Derivative filter type:", FILTERS, FILTERS[filtre]);
		gd.addMessage("Gradient options:");
		gd.addCheckbox("Show partial derivatives", deriveesPartielles);
		gd.addCheckbox("Show gradient direction", direction);
		gd.addCheckbox("Show local maxima of gradient norm", maximaLocaux);
		gd.addCheckbox("Perform hysteresis thresholding", seuillageHyst);
		gd.addNumericField("Hysteresis low value", seuilBas, 0);
		gd.addNumericField("Hysteresis high value", seuilHaut, 0);
		gd.addDialogListener(this);     		// the DialogItemChanged method will be called on user input
		gd.showDialog();                		// display the dialog; preview runs in the background now
		if (gd.wasCanceled()) return false;

       	// Lecture des valeurs saisies
		filtre = gd.getNextChoiceIndex();
		deriveesPartielles = gd.getNextBoolean();
		direction = gd.getNextBoolean();
		maximaLocaux = gd.getNextBoolean();
		seuillageHyst = gd.getNextBoolean();
		seuilBas  = (int) gd.getNextNumber();
		seuilHaut = (int) gd.getNextNumber();

		IJ.register(this.getClass());   		// protect static class variables (parameters) from garbage collection
		return true;
    }	

	
	// ---------------------------------------------------------------------------------
	/**
	 * M�thode appel�e sur modification (par un �v�nement) de la fen�tre de config
	 * Inspir�e de Fast_Filters.java (http://imagejdocu.tudor.lu/doku.php?id=plugin:filter:fast_filters:start)
	 * 
	 * @param gd Fen�tre de dialogue (GenericDialog)
	 * @param e �v�nement � traiter (AWTEvent) 
	 * @return true si la saisie est correcte, false sinon (boolean)
	 */
    
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		
		// Acc�s aux champs de la fen�tre
		Checkbox hystCheckbox = (Checkbox)gd.getCheckboxes().get(3);
		Vector numFields = gd.getNumericFields();
        TextField lowThresholdField = (TextField)numFields.get(0);
        TextField highThresholdField = (TextField)numFields.get(1);
 		if (e!=null) {	// e==null si clic sur OK
			// D�sactivation/activation des champs num�riques de seuils, selon seuillage par hyst�r�sis ou pas
	        if (e.getSource() == hystCheckbox) { 
	        	lowThresholdField.setEditable(hystCheckbox.getState());
	        	highThresholdField.setEditable(hystCheckbox.getState());
	        }
 		}
 		seuilBas = Integer.valueOf(lowThresholdField.getText());
 		seuilHaut = Integer.valueOf(highThresholdField.getText());
        return (!gd.invalidNumber() && (!hystCheckbox.getState() || seuilBas>0 && seuilHaut>=seuilBas));
    }


	// ---------------------------------------------------------------------------------
	/**
	 * Calcul de la norme et de la direction du gradient
	 * 
	 * @param imDX Image de la d�riv�e partielle selon X (ImageProcessor 32 bits)
	 * @param imDY Image de la d�riv�e partielle selon Y (ImageProcessor 32 bits)
	 * @param imNormeG Image de la norme du gradient (ImageProcessor 32 bits)
	 * @param imDirG Image de la direction du gradient (ImageProcessor 32 bits)
	 */	
	
	public void computeGradient(ImageProcessor imDX, ImageProcessor imDY, ImageProcessor imNormeG, ImageProcessor imDirG) {
		
		// Lecture de la taille de l'image
		int width = imDX.getWidth();
		int height = imDX.getHeight();
		
		// Calculs de la norme et de la direction pour chaque pixel
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				float p1 = imDX.getPixelValue(x,y);
				float p2 = imDY.getPixelValue(x,y);
				double norm = Math.sqrt(p1*p1+p2*p2);
				double dir = Math.atan2(p2,p1)*180/Math.PI;
				imNormeG.putPixelValue(x,y,norm);
				imDirG.putPixelValue(x,y,dir);
			}
		}
		
	}

	// ---------------------------------------------------------------------------------
	/**
	 * Calcul des maxima locaux de la norme du gradient dans sa direction
	 * 
	 * @param imNormeG Image de la norme du gradient (ImageProcessor 32 bits)
	 * @param imDirG Image de la direction du gradient (ImageProcessor 32 bits)
	 * @return maxLoc Image des maxima locaux (FloatProcessor)
	 */	
	
	public FloatProcessor maxLoc(ImageProcessor imNormeG, ImageProcessor imDirG) {
		
		// Lecture de la taille de l'image
		int width = imNormeG.getWidth();
		int height = imNormeG.getHeight();	
		
		// Image r�sultat des maxima locaux
		FloatProcessor out = new FloatProcessor(width,height);

		// Calculs pour chaque pixel
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				// Lecture de la norme et de la direction du gradient en ce pixel
				float theta = imDirG.getPixelValue(x,y);	// Direction du gradient [-180.0 .. 180.0]
				float p = imNormeG.getPixelValue (x,y); // Norme du gradient [0.0 .. 255.0]

				// D�termination des pixels voisins � prendre en compte
				float p1, p2;
				if ((theta>=22.5 && theta<=67.5) || (theta>=-157.5 && theta<=-112.5))
				{ 	// Voisins selon la premi�re diagonale
					p1 = imNormeG.getPixelValue(x+1,y+1);
					p2 = imNormeG.getPixelValue(x-1,y-1);
				} else {
					if ((theta>=67.5 && theta<=112.5) || (theta>=-112.5 && theta<=-67.5))
					{	// Voisins selon la direction Y
						p1 = imNormeG.getPixelValue(x,y+1);
						p2 = imNormeG.getPixelValue(x,y-1);
					} else {
						if ((theta>=112.5 &&theta<=157.5) || (theta>=-67.5 && theta<=-22.5))
						{	// Voisins selon la seconde diagonale
							p1 = imNormeG.getPixelValue(x+1,y-1);
							p2 = imNormeG.getPixelValue(x-1,y+1);
						} else {
							// Voisins selon la direction X
							p1 = imNormeG.getPixelValue(x+1,y);
							p2 = imNormeG.getPixelValue(x-1,y);
						}
					}
				}
				// Tester si le pixel courant est un maximum local
				if (p>= p1 && p > p2)
					out.putPixelValue(x,y,p);
				else
					out.putPixelValue(x,y,0);
			}
		}
		return (out);
	}
	
	// ---------------------------------------------------------------------------------
	/**
	 * Seuillage par hyst�r�sis de la norme du gradient
	 * 
	 * @param imNormeG Image de la norme du gradient (ImageProcessor 32 bits)
	 * @param seuilBas Seuil bas de l'hyst�r�sis (int)
	 * @param seuilHaut Seuil haut de l'hyst�r�sis (int)
	 * @return out Carte des maxima locaux de la norme du gradient
	 */

	public ByteProcessor hystIter(ImageProcessor imNormeG, int seuilBas, int seuilHaut) {
		int width = imNormeG.getWidth();
		int height = imNormeG.getHeight();
 
		// Image binaire r�sultat des points contours apr�s seuillage
		ByteProcessor out = new ByteProcessor(width,height);
		// Liste des pixels (coordonn�es (x,y)) d�tect�s comme contours
		List<int[]> highpixels = new ArrayList<int[]>();
		
		// Premier parcours de l'image pour seuillage initial
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				
				// lire la norme du gradient en ce point
				float g = imNormeG.getPixelValue(x,y);
				
				// Norme inf�rieure au seuil bas -> pas un pixel contour
				if (g<seuilBas) continue;
				
				// Norme sup�rieure au seuil haut -> pixel contour
				if (g>seuilHaut) {
					out.set(x,y,255);
					highpixels.add(new int[]{x,y});
					continue;
				}
				
				// Entre les 2 seuils -> d�pend des voisins (
				out.set(x,y,128);
			}
		}
		
		// d�calages en x et y pour les voisins � prendre en compte
		int[] dx8 = new int[] {-1, 0, 1,-1, 1,-1, 0, 1};
		int[] dy8 = new int[] {-1,-1,-1, 0, 0, 1, 1, 1};
		// Liste des nouveaux pixels contours d�tect�s
		List<int[]> newhighpixels = new ArrayList<int[]>();
		
		while(!highpixels.isEmpty()) {
			newhighpixels.clear();
			// pour chaque pixel contour ...
			for(int[] pixel : highpixels) {
				int x=pixel[0], y=pixel[1];
				// ... marquer comme contours ceux de ses 8 voisins pr�c�demment marqu�s ind�termin�s
				for(int k=0;k<8;k++) {
					int xk=x+dx8[k], yk=y+dy8[k];
					if (xk<0 || xk>=width) continue;
					if (yk<0 || yk>=height) continue;
					if (out.get(xk, yk)==128) {
						out.set(xk, yk, 255);
						newhighpixels.add(new int[]{xk, yk});
					}
				}
			}
			
			// �changer les listes de pixels contours avant nouveau parcours
			List<int[]> swap = highpixels; highpixels = newhighpixels; newhighpixels = swap;
		}
 
		// Mise � 0 des pixels non points contours 
		// (norme < seuil bas, ou norme > seuil bas mais non connect� � un pixel contour)
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				if (out.get(x, y)!=255) out.set(x,y,0);
		
		return out;
	}
	
	/**
	 * Seuillage par hyst�r�sis de la norme du gradient (Version r�cursive)
	 * 
	 * @param imNormeG Image de la norme du gradient (ImageProcessor 32 bits)
	 * @param seuilBas Seuil bas de l'hyst�r�sis (int)
	 * @param seuilHaut Seuil haut de l'hyst�r�sis (int)
	 * @return out Carte des maxima locaux de la norme du gradient
	 */
	public ByteProcessor hystRec(ImageProcessor imNormeG, int seuilBas, int seuilHaut) {
		int width = imNormeG.getWidth();
		int height = imNormeG.getHeight();
 
		// Image binaire r�sultat des points contours apr�s seuillage
		ByteProcessor out = new ByteProcessor(width,height);

	    /* Propagation � partir de chaque point pour lequel la norme du gradient est > au seuil haut */
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				// lire la norme du gradient en ce point
				double g = imNormeG.getPixelValue(x,y);
				if (g>seuilHaut && x>=0 && y>= 0 && x<width && y<height)
					propaRec(imNormeG, out, seuilBas, x, y);
			}

		return out;
	}
	
	/**
	 * Propagation r�cursive du seuillage par hyst�r�sis
	 */
	private void propaRec(ImageProcessor grad_norm, ImageProcessor cont_map, int seuilBas, int x, int y)
	{

		// Acc�s au pixel (x,y) dans les images de la norme et de la carte de points contours
	    float g = grad_norm.getPixelValue(x,y);
	    int c = cont_map.getPixel(x,y)&0xFF;
	    
		// Cas de sortie imm�diate : point d�j� contour, norme du gradient inf�rieure au seuil bas
		if ( c>0 || g<seuilBas)
			return;

	    // Sinon, on marque le point ...
		cont_map.set(x,y,255);

	    // ... et on propage sur les 8 voisins
		propaRec(grad_norm, cont_map, seuilBas, x-1, y-1);
		propaRec(grad_norm, cont_map, seuilBas, x  , y-1);
		propaRec(grad_norm, cont_map, seuilBas, x+1, y-1);
		propaRec(grad_norm, cont_map, seuilBas, x-1, y  );
		propaRec(grad_norm, cont_map, seuilBas, x+1, y  );
		propaRec(grad_norm, cont_map, seuilBas, x-1, y+1);
		propaRec(grad_norm, cont_map, seuilBas, x  , y+1);
		propaRec(grad_norm, cont_map, seuilBas, x+1, y+1);
	}
}