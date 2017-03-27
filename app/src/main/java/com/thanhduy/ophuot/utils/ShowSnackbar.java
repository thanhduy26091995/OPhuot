package com.thanhduy.ophuot.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 23/03/2017.
 */

public class ShowSnackbar {
    public static void showSnack(Activity activity, String mess) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.activity), mess, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView txtMess = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        txtMess.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
