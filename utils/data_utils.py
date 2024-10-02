from osgeo_utils import gdal_calc


def convert_to_float(img: str, filename: str, dir: str):
    gdal_calc.Calc(
        "A",
        A=img,
        type="Float64",
        outfile=dir + "\\" + filename,
        quiet=True,
        overwrite=True

    )
