package com.thanhduy.ophuot.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.term.TermActivity;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowSnackbar;

/**
 * Created by buivu on 24/04/2017.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private LinearLayout linearLayout, linearTemp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_help, container, false);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_feed_back);
        linearTemp = (LinearLayout) rootView.findViewById(R.id.linear_term);
        //event click
        linearLayout.setOnClickListener(this);
        linearTemp.setOnClickListener(this);
        return rootView;
    }

    private void sendEmail() {
        try {
            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", Constants.EMAIL_ADMIN, null));
            startActivity(i);
        } catch (Exception e) {
            ShowSnackbar.showSnack(getActivity(), getResources().getString(R.string.error));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == linearLayout) {
            sendEmail();
        } else if (v == linearTemp) {
            Intent intent = new Intent(getActivity(), TermActivity.class);
            startActivity(intent);
        }
    }
}
