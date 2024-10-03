import os
from typing import Tuple

from osgeo import gdal


def getBand(band_name: str) -> int | str | None:
    ret = band_name.split(".")[0]
    if ret.__contains__("QA_PIXEL"): return "QA"
    if ret.__contains__("_NDSI"): return "NDSI"
    ret = ret[len(ret) - 2:len(ret)]
    if ret[1].isnumeric() and ret[0] == "B": return int(ret[1])
    return None


def getNameNoBand(band_name: str) -> str:
    return band_name[0:40]


def isTiffImage(file: str):
    return file[len(file) - 4:len(file)].upper() == ".TIF" or file[len(file) - 5:len(file)].upper() == ".TIFF"


def doRecursiveSearch(folder: str, filter_function=None) -> list[tuple[str, str]]:
    folders = os.walk(folder)
    ret = []
    for folder in folders:
        files = os.listdir(folder[0])
        for file in files:
            if filter_function == None or filter_function(file):
                ret.append((file, folder[0]))
    return ret


def findBandForImage(img: tuple[str, str], band_index: int | str) -> tuple[str, str] | None:
    files = doRecursiveSearch(img[1], isTiffImage)
    name = getNameNoBand(img[0])
    for f in files:
        if name == getNameNoBand(f[0]) and band_index == getBand(f[0]): return f
    return None


def loadBand(img: tuple[str, str]) -> gdal.Dataset | None:
    return gdal.Open(imgPath(img))


def imgPath(img: tuple[str,str]):
    return img[1] + "\\" + img[0]
