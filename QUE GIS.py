import os 
from os import walk
import processing

from qgis.core import (QgsVectorLayer, QgsRasterLayer)

src_path = "C:/Users/Imperiums/Desktop/UFABC/Dor/SR/QGIS MISC/LANDSAT NOSSO/LC08_L1TP_219076_20231218_20240103_02_T1"

def load_raster(path, name = "LAYER"):
    rlayer = QgsRasterLayer(src_path + "/" + path, name)
    if not rlayer.isValid():
        print("Layer failed to load!")
        print("Path: " + src_path + "/" + path)
    else:
        QgsProject.instance().addMapLayer(rlayer)
        return rlayer
    
def raster_equation_onearg(input_path):
    input_raster = rasters[4]
    output_raster = src_path + "/TESTE"
    parameters = {
        'INPUT_A' : input_path,
        'BAND_A' : 1,
        'FORMULA' : '2',
        'OUTPUT' : output_raster}
        
    processing.runAndLoadResults('gdal:rastercalculator', parameters)

def move_layer(layer,group):
    if layer == None: return
    root = QgsProject.instance().layerTreeRoot()
    mylayer = root.findLayer(layer.id())
    myClone = mylayer.clone()
    parent = mylayer.parent()
    group.insertChildNode(0, myClone)
    parent.removeChildNode(mylayer)
    
def layer_search(upper_layer):
    l = upper_layer

filenames = next(walk(src_path), (None, None, []))[2]

rasters = []
paths = []
i = 0

for filen in filenames:
    rasters.append(load_raster(filen, name = filen))
    paths.append(filen)
    i+=1
    pass

exp = QgsExpression('1 + 1 = 2')
exp.evaluate()

#load_raster("/LC08_L1TP_219076_20231218_20240103_02_T1_B4.TIF")

#expression1 = QgsExpression('LC08_L1TP_219076_20231218_20240103_02_T1_B4/3')

root = QgsProject.instance().layerTreeRoot()

brute_node = root.addGroup("RAW")
for r in rasters:
    move_layer(r,brute_node)
    
#g.removeChildNode(h)

