import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class demat_ha implements PlugInFilter {

	ImagePlus imp; // Fenêtre contenant l'image de référence
	int width; // Largeur de la fenêtre
	int height; // Hauteur de la fenêtre

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

	public void run(ImageProcessor ip) {

		// Lecture des dimensions de la fenêtre
		width = imp.getWidth();
		height = imp.getHeight();

		// Dispositions possibles pour le CFA
		String[] orders = { "R-G-R", "B-G-B", "G-R-G", "G-B-G" };

		// Définition de l'interface
		GenericDialog dia = new GenericDialog("Génération de l'image CFA...", IJ.getInstance());
		dia.addChoice("Début de première ligne :", orders, orders[2]);
		dia.showDialog();

		// Lecture de la réponse de l'utilisateur
		if (dia.wasCanceled())
			return;
		int order = dia.getNextChoiceIndex();

		// Déclaration d'un noyau et d'un objet Convolver pour la convolution
		float[] kernel = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = kernel[i] / 4;
		}
		ImageProcessor red = cfa_samples(ip, 0);
		ImageProcessor green = est_G_hamilton(ip);
		ImageProcessor blue = cfa_samples(ip, 2);
		Convolver conv = new Convolver();
		conv.setNormalize(false); // SANS normalisation (par défaut, convolve()
									// normalise)
		// Composante R estimée par interpolation bilinéaire grâce à la
		// convolution
		conv.convolve(red, kernel, 3, 3);
		conv.convolve(blue, kernel, 3, 3);

		ImageStack stack = new ImageStack();
		ImageStack samples_stack = imp.createEmptyStack();
		samples_stack.addSlice("rouge", red); // Composante R
		samples_stack.addSlice("vert", green);// Composante G
		samples_stack.addSlice("bleu", blue); // Composante B

		// Création de l'image résultat
		ImagePlus cfa_samples_imp = imp.createImagePlus();
		cfa_samples_imp.setStack("Échantillons couleur CFA", samples_stack);
		cfa_samples_imp.show();
	}

	ImageProcessor cfa_samples(ImageProcessor cfa_ip, int k) {

		// Image couleur de référence et ses dimensions
		width = imp.getWidth();
		height = imp.getHeight();

		int pixel_value = 0; // Valeur du pixel source
		ImageProcessor res = new ByteProcessor(width, height); // Image CFA

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				final int valeur = cfa_ip.getPixel(i, j);
				switch (k) {
				case 0: // Composante rouge
					// Abscisse impaire, ordonnée paire
					if (i % 2 == 1 && j % 2 == 0) {
						res.putPixel(i, j, valeur);
					}
					break;

				case 1: // Composante verte
					// Abscisse et ordonnée de même parité
					if ((i + j) % 2 == 0) {
						res.putPixel(i, j, valeur);
					}
					break;

				case 2: // Composante bleue
					// Abscisse paire et ordonnée impaire
					if (i % 2 == 0 && j % 2 == 1) {
						res.putPixel(i, j, valeur);
					}
					break;
				}
			}
		}

		return res;
	}

	ImageProcessor est_G_hamilton(ImageProcessor cfa_ip) {
		width = cfa_ip.getWidth();
		height = cfa_ip.getHeight();
		ImageProcessor est_ip = cfa_ip.duplicate();
		for (int j = 0; j < height; j = j + 2) {
			for (int i = 1; i < width; i = i + 2) {

				int pCourant = cfa_ip.getPixel(i, j) & 0xff;

				int pXGauche = cfa_ip.getPixel(i - 1, j) & 0xff;
				int pXDroite = cfa_ip.getPixel(i + 1, j) & 0xff;
				int pXGaucheG = cfa_ip.getPixel(i - 2, j) & 0xff;
				int pXDroiteD = cfa_ip.getPixel(i + 2, j) & 0xff;

				int pYHaut = cfa_ip.getPixel(i, j - 1) & 0xff;
				int pYBas = cfa_ip.getPixel(i, j + 1) & 0xff;
				int pYHautH = cfa_ip.getPixel(i, j - 2) & 0xff;
				int pYBasB = cfa_ip.getPixel(i, j + 2) & 0xff;

				int gradX = Math.abs(pXGauche - pXDroite) + Math.abs(2 * pCourant - pXGaucheG - pXDroiteD);
				int gradY = Math.abs(pYHaut - pYBas) + Math.abs(2 * pCourant - pYHautH - pYBasB);
				if (gradX < gradY) {

					est_ip.putPixel(i, j, ((pXGauche + pXDroite) / 2 + (2 * pCourant - pXGaucheG - pXDroiteD) / 4));
				} else if (gradX > gradY) {
					est_ip.putPixel(i, j, ((pYHaut + pYBas) / 2 + (2 * pCourant - pYHautH - pYBasB) / 4));
				} else {
					est_ip.putPixel(i, j, ((pYHaut + pXGauche + pXDroite + pYBas) / 4
							+ (4 * pCourant - pYHautH - pXGaucheG - pXDroiteD - pYBasB) / 8));
				}
			}
		}

		for (int j = 1; j < height; j = j + 2) {
			for (int i = 0; i < width; i = i + 2) {

				int pCourant = cfa_ip.getPixel(i, j) & 0xff;

				int pXGauche = cfa_ip.getPixel(i - 1, j) & 0xff;
				int pXDroite = cfa_ip.getPixel(i + 1, j) & 0xff;
				int pXGaucheG = cfa_ip.getPixel(i - 2, j) & 0xff;
				int pXDroiteD = cfa_ip.getPixel(i + 2, j) & 0xff;

				int pYHaut = cfa_ip.getPixel(i, j - 1) & 0xff;
				int pYBas = cfa_ip.getPixel(i, j + 1) & 0xff;
				int pYHautH = cfa_ip.getPixel(i, j - 2) & 0xff;
				int pYBasB = cfa_ip.getPixel(i, j + 2) & 0xff;

				int gradX = Math.abs(pXGauche - pXDroite) + Math.abs(2 * pCourant - pXGaucheG - pXDroiteD);
				int gradY = Math.abs(pYHaut - pYBas) + Math.abs(2 * pCourant - pYHautH - pYBasB);
				if (gradX < gradY) {

					est_ip.putPixel(i, j, ((pXGauche + pXDroite) / 2 + (2 * pCourant - pXGaucheG - pXDroiteD) / 4));
				} else if (gradX > gradY) {
					est_ip.putPixel(i, j, ((pYHaut + pYBas) / 2 + (2 * pCourant - pYHautH - pYBasB) / 4));
				} else {
					est_ip.putPixel(i, j, ((pYHaut + pXGauche + pXDroite + pYBas) / 4
							+ (4 * pCourant - pYHautH - pXGaucheG - pXDroiteD - pYBasB) / 8));
				}
			}
		}

		return (est_ip);
	}
}