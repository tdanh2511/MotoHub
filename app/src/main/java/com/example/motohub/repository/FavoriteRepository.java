package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.Motorbike;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {

    private final MotoHubDbHelper dbHelper;

    public FavoriteRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public boolean isFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + MotoHubDbHelper.TABLE_FAVORITES + " WHERE user_id = ? AND motorbike_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean addFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_FAVORITE_USER_ID, userId);
        values.put(MotoHubDbHelper.COL_FAVORITE_MOTORBIKE_ID, motorbikeId);
        long result = db.insert(MotoHubDbHelper.TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public boolean removeFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(
                MotoHubDbHelper.TABLE_FAVORITES,
                MotoHubDbHelper.COL_FAVORITE_USER_ID + " = ? AND " + MotoHubDbHelper.COL_FAVORITE_MOTORBIKE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
        );
        return result > 0;
    }

    public boolean toggleFavorite(int userId, int motorbikeId) {
        if (isFavorite(userId, motorbikeId)) {
            return removeFavorite(userId, motorbikeId);
        } else {
            return addFavorite(userId, motorbikeId);
        }
    }

    public List<Motorbike> getFavoriteMotorbikes(int userId) {
        List<Motorbike> favorites = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT m.* FROM " + MotoHubDbHelper.TABLE_MOTORBIKES + " m " +
                "INNER JOIN " + MotoHubDbHelper.TABLE_FAVORITES + " f ON m." + 
                MotoHubDbHelper.COL_ID + " = f." + MotoHubDbHelper.COL_FAVORITE_MOTORBIKE_ID +
                " WHERE f." + MotoHubDbHelper.COL_FAVORITE_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Motorbike motorbike = new Motorbike();
                motorbike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ID)));
                motorbike.setName(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_NAME)));
                motorbike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_BRAND)));
                motorbike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PRICE)));
                motorbike.setImage(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_IMAGE)));
                motorbike.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_STOCK)));
                
                favorites.add(motorbike);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return favorites;
    }
}