package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;

public class FavoriteRepository {

    private final MotoHubDbHelper dbHelper;

    public FavoriteRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public boolean isFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM favorites WHERE user_id = ? AND motorbike_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public void addFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("motorbike_id", motorbikeId);
        db.insert("favorites", null, values);
        db.close();
    }

    public void removeFavorite(int userId, int motorbikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                "favorites",
                "user_id = ? AND motorbike_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
        );
        db.close();
    }

    public void toggleFavorite(int userId, int motorbikeId) {
        if (isFavorite(userId, motorbikeId)) {
            removeFavorite(userId, motorbikeId);
        } else {
            addFavorite(userId, motorbikeId);
        }
    }
}