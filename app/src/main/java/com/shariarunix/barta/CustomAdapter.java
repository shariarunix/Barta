package com.shariarunix.barta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shariarunix.barta.DataModel.GroupModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int length, layout;
    List<GroupModel> groupModelList = new ArrayList<>();
    String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGroupModelList(List<GroupModel> groupModelList) {
        this.groupModelList = groupModelList;
    }

    public CustomAdapter() {
        // Default Empty Constructor
    }

    public CustomAdapter(Context context, int length, int layout) {
        this.context = context;
        this.length = length;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if  (view == null){
            view = LayoutInflater.from(context).inflate(layout, viewGroup, false);
        }

        if (layout == R.layout.list_item_grp) {
            assert view != null;
            TextView txtGrpTitle = view.findViewById(R.id.txt_grp_title);
            TextView txtGrpLastMsg = view.findViewById(R.id.txt_grp_last_msg);
            TextView txtGrpLastMsgTime = view.findViewById(R.id.txt_grp_last_msg_time);

            GroupModel groupModel = groupModelList.get(i);

            if (!(groupModel.getGroupLastMsg() == null)){
                String[] lastMsgUser = groupModel.getGroupLastMsg().split(":");

                Date date = new Date(Long.parseLong(groupModel.getLastMsgTime()));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm");

                if (lastMsgUser[0].equals(userName)) {
                    txtGrpLastMsg.setText("You:" + lastMsgUser[1]);
                } else {
                    txtGrpLastMsg.setText(groupModel.getGroupLastMsg());
                }
                txtGrpLastMsgTime.setText(sdf.format(date));
            } else {
                Date date = new Date(Long.parseLong(groupModel.getGrpCreatingTime()));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm");

                if (groupModel.getGroupCreator().equals(userName)){
                    txtGrpLastMsg.setText("You created the group");
                }else {
                    txtGrpLastMsg.setText(groupModel.getGroupCreator() + " created the group");
                }
                txtGrpLastMsgTime.setText(sdf.format(date));
            }

            txtGrpTitle.setText(groupModel.getGroupName());
        }

        Animation viewAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_500);
        view.setAnimation(viewAnimation);

        return view;
    }
}
