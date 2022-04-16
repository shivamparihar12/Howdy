package com.example.howdy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.howdy.R;
import com.example.howdy.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    private int ITEM_SENT=1,ITEM_RECEIVED=2;
    private ArrayList<Message> messageArrayList;
    private Context context;

    public MessageAdapter(ArrayList<Message> messageArrayList, Context context) {
        this.messageArrayList = messageArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view= LayoutInflater.from(context).inflate(R.layout.sent_message,parent,false);
            return new sentViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.received_message,parent,false);
            return new receiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message= messageArrayList.get(position);
        if (holder.getClass()==sentViewHolder.class){
            ((sentViewHolder) holder).message.setText(message.getMessage());
            //((sentViewHolder) holder).sentTimeStamp.setText(message.getTimeStamp());
        }
        else {
            ((receiveViewHolder) holder).message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageArrayList.get(position).getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return ITEM_SENT;
        }
        else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class sentViewHolder extends RecyclerView.ViewHolder {
        TextView message,sentTimeStamp;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.sent_message);
            sentTimeStamp=itemView.findViewById(R.id.sent_time_stamp);
        }
    }

    public class receiveViewHolder extends RecyclerView.ViewHolder {
        TextView message,receivedTimeStamp;
        public receiveViewHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.received_message);
            receivedTimeStamp=itemView.findViewById(R.id.received_time_stamp);
        }
    }

}
