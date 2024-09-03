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

    public static LandsatBand<? extends Number> runOperation(JsonElement equation, LandsatPicture<?> picture) throws IOException {
        JsonObject eq = equation.getAsJsonObject();
        JsonElement opr = eq.get("operation");

        LandsatBand<? extends Number> band1 = decodeBand(eq.get("b1"),picture);
        LandsatBand<? extends Number> band2 = decodeBand(eq.get("b2"),picture);

        String operation = opr.getAsString();

        for(Operation act : OPERATIONS){
            if(operation.equals(act.getName())){
                return runOperation(act,band1,band2);
            }
        }
        throw new RuntimeException("Could not resolve operation \"" + operation + "\"");
    }

    public static LandsatBand<? extends Number> runOperation(Operation operation, LandsatBand<?> b1, LandsatBand<?> b2) throws IOException {
        Utils.log("Running operation \"" + operation.getName() + "\" for " + b1.name + " and " + b2.name,Utils.DEBUG_DETAIL);
        b1.bufferImage(); b2.bufferImage();
        Number[][] data = new Number[b1.sizeX][b2.sizeY];
        for (int x = 0; x < b1.sizeX; x++) {
            for (int y = 0; y < b1.sizeY; y++) {
                data[x][y] = operation.doOperation(b1.get(x,y),b2.get(x,y));
            }
        }
        imgCounter++;
        b1.discardBuffer(); b2.discardBuffer();
        try{Thread.sleep(50);} catch (InterruptedException ignored){}
        return LandsatBand.genImage(
                NeveAnalyitcs.CONFIG.get("temp_dir").getAsString(),
                b1.getNameNoBand() + "_" + operation.getName() + "_TEMP_" + imgCounter,
                data
        );
    }

    static LandsatBand<?> decodeBand(JsonElement band, LandsatPicture<?> picture) throws IOException {
        if(band.isJsonObject()){
            return runOperation(band,picture);} else
            {return picture.getBand(band.getAsInt());}
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
                if(a instanceof Float || a instanceof Double){
                    double dA = (double) a, dB = (double) b;
                    return dA + dB;
                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
                    long dA = a.longValue(),dB = b.longValue();
                    return dA + dB;
                } else {
                    return Integer.MIN_VALUE;
                }
            }

            @Override
            String getName() {
                return name;
            }
        });
        OPERATIONS.add(new Operation() {
            static final String name = "minus";
            @Override
            Number doOperation(Number a, Number b) {
                if(a instanceof Float || a instanceof Double){
                    double dA = (double) a, dB = (double) b;
                    return dA - dB;
                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
                    long dA = (long) a, dB = (long) b;
                    return dA - dB;
                } else {
                    return Integer.MIN_VALUE;
                }
            }

            @Override
            String getName() {
                return name;
            }
        });
        OPERATIONS.add(new Operation() {
            static final String name = "multiply";
            @Override
            Number doOperation(Number a, Number b) {
                if(a instanceof Float || a instanceof Double || b instanceof Float || b instanceof Double){
                    double dA = a.doubleValue(), dB = b.doubleValue();
                    return dA * dB;
                } else if (a instanceof Long || a instanceof Integer || a instanceof Short) {
                    long dA = a.longValue(), dB = b.longValue();
                    return dA * dB;
                } else {
                    return Integer.MIN_VALUE;
                }
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
        OPERATIONS.add(new Operation() {
            @Override
            Number doOperation(Number a, Number b) {
                return null; //never runs
            }

            @Override
            String getName() {
                return "operation";
            }
        });
    }
}
