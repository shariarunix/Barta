package com.shariarunix.barta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shariarunix.barta.DataModel.GroupModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ProgressBar listProgressBar;
    ListView listGrp;
    ImageView imgBtnBack;
    AppCompatButton btnCreateGroup;

    String userName;
    boolean hasUserName;
    List<GroupModel> groupModelList = new ArrayList<>();

    DatabaseReference mReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReference = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        hasUserName = sharedPreferences.getBoolean("hasUserName", false);
        userName = sharedPreferences.getString("userName", "");

        listProgressBar = findViewById(R.id.list_progress_bar);
        listGrp = findViewById(R.id.list_grp);
        imgBtnBack = findViewById(R.id.img_btn_back);
        btnCreateGroup = findViewById(R.id.btn_create_group);

        getUserName();
        loadGroups(listGrp);

        listGrp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class)
                        .putExtra("groupData", groupModelList.get(position)));
            }
        });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hold to exit.", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtnBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
                return false;
            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        editor.apply();
    }
    
    private void loadGroups(ListView listView) {
        mReference.child(DatabaseTableName.GROUP_TABLE_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GroupModel groupModel = dataSnapshot.getValue(GroupModel.class);

                    groupModelList.add(groupModel);
                }
                listProgressBar.setVisibility(View.GONE);

                Collections.reverse(groupModelList);

                CustomAdapter groupListAdapter = new CustomAdapter(MainActivity.this, groupModelList.size(), R.layout.list_item_grp);
                groupListAdapter.setGroupModelList(groupModelList);
                groupListAdapter.setUserName(userName);

                listView.setAdapter(groupListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    };

    private void createGroup() {
        Dialog createExamDialog = new Dialog(MainActivity.this);
        createExamDialog.setContentView(R.layout.dialog_create_group);

        Objects.requireNonNull(createExamDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createExamDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createExamDialog.getWindow().setGravity(Gravity.BOTTOM);

        EditText edtDialogGroupName = createExamDialog.findViewById(R.id.edt_dialog_group_name);

        TextView txtDialogShowError = createExamDialog.findViewById(R.id.txt_dialog_show_error);

        AppCompatButton btnDialogGroupCreate = createExamDialog.findViewById(R.id.btn_dialog_group_create);

        createExamDialog.show();

        btnDialogGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = edtDialogGroupName.getText().toString().trim();

                if (groupName.isEmpty()) {
                    txtDialogShowError.setVisibility(View.VISIBLE);
                    return;
                }

                String groupId = mReference.push().getKey();

                GroupModel groupModel = new GroupModel(groupId,
                        groupName,
                        null,
                        userName,
                        String.valueOf(System.currentTimeMillis()), null);

                assert groupId != null;
                mReference
                        .child(DatabaseTableName.GROUP_TABLE_NAME)
                        .child(groupId)
                        .setValue(groupModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                createExamDialog.dismiss();
            }
        });
    }

    private void getUserName(){
        Dialog getUserNameDialog = new Dialog(MainActivity.this);
        getUserNameDialog.setContentView(R.layout.dialog_user_name);

        Objects.requireNonNull(getUserNameDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getUserNameDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getUserNameDialog.getWindow().setGravity(Gravity.BOTTOM);
        getUserNameDialog.setCancelable(false);

        EditText edtDialogGroupName = getUserNameDialog.findViewById(R.id.edt_dialog_group_name);

        TextView txtDialogShowError = getUserNameDialog.findViewById(R.id.txt_dialog_show_error);

        AppCompatButton btnDialogGroupCreate = getUserNameDialog.findViewById(R.id.btn_dialog_group_save);

        btnDialogGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtDialogGroupName.getText().toString().trim();

                if (name.isEmpty()){
                    txtDialogShowError.setVisibility(View.VISIBLE);
                    return;
                }

                editor.putBoolean("hasUserName", true);
                editor.putString("userName", name);

                editor.apply();
                getUserNameDialog.dismiss();
            }
        });

        if (!hasUserName){
            getUserNameDialog.show();
        }
    }
}