package com.almusand.aaber.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Notification;
import com.almusand.aaber.utils.Utilities;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.StoresViewHolder> {

    private List<Notification> notificationList = new ArrayList<>();

    private onItemClick onItemClick;
    private String lang = "en";

    public interface onItemClick {
        void onItemClick(Notification notification, TextView textView);
    }

    public NotificationsAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new StoresViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {

        Notification notification = notificationList.get(position);

        holder.bind(notification);

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNotificationContent;
        private TextView tvTime;
        private ImageView imgNotification;

        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNotificationContent = itemView.findViewById(R.id.tv_notification_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            imgNotification = itemView.findViewById(R.id.img_notification);

        }

        void bind(final Notification notification) {

            if (notification.getReadAt() != null) {
                Glide.with(itemView.getContext()).load(R.drawable.ic_notification_read).into(imgNotification);
            } else {
                Glide.with(itemView.getContext()).load(R.drawable.ic_notification_unread).into(imgNotification);

            }

            if (lang.equals("en")) {
                tvNotificationContent.setText(notification.getData().getMessageEn());
            } else {
                tvNotificationContent.setText(notification.getData().getMessageAr());
            }

            tvTime.setText(Utilities.parseDateToddMMyyyy(notification.getCreatedAt()));

            tvNotificationContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(notification, tvNotificationContent);

                }
            });
        }


    }


}
