# Question 1
On sélectionne deux points capturant le motif répété afin d’obtenir la période :
$$Période= |y(\text{Point 1}) - y(\text{Point 2})| = |19 - 24| = 5 \text{pixels / cycle}$$
$$Fréquence = \frac{1}{\text{période}} = \frac{1}{5} = 0.2 \text{cycle / pixel}$$

# Question 2
Les coordonnées de la raie maximale sont $(128, 128)$, soit $(0, 0)$, soit l’origine, dans le plan de Fourier puisqu’il s’agit du point central de l’image.

# Question 3
Les coordonnées de la raie secondaire peut se calculer en cherchant à nouveau la valeur maximum à partir de l’image transformée où la raie principale a été remplacée par un point noir. On obtient alors les coordonnées $(128, 77)$, et $(128, 179)$, qui sont bien symétriques par rapport au centre. Voici la macro modifiée afin de chercher les raies secondaires :
``` java
macro "direction FFT"
{
    // ouverture d'une image si necessaire - sinon la macro analyse l'image courante
    //open ("/home/bmathon/Enseignement/TI/tp6_TF/images/256_a.jpg");

    // recuperation de l'identifiant de l'image
    image = getImageID();

    // application de la TDF (FFT : Fast Fourier Transform)
    run("FFT");

    // recuperation de l'ID du module de la FFT
    fourier = getImageID();

    // recuperation de la taille W x H du module de la FFT
    W = getWidth();
    H = getHeight();

    // recherche du max
    max_1 = 0; 
    i_max_1 = 0;
    j_max_1 = 0;
    
    for (j=0; j<H; j++)
    {
        for (i=0; i<W; i++) 
        {
            p = getPixel(i,j);
            if ( max_1 < p)
            {
                max_1 =p;
                i_max_1 = i;
                j_max_1 =j;
            } 
        }
    }

    // mise a zero de la valeur max

    setPixel (i_max_1,j_max_1,0);
	print("i_max_1=", i_max_1);
	print("j_max_1=", j_max_1);


	// Recherche de la raie secondaire en recherchant le max
	max_2 = 0;
	i_max_2 = newArray(2);
	j_max_2 = newArray(2);

	for (j = 0; j < H; j++) {
		for (i = 0; i < W; i++) {
			p = getPixel(i, j);
			
			if (max_2 < p) {
				max_2 = p;
				i_max_2[0] = i;
				j_max_2[0] = j;
			} else if (max_2 == p) {
				i_max_2[1] = i;
				j_max_2[1] = j;
			}
		}
	}

	// En colore en noir les points correspondant aux raies secondaires
	setPixel(i_max_2[0],  j_max_2[0], 0);
	setPixel(i_max_2[1],  j_max_2[1], 0);
	// On affiche les coordonnées
	print("i_max_2=", i_max_2[0], " ; ", i_max_2[1]);
	print("j_max_2=", j_max_2[0], " ; ", j_max_2[1]);
}
```
Afin de retrouver les coordonnées sur le plan de Fourier, il convient de soustraire la moitié de la largeur de l’image à l’abscisse et de soustraire l’ordonnée à la moitié de la hauteur de l'image dans le but de replacer l’origine au centre de l’image. Ensuite, il ne reste plus qu’à calculer le pourcentage de la coordonnée, et le réappliquer à la différence entre la valeur maximum et la valeur minimum de l’axe. Concrètement :
$$abscisse(y) = \frac{o(y) - \text{moitié de la largeur} } {\text{largeur}} \times (\text{borne max de l’axe d’arrivée} - \text{borne min de l’axe d’arrivée})$$
$$ordonnée(y) = \frac{\text{moitié de la hauteur} - o(y)} {\text{hauteur}} \times (\text{borne max de l’axe d’arrivée} - \text{borne min de l’axe d’arrivée})$$
Soit pour notre cas :
$$ordonnée(77) = \frac{\frac{256} {2} - 77} {256} \times (0.5 - (-0.5)) = \frac{51} {256} \approx 0.199$$
$$ordonnée(179) = \frac{\frac{256} {2} - 179} {256} \times (0.5 - (-0.5)) = -\frac{51} {256} \approx -0.199$$

Donc les coordonnées des raies secondaires sur le plan de Fourier sont : $(0, 0.199)$ et $(0, -0.199)$.

#  Question 4
## 128_a.jpg
La période est de 5 pixels / cycle. La fréquence est de 0,2 cycle/pixels.
La raie principale se situe aux coordonnées $(64, 64)$, soit $(0, 0)$ sur le plan de Fourier.
Les raies secondaires se situent aux cordonnées $(64, 38)$ et $(64, 90)$, soit $(0, 0.203)$ et $(0, -0.203)$ sur le plan de Fourier.

## 256_b.jpg
La période est de 5 pixels / cycle. La fréquence est de 0,2 cycle/pixels.
La raie principale se situe aux coordonnées $(128, 128)$, soit $(0, 0)$ sur le plan de Fourier.
Les raies secondaires se situent aux cordonnées $(77, 77)$ et $(179, 179)$, soit $(-0.199,  0.199)$ et $(0.199, -0.199)$ sur le plan de Fourier.

# Question 5
Si les abscisses des raies secondaires ont même signe, la texture est **horizontale**.
Si leurs ordonnées ont même signe, la texture est **verticale**.
Si les deux ont des signes différents, la texture est **diagonale**.
Ce qui donne en code :
``` java
	if (i_four[0] * i_four[1] >= 0) {
		class = "horizontale";
	} else if (j_four[0] * j_four[1] >= 0) {
		class = "verticale";
	} else {
		class = "diagonale";
	}
	```