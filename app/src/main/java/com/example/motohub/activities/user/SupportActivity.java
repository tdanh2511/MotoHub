package com.example.motohub.activities.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;

public class SupportActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnCallNow, btnSendEmail, btnOpenMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        btnBack = findViewById(R.id.btnBack);
        btnCallNow = findViewById(R.id.btnCallNow);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnOpenMap = findViewById(R.id.btnOpenMap);

        btnBack.setOnClickListener(v -> finish());

        btnCallNow.setOnClickListener(v -> openDial());
        btnSendEmail.setOnClickListener(v -> openEmail());
        btnOpenMap.setOnClickListener(v -> openMap());
    }

    private void openDial() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:1900123456"));
        startActivity(intent);
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@motohub.vn"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Yeu cau tu van mua xe");
        intent.putExtra(Intent.EXTRA_TEXT, "Xin chao MotoHub, toi can duoc tu van mua xe.");
        startActivity(intent);
    }

    private void openMap() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=123 Tran Duy Hung, Cau Giay, Ha Noi");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=123+Tran+Duy+Hung,+Cau+Giay,+Ha+Noi"));
            startActivity(fallbackIntent);
        }
    }
}