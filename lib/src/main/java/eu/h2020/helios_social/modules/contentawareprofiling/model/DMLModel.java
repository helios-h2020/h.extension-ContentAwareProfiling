package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;

/**
 * The implementation of the model based on Deep Metric Learning (DML).
 */
public class DMLModel extends AbstractModel {
    private ModelType modelType;
    Interpreter baseInterpreter;
    Interpreter topInterpreter;

    /**
     * @param assetManager The Android asset manager
     * @param ctx The Android context
     * @throws IOException
     */
    public DMLModel(AssetManager assetManager, Context ctx) throws IOException {
        super(ctx);
        modelType = ModelType.DML;
        baseInterpreter = ModelUtils.getInterpreter(assetManager, modelType.modelPaths.get("base"));
        topInterpreter = ModelUtils.getInterpreter(assetManager, modelType.modelPaths.get("top"));
    }

    @Override
    public ArrayList<ArrayList<Float>> forwardCNN(ArrayList<Image> images) {
        ArrayList<ArrayList<Float>> output = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            ByteBuffer byteBuffer = ModelUtils.preprocessBitmap(images.get(i).getBitmap(ctx),
                    modelType.inputSize, modelType.pixelSize, modelType.imageMean, modelType.imageSTD
            );
            output.add(ModelUtils.interpreterForward(byteBuffer, baseInterpreter, modelType.CNNOutputDimension));
        }
        return output;
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput) {
        ArrayList<Float> attentionWeights = new ArrayList<>(Collections.nCopies(cnnOutput.size(), 1.0f));
        return forwardCNN2Profile(cnnOutput, attentionWeights);
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput, ArrayList<Float> attentionWeights) {
        ArrayList<ArrayList<Float>> topInput = new ArrayList<>();
        topInput.add(ModelUtils.columnWiseAdd(cnnOutput, attentionWeights));
        ArrayList<ArrayList<Float>> topOutput = ModelUtils.interpreterForward(topInterpreter, topInput, modelType.outputDimension);
        return topOutput.get(0);
    }
}
