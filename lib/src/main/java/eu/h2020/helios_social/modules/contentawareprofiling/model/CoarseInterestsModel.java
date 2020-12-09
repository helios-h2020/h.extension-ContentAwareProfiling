package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;

/**
 * The implementation of the model based on the coarse interest categories.
 */
public class CoarseInterestsModel extends AbstractModel {
    private ModelType modelType;
    private Interpreter interpreter;

    /**
     * @param assetManager The Android asset manager
     * @param ctx The Android context
     * @throws IOException
     */
    public CoarseInterestsModel(AssetManager assetManager, Context ctx) throws IOException {
        super(ctx);
        modelType = ModelType.COARSE;
        interpreter = ModelUtils.getInterpreter(assetManager, modelType.modelPaths.get("coarse"));
    }

    @Override
    public ArrayList<ArrayList<Float>> forwardCNN(ArrayList<Image> images) {
        ArrayList<ArrayList<Float>> output = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            ByteBuffer byteBuffer = ModelUtils.preprocessBitmap(images.get(i).getBitmap(ctx),
                    modelType.inputSize, modelType.pixelSize, modelType.imageMean, modelType.imageSTD
            );
            output.add(ModelUtils.interpreterForward(byteBuffer, interpreter, modelType.outputDimension));
        }
        return output;
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput) {
        return ModelUtils.columnWiseAdd(cnnOutput);
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput, ArrayList<Float> attentionWeights) {
        return ModelUtils.columnWiseAdd(cnnOutput, attentionWeights);
    }
}
