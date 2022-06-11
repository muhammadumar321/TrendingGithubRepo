package com.example.childfinarapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;


//Type generic class for TaskId, bounded type parameter
public class TaskId {
    @Exclude
    public String TaskId;

    public  <T extends  TaskId> T withId(@NonNull final String id){
        this.TaskId = id;
        return  (T) this;
    }

}