# NeveAnalytics

Conjunto de ferramentas que eu desenvolvi como meu projeto final de Sensoriamento Remoto na UFABC, com função de calcular o NDSI (Normalized Difference Snow Index) de várias imagens de satélite em massa por linha de comando.

## Dependências
Para rodar o programa são necessários: 
- Sistema operacional Windows
- Java versão >= 17
- Python versão >= 3.11.6
- Numpy & Matlab (bibliotecas de Python)
- GDAL (versão mais recente)

## Instruções de uso
Coloque todas suas imagens com títulos _inalterados_ dentro de uma determinada pasta, em `Config.json` coloque a pasta de seus dados como valor de `sources_dir` e coloque o caminho do seu executável de python em `python_dir`. A partir daí é so rodar a pipeline (é de suma importância que ela seja executada na ordem, uma vez que os dados gerados poela fase anterior são necessários para a seguinte) 

## Processamento

O projeto é dividido em vário subcomponentes em 3 línguas diferentes (4 classes executáveis Java, 1 script Batch e 2 scripts Python). Todavia todos podem ser executados através da classe NeveAnalytics.java por meio de argumentos de execução. Assim, todos estes scripts standalones estão unificados em um só programa. Para scripts em Java a classe chama o método main de cada script conforme necessário, para scripts em outras linguagens é utilizada a biblioteca de runtime nativa do Java para executar-los, simulando o comportamento da execução por linha de comando.  
O arquivo .jar de release têm como classe principal o NeveAnalytics.java, assim todas as funções do programa estão disponíveis sem qualquer necessidade de recompilar o programa.  

![NDVI](https://github.com/Mega2223/NeveAnalytics/assets/59067466/b3b4256f-af22-40f8-a2bb-8d1bfa3d197e)

Assim, o comando a seguir pode efetuar toda a pipeline de forma expressa: 
```
java -jar NeveAnalytics.jar --clean-names --gen-in-metadata -sort --save-png --perform-raster-equations --extract-metadata --gen-out-metadata --gen-report --plot-graph
```

## Subcomponentes

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

## Parâmetros adicionais
- `--save-png`: Além de salvar os arquivos TIFF, também são salvas imagens PNG representando o NDSI.
- `--snow-threshold`: Troca o threashold que o programa considera como área de neve, por padrão se o NDSI é maior que 0.5 a região é considerada coberta por neve.
- `--data-path`: Troca a fonte de dados durante a execução

## Dependências de build
Para compilar este programa são necessários: 
- Java versão >= 17
- Maven
- [tiff-java](https://github.com/ngageoint/tiff-java) (dependência Maven)
- [GSON](https://github.com/google/gson) (dependência Maven)

