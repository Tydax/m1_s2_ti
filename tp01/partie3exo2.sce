// Double boucles pour calculer les sources
// L’éclairement pour un point donné = somme des éclairements calculés par chaque source

// Paramètres
n = 1      // Carré de N*N sources
cote = 4      // Côté de carré des sources
h = 0.50;    // Hauteur du carré de sources

// Définition de la surface à éclairer
 
axe = [0:99] / 100 + 5e-3 + 1.5;
x = ones (1:100)' * axe; // Offset pour centrer la surface sous le carré
y = axe' * ones (1:100);

// Intensité énergétique pour tous les points
i0 = 100 / (2 * %pi);
// Éclairement de base pour le point perpendiculaire à la source
e0 = i0/h^2

var = 0.05;

while var >= 0.05 do
    n = n + 1
    // Éclairement total
    efin = zeros(100, 100);
    
    // Boucle qui parcourt tous les x des sources
    for i = 0 : n - 1
        xs = i * cote / (n - 1); // x de la source
        // Boucle qui parcourt tous les y des sources
        for j = 0 : n - 1
            ys = j * cote / (n - 1); // y de la source
            
            // Calcul de la distance
            d = sqrt ((x - xs).^2 + (y - ys).^2);
            // Calcul de l'éclairement
            e = (e0 * h^4) ./ (d.^2 + h^2).^2;
            // Ajout à la matrice résultante
            efin = efin + e;
        end
    end
    var = (max(efin) - min(efin)) / max(efin);
end

plot3d (axe, axe, efin);
imshow (efin / 100);
