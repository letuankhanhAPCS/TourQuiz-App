package com.example.midtermmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyLocationAdapter extends ArrayAdapter<MyLocation> {
    private Context context;
    private int layoutID;
    private ArrayList<MyLocation> locations;

    public MyLocationAdapter(@NonNull Context context, int resource, @NonNull List<MyLocation> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.locations = (ArrayList<MyLocation>) objects;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutID, null, false);
        }

        assert convertView != null;

        ImageView imageView = convertView.findViewById(R.id.imageView_locationImage);
        TextView textViewName = convertView.findViewById(R.id.textView_locationName);;

        MyLocation myLocation = locations.get(position);
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), myLocation.getLocationPicture());
        imageView.setImageBitmap(bmp);
        textViewName.setText(myLocation.getLocationName());

        return convertView;
    }
}
