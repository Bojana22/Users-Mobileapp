package com.example.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NfcActivity extends AppCompatActivity {

    private TextView tvHeaderStudentName, tvHeaderStudentId, tvNfcStatusTitle, tvNfcStatusDetails;
    private View statusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        // Врзување на елементите од XML
        tvHeaderStudentName = findViewById(R.id.tvHeaderStudentName);
        tvHeaderStudentId = findViewById(R.id.tvHeaderStudentId);
        tvNfcStatusTitle = findViewById(R.id.tvNfcStatusTitle);
        tvNfcStatusDetails = findViewById(R.id.tvNfcStatusDetails);
        statusIndicator = findViewById(R.id.statusIndicator);
        Button btnStudentLogout = findViewById(R.id.btnStudentLogout);

        // Наместете го индикаторот за статус да биде кружен
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(getIntent().getBooleanExtra("isPresent", false) ? 0xFF4CAF50 : 0xFFE57373); // Зелено или Црвено
        statusIndicator.setBackground(circle);

        // Читање на податоците за најавениот студент од SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        String studentName = sharedPref.getString("studentName", "Непознат Студент");
        String userId = sharedPref.getString("userId", "Нема ID");

        // Поставување на податоците во Хедерот
        tvHeaderStudentName.setText(studentName);
        tvHeaderStudentId.setText("ID: " + userId);

        // Логика за одјавување (Logout)
        btnStudentLogout.setOnClickListener(v -> {
            // Бришење на зачуваната сесија
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(NfcActivity.this, "Успешно се одјавивте", Toast.LENGTH_SHORT).show();

            // Враќање назад на Login екранот (MainActivity)
            Intent intent = new Intent(NfcActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // ТУКА ПОНАТАМУ: Твојот постоечки код за NFC скенирање (BroadcastReceiver, итн.)
    }

    // Оваа метода можеш да ја повикаш од твојот NFC код кога успешно ќе се скенира картичка/телефон
    public void setStudentPresent(String classroomName) {
        tvNfcStatusTitle.setText("Успешно евидентирано присуство!");
        tvNfcStatusDetails.setText("Вашето присуство е зачувано во системот.");

        // Промена на индикаторот во зелена боја
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFF4CAF50); // Зелена боја
        statusIndicator.setBackground(circle);

        TextView tvCurrentClassroom = findViewById(R.id.tvCurrentClassroom);
        tvCurrentClassroom.setText(classroomName);
    }
}