import os 
from os import walk
import processing

from qgis.core import (QgsVectorLayer, QgsRasterLayer)

src_path = "C:/Users/oliveira.sampaio/Desktop/TRABALHO NEVE/BRUTOS"
subpaths = ["/LANDSAT 4-5"]
bands = {
    subpaths[0] : ["B2","B5"]
}

root = QgsProject.instance().layerTreeRoot()
brute_node = root.addGroup("RAW")
subpath_nodes = {}

for c in subpaths:
    cc = brute_node.addGroup(c)
    bandd = {}
    for b in bands[c]:
        bandd.update({str(b):cc.addGroup(str(b))})
    subpath_nodes.update({c:{"node":cc,"bands:":bands[c],"subgroups":bandd}})

def check_band(subpath_id,band) -> bool :
    for actact in bands[subpath_id]:
            #print(actact + " : " + band)
            if actact == band: return True
    return False
            
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

rasters = []
paths = []
i = 0

for path_act in subpaths:
    filenames = next(walk(src_path+path_act), (None, None, []))[2]
    for filen in filenames:
        ext = filen.split(".")[1]
        properties = filen.split("_")
        #print(filen + " -> " + ext)
        # print(len(properties))
        if (ext != "TIF") or (len(properties) < 9): continue
        band = properties[8].split(".")[0]
        #print("BAND = " + band)
        if not check_band(path_act,band): continue
        r = load_raster(path_act+"/"+filen, name = filen)
        move_layer(r,subpath_nodes[path_act]['subgroups'][band])
        rasters.append(r)
        paths.append(filen)
        i+=1
        pass

exp = QgsExpression('1 + 1')
exp.evaluate()

#load_raster("/LC08_L1TP_219076_20231218_20240103_02_T1_B4.TIF")

#expression1 = QgsExpression('LC08_L1TP_219076_20231218_20240103_02_T1_B4/3')
    
#g.removeChildNode(h)

