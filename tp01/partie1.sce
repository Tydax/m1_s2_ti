// Définition des échantillons sur un axe
axe = [0:99] / 100 + 5e-3;
// Définition des éléments de surface
x = ones (1:100)' * axe;    // Ensemble des abscisses des points de la surface
y = axe' * ones (1:100);    // Ensemble des ordonnées des points de la surface
// Position de la source
xs = 0.5;
ys = 0.5;
// Calcul de la distance
d = sqrt ((x - xs).^2 + (y - ys).^2);

// Intensité énergétique pour tous les points
i0 = 100 / (2 * %pi);
// Hauteur de la source ponctuelle
h = 0.50;
// Calcul de l'éclairement
e = (i0 * h) ./ (d.^2 + h^2).^1.5

// Trace de la fonction distance
plot3d1 (axe, axe, e);
// Visualisation sous forme d'image en niveaux de gris
imshow (e);
