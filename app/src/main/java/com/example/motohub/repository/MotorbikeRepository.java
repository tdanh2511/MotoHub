package com.example.motohub.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.Motorbike;

import java.util.ArrayList;
import java.util.List;

public class MotorbikeRepository {

    private final MotoHubDbHelper dbHelper;

    public MotorbikeRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public List<Motorbike> getAllMotorbikes() {
        List<Motorbike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM motorbikes ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            Motorbike bike = new Motorbike();
            bike.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            bike.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            bike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
            bike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            bike.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
            bike.setFeatured(cursor.getInt(cursor.getColumnIndexOrThrow("featured")) == 1);
            list.add(bike);
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Motorbike> searchMotorbikes(String keyword) {
        List<Motorbike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM motorbikes WHERE name LIKE ? OR brand LIKE ? ORDER BY id DESC";
        String searchValue = "%" + keyword + "%";

        Cursor cursor = db.rawQuery(sql, new String[]{searchValue, searchValue});

        while (cursor.moveToNext()) {
            Motorbike bike = new Motorbike();
            bike.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            bike.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            bike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
            bike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            bike.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
            bike.setFeatured(cursor.getInt(cursor.getColumnIndexOrThrow("featured")) == 1);
            list.add(bike);
        }

        cursor.close();
        db.close();
        return list;
    }
}