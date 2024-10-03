import json
import os
import subprocess

from osgeo_utils import gdal_calc

from utils import file_manager, data_utils, misc
from utils.misc import debug

print("Olá mundo :)")
misc.set_debug_lvl(misc.DEBUG_VERBOSE)
config = json.loads(open("Configs.json").read())
# print("Config tree = " + str(config))

root = config["src_dir"]
temp = config["temp_dir"]
dest = config["dest_dir"]
clones = dest + "\\clones"
ndsi_folder = dest + "\\NDSI"
cut_folder = dest + "\\crop"
mask_folder = dest + "\\masks"

if not os.path.exists(clones): os.mkdir(clones)
if not os.path.exists(ndsi_folder): os.mkdir(ndsi_folder)
if not os.path.exists(cut_folder): os.mkdir(cut_folder)
if not os.path.exists(mask_folder): os.mkdir(mask_folder)

band_dirs = file_manager.doRecursiveSearch(root, filter_function=file_manager.isTiffImage)

for b3 in band_dirs:
    debug("Loaded image: " + str(b3))

for i in range(0, len(band_dirs)):
    if os.path.isfile(clones + "\\" + band_dirs[i][0]): continue
    debug("Converting image " + str(band_dirs[i][0]) + " to float format.")
    data_utils.convert_to_float(
        file_manager.imgPath(band_dirs[i]),
        band_dirs[i][0],
        clones
    )

clones = file_manager.doRecursiveSearch(clones, filter_function=file_manager.isTiffImage)

for b3 in clones:
    if file_manager.getBand(b3[0]) == 3:
        b6 = file_manager.findBandForImage(b3, 6)
        name = file_manager.getNameNoBand(b3[0])
        print("calculating NDSI for " + b3[0] + " and " + b6[0])
        out_file = ndsi_folder + "\\" + name + "_NDSI.tif"

        if os.path.isfile(out_file): continue

        gdal_calc.Calc("(A-B)/numpy.maximum(1,numpy.minimum(A,1000000)+numpy.minimum(B,1000000))",
                       A=file_manager.imgPath(b3),
                       B=file_manager.imgPath(b6),
                       outfile=out_file,
                       NoDataValue=0,
                       overwrite=True,
                       quiet=True,
                       type="Float64"
                       )

pixel_dat = file_manager.doRecursiveSearch(root, filter_function=file_manager.isTiffImage)
for quality in pixel_dat:
    if file_manager.getBand(quality[0]) != "QA": continue
    ndsi = file_manager.findBandForImage(quality,"NDSI")
    print("Applying mask " + quality[0] + " for " + ndsi[0])

# raster = ogr.Open("C:\\Users\\Imperiums\\Desktop\\Parque Nacional Laguna San Rafael Shapefiles\\Límite_Parque_Nacional_Laguna_San_Rafael_2024.shp")

ndsis = file_manager.doRecursiveSearch(ndsi_folder, filter_function=file_manager.isTiffImage)
shape = "C:\\Users\\Imperiums\\Desktop\\Parque Nacional Laguna San Rafael Shapefiles\\Límite_Parque_Nacional_Laguna_San_Rafael_2024.shp"
debug("Cropping images in accordance to provided shapefile")
for img in ndsis:
    img_dir = file_manager.imgPath(img)
    img_to = cut_folder + "\\" + img[0]
    if os.path.exists(img_to): continue
    debug("Cropping " + img[0])
    subprocess.call(['gdalwarp', img_dir, img_to, '-cutline', shape, '-crop_to_cutline'], stderr=None)

