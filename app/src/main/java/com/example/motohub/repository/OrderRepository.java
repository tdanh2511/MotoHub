package com.example.motohub.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private MotoHubDbHelper dbHelper;

    public OrderRepository(Context context) {
        dbHelper = new MotoHubDbHelper(context);
    }

    public long createOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("motorbike_id", order.getMotorbikeId());
        values.put("customer_name", order.getCustomerName());
        values.put("motorbike_name", order.getMotorbikeName());
        values.put("price", order.getPrice());
        values.put("order_date", order.getOrderDate());
        values.put("status", order.getStatus());
        values.put("phone", order.getPhone());
        values.put("address", order.getAddress());
        
        long result = db.insert("orders", null, values);
        db.close();
        return result;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY id DESC", null);
        
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setMotorbikeId(cursor.getInt(cursor.getColumnIndexOrThrow("motorbike_id")));
                order.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
                order.setMotorbikeName(cursor.getString(cursor.getColumnIndexOrThrow("motorbike_name")));
                order.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return orders;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(price) as total FROM orders WHERE status = 'completed'", null);
        
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        
        cursor.close();
        db.close();
        return total;
    }

    public int updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        
        int result = db.update("orders", values, "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return result;
    }

    public int deleteOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("orders", "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return result;
    }
}
