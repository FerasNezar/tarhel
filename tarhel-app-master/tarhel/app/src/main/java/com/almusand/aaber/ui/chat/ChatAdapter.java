package com.almusand.aaber.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.MessageModel;
import com.almusand.aaber.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;
    private final onItemClick onItemClick;

    private List<MessageModel> mMessages;
    private Context mContext;
    private User currentUser;

    public ChatAdapter(Context context, User currentUser, onItemClick onItemClick) {
        mContext = context;
        this.onItemClick = onItemClick;
        this.currentUser = currentUser;
    }

    public interface onItemClick {
        void onItemClick(MessageModel messageModel, ImageView imageView);
    }


    public void setList(List<MessageModel> data) {
        this.mMessages = data;
        notifyItemInserted(mMessages.size() - 1);
        notifyItemRangeChanged(0, mMessages.size());

    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel item = mMessages.get(position);

        if (item.getUserId().equals(currentUser.getId())) return MY_MESSAGE;
        else return OTHER_MESSAGE;


    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageHolder messageHolder;
        if (viewType == MY_MESSAGE) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message, parent, false);
            messageHolder = new MessageHolder(row);

        } else {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_message, parent, false);
            messageHolder = new MessageHolder(row);

        }
        return messageHolder;


    }

    public void add(MessageModel message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
        notifyItemRangeChanged(mMessages.size() - 1, mMessages.size());
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, final int position) {
        MessageModel chatMessage = mMessages.get(position);

        holder.bind(chatMessage, onItemClick);


    }

    class MessageHolder extends RecyclerView.ViewHolder {

        TextView tvMessage, tvTime;
        ImageView ivImage;
        private ImageView imgImage;
        private CardView cardviewMessage;
        private TextView tvTimeImage;
        private VoicePlayerView audioPlayer;

        MessageHolder(View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivImage = itemView.findViewById(R.id.iv_image);
            imgImage = itemView.findViewById(R.id.img_image);
            cardviewMessage = itemView.findViewById(R.id.cardview_message);
            tvTimeImage = itemView.findViewById(R.id.tv_time_image);
            audioPlayer = itemView.findViewById(R.id.audio_player);

            cardviewMessage.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.transparant));
        }

        void bind(MessageModel chatMessage, onItemClick onItemClick) {

            tvTimeImage.setText(ConvertTimeStampToDate(chatMessage.getCreatedAt()));

            if (chatMessage.getType().equals("text")) {
                imgImage.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.GONE);
                tvTimeImage.setVisibility(View.VISIBLE);
                tvMessage.setText(chatMessage.getContent());

            } else if (chatMessage.getType().equals("image")) {
                tvMessage.setVisibility(View.GONE);
                imgImage.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.GONE);
                tvTimeImage.setVisibility(View.VISIBLE);
                tvTimeImage.setText(ConvertTimeStampToDate(chatMessage.getCreatedAt()));

                Glide.with(mContext)
                        .load(chatMessage.getContent())
                        .apply(new RequestOptions().error(R.drawable.aaber_logo))
                        .into(imgImage);

            }else if (chatMessage.getType().equals("voice")) {
                tvMessage.setVisibility(View.GONE);
                imgImage.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                tvTimeImage.setVisibility(View.VISIBLE);
                tvTimeImage.setText(ConvertTimeStampToDate(chatMessage.getCreatedAt()));

                audioPlayer.setVisibility(View.VISIBLE);
                audioPlayer.setAudio(chatMessage.getContent());
                audioPlayer.requestFocus();

            }

//            if (chatMessage.getUserId().equals(currentUser.getId())) {
//                Glide.with(mContext)
//                        .load(currentUser.getAvatar())
//                        .apply(new RequestOptions().error(R.drawable.ic_default_user))
//                        .into(ivImage);
//            } else {
//                Glide.with(mContext)
//                        .load(store.getAvatar())
//                        .apply(new RequestOptions().error(R.drawable.ic_default_user))
//                        .into(ivImage);
//            }


            imgImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(chatMessage, imgImage);
                }
            });

        }

    }

    public String ConvertTimeStampToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        output.setTimeZone(TimeZone.getDefault());

        Date d = null;
        String formattedTime = null;
        try {
            d = sdf.parse(time);
            formattedTime = output.format(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

}