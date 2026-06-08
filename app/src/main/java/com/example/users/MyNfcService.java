package com.example.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import java.nio.charset.StandardCharsets;

public class MyNfcService extends HostApduService {

    // Сигнал што ќе го пратиме до NfcActivity дека е извршено скенирање
    public static final String NFC_TRANSMITTED_ACTION = "com.example.users.NFC_TRANSMITTED";

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        // Овде читаме од локалната меморија кој е најавен
        SharedPreferences sharedPref = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "Непознато ID");
        String name = sharedPref.getString("studentName", "Бојана Лазарова");

        // Структуриран JSON Payload точно според барањето во 3.2
        String jsonPayload = "{"
                + "\"student_id\":\"" + userId + "\","
                + "\"student_name\":\"" + name + "\","
                + "\"course_enrolled\":\"Развој на мобилни системи\""
                + "}";

        // Праќаме сигнал до NfcActivity за да се смени екранот во "Успешно"
        Intent intent = new Intent(NFC_TRANSMITTED_ACTION);
        sendBroadcast(intent);

        // Враќање на податоците во бајти кон телефонот на професорот
        return jsonPayload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void onDeactivated(int reason) {
        // Врската е прекината
    }
}