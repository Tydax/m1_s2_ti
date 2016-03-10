# Question 1
On sélectionne deux points capturant le motif répété afin d’obtenir la période :
$$Période= |y(\text{Point 1}) - y(\text{Point 2})| = |19 - 24| = 5 \text{pixels / cycle}$$
$$Fréquence = \frac{1}{\text{période}} = \frac{1}{5} = 0.2 \text{cycle / pixel}$$

# Question 2
Les coordonnées de la raie maximale sont $(128, 128)$, soit $(0, 0)$, soit l’origine, dans le plan de Fourier puisqu’il s’agit du point central de l’image.

# Question 3
Les coordonnées de la raie secondaire peut se calculer en cherchant à nouveau la valeur maximum à partie de l’image transformée où la raie principale a été remplacée par un point noir. On obtient alors les coordonnées $(128, 77)$, et $(128, 179)$, qui sont bien symétriques par rapport au centre.
Afin de retrouver les coordonnées sur le plan de Fourier, il convient de soustraire la moitiée de l’image dans le but de replacer l’origine au centre de l’image. Ensuite, il ne reste plus qu’à calculer le pourcentage de la coordonnée, et le réappliquer à la différence entre la valeur maximum et la valeur minimum de l’axe. Concrètement :
$$f(y) = \frac{\text{moitié de l’axe d’origine} - o(y)} {\text{valeur maximum de l’axe d’origine}} \times (\text{valeur maximum de l’axe d’arrivée} - \text{valeur minimum de l’axe d’arrivée})$$
Soit pour notre cas :
$$f(77) = \frac{\frac{256} {2} - 77} {256} \times (0.5 - (-0.5)) = \frac{51} {256} \approx 0.199$$
$$f(179) = \frac{\frac{256} {2} - 179} {256} \times (0.5 - (-0.5)) = -\frac{51} {256} \approx -0.199$$

Donc les coordonnées des raies secondaires sur le plan de Fourier sont : $(0, 0.199)$ et $(0, -0.199)$.

#  Question 4
## 128_a.jpg
La période est de 5 pixels / cycle.
La raie principale se situe aux coordonnées $(64, 64)$, soit $(0, 0)$ sur le plan de Fourier.
Les raies secondaires se situent aux cordonnées $()$