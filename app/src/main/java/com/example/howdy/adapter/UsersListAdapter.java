package com.example.howdy.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.howdy.MainActivity;
import com.example.howdy.MessageActivity;
import com.example.howdy.R;
import com.example.howdy.model.Message;
import com.example.howdy.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    Context context;
    ArrayList<Users> usersArrayList;

    public UsersListAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

        if (!users.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.username.setText(users.getPhoneNo());
            Picasso.get().load(users.getUserProfileImage()).placeholder(R.drawable.ic_baseline_person_outline_24)
                    .into(holder.userImage);
            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(FirebaseAuth.getInstance().getUid()+users.getUserID())
                    .orderByChild("timeStamp")
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()){
                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                    holder.lastMessage.setText(snapshot1.child("message").getValue(String.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    //intent.putExtra("receiverID", users.getUserID());
                    intent.putExtra("receiverID",users.getUserID());
                    Log.d("UserListAdapter",users.getUserID());
                    intent.putExtra("pfp", users.getProfilePicture());
                    intent.putExtra("username", users.getPhoneNo());
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        //if (usersArrayList.size()>1) return usersArrayList.size()-1;
        //else
            return usersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView username, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.userLastMessage);
        }
    }
}
