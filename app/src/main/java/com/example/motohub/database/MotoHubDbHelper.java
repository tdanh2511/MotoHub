package com.example.motohub.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MotoHubDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "motohub.db";
    public static final int DB_VERSION = 2;

    // Bang motorbikes
    public static final String TABLE_MOTORBIKES = "motorbikes";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_BRAND = "brand";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE = "image";
    public static final String COL_FEATURED = "featured";

    // Bang users
    public static final String TABLE_USERS = "users";

    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FULLNAME = "fullname";
    public static final String COL_ROLE = "role";
    public MotoHubDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Motorbikes table
        String createMotorbikeTable = "CREATE TABLE " + TABLE_MOTORBIKES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_BRAND + " TEXT NOT NULL, "
                + COL_PRICE + " REAL NOT NULL, "
                + COL_IMAGE + " TEXT, "
                + COL_FEATURED + " INTEGER DEFAULT 0"
                + ");";

        db.execSQL(createMotorbikeTable);

        // Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_FULLNAME + " TEXT, "
                + COL_ROLE + " TEXT NOT NULL"
                + ");";

        db.execSQL(createUsersTable);

        // Seed data
        seedMotorbikes(db);
        seedUsers(db);
    }

    private void seedMotorbikes(SQLiteDatabase db) {
        db.execSQL("INSERT INTO motorbikes(name, brand, price, image, featured) VALUES" +
                "('Honda SH 160i', 'Honda', 92000000, 'ic_bike_placeholder', 1)");
        db.execSQL("INSERT INTO motorbikes(name, brand, price, image, featured) VALUES" +
                "('Yamaha Exciter 155', 'Yamaha', 51000000, 'ic_bike_placeholder', 1)");
        db.execSQL("INSERT INTO motorbikes(name, brand, price, image, featured) VALUES" +
                "('Honda Vision', 'Honda', 36000000, 'ic_bike_placeholder', 1)");
        db.execSQL("INSERT INTO motorbikes(name, brand, price, image, featured) VALUES" +
                "('Air Blade 160', 'Honda', 56000000, 'ic_bike_placeholder', 1)");
        db.execSQL("INSERT INTO motorbikes(name, brand, price, image, featured) VALUES" +
                "('Yamaha Grande', 'Yamaha', 47000000, 'ic_bike_placeholder', 1)");
    }


    private void seedUsers(SQLiteDatabase db) {
        db.execSQL("INSERT INTO users(username, password, fullname, role) VALUES" +
                "('admin', '123456', 'Quản trị viên', 'admin')");

        db.execSQL("INSERT INTO users(username, password, fullname, role) VALUES" +
                "('user1', '123456', 'Người dùng 1', 'user')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTORBIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}