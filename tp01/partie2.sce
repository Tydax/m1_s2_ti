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
// Éclairement pour le point perpendiculaire à la source
e0 = i0/h^2
// Calcul de l'éclairement
e = (e0 * h^4) ./ (d.^2 + h^2).^2


// Trace de la fonction distance
plot3d (axe, axe, e);
// Visualisation sous forme d'image en niveaux de gris
imshow (e/100);
