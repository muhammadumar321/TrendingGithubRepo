package com.example.childfinarapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.childfinarapp.Adapter.ChoreListTaskAdapter;
import com.example.childfinarapp.Model.ChoreListTaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChorelistActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView taskList;
    private FloatingActionButton addTaskBtn;
    private FirebaseFirestore firestoreDBToStoreTaskList;
    private ChoreListTaskAdapter  taskListItemsAdapter;
    private List<ChoreListTaskModel> arrayListForTaskList;
    private Query queryForSavedTasks;
    private ListenerRegistration taskDocumentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorelist);
        //app title bar is concealed
        getSupportActionBar().hide();

        taskList = findViewById(R.id.scrollingTaskList);
        addTaskBtn = findViewById(R.id.floatingAddTaskBtn);
        firestoreDBToStoreTaskList = FirebaseFirestore.getInstance();

        //ensures that the size of RecyclerView does not change
        taskList.setHasFixedSize(true);
        //sets LinearLayout for this activity
        taskList.setLayoutManager(new LinearLayoutManager(ChorelistActivity.this));

        //implementation of the floating task button
        //creates an array list for the task items
        //attaches the touch helper and the adapter to each task
        //data is retrieved for each task.
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskAdderActivity.newInstance().show(getSupportFragmentManager() , TaskAdderActivity.TAG);
            }
        });

        arrayListForTaskList = new ArrayList<>();
        taskListItemsAdapter = new ChoreListTaskAdapter(ChorelistActivity.this , arrayListForTaskList);

        ItemTouchHelper swipeMovementForTask = new ItemTouchHelper(new TouchHelper(taskListItemsAdapter));
        swipeMovementForTask.attachToRecyclerView(taskList);
        taskDataRetrieval();
        taskList.setAdapter(taskListItemsAdapter);
    }

    //retrieves tasks from firestore
    private void taskDataRetrieval(){
        String signedInUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        queryForSavedTasks = firestoreDBToStoreTaskList.collection("users").document(signedInUserUID).collection("task").orderBy("recordedTime" , Query.Direction.DESCENDING);

        taskDocumentListener = queryForSavedTasks.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String signedInUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String id = documentChange.getDocument().getId();
                        ChoreListTaskModel choreListModel = documentChange.getDocument().toObject(ChoreListTaskModel.class).withId(id);
                        arrayListForTaskList.add(choreListModel);
                        taskListItemsAdapter.notifyDataSetChanged();
                    }
                }
                taskDocumentListener.remove();

            }
        });
    }
//implements the following actions after closing the dialog box
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        arrayListForTaskList.clear();
        taskDataRetrieval();
        taskListItemsAdapter.notifyDataSetChanged();
    }
    }
