package com.example.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NfcActivity extends AppCompatActivity {

    private TextView tvHeaderStudentName, tvHeaderStudentId, tvNfcStatusTitle, tvNfcStatusDetails;
    private View statusIndicator;

    private final BroadcastReceiver nfcTransmittedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(() -> {
                tvNfcStatusTitle.setText("✅ Присуството е евидентирано!");
                tvNfcStatusDetails.setText("Вашите податоци беа испратени до наставникот.");

                GradientDrawable circle = new GradientDrawable();
                circle.setShape(GradientDrawable.OVAL);
                circle.setColor(0xFF4CAF50);
                statusIndicator.setBackground(circle);

                Toast.makeText(NfcActivity.this, "✅ НФЦ пренос успешен!", Toast.LENGTH_SHORT).show();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        tvHeaderStudentName = findViewById(R.id.tvHeaderStudentName);
        tvHeaderStudentId = findViewById(R.id.tvHeaderStudentId);
        tvNfcStatusTitle = findViewById(R.id.tvNfcStatusTitle);
        tvNfcStatusDetails = findViewById(R.id.tvNfcStatusDetails);
        statusIndicator = findViewById(R.id.statusIndicator);
        Button btnStudentLogout = findViewById(R.id.btnStudentLogout);

        // Сив круг = чека
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFF9E9E9E);
        statusIndicator.setBackground(circle);

        SharedPreferences sharedPref = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        String studentName = sharedPref.getString("studentName", "Непознат Студент");
        String userId = sharedPref.getString("userId", "Нема ID");

        tvHeaderStudentName.setText(studentName);
        tvHeaderStudentId.setText("ID: " + userId);
        tvNfcStatusTitle.setText("Подготвен за скенирање");
        tvNfcStatusDetails.setText("Доближете го телефонот до телефонот на наставникот.");

        btnStudentLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(NfcActivity.this, "Успешно се одјавивте", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NfcActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MyNfcService.NFC_TRANSMITTED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(nfcTransmittedReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(nfcTransmittedReceiver);
        } catch (IllegalArgumentException ignored) {}
    }
}