package com.example.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class MyNfcService extends HostApduService {

    private static final String TAG = "MyNfcService";
    public static final String NFC_TRANSMITTED_ACTION = "com.example.users.NFC_TRANSMITTED";

    // AID мора да се совпаѓа со apdu_service.xml и со Teacher App
    private static final byte[] SELECT_AID = {
            (byte) 0xF0, (byte) 0x01, (byte) 0x02, (byte) 0x03,
            (byte) 0x04, (byte) 0x05, (byte) 0x06
    };

    private static final byte[] SELECT_OK = {(byte) 0x90, (byte) 0x00};

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d(TAG, "Received APDU: " + bytesToHex(commandApdu));

        if (commandApdu == null || commandApdu.length < 4) {
            return SELECT_OK;
        }

        int ins = commandApdu[1] & 0xFF;

        // SELECT AID команда
        if (ins == 0xA4) {
            if (isSelectAid(commandApdu)) {
                Log.d(TAG, "SELECT AID ok");
                return SELECT_OK;
            }
            return new byte[]{(byte) 0x6A, (byte) 0x82}; // File not found
        }

        // GET DATA команда (0xCA) - Teacher App бара ги податоците
        if (ins == 0xCA || ins == 0xB0) {
            return buildStudentDataResponse();
        }

        return SELECT_OK;
    }

    private boolean isSelectAid(byte[] apdu) {
        if (apdu.length < 5 + SELECT_AID.length) return false;
        if (apdu[2] != 0x04) return false;
        int aidLength = apdu[4] & 0xFF;
        if (aidLength != SELECT_AID.length) return false;
        for (int i = 0; i < SELECT_AID.length; i++) {
            if (apdu[5 + i] != SELECT_AID[i]) return false;
        }
        return true;
    }

    private byte[] buildStudentDataResponse() {
        SharedPreferences sharedPref = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        String name = sharedPref.getString("studentName", "");

        if (userId.isEmpty()) {
            Log.e(TAG, "No userId in SharedPreferences!");
            return new byte[]{(byte) 0x6A, (byte) 0x82};
        }

        String json = "{\"userId\":\"" + userId + "\",\"studentName\":\"" + name + "\"}";
        byte[] jsonBytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Response = JSON bytes + 90 00
        byte[] response = new byte[jsonBytes.length + 2];
        System.arraycopy(jsonBytes, 0, response, 0, jsonBytes.length);
        response[jsonBytes.length] = (byte) 0x90;
        response[jsonBytes.length + 1] = (byte) 0x00;

        Log.d(TAG, "Sending student data: " + json);

        // Broadcast до UI
        Intent intent = new Intent(NFC_TRANSMITTED_ACTION);
        intent.putExtra("studentName", name);
        sendBroadcast(intent);

        return response;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(TAG, "NFC deactivated, reason: " + reason);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X ", b));
        return sb.toString();
    }
}