:: Programa em batch que utiliza do GDAL para computar e salvar os dados de cada arquivo .TIF presente na pasta 
:: Os dados sao salvos como um .json que pode ser facilmente acessado pelo programa que computa as equacoes de raster
for %%f in (*.TIF) do gdalinfo -stats -nomd -norat -noct -json %%f>%%f.json