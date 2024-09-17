from osgeo import gdal, gdalconst
from os import listdir
from os.path import isfile, join
import json

SRC_ROOT = json.loads(open("Configs.json").read())["sources_dir"]

def transfer_metadata(path_from: str, path_to: str):
    print("Populating " + path_to)
    tiff_src = gdal.Open(path_from, gdalconst.GA_ReadOnly)
    tiff_dest = gdal.Open(path_to,gdalconst.GA_Update)

    tiff_dest.SetProjection(tiff_src.GetProjection())
    # tiff_dest.SetMetadata(tiff_src.GetMetadata())
    tiff_dest.SetGeoTransform(tiff_src.GetGeoTransform())

    del(tiff_src)
    del(tiff_dest)

def find_src_img(title: str):
    title = title.split("_")
    corresp = SRC_ROOT + "\\Unprocessed\\" + title[3] + "\\" + title[2] + "\\" + title[0]
    dirs = listdir(corresp)
    for act in dirs:
        actS = act.split(".")
        if(actS[len(actS)-1] == 'TIF'): return corresp + "\\" + act
    return None
    
snow_files = listdir(SRC_ROOT+"\\NDSI")
for img in snow_files:
    title = img.split(".")
    if(title[len(title)-1] != 'TIF'): continue
    src_img = find_src_img(title[0])
    transfer_metadata(src_img,SRC_ROOT+"\\NDSI\\"+img)
    