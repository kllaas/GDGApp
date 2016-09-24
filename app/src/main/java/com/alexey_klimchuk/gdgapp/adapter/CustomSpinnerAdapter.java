package com.alexey_klimchuk.gdgapp.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alexey_klimchuk.gdgapp.R;

/**
 * Created by Alex on 25.03.2016.
 * Adapter for mood states
 */
public class CustomSpinnerAdapter extends ArrayAdapter {
    String[] spinnerValues;
    Context mContext;

    public CustomSpinnerAdapter(Context ctx, int txtViewResourceId,
                                String[] spinnerValues) {
        super(ctx, txtViewResourceId, spinnerValues);
        this.spinnerValues = spinnerValues;
        this.mContext = ctx;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mySpinner = inflater.inflate(R.layout.mood_item, parent, false);//get parent item view

        TextView main_text = (TextView) mySpinner
                .findViewById(R.id.mood_text);
        main_text.setText(spinnerValues[position]);

        View view = mySpinner.findViewById(R.id.mood_icon);
        GradientDrawable gd = new GradientDrawable();
        if (position == 0)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        if (position == 1)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorNormal));
        if (position == 2)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorBad));

        gd.setShape(GradientDrawable.OVAL);
        view.setBackground(gd);
        return mySpinner;
    }

}