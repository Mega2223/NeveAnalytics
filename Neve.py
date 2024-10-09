import calendar
import datetime
import json
import os
import subprocess

from osgeo import gdal, ogr
from osgeo_utils import gdal_calc
from fmask import fmask

from utils import file_manager, data_utils, misc, calculator
from utils.calculator import calcNDSI, applyCloudMask, cropToShapefile
from utils.misc import debug

print("Olá mundo :)")
misc.set_debug_lvl(misc.DEBUG_VERBOSE)
config = json.loads(open("Configs.json").read())
# print("Config tree = " + str(config))

root = config["src_dir"]
temp = config["temp_dir"]
dest = config["dest_dir"]

ndsi_folder = dest + "\\NDSI"
cropped_folder = dest + "\\crop"
mask_folder = dest + "\\masks"
yearly_averages = dest + "\\yearly_averages"
monthly_averages = dest + "\\monthly_averages"

band_dirs = file_manager.doRecursiveSearch(root, filter_function=file_manager.isTiffImage)

# clear = True
# if clear:
#     try:
#         os.removedirs(ndsi_folder)
#         os.removedirs(cut_folder)
#         os.removedirs(mask_folder)
#     except FileNotFoundError:
#         pass

if not os.path.exists(ndsi_folder): os.mkdir(ndsi_folder)
if not os.path.exists(cropped_folder): os.mkdir(cropped_folder)
if not os.path.exists(mask_folder): os.mkdir(mask_folder)
if not os.path.exists(monthly_averages): os.mkdir(monthly_averages)
if not os.path.exists(yearly_averages): os.mkdir(yearly_averages)

for img in band_dirs: debug("Found image: " + str(img))

for thermal in band_dirs:
    if file_manager.getBand(thermal[0]) == 3:
        green = file_manager.findBandForImage(thermal, 6)
        name = file_manager.getNameNoBand(thermal[0])
        if green == None:
            print("No green band bound for " + thermal[0])
            continue
        out_file = ndsi_folder + "\\" + name + "_NDSI.tif"
        if os.path.isfile(out_file): continue
        print("calculating NDSI for " + thermal[0] + " and " + green[0])
        calcNDSI(thermal,green,out_file)
    elif file_manager.getBand(thermal[0]) == 2:
        green = file_manager.findBandForImage(thermal, 5)
        name = file_manager.getNameNoBand(thermal[0])
        if green == None:
            print("No green band bound for " + thermal[0])
            continue
        out_file = ndsi_folder + "\\" + name + "_NDSI.tif"
        if os.path.isfile(out_file): continue
        print("calculating NDSI for " + thermal[0] + " and " + green[0])
        calcNDSI(thermal,green,out_file)

pixel_dat = file_manager.doRecursiveSearch(root, filter_function=file_manager.isTiffImage)
for quality in pixel_dat:
    if file_manager.getBand(quality[0]) != "QA": continue
    ndsi = file_manager.findBandForImage(quality,"NDSI",folder=ndsi_folder)
    out_file = mask_folder + "\\" + file_manager.getNameNoBand(quality[0]) + "_CLOUDMASK.tif"
    if ndsi == None:
        print("No NDSI found for " + quality[0])
        continue
    print("Applying mask " + quality[0] + " for " + ndsi[0])
    if os.path.isfile(out_file): continue
    applyCloudMask(ndsi,quality,out_file)
# raster = ogr.Open("C:\\Users\\Imperiums\\Desktop\\Parque Nacional Laguna San Rafael Shapefiles\\Límite_Parque_Nacional_Laguna_San_Rafael_2024.shp")

ndsis = file_manager.doRecursiveSearch(ndsi_folder, filter_function=file_manager.isTiffImage)
shape = "C:\\Users\\Imperiums\\Desktop\\Parque Nacional Laguna San Rafael Shapefiles\\Límite_Parque_Nacional_Laguna_San_Rafael_2024.shp"
# debug("Cropping images in accordance to provided shapefile")

# for img in ndsis:
#     img_dir = file_manager.imgPath(img)
#     img_to = cropped_folder + "\\" + img[0]
#     if os.path.exists(img_to): continue
#     debug("Cropping " + img[0])
#     cropToShapefile(img_dir,img_to,shape)
#
# cropped_ndsis = file_manager.doRecursiveSearch(cropped_folder, filter_function=file_manager.isTiffImage)

for year in range(1980, 2025, 1):
    debug("Finding images for the year " + str(year))
    imgs_for_year: list[tuple[str,str]] = []
    begin = datetime.date(year,1,1)
    end = datetime.date(year,12,31)
    for img in ndsis:
        if file_manager.isFromTimePeriod(img[0], begin, end):
            imgs_for_year.append(img)
            print("found " + img[0])

    img_count = len(imgs_for_year)
    debug("Found " + str(img_count) + " images for this year")
    if len(imgs_for_year) > 0:
        dest_file = yearly_averages + "\\" + file_manager.getNameNoBand(imgs_for_year[0][0]) + "_" + str(year) + "_MOSAIC.tif"
        if os.path.exists(dest_file): continue
        calculator.mosaicAndShape(imgs_for_year, dest_file,shape)

for year in range(1980, 2025, 1):
    for month in range(1,13,4):
        month_end = month + 3
        debug("Finding images for the period y:" + str(year) + " m:" + str(month) + "-" + str(month_end))
        imgs_for_period: list[tuple[str,str]] = []
        begin = datetime.date(year,month,1)
        end = datetime.date(year,month_end,calendar.monthrange(year,month_end)[1])
        for img in ndsis:
            if file_manager.isFromTimePeriod(img[0], begin, end):
                imgs_for_period.append(img)
                print("found " + img[0])

        img_count = len(imgs_for_period)
        debug("Found " + str(img_count) + " images for this period")
        if len(imgs_for_period) > 0:
            dest_file = (monthly_averages + "\\" + file_manager.getNameNoBand(imgs_for_period[0][0]) +
                         "_{:04d}_{:02d}_{:02d}_MOSAIC.tif".format(year,month,month_end))
            if os.path.exists(dest_file): continue
            calculator.mosaicAndShape(imgs_for_period, dest_file,shape)


mosaics = file_manager.doRecursiveSearch(yearly_averages, filter_function=file_manager.isTiffImage)

# for m in mosaics:
#     print(
#         gdal.Info(m[1]+"\\"+m[0],options=gdal.InfoOptions("-json"))
#     )