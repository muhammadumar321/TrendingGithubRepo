package com.example.childfinarapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.childfinarapp.ParentActivity;
import com.example.childfinarapp.TaskAdderActivity;
import com.example.childfinarapp.ChorelistActivity;
import com.example.childfinarapp.Model.ChoreListTaskModel;

import com.example.childfinarapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FieldValue;

import java.util.List;




public class ChoreListTaskAdapter extends RecyclerView.Adapter<ChoreListTaskAdapter.MyViewHolder> {

    private List<ChoreListTaskModel> choreList;
    private ChorelistActivity cListactivity;
    private FirebaseFirestore firestoreDB;
    private String signedInUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://child-fin-ar-app-83bb7-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference signUpInfoDB = database.getReference("signUpInfo");
    private final DatabaseReference signUpInfoUsers = signUpInfoDB.child("Users");
    private final DatabaseReference currentUser = signUpInfoDB.child("Users").child(signedInUserUID);

    public ChoreListTaskAdapter(ChorelistActivity mainChoreActivity , List<ChoreListTaskModel> choreList){
        this.choreList = choreList;
        cListactivity = mainChoreActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cListactivity).inflate(R.layout.task_scheme_format , parent , false);
        firestoreDB = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

    //deteles Task
    public void deleteTask(int position){
        ChoreListTaskModel choreListTaskModel = choreList.get(position);
        firestoreDB.collection("users").document(signedInUserUID).collection("task").document(choreListTaskModel.TaskId).delete();
        choreList.remove(position);
        notifyItemRemoved(position);
    }

    //returns to chore list page
    public Context getContext(){
        return cListactivity;
    }

    //edits task
    // creates bundle of data to be passed on to the object created
    // using TaskAdderActivity class
    public void editTask(int position){
        ChoreListTaskModel choreListTaskModel = choreList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , choreListTaskModel.getTask());
        bundle.putString("due date" , choreListTaskModel.getDueDate());
        bundle.putString("id" , choreListTaskModel.TaskId);
        bundle.putString("due time",choreListTaskModel.getDueTime());

        TaskAdderActivity addNewTask = new TaskAdderActivity();
        addNewTask.setArguments(bundle);
        addNewTask.show(cListactivity.getSupportFragmentManager() , addNewTask.getTag());
    }

    //binds saved information to the resulting view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ChoreListTaskModel choreListTaskModel = choreList.get(position);
        holder.taskCompletionCheckBox.setText(choreListTaskModel.getTask());
        holder.dueDateAndTimeTextViewer.setText("Due On " + choreListTaskModel.getDueDate() + ", " + choreListTaskModel.getDueTime());

        holder.taskCompletionCheckBox.setChecked(toBoolean(choreListTaskModel.getStatus()));

        holder.taskCompletionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firestoreDB.collection("users").document(signedInUserUID).collection("task").document(choreListTaskModel.TaskId).update("status" , 1);
                    firestoreDB.collection("users").document(signedInUserUID).update("creditNumber" , FieldValue.increment(3));
                }else{
                    firestoreDB.collection("users").document(signedInUserUID).collection("task").document(choreListTaskModel.TaskId).update("status" , 0);

            }}});
        }


    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return choreList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dueDateAndTimeTextViewer;
        CheckBox taskCompletionCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dueDateAndTimeTextViewer = itemView.findViewById(R.id.task_due_date);
            taskCompletionCheckBox = itemView.findViewById(R.id.completed_checkBox);


        }
    }
}