package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.CartItem;
import com.example.motohub.models.Motorbike;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private final MotoHubDbHelper dbHelper;

    public CartRepository(Context context) {
        this.dbHelper = new MotoHubDbHelper(context);
    }

    public boolean addToCart(int userId, int motorbikeId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Check if item already exists in cart
        Cursor cursor = db.query(
                MotoHubDbHelper.TABLE_CART,
                null,
                MotoHubDbHelper.COL_CART_USER_ID + " = ? AND " + MotoHubDbHelper.COL_CART_MOTORBIKE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(motorbikeId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            // Update quantity
            int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_CART_QUANTITY));
            ContentValues values = new ContentValues();
            values.put(MotoHubDbHelper.COL_CART_QUANTITY, currentQuantity + quantity);
            
            int result = db.update(
                    MotoHubDbHelper.TABLE_CART,
                    values,
                    MotoHubDbHelper.COL_CART_USER_ID + " = ? AND " + MotoHubDbHelper.COL_CART_MOTORBIKE_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
            );
            cursor.close();
            return result > 0;
        } else {
            // Insert new item
            ContentValues values = new ContentValues();
            values.put(MotoHubDbHelper.COL_CART_USER_ID, userId);
            values.put(MotoHubDbHelper.COL_CART_MOTORBIKE_ID, motorbikeId);
            values.put(MotoHubDbHelper.COL_CART_QUANTITY, quantity);
            
            long result = db.insert(MotoHubDbHelper.TABLE_CART, null, values);
            cursor.close();
            return result != -1;
        }
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT c.*, m.* FROM " + MotoHubDbHelper.TABLE_CART + " c " +
                "INNER JOIN " + MotoHubDbHelper.TABLE_MOTORBIKES + " m ON c." + 
                MotoHubDbHelper.COL_CART_MOTORBIKE_ID + " = m." + MotoHubDbHelper.COL_ID +
                " WHERE c." + MotoHubDbHelper.COL_CART_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_CART_ID)));
                item.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_CART_USER_ID)));
                item.setMotorbikeId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_CART_MOTORBIKE_ID)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_CART_QUANTITY)));

                Motorbike motorbike = new Motorbike();
                motorbike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ID)));
                motorbike.setName(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_NAME)));
                motorbike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_BRAND)));
                motorbike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PRICE)));
                motorbike.setImage(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_IMAGE)));
                motorbike.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_STOCK)));

                item.setMotorbike(motorbike);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }

    public boolean updateQuantity(int cartItemId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MotoHubDbHelper.COL_CART_QUANTITY, quantity);

        int result = db.update(
                MotoHubDbHelper.TABLE_CART,
                values,
                MotoHubDbHelper.COL_CART_ID + " = ?",
                new String[]{String.valueOf(cartItemId)}
        );

        return result > 0;
    }

    public boolean removeFromCart(int cartItemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(
                MotoHubDbHelper.TABLE_CART,
                MotoHubDbHelper.COL_CART_ID + " = ?",
                new String[]{String.valueOf(cartItemId)}
        );

        return result > 0;
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                MotoHubDbHelper.TABLE_CART,
                MotoHubDbHelper.COL_CART_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    public int getCartItemCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + MotoHubDbHelper.COL_CART_QUANTITY + ") FROM " + 
                MotoHubDbHelper.TABLE_CART + " WHERE " + MotoHubDbHelper.COL_CART_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public double getCartTotal(int userId) {
        List<CartItem> items = getCartItems(userId);
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }
}
