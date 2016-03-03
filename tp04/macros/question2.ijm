macro "Question 2" {
	a = -50;
	b = 2;

	image = getImageID();
	w = getWidth();
	h = getHeight();

	for (j = 0; j < h; j++) {
		for (i = 0; i < w; i++) {
			pix = getPixel(i, j);
			setPixel(i, j, a + b * pix);
		}
	}
}
