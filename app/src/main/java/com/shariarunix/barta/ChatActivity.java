package com.shariarunix.barta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shariarunix.barta.DataModel.ChatModel;
import com.shariarunix.barta.DataModel.GroupModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    TextView txtGroupName, txtGrpCreateStamp;
    EditText edtMsg;
    ImageView imgBtnSendMsg;

    LinearLayout layoutShowMsg;
    ScrollView msgScrollView;

    DatabaseReference mReference;
    GroupModel groupModel;
    String userName;
    SharedPreferences sharedPreferences;
    boolean isEdtFocusChanged = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        groupModel = (GroupModel) getIntent().getSerializableExtra("groupData");

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "");

        mReference = FirebaseDatabase.getInstance().getReference();

        txtGroupName = findViewById(R.id.txt_group_name);
        txtGrpCreateStamp = findViewById(R.id.txt_grp_create_stamp);

        edtMsg = findViewById(R.id.edt_msg);
        imgBtnSendMsg = findViewById(R.id.img_btn_send_msg);

        layoutShowMsg = findViewById(R.id.layout_show_msg);
        msgScrollView = findViewById(R.id.msg_scroll_view);

        assert groupModel != null;
        txtGroupName.setText(groupModel.getGroupName());

        Date date = new Date(Long.parseLong(groupModel.getGrpCreatingTime()));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm");

        if (groupModel.getGroupCreator().equals(userName)) {
            txtGrpCreateStamp.setText("You created the group" + ", " + sdf.format(date));
        } else {
            txtGrpCreateStamp.setText(groupModel.getGroupCreator() + " created the group" + ", " + sdf.format(date));
        }

        findViewById(R.id.img_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        loadMsg();

        edtMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!isEdtFocusChanged) {
                    scrollToBottom();
                    isEdtFocusChanged = true;
                }
            }
        });

        edtMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToBottom();
            }
        });

        imgBtnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edtMsg.getText().toString().trim();

                if (msg.isEmpty()) {
                    return;
                }
                edtMsg.setText("");

                String chatKey = mReference.push().getKey();
                assert chatKey != null;
                mReference
                        .child(DatabaseTableName.CHAT_TABLE_NAME)
                        .child(groupModel.getGroupId())
                        .child(chatKey)
                        .setValue(new ChatModel(chatKey, msg, userName, System.currentTimeMillis(), false));

                mReference
                        .child(DatabaseTableName.GROUP_TABLE_NAME)
                        .child(groupModel.getGroupId())
                        .child("groupLastMsg")
                        .setValue(userName + ": " + msg);

                mReference
                        .child(DatabaseTableName.GROUP_TABLE_NAME)
                        .child(groupModel.getGroupId())
                        .child("lastMsgTime")
                        .setValue(String.valueOf(System.currentTimeMillis()));
            }
        });
    }

    private void scrollToBottom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msgScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    private void loadMsg() {
        mReference
                .child(DatabaseTableName.CHAT_TABLE_NAME)
                .child(groupModel.getGroupId())
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ChatModel chatModel = snapshot.getValue(ChatModel.class);

                        assert chatModel != null;
                        Date date = new Date(chatModel.getMsgTime());
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm");

                        View view;
                        if (chatModel.getUserName().equals(userName)) {
                            view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.list_item_chat_right, layoutShowMsg, false);

                            if (!chatModel.isDeleted()) {
                                view.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {

                                        TextView txtMsg = view.findViewById(R.id.txt_msg);

                                        Dialog unsentMsgDialog = new Dialog(ChatActivity.this);
                                        unsentMsgDialog.setContentView(R.layout.dialog_unsent_msg);

                                        Objects.requireNonNull(unsentMsgDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        unsentMsgDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        unsentMsgDialog.getWindow().setGravity(Gravity.BOTTOM);

                                        AppCompatButton btnDialogUnsentMsg = unsentMsgDialog.findViewById(R.id.btn_dialog_unsent_msg);

                                        unsentMsgDialog.show();

                                        btnDialogUnsentMsg.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mReference
                                                        .child(DatabaseTableName.CHAT_TABLE_NAME)
                                                        .child(groupModel.getGroupId())
                                                        .child(chatModel.getChatId())
                                                        .child("deleted")
                                                        .setValue(true)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(ChatActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                mReference
                                                        .child(DatabaseTableName.GROUP_TABLE_NAME)
                                                        .child(groupModel.getGroupId())
                                                        .child("groupLastMsg")
                                                        .setValue(userName + ": unsent a message");

                                                unsentMsgDialog.dismiss();
                                                txtMsg.setText("You unsent a message");
                                                txtMsg.setTextColor(R.color.black_sc);
                                                txtMsg.setTextSize(16);
                                            }
                                        });
                                        return false;
                                    }
                                });
                            }

                        } else {
                            view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.list_item_chat_left, layoutShowMsg, false);
                        }

                        TextView txtName = view.findViewById(R.id.txt_name);
                        TextView txtTime = view.findViewById(R.id.txt_time);
                        TextView txtMsg = view.findViewById(R.id.txt_msg);

                        txtName.setText(chatModel.getUserName());
                        txtTime.setText(sdf.format(date));

                        if (chatModel.isDeleted()) {
                            txtMsg.setTextColor(R.color.black_sc);
                            txtMsg.setTextSize(16);
                            if (chatModel.getUserName().equals(userName)) {
                                txtMsg.setText("You unsent a message");
                            } else {
                                txtMsg.setText(chatModel.getUserName() + " unsent a message");
                            }
                        } else {
                            txtMsg.setText(chatModel.getChatMessage());
                        }

                        Animation viewAnimation = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.anim_alpha_500);
                        view.setAnimation(viewAnimation);

                        layoutShowMsg.addView(view);

                        scrollToBottom();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}