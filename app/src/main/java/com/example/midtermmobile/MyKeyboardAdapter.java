package com.example.midtermmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyKeyboardAdapter extends ArrayAdapter<String> {
    private Context context;
    private int layoutID;
    private ArrayList<String> keyboard;

    public MyKeyboardAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.keyboard = (ArrayList<String>) objects;
    }

    @Override
    public int getCount() {
        return keyboard.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutID, null, false);
        }

        assert convertView != null;

        TextView textView = convertView.findViewById(R.id.textView_keyboard);
        textView.setText(keyboard.get(position));

        return convertView;
    }
}
