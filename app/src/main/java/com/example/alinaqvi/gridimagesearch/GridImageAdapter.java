package com.example.alinaqvi.gridimagesearch;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Created by alinaqvi on 2/2/15.
 */
public class GridImageAdapter extends ArrayAdapter<Image> {
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private final Random mRandom;

    public GridImageAdapter(Context context) {
        super(context, R.layout.item_image);
        mRandom = new Random();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Image image = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        }
        DynamicHeightImageView ivItem = (DynamicHeightImageView) convertView.findViewById(R.id.ivImage);
        double positionHeight = getPositionRatio(position);
        ivItem.setHeightRatio(positionHeight);
        Picasso.with(getContext()).load(image.tbUrl).into(ivItem);
        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(this.getClass().getName(), "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }
}
