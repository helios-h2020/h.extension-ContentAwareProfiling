package eu.h2020.helios_social.happs.contentawareprofiling;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.inject.Inject;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.CoarseInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ImageInterest;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;
import eu.h2020.helios_social.happs.contentawareprofiling.activepeers.ActivePeersActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.ActivityComponent;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.BaseActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationManager;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ProfilingWorker;

import static java.util.logging.Logger.getLogger;

/**
 * Main Activity of Content Aware Profiling Demo Application. User can start running profiling
 * module
 * as a foreground service. The service starts only if the phone is charging.
 */
public class MainActivity extends BaseActivity {

    private static String TAG = MainActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);
    private static final String MODEL = "MODEL";
    private String modelType;

    private static final int REQUEST_ACCESS_MEDIA_METADATA = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] MEDIA_LOCATION_PERMISSION = {
            Manifest.permission.ACCESS_MEDIA_LOCATION
    };

    @Inject
    WorkManager workManager;

    @Inject
    volatile ContextualEgoNetwork egoNetwork;

    @Override
    public void injectActivity(ActivityComponent component) {
        component.inject(this);
    }

    private TextView results_title;
    private WorkRequest request;
    private Button scheduleJob;
    private MenuItem showNetwork;
    private TableLayout tableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, CommunicationManager.class));

        scheduleJob = findViewById(R.id.schedule_profiler);
        results_title = findViewById(R.id.results_title);

        RadioButton coarse_button = findViewById(R.id.radio_coarse);

        coarse_button.setOnClickListener(a -> {
            updateProfileTags(CoarseInterestsProfile.class);
            modelType = ModelType.COARSE.name();
        });

        RadioButton fine_button = findViewById(R.id.radio_fine);
        fine_button.setOnClickListener(a -> {
            updateProfileTags(FineInterestsProfile.class);
            modelType = ModelType.FINE.name();
        });

        scheduleJob.setOnClickListener(a -> {
            verifyStoragePermissions();
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getString(MODEL) != null) {
            modelType = extras.getString(MODEL);
            if (modelType.equals(ModelType.COARSE.name())) {
                coarse_button.performClick();
            } else {
                fine_button.performClick();
            }
        } else {
            modelType = ModelType.COARSE.name();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_menu, menu);

        showNetwork = menu.findItem(R.id.network);
        showNetwork.setOnMenuItemClickListener(l -> {
                    startActivity(new Intent(this, ActivePeersActivity.class));
                    return true;
                }
        );

        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Present profiling results into a table
     *
     * @param profile
     */
    private void createTable(ArrayList<Interest> profile) {
        TableLayout table = findViewById(R.id.profiling_results);
        table.removeAllViews();

        for (Interest interest : profile) {
            TableRow tableRow = new TableRow(this);
            TextView interestView = new TextView(this);
            interestView.setPadding(3, 3, 3, 3);
            interestView.setText(interest.getName());
            tableRow.addView(interestView);
            TextView scoreView = new TextView(this);
            scoreView.setPadding(3, 3, 3, 3);
            scoreView.setGravity(Gravity.RIGHT);
            scoreView.setText(interest.getWeight() + "");
            tableRow.addView(scoreView);
            table.addView(tableRow);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (modelType != null && modelType.equals(ModelType.COARSE.name()))
            updateProfileTags(CoarseInterestsProfile.class);
        else
            updateProfileTags(FineInterestsProfile.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (modelType != null && modelType.equals(ModelType.COARSE.name()))
            updateProfileTags(CoarseInterestsProfile.class);
        else
            updateProfileTags(FineInterestsProfile.class);
    }


    // EDITED (DETAILED INTERESTS)
    /**
     * Provide top 3 User Interests in text view based on the analysed photos from the user
     * collection of images given the selected profile class
     *
     * @param profileClass {@link CoarseInterestsProfile} or {@link FineInterestsProfile}
     */
    private void updateProfileTags(Class<? extends ContentAwareProfile> profileClass) {
        ListView listView = findViewById(R.id.detailed_interests_listview);
        if (profileClass.equals(CoarseInterestsProfile.class)) {
            ArrayList<Interest> interests =
                    egoNetwork.getEgo().getOrCreateInstance(CoarseInterestsProfile.class).getInterests();

            HashMap<Interest, ArrayList<ImageInterest>> detailedInterests =
                    egoNetwork.getEgo().getOrCreateInstance(CoarseInterestsProfile.class).getDetailedInterests();


            if (interests.size() > 0) {
                // Collections.sort(interests);
                // createTable(interests);
                results_title.setText("Coarse Interest Profiling Results: ");
                // LOG.info(interests.toString());
                Log.d("detailed interests",detailedInterests.toString());

                // get categories and their scores
                ArrayList<Interest> interestsArrayList = new ArrayList<>(detailedInterests.keySet());
                // sort them
                Collections.sort(interestsArrayList);
                // show them
                CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this,interestsArrayList);
                listView.setAdapter(customArrayAdapter);
//                HashMapAdapter adapter = new HashMapAdapter(this,detailedInterests);
//                ListView list = findViewById(R.id.detailed_interests_listview);
//                list.setAdapter(adapter);
            }
        } else {
            ArrayList<Interest> interests =
                    egoNetwork.getEgo().getOrCreateInstance(FineInterestsProfile.class).getInterests();

            HashMap<Interest, ArrayList<ImageInterest>> detailedInterests =
                    egoNetwork.getEgo().getOrCreateInstance(FineInterestsProfile.class).getDetailedInterests();

            if (interests.size() > 0) {
                // Collections.sort(interests);
                results_title.setText("Fine Interest Profiling Results: ");
                //createTable(interests);
                LOG.info(interests.toString());

                // get categories and their scores
                ArrayList<Interest> interestsArrayList = new ArrayList<>(detailedInterests.keySet());
                // sort them
                Collections.sort(interestsArrayList);
                // show them
                CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this,interestsArrayList);
                listView.setAdapter(customArrayAdapter);

//                HashMapAdapter adapter = new HashMapAdapter(this,detailedInterests);
//                ListView list = findViewById(R.id.detailed_interests_listview);
//                list.setAdapter(adapter);
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Interest interest = (Interest) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ImageListActivity.class);
                intent.putExtra("keyName",interest.getName());
                intent.putExtra("keyWeight", interest.getWeight());
                if (profileClass.equals(CoarseInterestsProfile.class)) {
                    intent.putExtra("profileClass","Coarse");
                } else if (profileClass.equals(FineInterestsProfile.class)){
                    intent.putExtra("profileClass","Fine");
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Run Profiling as a Foreground work (we need to run it as a foreground since it takes more
     * than 10 minutes to run)
     */
    public void runProfilingAsForegroundWork() {
        scheduleJob.setEnabled(false);
        Constraints constraints = new Constraints.Builder()
                .build();

        request = new OneTimeWorkRequest.Builder(ProfilingWorker.class).setConstraints(constraints)
                .setInputData(new Data.Builder().putString(MODEL, modelType).build()).build();
        //workManager.cancelAllWork();
        workManager.enqueueUniqueWork(
                "ContentAwareProfiler",
                ExistingWorkPolicy.REPLACE,
                (OneTimeWorkRequest) request
        );

        LOG.info("Work request with id " + request.getId() + " enqueued!");
        workManager.getWorkInfoByIdLiveData(request.getId())
                .observe(this, workStatus -> {
                    if (workStatus != null){
                        if (workStatus.getState() == WorkInfo.State.SUCCEEDED) {
                            if (modelType != null && modelType.equals(ModelType.COARSE.name()))
                                updateProfileTags(CoarseInterestsProfile.class);
                            else
                                updateProfileTags(FineInterestsProfile.class);
                            scheduleJob.setEnabled(true);
                        }
                    }

                });
    }

    /**
     * request storage permissions from user to access image collection
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            verifyMetadataPermissions();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Access Storage Permission!")
                    .setMessage(R.string.profiling_storage_permissions)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    PERMISSIONS_STORAGE,
                                    REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void verifyMetadataPermissions() {
        //check if access to metadata has been granted.
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            runProfilingAsForegroundWork();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Access to Media Metadata!")
                    .setMessage(R.string.profiling_metadata_permissions)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    MEDIA_LOCATION_PERMISSION,
                                    REQUEST_ACCESS_MEDIA_METADATA);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    MEDIA_LOCATION_PERMISSION,
                    REQUEST_ACCESS_MEDIA_METADATA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verifyMetadataPermissions();
            }
        } else if (requestCode == REQUEST_ACCESS_MEDIA_METADATA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runProfilingAsForegroundWork();
            }
        } else {
            Toast.makeText(this, "Permissions did not grant!", Toast.LENGTH_LONG).show();
        }
    }

    //to remember the selected model type even if you left the application for a moment
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    //restore previously selected model type
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        modelType = savedInstanceState.getString(MODEL);
    }

}
