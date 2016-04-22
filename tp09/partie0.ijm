// Calcul de la norme du gradient par masque de Sobel
//
requires("1.41i");	// requis par substring(string, index)
setBatchMode(true);

sourceImage = getImageID();
filename = getTitle();
extension = "";
if (lastIndexOf(filename, ".") > 0) {
    extension = substring(filename, lastIndexOf(filename, "."));
    filename = substring(filename, 0, lastIndexOf(filename, "."));
}
filenameDerX = filename+"_der_x"+extension;
filenameDerY = filename+"_der_y"+extension;
run("Duplicate...", "title="+filenameDerX);
run("32-bit");	// Conversion en Float avant calcul des dérivées !!

/****** Calcul de la norme du gradient ******/
// récupération de la taille de l'image
w = getWidth();
h = getHeight();

// Calculs pour chaque pixel
setBatchMode("exit and display");

filenameDerXTan = filenameDerX + "tan"
filenameDerYTan = filenameDerY + "tan"

run("Duplicate...", "title=" + filenameDerX);
run("Duplicate...", "title=" + filenameDerY);

selectImage(filenameDerX);
run("32-bit");    // Conversion en Float avant calcul des dérivées !!
run("Convolve...", "text1=[-1 -2 -1\n0 0 0\n+1 +2 +1\n] normalize");
run("Duplicate...", "title="+filenameDerXTan);

selectImage(filenameDerX);
run("Square");

selectImage(filenameDerY);
run("32-bit");    // Conversion en Float avant calcul des dérivées !!
run("Convolve...", "text1=[-1 0 +1\n-2 0 +2\n-1 0 +1\n] normalize");
run("Duplicate...", "title="+filenameDerYTan);

selectImage(filenameDerY);
run("Square");

selectImage(filenameDerXTan);


imageCalculator("Add create 32-bit", filenameDerX, filenameDerY);
run("Square Root");
