package DCT;

import ij.process.*; // Pour classe Float Processor
import java.awt.Rectangle;

abstract public class DCT2D {

	// ---------------------------------------------------------------------------------
	/**
	 * Transformation DCT 2D directe (m�thode de classe) utilisant la
	 * s�parabilit�
	 * 
	 * @param fp
	 *            Signal 2D d'entr�e et de sortie (MxN) (FloatProcessor)
	 */
	public static void forwardDCT(FloatProcessor f) {
		final Rectangle roi = f.getRoi();
		final int M = (int) roi.getMaxX();
		final int N = (int) roi.getMaxY();
		final int m = (int) roi.getMinX();
		final int n = (int) roi.getMinX();

		// Traiter les lignes
		for (int u = n; u < N; u++) {
			final double[] line = f.getLine(n, u, N, u);
			final double[] treatedLine = DCT1D.forwardDCT(line);

			// Réattribution des valeurs dans le tableau d'origine pour la ligne
			// courante
			for (int i = 0; i < roi.getWidth(); i++) {
				f.putPixelValue(i + m, u, treatedLine[i]);
			}
		}

		// Traiter les colonnes de l'image résultant du traitement des lignes
		for (int v = 0; v < M; v++) {
			final double[] column = f.getLine(v, m, v, M);
			final double[] treatedColumn = DCT1D.forwardDCT(column);

			// Réattribution des valeurs pour la colonne courante
			for (int j = n; j < roi.getHeight(); j++) {
				f.putPixelValue(v + n, j, treatedColumn[j]);
			}
		}
	}

	
	// ---------------------------------------------------------------------------------
	/**
	 * Transformation DCT 2D inverse (m�thode de classe)
	 * 
	 * @param fp
	 *            Signal 2D d'entr�e et de sortie (FloatProcessor)
	 */
	public static void inverseDCT(FloatProcessor fp) {

		// Traiter les lignes
		/* � compl�ter */

		// Traiter les colonnes de l'image r�sultant du traitement des lignes
		/* � compl�ter */
	}
}