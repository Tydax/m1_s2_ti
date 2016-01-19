// Double boucles pour calculer les sources
// L’éclairement pour un point donné = somme des éclairements calculés par chaque source

// Paramètres
n = 2      // Carré de N*N sources
cote = 400      // Côté de carré de surface à éclairer
h = 0.50;    // Hauteur de la source ponctuelle

// Définition de la surface à éclairer
axe = [0:cote-1] / 100 + 5e-3;
x = ones (1:cote)' * axe;
y = axe' * ones (1:cote);

// Intensité énergétique pour tous les points
i0 = 100 / (2 * %pi);
// Éclairement de base pour le point perpendiculaire à la source
e0 = i0/h^2

// Éclairement total
efin = zeros(cote, cote);

// Boucle qui parcourt tous les x des sources
for i = 1 : n
    ref = cote / 100;
    xs = ref / (n * 2) + (i - 1) * ref / n; // x de la source
    // Boucle qui parcourt tous les y des sources
    for j = 1 : n
        ys = ref / (n * 2) + (j - 1) * ref / n; // y de la source
        
        // Calcul de la distance
        d = sqrt ((x - xs).^2 + (y - ys).^2);
        // Calcul de l'éclairement
        e = (e0 * h^4) ./ (d.^2 + h^2).^2;
        // Ajout à la matrice résultante
        efin = efin + e;
    end
end

plot3d (axe, axe, efin);
