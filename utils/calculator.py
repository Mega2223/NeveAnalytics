import subprocess

from osgeo_utils import gdal_calc

from utils import file_manager


def calcNDSI(thermal, green, out_file: str):
    gdal_calc.Calc("(A.astype(numpy.float64)-B)/numpy.maximum(1,A.astype(numpy.float64)+B)",
                   A=file_manager.imgPath(thermal),
                   B=file_manager.imgPath(green),
                   outfile=out_file,
                   NoDataValue=-10.0,
                   overwrite=True,
                   quiet=True,
                   type="Float64"
                   )


def applyCloudMask(ndsi, quality, out_file):
    gdal_calc.Calc("A*(1-((B==22280)+(B==24088)+(B==24216)+(B==24344)+(B==24472)+(B==55052)))",
                   A=file_manager.imgPath(ndsi),
                   B=file_manager.imgPath(quality),
                   outfile=out_file,
                   NoDataValue=0.0,
                   overwrite=True,
                   quiet=True
                   )


def cropToShapefile(img_from, img_to, shape):
    subprocess.call(['gdalwarp', img_from, img_to, '-cutline', shape, '-crop_to_cutline'], stderr=None)


def genMosaic(imgs: list[str], img_to: str):
    args = imgs.copy()
    args = ("gdal_merge -o" + img_to + "-n 0").split(" ") + args
    subprocess.call(args, stderr=None)
