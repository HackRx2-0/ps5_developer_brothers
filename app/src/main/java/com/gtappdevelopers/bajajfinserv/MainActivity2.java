package com.gtappdevelopers.bajajfinserv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class MainActivity2 extends AppCompatActivity {

    /*
    dampXgPvQ5WtFtYEs6w5Dd:APA91bGtnTeCm4NlrFo8KLSiA5kgMQMYw4WLEfJLQv0gAw_Xoa9FnwIngI4IR_YBn1rVrvyBLeZkJJ4AS3evO0CFB_9IHpiEET462QBucOyH_euKMaJKrrEOuPXX2sXJNLA8fSfh76Gz
     */
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String LANGUAGE_KEY = "language_key";
    public static final String NOTIFICATION_KEY = "notification_key";
    SharedPreferences sharedpreferences;


    String language, notification;
    Button speakBtn;
    RadioButton enableNot, disableNot, englishRB, hindiRB, languageRB, notificationRB;
    RadioGroup languageRG, notificationRG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        speakBtn = findViewById(R.id.idBtnSpeak);
        enableNot = findViewById(R.id.rbEnable);
        disableNot = findViewById(R.id.rbDisable);
        englishRB = findViewById(R.id.rbEnglish);
        hindiRB = findViewById(R.id.rbHindi);
        languageRG = findViewById(R.id.idRGLanguage);
        notificationRG = findViewById(R.id.idRGNotification);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        notification = sharedpreferences.getString(NOTIFICATION_KEY, null);
        language = sharedpreferences.getString(LANGUAGE_KEY, null);

        Log.e("TAG","NOTIFY = "+language+"\n\n"+notification);

        if (notification != null) {
            if (notification.equals("Enable Audio Notifications")) {
                enableNot.setChecked(true);
            } else {
                disableNot.setChecked(true);
            }
        }


        if (language != null) {
            if (language.equals("English")) {
                englishRB.setChecked(true);
            } else {
                hindiRB.setChecked(true);
            }
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        //Redmis Note 7s token is
                       // d_DB-rUoSHSUV2AACIZM2a:APA91bFqVyKKXRt63lhY3ZaGn1SziYU2DWS5vfE2t7CH7CxypvBpZDOSuAjOUdW3QHmKlEdMpx4al4M4p0kPNhDL5XU_gaqtuQuCL5lmEhWEcMiBeZTyD6rAC0gQMm3FfAbobRaoWyri

                        //Redmi 4 token is below
                       // eND5L-4KRuaqV2-MSF1cZV:APA91bFp24DLu3jCD4iEIb39G5Q1HW2kIbE8GHidAcnX3SPKQ31RofdULIYxTn3LfBPdpoDIWINCF1DHnWiVNPrl_Djzjr-1Xyq2YcY8rpIbDmT4iX5bgW2PkqWvWO_n_Nnuwk5XGAQh

                        // Log and toast
                        Log.e("TAG", "TOKEN IS " + token);
                        //Toast.makeText(MainActivity2.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedLanguageID = languageRG.getCheckedRadioButtonId();
                languageRB = findViewById(selectedLanguageID);

                int selectedNotificationID = notificationRG.getCheckedRadioButtonId();
                notificationRB = findViewById(selectedNotificationID);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(LANGUAGE_KEY, languageRB.getText().toString());
                editor.putString(NOTIFICATION_KEY, notificationRB.getText().toString());

                editor.apply();

                Toast.makeText(MainActivity2.this, "Settings Saved" , Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void translateText(int fromLanguageCode, int toLanguageCode, String sourceText) {

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
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        //Toast.makeText(MainActivity2.this, "data is "+s, Toast.LENGTH_SHORT).show();
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


}