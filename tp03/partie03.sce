function res = sous_quantification(img, m, vmin, vmax)
    intervales = linspace(vmin, vmax, m+1);
    dim = size(img);
    l = dim(1);
    h = dim(2);
    c = dim(3);
   
    res = zeros(dim);
    for i = 1:l, for j = 1:h, for k = 1:c
        ind = find(intervales <= img(i, j, k));
        res(i, j, k) = ind($) / m;
    end; end; end;
endfunction