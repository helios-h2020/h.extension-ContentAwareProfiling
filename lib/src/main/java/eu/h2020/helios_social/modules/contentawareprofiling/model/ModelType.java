package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.HashMap;

/**
 * Define the three different model types that are implemented; the ones based on the coarse and
 * the fine interest categories, as well as the one base on DML. For each type a set of default
 * values for its configuration parameters is chosen. It is advised not to modify these values.
 */
public enum ModelType {
    DML("DML") {
        @Override
        public AbstractModel getModel(AssetManager assetManager, Context ctx) throws IOException {
            return new DMLModel(assetManager, ctx);
        }
    },
    COARSE("COARSE") {
        @Override
        public AbstractModel getModel(AssetManager assetManager, Context ctx) throws IOException {
            return new CoarseInterestsModel(assetManager, ctx);
        }
    },
    FINE("FINE") {
        @Override
        public AbstractModel getModel(AssetManager assetManager, Context ctx) throws IOException {
            return new FineInterestsModel(assetManager, ctx);
        }
    };

    public HashMap<String, String> modelPaths = new HashMap<>();
    public int CNNOutputDimension;
    public int outputDimension;
    public int inputSize;
    public int pixelSize;
    public float imageMean;
    public float imageSTD;
    public float threshold;


    ModelType(String name) {
        inputSize = 224;
        pixelSize = 3;
        imageMean = (float) 127.5;
        imageSTD = (float) 127.5;
        threshold = (float) 0.9;

        switch (name) {
            case "DML":
                modelPaths.put("base", "DMLBaseModel.tflite");
                modelPaths.put("top", "DMLTopModel.tflite");
                CNNOutputDimension = 1280;
                outputDimension = 256;
                break;
            case "COARSE":
                modelPaths.put("coarse", "coarseModel.tflite");
                outputDimension = 15;
                break;
            case "FINE":
                modelPaths.put("coarse", "coarseModel.tflite");
                modelPaths.put("fashion", "fashionModel.tflite");
                modelPaths.put("entertainment", "entertainmentModel.tflite");
                modelPaths.put("sports", "sportsModel.tflite");
                modelPaths.put("vehicles", "vehiclesModel.tflite");
                outputDimension = 42;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + name);
        }
    }

    public abstract AbstractModel getModel(AssetManager assetManager, Context ctx) throws IOException;

}
