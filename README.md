# HELIOS Content Aware Profiling Module #
## Introduction

This module aims to provide semantic analysis of the HELIOS users image collections. This analysis facilitates the creation of content aware user profiles that are either related to the concept of interests or stem from a more abstract, machine generated user representation space. It will also be possible, although not yet implemented, to condition the profiles based on a reference spatio-temporal context.

Here we detail how to develop applications leveraging this module's profiling results to provide recommendation capabilities or used by other helios modules such as Trust Manager.

### Instantiating and Using the Content Aware Profile Manager
Setting up the Content Aware Profile Manager starts from a contextual ego network instance. If such an instance is not available, it can be created when the application starts using the following code:
```java
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;

String userid = "ego"; // can use any kind of user id for the ego node
String internalStoragePath = app.getApplicationContext().getDir("egonetwork", MODE_PRIVATE); // an internal storage path in Android devices
ContextualEgoNetwork contextualEgoNetwork = ContextualEgoNetwork.createOrLoad(internalStoragePath, userid, null);
```
The contextual ego network library is used by this module to attach information on the perceived
social graph structure and can be saved using the command `contextualEgoNetwork.save()`. For a more detailed description see the documentation of the library. Only a **singleton** ContextualEgoNetwork instance should be created in an application and it should be shared with any other modules that potentially depend on it.

Given the contextual ego network instance and the application's context, we can then request from the Manager to update or create user's profile, given the corresponding Content Aware Profile class e.g.`CoarseInterestProfile.class`, she wants to calculate and a collection of images (`ArrayList<Image>`). There are three different profiling models that result three different Content Aware Profiles, two of them are interest based and differ with respect to the granularity of the interest categories, either coarse or fine (`CoarseInterestProfile.class`, `FineInterestProfile.class`), and the third one is based on Deep Metric Learning (`DMLProfile.class`).

```java
import eu.h2020.helios_social.modules.contentawareprofiling.ContentAwareProfileManager;

ContentAwareProfileManager contentAwareProfileManager = new ContentAwareProfileManager(appContext, egoNetwork);
ContentAwareProfile profile = contentAwareProfileManager.updateOrCreateProfile(CoarseInterestProfile.class, images);
System.out.println(profile.getInterestProfile());
```
Since calculating these semantic profiles can be considered long running tasks, we suggest to run the updates and the creation of a content aware profile as a Foreground Service using [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager?gclid=CjwKCAjwlID8BRAFEiwAnUoK1QQVgYienkomASAFB5LK6acFv_jhfXK1ulfDTBVE5goIhQhMoLZp_RoCkg0QAvD_BwE&gclsrc=aw.ds) maybe once a month and when the phone is charging.

The `ContentAwareProfileManager` stores the mining results on the contextual ego network and someone can access the already calculated profile as follows:
```java
contentAwareProfileManager.getProfile(CoarseInterestProfile.class);
```

Note that, when updating a profile, the corresponding miner does not re-calculate the profile but updates the profile based only on the new images in the collection.

The Content Aware Profiling Manager also allows the comparison between two Content Aware Profiles as follows:
```java
contentAwareProfileManager.getMatchingScore(profile1, profile2)
```

The matching score is calculated based on the cosine similarity of the between the two raw profiles.

### Extending Content Aware Profiling Module
The Content Aware Profiling module allows developers to extend the functionality of the module by building their own CNN (Convolutional Neural Network) models, miners and content aware profiles. More specifically, it allows developers to extend `AbstractModel`, `ContentAwareProfileMiner` and `ContentAwareProfile`. In case, you decide to create another interest profile (lets say more granular), you can extend instead of `ContentAwareProfile` the `InterestProfile` class. Below, we demonstrate an example:
```java
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.miners.ContentAwareProfileMiner;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.InterestProfile;

public class GranularInterestProfile extends InterestProfile {

    public GranularInterestProfile() {
        super(new CNNModelData());
  }

    public GranularInterestProfile(CNNModelData modelData) {
        super(modelData);
  }

    @Override
    public ArrayList<Interest> getInterestProfile() {
        //RETURN ARRAY OF INTERESTS
  }

    @Override
    public ArrayList<Interest> getInterestProfile(float threshold) {
        //RETURN ARRAY OF INTERESTS
    }

    @Override
    public ArrayList<Interest> getInterestProfile(SpatioTemporalContext context){
        //RETURN ARRAY FOR THE GIVEN SpatioTemporalContext
  }

    @Override
    public ArrayList<Interest> getInterestProfile(SpatioTemporalContext context, float threshold) {
        //RETURN ARRAY FOR THE GIVEN THRESHOLD
  }

    public class GranularModel extends AbstractModel {

    /**
    * @param ctx The Android context.
    * */
    public GranularModel(Context ctx) {
            super(ctx);
    }

        @Override
        public ArrayList<ArrayList<Float>> forwardCNN(ArrayList<Image> images){
            //DO CALCULATIONS
        }

        @Override
        public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput) {
            //DO CALCULATIONS
            }

        @Override
        public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput, ArrayList<Float> attentionWeights) {
            //DO CALCULATIONS
        }
    }

    public class GranularInterestMiner extends ContentAwareProfileMiner {

        /**
        * @param assetManager The android asset manager.
        * @param ctx The android context.
        * @param egoNetwork The egoNetwork provided from the CEN library.
        **/
        public GranularInterestMiner(AssetManager assetManager, Context ctx, ContextualEgoNetwork egoNetwork) {
            super(assetManager, ctx, egoNetwork);
        }

        @Override
        public GranularInterestProfile calculateContentAwareProfile(ArrayList<Image> images) {
            //calculate Granular Interest Profile from the given collection of images
        }
    }
}
```
After, you have created your model, miner and profile, you need to add the profile and miner to the Content Aware Profile Manager as follows:

```java
contentAwareManager.addMiner(GranularInterestProfile.class, GranularInterestProfileMiner.class);
```

## Project Structure
This project contains the following components:

lib/src - The source code files.

doc - Additional documentation files.
