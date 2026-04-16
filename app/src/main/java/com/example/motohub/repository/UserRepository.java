package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.User;

import java.util.ArrayList;
import java.util.List;

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
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        }

        cursor.close();
        db.close();

        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + MotoHubDbHelper.TABLE_USERS +
                " WHERE " + MotoHubDbHelper.COL_USER_ID + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PASSWORD)));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_FULLNAME)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ROLE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        }

        cursor.close();
        db.close();

        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.rawQuery("SELECT * FROM " + MotoHubDbHelper.TABLE_USERS + " ORDER BY id DESC", null);
        
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PASSWORD)));
                user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_FULLNAME)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ROLE)));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                users.add(user);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return users;
    }

    public long addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_USERNAME, user.getUsername());
        values.put(MotoHubDbHelper.COL_PASSWORD, user.getPassword());
        values.put(MotoHubDbHelper.COL_FULLNAME, user.getFullname());
        values.put(MotoHubDbHelper.COL_ROLE, user.getRole());
        values.put(MotoHubDbHelper.COL_PHONE, user.getPhone());
        values.put(MotoHubDbHelper.COL_EMAIL, user.getEmail());
        values.put(MotoHubDbHelper.COL_ADDRESS, user.getAddress());
        
        long result = db.insert(MotoHubDbHelper.TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_PASSWORD, user.getPassword());
        values.put(MotoHubDbHelper.COL_FULLNAME, user.getFullname());
        values.put(MotoHubDbHelper.COL_ROLE, user.getRole());
        values.put(MotoHubDbHelper.COL_PHONE, user.getPhone());
        values.put(MotoHubDbHelper.COL_EMAIL, user.getEmail());
        values.put(MotoHubDbHelper.COL_ADDRESS, user.getAddress());
        
        int result = db.update(MotoHubDbHelper.TABLE_USERS, values, 
                MotoHubDbHelper.COL_USER_ID + "=?", 
                new String[]{String.valueOf(user.getId())});
        db.close();
        return result;
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(MotoHubDbHelper.TABLE_USERS, 
                MotoHubDbHelper.COL_USER_ID + "=?", 
                new String[]{String.valueOf(userId)});
        db.close();
        return result;
    }

    public void updateUserInfo(int userId, String fullname, String phone, String email, String address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "UPDATE " + MotoHubDbHelper.TABLE_USERS + " SET "
                + MotoHubDbHelper.COL_FULLNAME + "=?, "
                + MotoHubDbHelper.COL_PHONE + "=?, "
                + MotoHubDbHelper.COL_EMAIL + "=?, "
                + MotoHubDbHelper.COL_ADDRESS + "=? "
                + "WHERE " + MotoHubDbHelper.COL_USER_ID + "=?";

        db.execSQL(sql, new Object[]{fullname, phone, email, address, userId});
        db.close();
    }

    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int result = db.update("users", values, "id=?", new String[]{String.valueOf(userId)});

        return result > 0;
    }
}
