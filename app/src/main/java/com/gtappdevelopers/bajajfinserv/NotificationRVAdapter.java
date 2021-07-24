package com.gtappdevelopers.bajajfinserv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    private final ArrayList<DataModal> notifications;
    private final Context context;

    public NotificationRVAdapter(ArrayList<DataModal> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationRVAdapter.ViewHolder holder, int position) {
        holder.notificationTV.setText(notifications.get(position).getMessage());
        holder.dateTV.setText("Received at : " + notifications.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView notificationTV;
        private final TextView dateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTV = itemView.findViewById(R.id.idTVNotificationMessage);
            dateTV = itemView.findViewById(R.id.idTVDate);
        }
    }
}
