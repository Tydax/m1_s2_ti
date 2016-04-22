// Histogramme cumulé
getRawStatistics(surf, moy, min, max, std, h); // h[0..255] <-> histo
hc=newArray(256);
hc[0]=h[0];
for (i=1;i< h.length;i++) {
    hc[i] = hc[i-1]+h[i] ;
}

nbPixels = hc[255] * 0.80;
print("Nombre pixels : " + nbPixels);

seuil = 0;
for (i = 255; i < h.length; i--) {
	if (hc[i] <= nbPixels) {
		seuil = i;
		break;
	}
}
print("Seuil : " + seuil);

Plot.create("Histogramme cumulé de "+getTitle, "Niveau", "hc", hc);
Plot.show();