from osgeo import gdal, gdalconst

tiff_src = gdal.Open("C:\\Users\\Imperiums\\Desktop\\Neve\\Unprocessed\\19840312\\001092\\LT05\\LT05_L1GS_001092_19840312_20200918_02_T2_B2.TIF", gdalconst.GA_ReadOnly)
tiff_dest = gdal.Open("C:\\Users\\Imperiums\\Desktop\\Neve\\NDSI\\LT05_L1GS_001092_19840312_20200918_02_T2.TIF",gdalconst.GA_Update)

tiff_dest.SetProjection(tiff_src.GetProjection())
tiff_dest.SetMetadata(tiff_src.GetMetadata())
tiff_dest.SetGeoTransform(tiff_src.GetGeoTransform())

del(tiff_src)
del(tiff_dest)