package net.mega2223.neveanalytics.objects;

import net.mega2223.neveanalytics.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LandsatPicture<DataType extends Number> {

    ArrayList<LandsatBand<DataType>> bands = new ArrayList<>();
    public static String name = null;
    LandsatPicture(List<LandsatBand<DataType>> images){
        bands.addAll(images);
        name = images.get(0).getNameNoBand();
    }
    LandsatPicture(LandsatBand<DataType> image){
        this(List.of(image));
    }

    public LandsatBand<DataType> getBand(int band){
        for (LandsatBand<DataType> act : bands){
            if(act.band == band){return act;}
        }
        Utils.log("Warning, could not find band " + band + " for image " + name, Utils.DEBUG_IMPORTANT);
        return null;
    }

    public boolean isFromImage(LandsatBand<?> band){
        return band.isSameImage(bands.get(0));
    }

    public static List<LandsatPicture<? extends Number>> scanFolder(String folder) throws IOException {
        Utils.log("Loading folder " + folder,Utils.DEBUG_DETAIL);
        ArrayList<LandsatBand<? extends Number>> imgs = new ArrayList<>();
        ArrayList<LandsatPicture<? extends Number>> pics = new ArrayList<>();

        File[] repo = new File(folder).listFiles();

        for(File act : repo){
            String[] name = act.getName().split("\\.");
            if(act.isDirectory() || name.length < 2 ||
                    !(name[name.length-1].equals("TIF") || name[name.length-1].equals("TIFF"))){
                continue;
            }
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
        int sizeX = bands.get(0).sizeX;
        if(sizeX < 0){
            try {
                bands.get(0).bufferImage();
                sizeX = bands.get(0).sizeX;
            } catch (IOException ignored) {}
        }
        return sizeX;
    }
    public int getY(){
        int sizeY = bands.get(0).sizeY;
        if(sizeY < 0){
            try {
                bands.get(0).bufferImage();
                sizeY = bands.get(0).sizeY;
            } catch (IOException ignored) {}
        }
        return sizeY;
    }

    public List<LandsatBand<DataType>> getBands() {
        return List.copyOf(bands);
    }

    public String getName(){
        return bands.get(0).nameNoBand;
    }

    LandsatBand<?> mask = null;

    public void setMask(LandsatBand<?> mask) {
        this.mask = mask;
        for(LandsatBand<?> act : bands){
            act.setMask(mask);
        }
    }
}
