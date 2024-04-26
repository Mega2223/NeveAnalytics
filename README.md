# NeveAnalytics

Conjunto de programas que eu fiz pro meu projeto final de Sensoriamento Remoto na UFABC, com função de calcular o NDSI de várias imagens de satélite em massa por linha de comando.

## Componentes

### Info.bat 
Script que usa do GDAL para criar metadados para as imagens em JSON

### src/main/java/net/mega2223/neveanalytics/
Source do código Java da aplicação, a classe NeveAnalytics serve de 'Hub' para os outros scripts em Java

### FileRenamer.java
Tira os "(1),(2),..." do final dos arquivos, uma vez que o EarthExplorer coloca eles toda vez quee ele falha um download

### FileSorter.java
Divide os arquivos em uma hierarquia de pastas

### NDSICalculator.java
Entra nos diretórios, procura os pares de raster e calcula e salva o índice NDSI para cada um, a imagem resultante é salva

### StatsReportGenerator.java
Calcula a área coberta por neve conforme a especificação e gera os dados necessários para a compilação dos dados

### Metadata.py
Extrai os metadados dos arquivos de LANDSAT brutos para as imagens geradas pela aplicação, caso seja necessário as colocar em algum software de geoprocessamento.

### Plot.py
Plotta os dados conforme a última run do StatsReportGenerator

