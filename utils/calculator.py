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
                   NoDataValue=-2.0,
                   overwrite=True,
                   quiet=True
                   )


def cropToShapefile(img_from, img_to, shape):
    print("Cropping " + img_from + " -> (" + shape + ") -> " + img_to)
    subprocess.call(['gdalwarp', img_from, img_to, '-cutline', shape, '-crop_to_cutline', '-q'], stderr=None)


def genMosaic(imgs: list[tuple[str, str]], img_to: str):
    args = []
    for i in imgs: args.append(i[1] + "\\" + i[0])
    # gdal_merge.main(['','', '-o', img_to] + args)
    subprocess.call(['gdalwarp','-r','average'] + args + [img_to], stderr=None)


def mosaicAndShape(imgs: list[tuple[str, str]], img_to: str, shapefile: str):
    args = []
    print("generating mosaic " + img_to)
    for i in imgs: args.append(i[1] + "\\" + i[0])
    subprocess.call(['gdalwarp','-cutline',shapefile,'-r','average','-q'] + args + [img_to], stderr=None)
    print(args)
