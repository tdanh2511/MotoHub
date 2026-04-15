package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.Brand;

import java.util.ArrayList;
import java.util.List;

public class BrandRepository {
    private final MotoHubDbHelper dbHelper;

    public BrandRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public List<Brand> getAllBrands() {
        List<Brand> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM brands", null);

        while (cursor.moveToNext()) {
            Brand b = new Brand();
            b.setId(cursor.getInt(0));
            b.setName(cursor.getString(1));
            list.add(b);
        }

        cursor.close();
        db.close();
        return list;
    }

    public long addBrand(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);

        long result = db.insert("brands", null, values);
        db.close();
        return result;
    }

    public int updateBrand(int id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);

        int result = db.update("brands", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public int deleteBrand(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("brands", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
}
