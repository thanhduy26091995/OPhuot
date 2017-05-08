package com.thanhduy.ophuot.report.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.report.view.ReportActivity;
import com.thanhduy.ophuot.utils.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 04/05/2017.
 */

public class ReportPresenter {
    private DatabaseReference mDatabase;
    private ReportActivity view;

    public ReportPresenter(ReportActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void sendReport(String userReported, String reportBy, String content) {
        String key = mDatabase.child(Constants.REPORT).child(userReported).push().getKey();
        long timestamp = new Date().getTime();
        //create object
        Map<String, Object> reportMap = new HashMap<>();
        reportMap.put(Constants.USER_REPORTED, userReported);
        reportMap.put(Constants.REPORT_BY, reportBy);
        reportMap.put(Constants.CONTENT, content);
        reportMap.put(Constants.TIMESTAMP, timestamp);
        //save database
        mDatabase.child(Constants.REPORT).child(userReported).child(key).setValue(reportMap);
    }
}
