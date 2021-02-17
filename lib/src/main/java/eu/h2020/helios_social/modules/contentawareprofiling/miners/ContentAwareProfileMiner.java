package eu.h2020.helios_social.modules.contentawareprofiling.miners;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.ArrayList;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;

/**
 * This class defines a common abstraction for all the content aware profile miners. A miner is tasked
 * with most of the heavy calculations needed for the creation of a content aware profile. The
 * calculations mainly involve running the appropriate deep learning model on the user's image collection.
 */
public abstract class ContentAwareProfileMiner {

    protected AssetManager assetManager;
    protected ContextualEgoNetwork egoNetwork;
    protected Context ctx;

    /**
     * @param assetManager The android asset manager.
     * @param ctx          The android context.
     * @param egoNetwork   The egoNetwork provided from the CEN library.
     */
    public ContentAwareProfileMiner(AssetManager assetManager, Context ctx, ContextualEgoNetwork egoNetwork) {
        this.assetManager = assetManager;
        this.ctx = ctx;
        this.egoNetwork = egoNetwork;
    }

    public abstract void calculateContentAwareProfile(ArrayList<Image> images);

    public abstract ContentAwareProfile getProfile();

    public abstract ContentAwareProfile getProfile(SpatioTemporalContext context);
}
