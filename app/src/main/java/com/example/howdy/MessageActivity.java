package com.example.howdy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.howdy.adapter.MessageAdapter;
import com.example.howdy.databinding.ActivityMessageBinding;
import com.example.howdy.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String senderID, receiverID, senderRoom, receiverRoom;
    private final String TAG="MessageActivity";
    private ArrayList<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderID = mAuth.getCurrentUser().getUid();
        Log.d(TAG,senderID);
        receiverID = getIntent().getStringExtra("receiverID");
        Log.d(TAG,receiverID);
        String pfp = getIntent().getStringExtra("pfp");
        String username = getIntent().getStringExtra("username");

        senderRoom = senderID + receiverID;
        receiverRoom = receiverID + senderID;

        Picasso.get().load(pfp).placeholder(R.drawable.ic_baseline_person_outline_24).into(binding.pfp);
        binding.userName.setText(username);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class));
            }
        });

        messageArrayList = new ArrayList<>();
        final MessageAdapter messageAdapter = new MessageAdapter(messageArrayList, this);
        binding.chatRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Message message=snapshot1.getValue(Message.class);
                    messageArrayList.add(message);
                    Log.d(TAG,message.getMessage());
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.message.getText().toString().isEmpty()){
                    Message message = new Message(senderID, binding.message.getText().toString(), new Date().getTime());
                    binding.message.setText("");
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(message)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Log.d(TAG,"messageSent");
                                    database.getReference()
                                            .child("chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(message)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

    }
}