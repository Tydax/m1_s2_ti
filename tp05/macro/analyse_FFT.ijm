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

	// On affiche les coordonnŽes
	print("i_max_2=", i_max_2[0], " ; ", i_max_2[1]);
	print("j_max_2=", j_max_2[0], " ; ", j_max_2[1]);

	// Calcul des coordonnŽes sur le plan de fourier
	i_four = newArray(2);
	j_four = newArray(2);

	for (i = 0; i < 2; i++) {
		i_four[i] = (i_max_2[i] - W / 2) / W;
		j_four[i] = (H / 2 - j_max_2[i]) / H;
	}
	
	print("i_four=", i_four[0], " ; ", i_four[1]);
	print("j_four=", j_four[0], " ; ", j_four[1]);

	if (i_four[0] * i_four[1] >= 0) {
		class = "horizontale";
	} else if (j_four[0] * j_four[1] >= 0) {
		class = "verticale";
	} else {
		class = "diagonale";
	}

	print("Classe : ", class);
}
