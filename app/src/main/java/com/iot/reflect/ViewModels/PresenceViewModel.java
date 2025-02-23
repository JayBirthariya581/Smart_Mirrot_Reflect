package com.iot.reflect.ViewModels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;


public class PresenceViewModel extends ViewModel {

    String presence;
    String uid;



    public PresenceViewModel() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;

        FirebaseDatabase.getInstance().getReference("presence").child(uid).setValue(this.presence);

    }




    @Override
    protected void onCleared() {
        super.onCleared();
        this.presence = "offline";
        FirebaseDatabase.getInstance().getReference("presence").child(uid).setValue(this.presence);
    }
}
