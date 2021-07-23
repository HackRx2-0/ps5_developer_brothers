package com.gtappdevelopers.bajajfinserv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    private ArrayList<String> notifications;
    private Context context;

    public NotificationRVAdapter(ArrayList<String> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRVAdapter.ViewHolder holder, int position) {
        holder.notificationTV.setText(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView notificationTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTV = itemView.findViewById(R.id.idTVNotification);
        }
    }
}
