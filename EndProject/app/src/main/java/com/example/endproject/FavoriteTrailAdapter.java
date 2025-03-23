// FavoriteTrailAdapter.java
package com.example.endproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
//Adapter to create favorite list
public class FavoriteTrailAdapter extends ArrayAdapter<String> {

    public FavoriteTrailAdapter(Context context, List<String> trails) {
        super(context, 0, trails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite_trail, parent, false);
        }

        TextView trailNameTextView = convertView.findViewById(R.id.trail_name_text_view);
        trailNameTextView.setText(getItem(position));

        return convertView;
    }
}
