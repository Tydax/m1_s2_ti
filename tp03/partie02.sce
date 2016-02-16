function res = sous_echantillonnage(img, n)
    dim = size(img) / n
    l = dim(1);
    height = dim(2);
    res = zeros(l, height);
    for i = 0 : l - 1
        orix = i * n + 1;
        for j = 0 : height - 1
            oriy = j * n + 1;
            mat = img(orix:orix + n - 1, oriy:oriy + n - 1);
            res(i + 1, j + 1) = mean(double(mat));
        end
    end
endfunction

function res = sur_echantillonnage(img, n)
    dim = size(img) * n
    l = dim(1);
    h = dim(2);
    res = zeros(l, h);
    for i = 1 : l / n - 1
        orix = i * n - 1;
        for j = 1 : h / n - 1
            oriy = j * n - 1;
            res(orix:orix + n, oriy:oriy + n - 1) = img(i, j);
        end
    end
endfunction
