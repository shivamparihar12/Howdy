package com.example.howdy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.howdy.R;
import com.example.howdy.model.StoryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<StoryModel> storyModelArrayList;

    public StoriesAdapter(Context context, ArrayList<StoryModel> storyModelArrayList) {
        this.context = context;
        this.storyModelArrayList = storyModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.story_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryModel storyModel= storyModelArrayList.get(position);
        holder.userName.setText(storyModel.getUserName());
    }

    @Override
    public int getItemCount() {
        return storyModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.user_image);
            userName=itemView.findViewById(R.id.userName);
        }
    }
}
