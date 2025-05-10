# NeveAnalytics

Aplicação que eu fiz para o o meu artigo [ANÁLISE ESPAÇO-TEMPORAL DA COBERTURA DE NEVE NO PARQUE NACIONAL LAGUNA SAN RAFAEL NO CHILE ENTRE 1998 E 2023](https://proceedings.science/sbsr-2025/trabalhos/analise-espaco-temporal-da-cobertura-de-neve-no-parque-nacional-laguna-san-rafae?lang=pt-br#) apresentado e publicado no XXI Simpósio Brasileiro de Sensoriamento Remoto.

A aplicação calcula o nível de neve em uma determinada região com o uso do NDSI (Normalized Difference Snow Index). A ideia é ela ser facilmente modificável para outros propositos de análise de dados de raster.

## Fluxograma
![Diagrama](https://github.com/user-attachments/assets/13a91d42-bf1e-4e2c-8899-4a49be11b4d9)

# Coleção de dados

Os dados são coletados com base no filtro do EarthEngine, são pegas as imagens que cumprem o seguinte critério
- São imagens LANDSAT 6, 7, 8 ou 9
- Estão no intervalo temporal especificado
- Cobrem a área de estudo

# Cálculo do NDSI

O Normalized Difference Snow Index é um indice que mede a probabilidade de um determinado pixel estar coberto por neve, ele se dá por  

$\Huge \frac{BV-BI}{BV+BI}$   

Onde $BI$ é a Banda Infravermelho Próximo 1 e $BV$ é a Banda Verde.

# Contribuições

Embora o código tenha sido inteiramente por [mim](http://lattes.cnpq.br/1515250772926471), houveram contribuições adicionais para este trabalho
- [Dr. Victor Fernandez Nascimento](http://lattes.cnpq.br/3373815361504494) (UFABC / LABGRIs)
- [Dra. Fernanda Casagrande](http://lattes.cnpq.br/9240753968751215) (INPE / DIMNT)

# Licença
