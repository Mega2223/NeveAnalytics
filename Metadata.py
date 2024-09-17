from osgeo import gdal, gdalconst
from os import listdir
from os.path import isfile, join
import json
import sys

def transfer_metadata(path_from: str, path_to: str):
    print("Populating " + path_to)
    tiff_src = gdal.Open(path_from, gdalconst.GA_ReadOnly)
    tiff_dest = gdal.Open(path_to,gdalconst.GA_Update)

    tiff_dest.SetProjection(tiff_src.GetProjection())
    tiff_dest.SetMetadata(tiff_src.GetMetadata())
    tiff_dest.SetGeoTransform(tiff_src.GetGeoTransform())

    del(tiff_src)
    del(tiff_dest)

transfer_metadata(sys.argv[1],sys.argv[2])