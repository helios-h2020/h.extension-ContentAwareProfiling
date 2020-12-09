package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategoriesHierarchy;

/**
 * The implementation of the model based on the fine interest categories.
 */
public class FineInterestsModel extends AbstractModel {
    ModelType modelType;
    Interpreter coarseInterpreter;
    Interpreter fashionInterpreter;
    Interpreter entertainmentInterpreter;
    Interpreter sportsInterpreter;
    Interpreter vehiclesInterpreter;
    HashMap<InterestCategoriesHierarchy.CompoundCategories, Interpreter> compoundCat2Interpreter;

    /**
     * @param assetManager The Android asset manager
     * @param ctx The Android context
     * @throws IOException
     */
    public FineInterestsModel(AssetManager assetManager, Context ctx) throws IOException {
        super(ctx);
        modelType = ModelType.FINE;
        coarseInterpreter = ModelUtils.getInterpreter(assetManager,
                modelType.modelPaths.get("coarse"));
        entertainmentInterpreter = ModelUtils.getInterpreter(assetManager,
                modelType.modelPaths.get("entertainment"));
        fashionInterpreter = ModelUtils.getInterpreter(assetManager,
                modelType.modelPaths.get("fashion"));
        sportsInterpreter = ModelUtils.getInterpreter(assetManager,
                modelType.modelPaths.get("sports"));
        vehiclesInterpreter = ModelUtils.getInterpreter(assetManager,
                modelType.modelPaths.get("vehicles"));
        compoundCat2Interpreter = new HashMap<InterestCategoriesHierarchy.CompoundCategories,
                Interpreter>() {
            {
                put(InterestCategoriesHierarchy.CompoundCategories.entertainment, entertainmentInterpreter);
                put(InterestCategoriesHierarchy.CompoundCategories.fashion, fashionInterpreter);
                put(InterestCategoriesHierarchy.CompoundCategories.sports, sportsInterpreter);
                put(InterestCategoriesHierarchy.CompoundCategories.vehicles, vehiclesInterpreter);
            }
        };
    }

    @Override
    public ArrayList<ArrayList<Float>> forwardCNN(ArrayList<Image> images) {
        InterestCategoriesHierarchy.CompoundCategories[] compCat =
                InterestCategoriesHierarchy.CompoundCategories.values();

        ArrayList<Integer> compoundCatIdx = new ArrayList<>();
        ArrayList<Integer> numOfSubcat = new ArrayList();
        for (int i = 0; i < compCat.length; i++) {
            compoundCatIdx.add(compCat[i].getIndex());
            numOfSubcat.add(compCat[i].getSubcategories().size());
        }

        ArrayList<ArrayList<Float>> fineProfiles = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            ByteBuffer byteBuffer = ModelUtils.preprocessBitmap(images.get(i).getBitmap(ctx),
                    modelType.inputSize, modelType.pixelSize,
                    modelType.imageMean, modelType.imageSTD);

            ArrayList<Float> output = ModelUtils.interpreterForward(byteBuffer, coarseInterpreter, ModelType.COARSE.outputDimension);

            ArrayList<ArrayList<Float>> compoundCatProfiles = ModelUtils.getDefaultCompoundCatProfiles(numOfSubcat);
            for (int j = 0; j < compCat.length; j++) {
                if (ModelUtils.getMaxIdx(output) == compCat[j].getIndex()) {
                    ArrayList<Float> fineClassifierOutput = ModelUtils.interpreterForward(byteBuffer, compoundCat2Interpreter.get(compCat[j]), numOfSubcat.get(j));
                    compoundCatProfiles.set(j, fineClassifierOutput);
                }
            }
            fineProfiles.add(
                    ModelUtils.calculateFineProfile(output,
                            compoundCatIdx, numOfSubcat, compoundCatProfiles));
        }
        return fineProfiles;
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput) {
        return ModelUtils.columnWiseAdd(cnnOutput);
    }

    @Override
    public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput,
                                               ArrayList<Float> attentionWeights) {
        return ModelUtils.columnWiseAdd(cnnOutput, attentionWeights);
    }
}
