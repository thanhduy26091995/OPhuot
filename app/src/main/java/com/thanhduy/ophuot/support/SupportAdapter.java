package com.thanhduy.ophuot.support;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.model.Supporter;

import java.util.List;

/**
 * Created by buivu on 21/04/2017.
 */

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.SupportViewHolder> {

    public Activity activity;
    public List<Supporter> listSupporter;
    private SqlLiteDbHelper sqlLiteDbHelper;

    public SupportAdapter(Activity activity, List<Supporter> listSupporter) {
        this.activity = activity;
        this.listSupporter = listSupporter;
        sqlLiteDbHelper = new SqlLiteDbHelper(activity);
    }

    @Override
    public SupportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_supporter, null);
        return new SupportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SupportViewHolder holder, int position) {
        final Supporter supporter = listSupporter.get(position);
        //load data
        holder.txtName.setText(supporter.getName());
        holder.txtNote.setText(supporter.getNote());
        holder.txtPhone.setText(supporter.getPhone());
        holder.txtProvince.setText(sqlLiteDbHelper.getProvinceNameByProvinceId(String.valueOf(supporter.getProvinceId())));
        //event click
        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore(v, supporter);
            }
        });
    }

    private void showMore(View v, final Supporter supporter) {
        try {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_action_support, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_call) {
                        phoneCall(supporter.getPhone());
                    } else if (item.getItemId() == R.id.action_facebook) {
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                        String facebookUrl = getFacebookPageURL(supporter.getFacebook());
                        facebookIntent.setData(Uri.parse(facebookUrl));
                        activity.startActivity(facebookIntent);
                    }
                    return true;
                }
            });
        } catch (Exception e) {

        }
    }

    public String getFacebookPageURL(String url) {
        PackageManager packageManager = activity.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + url;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return url; //normal web url
        }
        return "";
    }

    private void phoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

        if (ActivityCompat.checkSelfPermission(activity
                , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        activity.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return listSupporter.size();
    }

    public class SupportViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtPhone, txtNote, txtProvince;
        public ImageView imgMore;

        public SupportViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txt_support_name);
            txtPhone = (TextView) itemView.findViewById(R.id.txt_support_phone);
            txtNote = (TextView) itemView.findViewById(R.id.txt_support_note);
            txtProvince = (TextView) itemView.findViewById(R.id.txt_support_province);
            imgMore = (ImageView) itemView.findViewById(R.id.img_support_more);
        }
    }
}
