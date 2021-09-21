package eu.h2020.helios_social.happs.contentawareprofiling;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;

import static java.security.AccessController.getContext;

public class CustomArrayAdapter extends ArrayAdapter<Interest> {
    public CustomArrayAdapter(Context context, ArrayList<Interest> interests) {
        super(context, 0, interests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Interest interest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_interests, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvScore = (TextView) convertView.findViewById(R.id.score);
        // Populate the data into the template view using the data object
        tvName.setText(interest.getName());
        tvScore.setText(interest.getWeight().toString());
        // Return the completed view to render on screen
        return convertView;
    }


}