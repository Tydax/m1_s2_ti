import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class sample_cfa implements PlugInFilter {

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

		// Génération de l'image CFA
		// Calcul des échantillons de chaque composante de l'image CFA
		ImageStack samples_stack = imp.createEmptyStack();
		samples_stack.addSlice("rouge", cfa_samples(ip,0));	// Composante R
		samples_stack.addSlice("vert", cfa_samples(ip,1));// Composante G
		samples_stack.addSlice("bleu", cfa_samples(ip,2));	// Composante B

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
}