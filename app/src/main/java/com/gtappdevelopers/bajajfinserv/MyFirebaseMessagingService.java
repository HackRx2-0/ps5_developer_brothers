package com.gtappdevelopers.bajajfinserv;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MSGTAG";
    private DBHandler dbHandler;
    TextToSpeech textToSpeech;

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String LANGUAGE_KEY = "language_key";
    public static final String NOTIFICATION_KEY = "notification_key";
    SharedPreferences sharedpreferences;
    String notification, language;
    boolean isSilent = false;
    int notificationPriority = 0;
    ArrayList<String> notificationQueue = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                isSilent = true;
                //silent mode.
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                isSilent = true;
                //vibrate mode
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                isSilent = false;
                //general mode.
                break;
        }
        dbHandler = new DBHandler(this);
        notificationQueue.add(remoteMessage.getData().get("key1"));

        if (isSilent) {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(notificationQueue);
            editor.putString("notificationList", json);
            editor.apply();
        }

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        notification = sharedpreferences.getString(NOTIFICATION_KEY, null);
        language = sharedpreferences.getString(LANGUAGE_KEY, null);

        String title = remoteMessage.getData().get("key1");
        String body = remoteMessage.getData().get("key2");

        if (notification.equals("Enable Audio Notifications")) {
            if (language.equals("Hindi")) {
                translateText(FirebaseTranslateLanguage.EN, FirebaseTranslateLanguage.HI, title, body, remoteMessage, notification);
            } else {
                if (!isSilent) {
                    speechToText(title, "English");
                }
                sendNotification(title, body, remoteMessage);
                notificationPriority++;
            }
        } else {
            if (language.equals("Hindi")) {
                translateText(FirebaseTranslateLanguage.EN, FirebaseTranslateLanguage.HI, title, body, remoteMessage, notification);
            } else {
                sendNotification(title, body, remoteMessage);
            }
        }
    }

    private void sendNotification(String title, String body, RemoteMessage remoteMessage) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm");
        String currentDateandTime = sdf.format(new Date());
        dbHandler.addNewNotification(title, currentDateandTime);
        Notification notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bajlogo)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        SystemClock.sleep(2000);
        notificationManager.notify(notificationPriority /* ID of notification */, notificationBuilder);

    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String title, String body, RemoteMessage remoteMessage, String notification) {

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(title).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (notification.equals("Enable Audio Notifications")) {
                            if (!isSilent) {
                                speechToText(s, "Hindi");
                            }
                        }
                        sendNotification(s, body, remoteMessage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "ERROR = " + e.getMessage());
                        //Toast.makeText(MainActivity.this, "error = " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "ERROR = " + e.getMessage());
                // Toast.makeText(MainActivity.this, "1 stmtd error =" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void speechToText(String message, String language) {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    if (language.equals("Hindi")) {
                        textToSpeech.setLanguage(new Locale("hi", "IN"));
                    } else {
                        textToSpeech.setLanguage(Locale.UK);
                    }

                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }
}
