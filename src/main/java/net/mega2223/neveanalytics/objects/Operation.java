package net.mega2223.neveanalytics.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mega2223.neveanalytics.NeveAnalyitcs;
import net.mega2223.neveanalytics.Utils;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Operation {

    abstract Number doOperation(Number a, Number b);
    abstract String getName();

    static int imgCounter = 0;
    static final ArrayList<Operation> OPERATIONS = new ArrayList<>();

    static LandsatBand<?> evaluate(JsonElement band, LandsatPicture<?> picture, boolean unbuffer) throws IOException {
        //Returns band that coincides with element if the element is a band, otherwise returns the operation result
        return band.isJsonPrimitive() ? picture.getBand(band.getAsJsonPrimitive().getAsInt()) :
                runOperation(picture,band.getAsJsonObject(),null,unbuffer);
    }
    public static LandsatBand<? extends Number> runOperation(LandsatPicture<?> reference, JsonObject equation, String name, boolean unbuffer) throws IOException {
        return runOperation(reference,equation,name,NeveAnalyitcs.CONFIG.get("temp_dir").getAsString(), unbuffer);
    }
    public static LandsatBand<? extends Number> runOperation(LandsatPicture<?> reference, JsonObject equation, String name, String dir, boolean unbuffer) throws IOException {
        JsonObject eq = equation.getAsJsonObject();
        JsonElement opr = eq.get("operation");
        String operationJ = opr.getAsString();

        LandsatBand<?> bandA = evaluate(eq.get("b1"),reference,unbuffer),
                bandB = evaluate(eq.get("b2"),reference,unbuffer);

        bandA.bufferImage(); bandB.bufferImage();
        Operation operation = getFromName(operationJ);

        if(name == null){name = bandA.getNameNoBand() + "_" + operation.getName() + "_" + imgCounter;}
        final int xMax = reference.getX(), yMax = reference.getY();
        LandsatBand<?> result = LandsatBand.genImage(
                dir,
                name,
                xMax,yMax, (float) 0);
        Utils.copyGEOTIFFProperties(bandA.file,result.file);
        result.bufferImage();
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                Number res = operation.doOperation(bandA.get(x, y), bandB.get(x, y));
                res = Float.isNaN(res.floatValue()) || Float.isInfinite(res.floatValue()) ? 0 : res;
                result.raster.setFirstPixelSample(x,y,
                        res
                );
                if(res.doubleValue() != 0){
                    dos();
                }
            }
        }
        if(unbuffer){bandA.discardBuffer(); bandB.discardBuffer();}
        imgCounter++;
        result.save();
        return result;
    }
    static void dos(){}

    public static Operation getFromName(String name){
        for(Operation act : OPERATIONS){
            if(name.equals(act.getName())){return act;}
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Operation o && o.getName().equals(this.getName());
    }

    static {
        OPERATIONS.add(new Operation() {
            static final String name = "sum";
            @Override
            Number doOperation(Number a, Number b) {
//                if(a instanceof Float || a instanceof Double){
//                    double dA = (double) a, dB = (double) b;
//                    return dA + dB;
//                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
//                    long dA = a.longValue(),dB = b.longValue();
//                    return dA + dB;
//                } else {
//                    return Integer.MIN_VALUE;
//                } TODO olha isso dps
                return a.doubleValue() + b.doubleValue();
            }

            @Override
            String getName() {
                return name;
            }
        });
        OPERATIONS.add(new Operation() {
            static final String name = "subtraction";
            @Override
            Number doOperation(Number a, Number b) {
//                if(a instanceof Float || a instanceof Double){
//                    double dA = (double) a, dB = (double) b;
//                    return dA - dB;
//                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
//                    long dA = (long) a, dB = (long) b;
//                    return dA - dB;
//                } else {
//                    return Integer.MIN_VALUE;
//                } TODO
                return a.doubleValue() - b.doubleValue();
            }

            @Override
            String getName() {
                return name;
            }
        });
        OPERATIONS.add(new Operation() {
            static final String name = "multiplication";
            @Override
            Number doOperation(Number a, Number b) {
//                if(a instanceof Float || a instanceof Double || b instanceof Float || b instanceof Double){
//                    double dA = a.doubleValue(), dB = b.doubleValue();
//                    return dA * dB;
//                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
//                    long dA = a.longValue(), dB = b.longValue();
//                    return dA * dB;
//                } else {
//                    return Integer.MIN_VALUE;
//                } TODO
                return a.doubleValue() * b.doubleValue();
            }

            @Override
            String getName() {
                return name;
            }
        });
        OPERATIONS.add(new Operation() {
            static final String name = "division";
            @Override
            Number doOperation(Number a, Number b) {
                double dA = a.doubleValue(), dB = b.doubleValue();
                return dA / dB;
            }
            @Override
            String getName() {
                return name;
            }
        });
    }
}
