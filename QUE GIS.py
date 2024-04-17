import os 
from os import walk

from qgis.core import (QgsVectorLayer, QgsRasterLayer)

src_path = "C:/Users/Imperiums/Desktop/UFABC/Dor/SR/QGIS MISC/LANDSAT NOSSO/LC08_L1TP_219076_20231218_20240103_02_T1"

def load_raster(path, name = "LAYER"):
    rlayer = QgsRasterLayer(src_path + "/" + path, name)
    if not rlayer.isValid():
        print("Layer failed to load!")
        print("Path: " + src_path + "/" + path)
    else:
        QgsProject.instance().addMapLayer(rlayer)

filenames = next(walk(src_path), (None, None, []))[2]
for filen in filenames:
    load_raster(filen, name = filen)

#load_raster("/LC08_L1TP_219076_20231218_20240103_02_T1_B4.TIF")

