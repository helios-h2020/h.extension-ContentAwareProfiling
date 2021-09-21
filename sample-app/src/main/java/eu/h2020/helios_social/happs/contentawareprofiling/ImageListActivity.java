package eu.h2020.helios_social.happs.contentawareprofiling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.ActivityComponent;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.BaseActivity;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.CoarseInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ImageInterest;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;

public class ImageListActivity extends BaseActivity {

    @Inject
    volatile ContextualEgoNetwork egoNetwork;

    @Override
    public void injectActivity(ActivityComponent component) {
        component.inject(this);
    }

    TextView tvDescription;
    TextView score1 ;
    TextView score2 ;
    TextView score3 ;

    ImageView image1 ;
    ImageView image2 ;
    ImageView image3 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // get extras
        Intent intent = getIntent();
        String name = intent.getStringExtra("keyName");
        Double weight = intent.getDoubleExtra("keyWeight",0);
        String profileClass = intent.getStringExtra("profileClass");
        // create an interest object
        Interest interest = new Interest();
        interest.setName(name);
        interest.setWeight(weight);

        HashMap<Interest, ArrayList<ImageInterest>> detailedInterests = new HashMap<>();
        // get the hashmap
        if (profileClass!=null) {
            if (profileClass.equals("Coarse")) {
                detailedInterests = egoNetwork.getEgo().getOrCreateInstance(CoarseInterestsProfile.class).getDetailedInterests();
            } else if (profileClass.equals("Fine")) {
                detailedInterests = egoNetwork.getEgo().getOrCreateInstance(FineInterestsProfile.class).getDetailedInterests();
            }
        }
        if (detailedInterests!=null) {
            // get images with their scores
            ArrayList<ImageInterest> imageInterests = (ArrayList<ImageInterest>) detailedInterests.get(interest);

            tvDescription = findViewById(R.id.description_text);
            score1 = findViewById(R.id.score1);
            score2 = findViewById(R.id.score2);
            score3 = findViewById(R.id.score3);

            image1 = findViewById(R.id.image1);
            image2 = findViewById(R.id.image2);
            image3 = findViewById(R.id.image3);

            String text = tvDescription.getText() + name;
            tvDescription.setText(text);

            score1.setText(imageInterests.get(0).getWeight().toString());
            score2.setText(imageInterests.get(1).getWeight().toString());
            score3.setText(imageInterests.get(2).getWeight().toString());


            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);


            Glide.with(this).load(imageInterests.get(0).getImageURI()).apply(options).into(image1);
            Glide.with(this).load(imageInterests.get(1).getImageURI()).apply(options).into(image2);
            Glide.with(this).load(imageInterests.get(2).getImageURI()).apply(options).into(image3);
        }
    }
}