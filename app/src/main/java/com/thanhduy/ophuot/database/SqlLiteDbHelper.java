package com.thanhduy.ophuot.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thanhduy.ophuot.database.model.District;
import com.thanhduy.ophuot.database.model.Province;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SqlLiteDbHelper extends SQLiteOpenHelper {

    // All Static variables
// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "cities.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    static Context ctx;

    public SqlLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    // Getting single contact
    public void CopyDataBaseFromAsset() throws IOException {

        InputStream myInput = ctx.getAssets().open(DATABASE_NAME);

// Path to the just created empty db
        String outFileName = getDatabasePath();

// if the path doesn't exist first, create it
        File f = new File(ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();

// Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

// transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

// Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private static String getDatabasePath() {
        return ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_NAME;
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Log.d("ERROR", "Copying sucess from Assets folder");
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub

    }

    //get list province
    public List<Province> getAllProvince() {
        SQLiteDatabase database = this.getReadableDatabase();

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
        database.close();
        return provinces;
    }

    //get list district by provinceName
    public List<District> getDistrictsByProvinceName(String provinceName) {
        SQLiteDatabase database = this.getReadableDatabase();

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
        database.close();
        return districts;
    }

    //get list district by provinceName
    public List<District> getDistrictsByProvinceId(int provinceId) {
        SQLiteDatabase database = this.getReadableDatabase();

        List<District> districts = new ArrayList<>();
        String selectQuery = "SELECT * FROM districts where province_id = '" + provinceId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            District district = new District();
            int districtId = cursor.getInt(0);
            String districtName = cursor.getString(2);
            //save data
            district.setProvinceId(provinceId);
            district.setDistrictId(districtId);
            district.setDistrictName(districtName);
            //save data to list
            districts.add(district);
        }
        cursor.close();
        database.close();
        return districts;
    }

    //get provinceId by province_name
    public int getProvinceIdByProvinceName(String provinceName) {
        SQLiteDatabase database = this.getReadableDatabase();
        int provinceId = 0;
        String selectQuery = "select id from provinces where name like '%" + provinceName + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor != null && cursor.moveToFirst()) {
            provinceId = cursor.getInt(0);
            cursor.close();

        }
        database.close();
        return provinceId;
    }

    //get districtId by districtName
    public int getDistrictIdByDistrictName(String districtName) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "select id from districts where name like '%" + districtName + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int districtId = cursor.getInt(0);
        cursor.close();
        database.close();
        return districtId;
    }

    //get districtName by districtId
    public String getDistrictNameByDistrictId(String districtId) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "select name from districts where id = '" + districtId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        String districtName = cursor.getString(0);
        cursor.close();
        database.close();
        return districtName;
    }

    //get provinceName by provinceId
    public String getProvinceNameByProvinceId(String provinceId) {
        String provinceName = "";
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            String selectQuery = "select name from provinces where id = '" + provinceId + "'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            if (cursor != null && cursor.moveToFirst()) {
                provinceName = cursor.getString(0);
                cursor.close();
            }
            database.close();
        } catch (Exception e) {

        }
        return provinceName;
    }
}
