package com.example.childfinarapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.Calendar;
import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class TaskAdderActivity extends BottomSheetDialogFragment {

    public static final String TAG = "TaskAdderActivity";

    private TextView taskDateSetter;
    private TextView taskTimeSetter;
    private EditText taskEditor;
    private Button saveTaskButton;
    private FirebaseFirestore firestoreDB;
    private Context appCurrentContext;
    private String taskID = "";
    private String taskDateDeadline = "";
    private String taskDateUpdate = "";
    private String taskTimeDeadline = "";
    private String taskTimeUpdate = "";

    public static TaskAdderActivity newInstance() {
        return new TaskAdderActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_task_adder, container, false);

    } //inflates view for each task

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskTimeSetter = view.findViewById(R.id.task_due_time);
        taskDateSetter = view.findViewById(R.id.task_due_date);
        taskEditor = view.findViewById(R.id.task_editor);
        saveTaskButton = view.findViewById(R.id.save_button);

        firestoreDB = FirebaseFirestore.getInstance();

        boolean isUpdate = false;
        //maps objects entered by users to string keys so that it
        //can be passed on to the next activity in a Bundle
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            isUpdate = true;
            String taskToBeAdded = bundle.getString("task");
            taskID = bundle.getString("id");
            taskDateUpdate = bundle.getString("due date");
            taskTimeUpdate = bundle.getString("due time");

            taskEditor.setText(taskToBeAdded);
            taskDateSetter.setText(taskDateUpdate);
            taskTimeSetter.setText(taskTimeUpdate);

            if (taskToBeAdded.length() > 0) {
                saveTaskButton.setEnabled(false);
                saveTaskButton.setBackgroundColor(getResources().getColor(R.color.greyish_white));
            }
        }
        //listens for text changes and permits a save event occur if there is text
        taskEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    saveTaskButton.setEnabled(false);
                    saveTaskButton.setBackgroundColor(getResources().getColor(R.color.greyish_white));
                } else {
                    saveTaskButton.setEnabled(true);
                    saveTaskButton.setBackgroundColor(getResources().getColor(R.color.pastel_purple));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//sets date via the datePickerDialog and shows it after user has set it
        taskDateSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                Calendar setDate = Calendar.getInstance();

                int MONTH = setDate.get(Calendar.MONTH);
                int YEAR = setDate.get(Calendar.YEAR);
                int DAY = setDate.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(appCurrentContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        taskDateSetter.setText(dayOfMonth + "/" + month + "/" + year);
                        taskDateDeadline = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);

                datePickerDialog.show();
            }
        });

        //sets time via the timePickerDialog and shows it after user has set it
        taskTimeSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                Calendar currentTime = Calendar.getInstance();

                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog setTime = new TimePickerDialog(appCurrentContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        taskTimeSetter.setText(selectedHour + ":" + selectedMinute);
                        taskTimeDeadline = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true); //24 hours
                setTime.show();

            }
        });

        // attaches the values that entered by users to the Bundle
        //writes to or updates database
        boolean finalIsUpdate = isUpdate;
        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                String addedTask = taskEditor.getText().toString();

                String signedInUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (finalIsUpdate) {
                    //firestoreDB.collection("users").document(signedInUserUID).update("creditNumber" , 0);
                    firestoreDB.collection("users").document(signedInUserUID).collection("task").document(taskID).update("task", addedTask, "dueDate", taskDateDeadline, "dueTime", taskTimeDeadline);
                    Toast.makeText(appCurrentContext, "Task Updated", Toast.LENGTH_SHORT).show();

                } else {
                    if (addedTask.isEmpty()) {
                        Toast.makeText(appCurrentContext, "Empty tasks are not allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", addedTask);
                        taskMap.put("dueDate", taskDateDeadline);
                        taskMap.put("dueTime", taskTimeDeadline);
                        taskMap.put("status", 0);
                        taskMap.put("recordedTime", FieldValue.serverTimestamp());

                        firestoreDB.collection("users").document(signedInUserUID).collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(appCurrentContext, "Task Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(appCurrentContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(appCurrentContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }

    //function attaches the app context
    @Override
    public void onAttach(@NonNull Context appCurrentContext) {
        super.onAttach(appCurrentContext);
        this.appCurrentContext = appCurrentContext;
    }

    //closes dialog box upon completion of each activity
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}