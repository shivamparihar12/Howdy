package com.example.howdy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.howdy.model.Users;

import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Users>> contactNoList= new MutableLiveData<>();

    public LiveData<ArrayList<Users>> getContactNoList() {
        return contactNoList;
    }

    private void loadContactList(){

    }
}
