package com.thanhduy.ophuot.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thanhduy.ophuot.database.model.District;
import com.thanhduy.ophuot.database.model.Province;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by buivu on 26/02/2017.
 */

public class DatabaseAdapter {
    String DATABASE_NAME = "cities.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    private Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
        database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    }


    //get list province
    public List<Province> getAllProvince() {
        List<Province> provinces = new ArrayList<>();
        Cursor cursor = database.query("Provinces", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Province province = new Province();
            int provinceId = cursor.getInt(0);
            String provinceName = cursor.getString(1);
            //save data
            province.setProvinceId(provinceId);
            province.setProvinceName(provinceName);
            provinces.add(province);
        }
        cursor.close();
        return provinces;
    }

    //get list district by provinceName
    public List<District> getDistrictsByProvinceName(String provinceName) {
        List<District> districts = new ArrayList<>();
        String selectQuery = "SELECT * FROM districts where province_id in (select id from provinces where name like '%" + provinceName + "%')";
        Cursor cursor = database.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            District district = new District();
            int districtId = cursor.getInt(0);
            String districtName = cursor.getString(2);
            int provinceId = cursor.getInt(1);
            //save data
            district.setProvinceId(provinceId);
            district.setDistrictId(districtId);
            district.setDistrictName(districtName);
            //save data to list
            districts.add(district);
        }
        cursor.close();
        return districts;
    }

    //get provinceId by province_name
    public int getProvinceIdByProvinceName(String provinceName) {
        String selectQuery = "select id from provinces where name like '%" + provinceName + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int provinceId = cursor.getInt(0);
        return provinceId;
    }

    //get districtId by districtName
    public int getDistrictIdByDistrictName(String districtName) {
        String selectQuery = "select id from districts where name like '%" + districtName + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int districtId = cursor.getInt(0);
        return districtId;
    }

    public void copyDatabase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                copyDataBaseFromAsset();

            } catch (Exception e) {
            }
        }

    }

    private void copyDataBaseFromAsset() {
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            String outFileName = getStorePath();
            File f = new File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception ex) {
            Log.e("Lỗi sao chép", ex.toString());
        }
    }

    private String getStorePath() {
        return context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }


}
