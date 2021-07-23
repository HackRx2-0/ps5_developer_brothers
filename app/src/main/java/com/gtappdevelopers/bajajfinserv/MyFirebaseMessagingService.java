package com.gtappdevelopers.bajajfinserv;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
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

import java.util.ArrayList;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MSGTAG";
    Bitmap bitmap;
    TextToSpeech textToSpeech;

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String LANGUAGE_KEY = "language_key";
    public static final String NOTIFICATION_KEY = "notification_key";
    SharedPreferences sharedpreferences;
    String notification, language;
    boolean isSilent = false;
    ArrayList<String> notificationQueue = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationQueue.add(remoteMessage.getData().get("key1"));
        Log.e("SIZE","QUEUE SIZE IS "+notificationQueue.size()+notificationQueue);
//        for(int i=0; i<notificationQueue.size(); i++){
//            //sendNotification(notificationQueue.get(i),"body",remoteMessage);
//            speechToText(notificationQueue.get(i),"English");
//        }

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                isSilent = true;
                Log.e("MyApp", "Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                isSilent = true;
                Log.e("MyApp", "Vibrate mode");
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                isSilent = false;
                Log.e("MyApp", "Normal mode");
                break;
        }
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        notification = sharedpreferences.getString(NOTIFICATION_KEY, null);
        language = sharedpreferences.getString(LANGUAGE_KEY, null);
        Log.e("TAG", "PREFS VAL IS " + notification + "\n" + language);
        //Log.e(TAG, "From: " + remoteMessage.getFrom());
        String title = remoteMessage.getData().get("key1");
        String body = remoteMessage.getData().get("key2");
        //only title is of our use/

        Log.e("SIZE", "LIST SIZE IS " + notificationQueue.size()+notificationQueue);

        if (notification.equals("Enable Audio Notifications")) {
            if (language.equals("Hindi")) {
                translateText(FirebaseTranslateLanguage.EN, FirebaseTranslateLanguage.HI, title, body, remoteMessage, notification);
            } else {
                if (!isSilent) {
                    speechToText(title, "English");
                }
                sendNotification(title, body, remoteMessage);
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                //.setContentText(body)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        if (remoteMessage.getData().size() > 0) {
            //Log.e("TAG", "SIZE IS " + remoteMessage.getData().size());
            //Log.e("TAG", "REMOTE MESSAGE DATA IS " + remoteMessage.getData().toString() + "\n" + remoteMessage.toString() + "\n");

            for (String key : remoteMessage.getData().keySet()) {
               // Log.e("TAG", "Key " + key + " " + remoteMessage.getData().get(key));
            }
            String key1 = remoteMessage.getData().get("key1");
            String key2 = remoteMessage.getData().get("key2");

            //Log.e("TAG", "DATA IN KEY 1 and KEY 2 is " + key1 + "\n" + key2);
        }
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
