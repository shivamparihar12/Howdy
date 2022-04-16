package com.example.howdy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.howdy.databinding.FragmentCallsBinding;
import com.example.howdy.databinding.FragmentChatBinding;


public class CallsFragment extends Fragment {
    private FragmentCallsBinding binding;
    public CallsFragment() {
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
        binding=FragmentCallsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}