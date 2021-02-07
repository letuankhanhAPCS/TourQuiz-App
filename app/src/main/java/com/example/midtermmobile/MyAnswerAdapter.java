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

public class MyAnswerAdapter extends ArrayAdapter<MyAnswer> {
    private Context context;
    private int layoutID;
    private ArrayList<MyAnswer> answer;

    public MyAnswerAdapter(@NonNull Context context, int resource, @NonNull List<MyAnswer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.answer = (ArrayList<MyAnswer>) objects;
    }

    @Override
    public int getCount() {
        return answer.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutID, null, false);
        }

        assert convertView != null;

        TextView textView = convertView.findViewById(R.id.textView_answer);
        TextView textViewLine = convertView.findViewById(R.id.textView_line);

        if(answer.get(position).getCorrect() == ' ')
            textViewLine.setVisibility(View.INVISIBLE);

        textView.setText(answer.get(position).getInput());

        return convertView;
    }
}
