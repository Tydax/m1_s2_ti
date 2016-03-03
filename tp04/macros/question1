macro "Question 1" {
	image = getImageID();
	w = getWidth();
	h = getHeight();

	min = 255;
	max = 0;
	
	for (j = 0; j < h; j++) {
		for (i = 0; i < w; i++) {
			pix = getPixel(i, j);
			
			if (pix < min) {
				min = pix;
			}
			
			if (pix > max) {
				max = pix;
			}
		}
	}

	print("min = ", min);
	print("max = ", max);

	for (j = 0; j < h; j++) {
		for (i = 0; i < w; i++) {
			val = getPixel(i, j);
			val = (val - min) / (max - min) * 255;
			setPixel(i, j, val);
		}
	}
}
