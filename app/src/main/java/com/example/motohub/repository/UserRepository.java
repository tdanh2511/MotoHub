package com.example.motohub.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.User;

public class UserRepository {

    private final MotoHubDbHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public User login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + MotoHubDbHelper.TABLE_USERS +
                " WHERE " + MotoHubDbHelper.COL_USERNAME + "=? AND " +
                MotoHubDbHelper.COL_PASSWORD + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{username, password});

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PASSWORD)));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_FULLNAME)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ROLE)));
        }

        cursor.close();
        db.close();

        return user;
    }
}