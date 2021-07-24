package com.gtappdevelopers.bajajfinserv;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        DBHandler dbHandler = new DBHandler(this);
        ArrayList<DataModal> notificationList = new ArrayList<>();
        RecyclerView notificationRV = findViewById(R.id.idRVNotifications);
        notificationList = dbHandler.readNotifications();
        NotificationRVAdapter notificationRVAdapter = new NotificationRVAdapter(notificationList, this);
        notificationRV.setLayoutManager(new LinearLayoutManager(this));
        notificationRV.setAdapter(notificationRVAdapter);

    }
}