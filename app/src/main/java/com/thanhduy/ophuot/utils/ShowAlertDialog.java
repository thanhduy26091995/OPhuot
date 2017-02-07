package com.thanhduy.ophuot.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thanhduy.ophuot.R;

/**
 * Created by buivu on 14/01/2017.
 */
public class ShowAlertDialog {
    public static void showAlert(String mess, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.tv);
        text.setText(mess);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
