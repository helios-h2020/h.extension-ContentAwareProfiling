package eu.h2020.helios_social.happs.contentawareprofiling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import eu.h2020.helios_social.modules.contentawareprofiling.profile.ImageInterest;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;
// EDITED (DETAILED PROFILE)
public class HashMapAdapter extends BaseAdapter {

    private ArrayList<Interest> keysArrayList;
    private Context context;
    private HashMap<Interest, ArrayList<ImageInterest>> mData;
    // Public constructor
    public HashMapAdapter(Context context, HashMap<Interest, ArrayList<ImageInterest>> mData) {
        this.context = context;
        this.mData = mData;
        keysArrayList = new ArrayList<>(mData.keySet());
        Collections.sort(keysArrayList);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(keysArrayList.get(position));
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Interest key = keysArrayList.get(pos);
        ArrayList<ImageInterest> Value = (ArrayList<ImageInterest>) getItem(pos);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_detailed_interests, parent, false);

        TextView category = (TextView) row.findViewById(R.id.category);
        TextView category_score = (TextView) row.findViewById(R.id.score_category);
        TextView score1 = (TextView) row.findViewById(R.id.score1);
        TextView score2 = (TextView) row.findViewById(R.id.score2);
        TextView score3 = (TextView) row.findViewById(R.id.score3);

        ImageView image1 = (ImageView) row.findViewById(R.id.image1);
        ImageView image2 = (ImageView) row.findViewById(R.id.image2);
        ImageView image3 = (ImageView) row.findViewById(R.id.image3);


        category.setText(key.getName());
        category_score.setText(key.getWeight().toString());
        score1.setText(Value.get(0).getWeight().toString());
        score2.setText(Value.get(1).getWeight().toString());
        score3.setText(Value.get(2).getWeight().toString());

//        image1.setImageBitmap(Value.get(0).getImageURI().getBitmap(context));
//        image2.setImageBitmap(Value.get(1).getImageURI().getBitmap(context));
//        image3.setImageBitmap(Value.get(2).getImageURI().getBitmap(context));

        return row;
    }
}