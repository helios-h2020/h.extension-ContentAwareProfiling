package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategoriesHierarchy;
import eu.h2020.helios_social.modules.contentawareprofiling.utils.ProfileUtils;

/**
 * A set of utilities to facilitate the implementation of the content aware profiling models.
 */
public class ModelUtils {

    /**
     * @param inputArray A 2d input array.
     * @return The column wise mean.
     */
    public static ArrayList<Float> columnWiseAdd(ArrayList<ArrayList<Float>> inputArray) {
        int numOfRows = inputArray.size();
        ArrayList<Float> weights = new ArrayList(Collections.nCopies(numOfRows, 1.0f));
        return columnWiseAdd(inputArray, weights);
    }

    /**
     * @param inputArray A 2d input array.
     * @param weights The weight assigned to each row.
     * @return The column wise weighted sum.
     */
    public static ArrayList<Float> columnWiseAdd(ArrayList<ArrayList<Float>> inputArray, ArrayList<Float> weights) {
        int numOfColumns = inputArray.get(0).size();
        int numfOfRows = inputArray.size();
        ArrayList<Float> result = new ArrayList(numOfColumns);
        for (int i = 0; i < numOfColumns; i++) {
            result.add(0.0f);
            float count = 0.0f;
            for (int j = 0; j < numfOfRows; j++) {
                result.set(i, result.get(i) + weights.get(j) * inputArray.get(j).get(i));
                count += weights.get(j);
            }
            result.set(i, result.get(i) / count);
        }
        return result;
    }

    /**
     * If a row has at least one value above the threshold
     *
     * @param inputArray A 2d input array.
     * @param threshold The threshold value.
     * @return A binary mask to filter the rows of the array in the case of the coarse model.
     */
    public static ArrayList<Float> getThresholdWeights(ArrayList<ArrayList<Float>> inputArray, float threshold) {
        ArrayList<Float> thresholdFilterWeights = new ArrayList(Collections.nCopies(inputArray.size(), 0f));
        for (int i = 0; i < inputArray.size(); i++) {
            float maxValue = Collections.max(inputArray.get(i));
            if (maxValue >= threshold) {
                thresholdFilterWeights.set(i, 1.0f);
            }
        }
        return thresholdFilterWeights;
    }

    /**
     * @param inputArray A 2d input array.
     * @param threshold The threshold value.
     * @return A binary mask to filter the rows of the array in the case of the fine model.
     */
    public static ArrayList<Float> getFineThresholdWeights(ArrayList<ArrayList<Float>> inputArray, float threshold) {
        ArrayList<ArrayList<Float>> coarsePerImageOutput = new ArrayList<>();
        InterestCategoriesHierarchy.CompoundCategories[] compoundCat =
                InterestCategoriesHierarchy.CompoundCategories.values();
        int[] startEndIdx = new int[2 * compoundCat.length];
        int j = 0;
        int numOfSubcatAdded = 0;
        for (int i = 0; i < compoundCat.length; i++) {
            int catIdx = compoundCat[i].getIndex();
            startEndIdx[j] = numOfSubcatAdded + catIdx;
            j++;
            startEndIdx[j] = numOfSubcatAdded + catIdx + compoundCat[i].getSubcategories().size() - 1;
            j++;
            numOfSubcatAdded += compoundCat[i].getSubcategories().size() - 1;
        }
        for (int i = 0; i < inputArray.size(); i++) {
            coarsePerImageOutput.add(ProfileUtils.getCoarseProfileFromFine(inputArray.get(i), startEndIdx));
        }
        return ModelUtils.getThresholdWeights(coarsePerImageOutput, threshold);
    }

    /**
     * @param context The reference spatio-temporal context.
     * @param images An list of images.
     * @param thresholdWeights The calculated threshold weights.
     * @return The attention vector over the images.
     */
    public static ArrayList<Float> getAttentionWeights(SpatioTemporalContext context,
                                                       ArrayList<Image> images,
                                                       ArrayList<Float> thresholdWeights) {
        ArrayList<Float> attentionWeights = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            attentionWeights.set(i,
                    thresholdWeights.get(i) * images.get(i).getContext().weightAgainstReferenceContext(context));
        }
        return attentionWeights;
    }

    /**
     * @param inputArray An 1d input array.
     * @return The index where the maximum occurs.
     */
    public static Integer getMaxIdx(ArrayList<Float> inputArray) {
        return inputArray.indexOf(Collections.max(inputArray));
    }

    /**
     * @param coarseProfile The coarse profile
     * @param compoundCatIdx The index of the compound categories
     * @param numOfSubcat The number of subcategories
     * @param compoundCatProfiles The profiles calculated with the local classifiers for the
     *                            compound categories
     * @return
     */
    public static ArrayList<Float> calculateFineProfile(ArrayList<Float> coarseProfile, ArrayList<Integer> compoundCatIdx, ArrayList<Integer> numOfSubcat, ArrayList<ArrayList<Float>> compoundCatProfiles) {
        int totalNumOfSubCat = 0;
        for (int i = 0; i < numOfSubcat.size(); i++) {
            totalNumOfSubCat += numOfSubcat.get(i);
        }
        ArrayList<Float> fineProfile = new ArrayList<>(Collections.nCopies(coarseProfile.size() + totalNumOfSubCat - numOfSubcat.size(), 0f));
        int compoundCatCounter = 0;
        int fineIdx = 0;
        for (int coarseIdx = 0; coarseIdx < coarseProfile.size(); coarseIdx++) {
            if (coarseIdx == compoundCatIdx.get(compoundCatCounter)) {
                for (int i = 0; i < numOfSubcat.get(compoundCatCounter); i++) {
                    fineProfile.set(
                            fineIdx,
                            coarseProfile.get(coarseIdx) * compoundCatProfiles.get(compoundCatCounter).get(i)
                    );
                    fineIdx++;
                }
                compoundCatCounter++;
            } else {
                fineProfile.set(fineIdx, coarseProfile.get(coarseIdx));
                fineIdx++;
            }
        }
        return fineProfile;
    }

    /**
     * @param coarseProfile The coarse profile
     * @param compoundCatIdx The index of the compound categories
     * @param numOfSubcat The number of subcategories
     * @return
     */
    public static ArrayList<Float> calculateFineProfile(ArrayList<Float> coarseProfile, ArrayList<Integer> compoundCatIdx, ArrayList<Integer> numOfSubcat) {
        ArrayList<ArrayList<Float>> compoundCatProfiles = getDefaultCompoundCatProfiles(numOfSubcat);
        return calculateFineProfile(coarseProfile, compoundCatIdx, numOfSubcat, compoundCatProfiles);
    }

    /**
     * @param numOfSubcat The number of subcategories
     * @return
     */
    public static ArrayList<ArrayList<Float>> getDefaultCompoundCatProfiles(ArrayList<Integer> numOfSubcat) {
        ArrayList<ArrayList<Float>> compoundCatProfiles = new ArrayList();
        for (int i = 0; i < numOfSubcat.size(); i++) {
            ArrayList<Float> uniformProfile = new ArrayList(Collections.nCopies(numOfSubcat.get(i), 1.0f / numOfSubcat.get(i)));
            compoundCatProfiles.add(uniformProfile);
        }
        return compoundCatProfiles;
    }

    /**
     * @param assetManager The Android asset manager
     * @param modelPath The path of the model
     * @return The model interpreter
     * @throws IOException
     */
    public static Interpreter getInterpreter(AssetManager assetManager, String modelPath) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(5);
        options.setUseNNAPI(true);
        return new Interpreter(loadModelFile(assetManager, modelPath));
    }

    /**
     * @param assetManager The Android asset manager
     * @param modelPath The path of the model
     * @return The mapped byte buffer for the model file
     * @throws IOException
     */
    private static MappedByteBuffer loadModelFile(AssetManager assetManager,
                                                  String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream =
                new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset,
                declaredLength);
    }

    public static ByteBuffer allocate1DTensor(ArrayList<Float> input) {
        int inputSize = input.size();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < inputSize; i++) {
            byteBuffer.putFloat(input.get(i));
        }
        return byteBuffer;
    }

    /**
     * @param bitmap The input image as a bitmap
     * @param inputSize The image input size
     * @param pixelSize The number of channels
     * @param imageMean The pre-calculated image mean to be used for normalization.
     * @param imageSTD The pre-calculated image STD to be used for normalization
     * @return The preprocessed bitmap as a ByteBuffer
     */
    public static ByteBuffer preprocessBitmap(Bitmap bitmap, int inputSize, int pixelSize, float imageMean, float imageSTD) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);
        ByteBuffer byteBuffer =
                ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];

        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                int input = intValues[pixel++];

                byteBuffer.putFloat((((input >> 16) & 0xFF) - imageMean) / imageSTD);
                byteBuffer.putFloat((((input >> 8) & 0xFF) - imageMean) / imageSTD);
                byteBuffer.putFloat((((input & 0xFF) - imageMean) / imageSTD));
            }
        }
        return byteBuffer;
    }

    /**
     * @param bitmap The input bitmap
     * @return The preprocessed bitmap as a ByteBuffer
     */
    public static ByteBuffer preprocessBitmap(Bitmap bitmap) {
        int inputSize = 224;
        int pixelSize = 3;
        float imageMean = (float) 127.5;
        float imageSTD = (float) 127.5;
        return preprocessBitmap(bitmap, inputSize, pixelSize, imageMean, imageSTD);
    }

    /**
     * @param byteBuffer The input ByteBuffer representing the input image.
     * @param interpreter The model interpreter
     * @param outputDimension The dimension of the output tensor
     * @return The output tensor of the model.
     */
    public static ArrayList<Float> interpreterForward(ByteBuffer byteBuffer, Interpreter interpreter, int outputDimension) {
        float[][] i_result = new float[1][outputDimension];
        interpreter.run(byteBuffer, i_result);
        ArrayList<Float> result = new ArrayList<>();

        for (float f : i_result[0]) {
            result.add(f);
        }
        return result;
    }

    /**
     * @param interpreter The model interpreter.
     * @param inputArray The input batch an array of input tensors.
     * @param outputDimension The dimension of the output tensor.
     * @return The batch of output tensors.
     */
    public static ArrayList<ArrayList<Float>> interpreterForward(Interpreter interpreter, ArrayList<ArrayList<Float>> inputArray, int outputDimension) {
        float[][] i_result = new float[1][outputDimension];
        ArrayList<ArrayList<Float>> result = new ArrayList();
        for (int i = 0; i < inputArray.size(); i++) {
            ByteBuffer bufferedInput = allocate1DTensor(inputArray.get(i));
            interpreter.run(bufferedInput, i_result);
            ArrayList<Float> temp = new ArrayList<>();
            for (float f : i_result[0]) {
                temp.add(f);
            }
            result.add(temp);
        }
        return result;
    }
}
