import ee
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import subprocess

import os
import datetime
import calendar
from typing import Tuple

import geemap

ee.Authenticate()
ee.Initialize(project='ee-juliocesarborgesdeoliv-neve')

def calcNDSI(img):
    # print("bands=",img.bandNames().getInfo())
    ndsi = img.select('GREEN').subtract(img.select('INFRARED')).divide(img.select('GREEN').add(img.select('INFRARED'))).rename('NDSI')
    mask = img.expression('(1 != (b("QA_PIXEL")==22280)+(b("QA_PIXEL")==24088)+(b("QA_PIXEL")==24216)+(b("QA_PIXEL")==24344)+(b("QA_PIXEL")==24472)+(b("QA_PIXEL")==55052))').rename('MASK')
    ret = ndsi.updateMask(mask)
    return ret, mask, ndsi

def get_landsat_images(min_date: datetime.date, max_date: datetime.date, study_area_broad) -> ee.ImageCollection:
  min_date_str = min_date.strftime("%Y-%m-%d")
  max_date_str = max_date.strftime("%Y-%m-%d")
  print(min_date_str); print(max_date_str);

  landsat_8 = ee.ImageCollection("LANDSAT/LC08/C02/T1_L2").filterBounds(study_area_broad).sort("CLOUD_COVER")
  landsat_8 = landsat_8.select(['SR_B3', 'SR_B6', 'QA_PIXEL'],['GREEN', 'INFRARED', 'QA_PIXEL'])

  landsat_9 = ee.ImageCollection("LANDSAT/LC09/C02/T1_L2").filterBounds(study_area_broad).sort("CLOUD_COVER")
  landsat_9 = landsat_9.select(['SR_B3', 'SR_B6', 'QA_PIXEL'],['GREEN', 'INFRARED', 'QA_PIXEL'])

  landsat_7 = ee.ImageCollection("LANDSAT/LE07/C02/T1_L2").filterBounds(study_area_broad).sort("CLOUD_COVER")
  landsat_7 = landsat_7.select(['SR_B2', 'SR_B5', 'QA_PIXEL'],['GREEN', 'INFRARED', 'QA_PIXEL'])

  landsat_5 = ee.ImageCollection("LANDSAT/LT05/C02/T1_L2").filterBounds(study_area_broad).sort("CLOUD_COVER")
  landsat_5 = landsat_5.select(['SR_B2', 'SR_B5', 'QA_PIXEL'],['GREEN', 'INFRARED', 'QA_PIXEL'])

  landsat_4 = ee.ImageCollection("LANDSAT/LT04/C02/T1_L2").filterBounds(study_area_broad).sort("CLOUD_COVER")
  landsat_4 = landsat_4.select(['SR_B2', 'SR_B5', 'QA_PIXEL'],['GREEN', 'INFRARED', 'QA_PIXEL'])

  all_landsat = landsat_8.merge(landsat_9).merge(landsat_5).merge(landsat_4).merge(landsat_7).filterDate(min_date_str,max_date_str)
  all_landsat = all_landsat.filter(ee.Filter.calendarRange(6,18,'hour'))
  return all_landsat


def export_image(img, name, folder, study_area, description='NDSI'):
  print("saving",name)
  projection = img.projection().getInfo()
  task = ee.batch.Export.image.toDrive(
    image=img,
    description=description,
    folder=folder,
    fileNamePrefix=name,
    region=study_area.geometry(),
    scale=30,
    crs='EPSG:32618'
    )
  task.start()

def gen_NDSIs(min_date:datetime.date, max_date:datetime.date, export = True) -> list:
  imgs_range = []
  str_range: str = str(min_date) + " -> " + str(max_date)
  print("Range (" + str_range + ")")
  images: ee.ImageCollection = get_landsat_images(min_date, max_date,study_area_broad)
  img_list = images.toList(len(images.getInfo()['features']))
  count = len(images.getInfo()['features'])
  print("found",count,"images")
  if count == 0: return imgs_range
  info = images.getInfo()
  for i in range(0,count):
    print("Found",images.getInfo()['features'][i]['id'])
    img = ee.Image(img_list.get(i)).clip(study_area_broad)
    img_ndsi, mask, original = calcNDSI(img)
    imgs_range.append(img_ndsi)
  imgs_range = ee.ImageCollection(imgs_range)
  local_mean = imgs_range.mean()
  if export: export_image(local_mean, "NDSI_"+str_range, "NDSI_IMGS", study_area_broad, description='NDSI_'+str_range.replace('->','to'))
  return imgs_range

print("Olá mundo")

study_area_broad = ee.FeatureCollection('projects/ee-juliocesarborgesdeoliv-neve/assets/SanRafael')
i = 0

export_year, export_station = False, True

for year in range(1980, 2025):
  print("\nYear {:04d}:\n".format(year))
  if export_year:
    print("TOTAL:")
    gen_NDSIs(datetime.date(year,1,1),datetime.date(year,12,31))
  if export_station:
    print("STATIONS:")
    print("SUMMER:")
    gen_NDSIs(datetime.date(year-1,12,21),datetime.date(year,3,20))
    print("FALL:")
    gen_NDSIs(datetime.date(year,3,21),datetime.date(year,6,20))
    print("WINTER:")
    gen_NDSIs(datetime.date(year,6,21),datetime.date(year,9,22))
    print("SPRING:")
    gen_NDSIs(datetime.date(year,9,23),datetime.date(year,12,20))

# display(imgs_year)
# display(img)
# display(mask)
# display(mean)

ee.Authenticate()
ee.Initialize(project='ee-juliocesarborgesdeoliv-neve')
print("Olá mundo")

study_area_broad = ee.FeatureCollection('projects/ee-juliocesarborgesdeoliv-neve/assets/SanRafael')
i = 0

for year in range(1980, 2025):
  print("Year " + str(year))
  imgs_year = []
  for month in range(1, 12, 4):
    imgs_range = []
    print("Range y:{:04d} m:({:02d} - {:02d}): ".format(year,month, month + 3))
    min_date = datetime.date(year, month, 1)
    max_date = datetime.date(year,month + 3,calendar.monthrange(year,month + 3)[1])

    images = get_landsat_images(min_date, max_date,study_area_broad).limit(120)
    img_list = images.toList(len(images.getInfo()['features']))
    count = len(images.getInfo()['features'])
    print("found",count,"images")
    if count == 0: continue
    info = images.getInfo()
    for i in range(0,count):
      print("found",images.getInfo()['features'][i]['id'])
      img = ee.Image(img_list.get(i)).clip(study_area_broad)
      img_ndsi, mask, original = calcNDSI(img)
      imgs_year.append(img_ndsi)
      imgs_range.append(img_ndsi)
    imgs_range = ee.ImageCollection(imgs_range)
    local_mean = imgs_range.mean()
    export_image(local_mean.gt(.5).clip(study_area_broad), "NDSI_{:04d}-{:02d}-{:02d}_NDSI_GEQ".format(year,month,month+3), "NDSI_COMP", study_area_broad, description='COMP-{:04d}_{:02d}-{:02d}_NDSI'.format(year,month,month+3))

  if len(imgs_year) == 0: continue
  imgs_year = ee.ImageCollection(imgs_year)
  mean = imgs_year.mean().clip(study_area_broad)
  export_image(mean.gt(.5).clip(study_area_broad), "{:04d}_NDSI_GEQ".format(year), "NDSI_COMP", study_area_broad, description='COMP-{:04d}_NDSI'.format(year))

map = geemap.Map(center=[-46.5330, -75.0132], zoom=8)

map.add_layer(study_area_broad, {}, 'area')

map.add_layer(img.clip(study_area_broad), {min: -1, max: 1}, 'img'+str(i))

map.add_layer(mean, {min: -1, max: 1}, 'NDSI')
map.add_layer(mean.gt(.5), {}, 'SNOW')
# map.add_layer(original, {}, 'og')
# map.add_layer(mask, {min: 0, max: 1}, 'mask')

display(map)