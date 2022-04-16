package com.example.howdy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.howdy.adapter.StoriesAdapter;
import com.example.howdy.adapter.UsersListAdapter;
import com.example.howdy.databinding.FragmentChatBinding;
import com.example.howdy.model.StoryModel;
import com.example.howdy.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ArrayList<Users> usersArrayList;
    private ArrayList<StoryModel> storyModelArrayList;
    private FirebaseDatabase firebaseDatabase;
    private final String TAG = "ChatFragment";
    MainActivityViewModel viewModel;
    ArrayList<Users> arrayList;
    String imageUri;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        arrayList = viewModel.getContactNoList().getValue();

        binding.addYourStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CameraActivity.class));
            }
        });

        binding.progress.setVisibility(View.VISIBLE);
        storyModelArrayList = new ArrayList<>();
        usersArrayList = new ArrayList<>();

        //adapter for stories
        StoriesAdapter storiesAdapter = new StoriesAdapter(getActivity(), storyModelArrayList);
        binding.storiesRecyclerview.setAdapter(storiesAdapter);
        LinearLayoutManager linearLayoutManagerStories = new LinearLayoutManager(getContext());
        binding.storiesRecyclerview.setLayoutManager(linearLayoutManagerStories);


        //adapter for user list
        UsersListAdapter usersListAdapter = new UsersListAdapter(getActivity(), usersArrayList);
        binding.userListRecyclerView.setAdapter(usersListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.userListRecyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserID(dataSnapshot.getKey());

                    if (arrayList!=null){
                        for (int i = 0; i < arrayList.size(); i++) {
                            Users fromCursor = arrayList.get(i);
                            if (users.getPhoneNo().equals(fromCursor.getPhoneNo())) {
                                users.setUsername(fromCursor.getUsername());
                                usersArrayList.add(users);
                                Log.d("userlistAdded", users.getUserID() + " " + users.getPhoneNo());
                            }
                        }
                        Log.d("userlist", users.getUserID() + " " + users.getPhoneNo());
                    }

                }
                usersListAdapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progress.setVisibility(View.GONE);
            }
        });

        checkIfUserHasStory();

    }


    public void getStories() {
        firebaseDatabase.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    StoryModel storyModel = snapshot1.getValue(StoryModel.class);
                    storyModel.setUserID(snapshot1.getKey());
                    for (int i = 0; i < arrayList.size(); i++) {
                        Users fromCursor = arrayList.get(i);
                        if (storyModel.getUserContact().equals(fromCursor.getPhoneNo())) {
                            storyModel.setUserName(fromCursor.getUsername());
                            storyModelArrayList.add(storyModel);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addStory() {
        binding.addYourStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    imagePicker();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 102);
                }

            }
        });
    }

    private void checkIfUserHasStory(){
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("todayStory")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            String uri=snapshot1.getValue(String.class);
                            if (uri!=null){
                                binding.userStoryCardView.setStrokeColor(R.color.appLightBLue);
                                binding.userStoryCardView.setStrokeWidth(3);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void imagePicker() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 100);

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Choose Image"),990);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && data!=null){

        }
        if (requestCode==990 && data!=null){
            imageUri=data.getData().toString();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                imagePicker();
            }
        }
    }
}