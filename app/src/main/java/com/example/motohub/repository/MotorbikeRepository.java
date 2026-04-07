package com.example.motohub.repository;

import android.content.ContentValues;
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
            
            int stockIndex = cursor.getColumnIndex("stock");
            if (stockIndex != -1) {
                bike.setStock(cursor.getInt(stockIndex));
            }
            
            list.add(bike);
        }

        cursor.close();
        db.close();
        return list;
    }

    public Motorbike getMotorbikeById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM motorbikes WHERE id = ?", new String[]{String.valueOf(id)});

        Motorbike bike = null;
        if (cursor.moveToFirst()) {
            bike = new Motorbike();
            bike.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            bike.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            bike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
            bike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            bike.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
            bike.setFeatured(cursor.getInt(cursor.getColumnIndexOrThrow("featured")) == 1);
            
            int stockIndex = cursor.getColumnIndex("stock");
            if (stockIndex != -1) {
                bike.setStock(cursor.getInt(stockIndex));
            }
        }

        cursor.close();
        db.close();
        return bike;
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

    // lọc theo hãng
    public List<Motorbike> filterByBrand(String brand) {
        List<Motorbike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM motorbikes WHERE brand = ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{brand});

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

    // lọc theo giá
    public List<Motorbike> filterByPrice(double minPrice, double maxPrice) {
        List<Motorbike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM motorbikes WHERE price >= ? AND price <= ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(minPrice),
                String.valueOf(maxPrice)
        });

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

    public long addMotorbike(Motorbike bike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_NAME, bike.getName());
        values.put(MotoHubDbHelper.COL_BRAND, bike.getBrand());
        values.put(MotoHubDbHelper.COL_PRICE, bike.getPrice());
        values.put(MotoHubDbHelper.COL_IMAGE, bike.getImage());
        values.put(MotoHubDbHelper.COL_FEATURED, bike.isFeatured() ? 1 : 0);
        
        long result = db.insert(MotoHubDbHelper.TABLE_MOTORBIKES, null, values);
        db.close();
        return result;
    }

    public int updateMotorbike(Motorbike bike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_NAME, bike.getName());
        values.put(MotoHubDbHelper.COL_BRAND, bike.getBrand());
        values.put(MotoHubDbHelper.COL_PRICE, bike.getPrice());
        values.put(MotoHubDbHelper.COL_IMAGE, bike.getImage());
        values.put(MotoHubDbHelper.COL_FEATURED, bike.isFeatured() ? 1 : 0);
        
        int result = db.update(MotoHubDbHelper.TABLE_MOTORBIKES, values, 
                MotoHubDbHelper.COL_ID + "=?", 
                new String[]{String.valueOf(bike.getId())});
        db.close();
        return result;
    }

    public int deleteMotorbike(int motorbikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(MotoHubDbHelper.TABLE_MOTORBIKES, 
                MotoHubDbHelper.COL_ID + "=?", 
                new String[]{String.valueOf(motorbikeId)});
        db.close();
        return result;
    }

    public List<Motorbike> filterMotorbikes(String brand, double minPrice, double maxPrice) {
        List<Motorbike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql;
        Cursor cursor;

        if (brand == null || brand.isEmpty()) {
            sql = "SELECT * FROM motorbikes WHERE price >= ? AND price <= ? ORDER BY id DESC";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(minPrice),
                    String.valueOf(maxPrice)
            });
        } else {
            sql = "SELECT * FROM motorbikes WHERE brand = ? AND price >= ? AND price <= ? ORDER BY id DESC";
            cursor = db.rawQuery(sql, new String[]{
                    brand,
                    String.valueOf(minPrice),
                    String.valueOf(maxPrice)
            });
        }

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