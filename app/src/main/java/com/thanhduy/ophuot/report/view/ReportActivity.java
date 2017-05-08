package com.thanhduy.ophuot.report.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.report.presenter.ReportPresenter;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 28/04/2017.
 */

public class ReportActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_report)
    Button btnReport;
    @BindView(R.id.chk1)
    CheckBox chk1;
    @BindView(R.id.chk2)
    CheckBox chk2;
    @BindView(R.id.chk3)
    CheckBox chk3;
    @BindView(R.id.chk4)
    CheckBox chk4;
    @BindView(R.id.chk5)
    CheckBox chk5;
    @BindView(R.id.edt_content)
    EditText edtContent;

    private ReportPresenter presenter;
    private User user;
    private String content = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //init data
        presenter = new ReportPresenter(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.reportUpper));
        //get intent
        user = (User) getIntent().getSerializableExtra(Constants.USERS);
        //event click
        btnReport.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getContent() {
        //get content
        if (chk1.isChecked()) {
            content = getResources().getString(R.string.report1);
        }
        if (chk2.isChecked()) {
            if (content.length() != 0) {
                content = String.format("%s, %s", content, getResources().getString(R.string.report2));
            } else {
                content = getResources().getString(R.string.report2);
            }
        }
        if (chk3.isChecked()) {
            if (content.length() != 0) {
                content = String.format("%s, %s", content, getResources().getString(R.string.report3));
            } else {
                content = getResources().getString(R.string.report3);
            }
        }
        if (chk4.isChecked()) {
            if (content.length() != 0) {
                content = String.format("%s, %s", content, getResources().getString(R.string.report4));
            } else {
                content = getResources().getString(R.string.report4);
            }
        }
        if (chk5.isChecked()) {
            if (content.length() != 0) {
                content = String.format("%s, %s", content, getResources().getString(R.string.report5));
            } else {
                content = getResources().getString(R.string.report5);
            }
        }
        //append with content in edittext
        if (content.length() != 0) {
            content = String.format("%s, %s", content, edtContent.getText().toString());
        } else {
            content = edtContent.getText().toString();
        }
        return content;
    }

    @Override
    public void onClick(View v) {
        if (v == btnReport) {
            if (!chk1.isChecked() && !chk2.isChecked() && !chk3.isChecked() && !chk4.isChecked() &&
                    !chk5.isChecked() && edtContent.getText().length() == 0) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.fillAtLeastOne), ReportActivity.this);
            } else {
                presenter.sendReport(user.getUid(), getUid(), getContent());
                final Dialog dialog = new Dialog(ReportActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setCancelable(false);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.tv);
                text.setText(getResources().getString(R.string.savedReportContent));
                Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.show();
            }
        }

    }
}
