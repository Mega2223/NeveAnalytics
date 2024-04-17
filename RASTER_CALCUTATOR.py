
def perform_raster_eq(upper_input, lower_input, output_raster):

    # NDSI calculations :D:D:D::D:D:D:D::D:D:DD:D:::):):):)::):)):):)):):):): \o/
    print(lower_input)
    parameters = {
        'INPUT_A' : lower_input,
        'BAND_A' : 1,
        'INPUT_B' : upper_input,
        #'BAND_B' : 1,
        #'FORMULA' : 'A',
        'OUTPUT' : output_raster
        }
    return processing.runAndLoadResults('gdal:rastercalculator', parameters)
    layer = upper_input

    

post_node = root.addGroup("POS")

for subd in subpath_nodes:
    subd = subpath_nodes[subd]
    landsat_45 = 'B2' in subd['subgroups']
    lower = upper = None
    
    if landsat_45:
        lower = subd['subgroups']['B2']
        upper = subd['subgroups']['B5']
    else:
        lower = subd['subgroups']['B3']
        upper = subd['subgroups']['B6']
        
    for i in lower.findLayers():
        info = i.name().split("_")
        equivalent = None
        for j in upper.findLayers():
            jnfo = j.name().split("_")
            if info[3] == jnfo[3] and info[2] == jnfo[2]:
                equivalent = j
                break
        
        if equivalent == None:
            print("ué")
            print(i.name())
            continue
        
        i = subd['subgroups'][i.name()+'_RASTER']
        equivalent = subd['subgroups'][equivalent.name()+'_RASTER']
        print("LAYERS:: "+i.name() + ":::" + equivalent.name())
        n_Raster = perform_raster_eq(equivalent,i,src_path+"\SNOW_"+i.name())
        print(n_Raster)
        break
        #move_layer(n_Raster,post_node)
        
    