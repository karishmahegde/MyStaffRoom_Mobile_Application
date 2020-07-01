package com.mystaffroom.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mystaffroom.views.ImageActivity;
import com.mystaffroom.R;
import com.mystaffroom.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        final Chat chat = mChat.get(position);
        String msg_type = chat.getType();
        if (msg_type.equals("text")){
            holder.image_message.setVisibility(View.GONE);
            holder.file_message.setVisibility(View.GONE);
            holder.show_message.setText(chat.getMessage());
        }else if (msg_type.equals("image")){
            holder.show_message.setVisibility(View.GONE);
            holder.file_message.setVisibility(View.GONE);
            Glide.with(holder.image_message.getContext()).load(chat.getMessage()).placeholder(R.drawable.ic_image_icon).into(holder.image_message);
        }else if (msg_type.equals("file")){
            holder.image_message.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
            holder.file_message.setVisibility(View.VISIBLE);
        }
        holder.image_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image_intent = new Intent(mContext, ImageActivity.class);
                image_intent.putExtra("image_url",chat.getMessage());
                mContext.startActivity(image_intent);
            }
        });
        holder.file_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        //Checking for last message
        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            }else{
                holder.txt_seen.setText("Delivered");
            }
        }else
        {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public ImageView image_message,file_message;

        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            image_message = itemView.findViewById(R.id.image_message);
            file_message = itemView.findViewById(R.id.file_message);
        }
    }


    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }
}
