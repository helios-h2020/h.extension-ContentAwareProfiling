package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.Context;
import android.content.res.AssetManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.miners.CoarseInterestProfileMiner;
import eu.h2020.helios_social.modules.contentawareprofiling.miners.ContentAwareProfileMiner;
import eu.h2020.helios_social.modules.contentawareprofiling.miners.DMLProfileMiner;
import eu.h2020.helios_social.modules.contentawareprofiling.miners.FineInterestProfileMiner;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.CoarseInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.DMLProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;

/**
 * The Manager of the Content Aware Profile. It can be used create, get or update the profile as
 * well as compare the similarity between two profiles.
 */
public class ContentAwareProfileManager {
    public static final String TAG = ContentAwareProfileManager.class.getName();
    private static final Logger LOG = Logger.getLogger(TAG);

    private AssetManager assetManager;
    private Context ctx;
    private ContextualEgoNetwork egoNetwork;
    private HashMap<Class<? extends ContentAwareProfile>, Class<?
            extends ContentAwareProfileMiner>> miners = new HashMap<Class<?
            extends ContentAwareProfile>, Class<? extends ContentAwareProfileMiner>>();

    /**
     * @param ctx        The android context.
     * @param egoNetwork The egoNetwork provided from the CEN library.
     */
    public ContentAwareProfileManager(Context ctx, ContextualEgoNetwork egoNetwork) {
        this.assetManager = ctx.getAssets();
        this.ctx = ctx;
        this.egoNetwork = egoNetwork;
        miners.put(CoarseInterestsProfile.class, CoarseInterestProfileMiner.class);
        miners.put(FineInterestsProfile.class, FineInterestProfileMiner.class);
        miners.put(DMLProfile.class, DMLProfileMiner.class);
    }

    /**
     * returns the content aware profile stored in the ego network given the profile class (e.g.
     * CoarseInterestProfile.class)
     *
     * @param profileClass
     * @return the requested ContentAwareProfile
     */
    public <ContentAwareProfileClass extends ContentAwareProfile> ContentAwareProfileClass getProfile(Class<ContentAwareProfileClass> profileClass) {
        return egoNetwork.getEgo().getOrCreateInstance(profileClass);
    }

    /**
     * calculates or updates the content aware profile based on the given collection of images
     * and the class of the profile you want to be calculated
     *
     * @param profileClass ContentAwareProfile class
     * @param images collection of images
     * @return the calculated content aware profile
     */
    public <ContentAwareProfileClass extends ContentAwareProfile> ContentAwareProfileClass updateOrCreateProfile(Class<ContentAwareProfileClass> profileClass, ArrayList<Image> images) {
        ContentAwareProfileMiner miner = createMiner(profileClass);
        return (ContentAwareProfileClass) miner.calculateContentAwareProfile(images);
    }

    /**
     * Constructs a miner for the given ContentAwareProfile class
     * @param profileClass
     * @return a ContentAwareProfileMiner
     */
    private ContentAwareProfileMiner createMiner(Class<? extends ContentAwareProfile> profileClass) {
        try {
            Constructor<? extends ContentAwareProfileMiner> constructor =
                    Class.forName(miners.get(profileClass).getName())
                            .asSubclass(ContentAwareProfileMiner.class).getDeclaredConstructor(AssetManager.class, Context.class, ContextualEgoNetwork.class);
            return constructor.newInstance(assetManager, ctx, egoNetwork);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * a new miner mapped to a new ContentAwareProfile is added to the manager
     * @param profileClass
     * @param minerClass
     */
    public void addMiner(Class<? extends ContentAwareProfile> profileClass, Class<?
            extends ContentAwareProfileMiner> minerClass) {
        miners.put(profileClass, minerClass);
    }

    /**
     * Calculates the matching score between the two given content aware profiles
     *
     * @param profile1
     *
     * @param profile2
     * @return cosine simimarity of the two given profiles
     */
    public <Profile extends ContentAwareProfile> double getMatchingScore(Profile profile1,
                                                                         Profile profile2) {
        if (profile1.getModelData().getRawProfile().size() > 0 && profile2.getModelData().getRawProfile().size() > 0) {
            return cosineSimilarity(profile1.getModelData().getRawProfile(),
                    profile2.getModelData().getRawProfile());
        } else {
            throw new IllegalArgumentException("Given profiles need to be instances of the same " +
                    "class");
        }
    }

    /**
     * Calculate the cosine similarity between two profiles.
     *
     * @param profile1 The first profile to be compared.
     * @param profile2 The second profile to be compared.
     * @return The cosine similarity between the two profiles.
     */
    public double getMatchingScore(ArrayList<Float> profile1, ArrayList<Float> profile2) {
        return cosineSimilarity(profile1, profile2);
    }

    /**
     * Calculates the cosine similarity between two vectors .
     *
     * @param profile1 The first vector.
     * @param profile2 The second vector.
     * @return The cosine similarity.
     */
    private double cosineSimilarity(ArrayList<Float> profile1, ArrayList<Float> profile2) {
        ArrayList<Float> nProfile1 = normalizeVector(profile1);
        ArrayList<Float> nProfile2 = normalizeVector(profile2);
        float result = 0.0f;
        for (int i = 0; i < nProfile1.size(); i++) {
            result += nProfile1.get(i) * nProfile2.get(i);
        }
        return result;
    }

    /**
     * @param x A vector
     * @return The normalized vector.
     */
    private ArrayList<Float> normalizeVector(ArrayList<Float> x) {
        float norm = 0;
        for (int i = 0; i < x.size(); i++) {
            norm += x.get(i) * x.get(i);
        }
        norm = (float) Math.sqrt(norm);

        ArrayList<Float> y = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            y.add(x.get(i) / norm);
        }
        return y;

    }
}
