function res = sous_quantification(img, m, vmin, vmax)
    intervales = linspace(vmin, vmax, m + 1);
    dim = size(img);
    l = dim(1);
    h = dim(2);
    
    res = zeros(dim);
    for i = 1:l, for j = 1:h
        ind = find(intervales <= img(i, j));
        res(i, j) = (ind($) - 1) / (m - 1);
    end; end;
endfunction
