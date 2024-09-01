package net.mega2223.neveanalytics.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LandsatPicture<DataType extends Number> {
    ArrayList<LandsatBand<DataType>> bands = new ArrayList<>();

    LandsatPicture(List<LandsatBand<DataType>> images){
        bands.addAll(images);
    }
    LandsatPicture(LandsatBand<DataType> image){
        bands.add(image);
    }

    public LandsatBand<DataType> getBand(int band){
        for (LandsatBand<DataType> act : bands){
            if(act.band == band){return act;}
        }
        return null;
    }

    public boolean isFromImage(LandsatBand<?> band){
        return band.isSameImage(bands.get(0));
    }

    public static List<LandsatPicture<? extends Number>> scanFolder(String folder) throws IOException {
        ArrayList<LandsatBand<? extends Number>> imgs = new ArrayList<>();
        ArrayList<LandsatPicture<? extends Number>> pics = new ArrayList<>();

        File[] repo = new File(folder).listFiles();

        for(File act : repo){
            LandsatBand<? extends Number> bandAct = LandsatBand.LoadImage(act.getAbsolutePath());
            boolean alreadyHasPicture = false;
            for(LandsatPicture p : pics){
                if(p.isFromImage(bandAct)){
                    p.bands.add(bandAct);
                    alreadyHasPicture = true;
                    break;
                }
            }
            if(!alreadyHasPicture){
                LandsatPicture<? extends Number> pic = new LandsatPicture<>(bandAct);
                pics.add(pic);
            }
        }

        return pics;
    }

    public int getX(){
        return bands.get(0).sizeX;
    }
    public int getY(){
        return bands.get(0).sizeY;
    }
}
