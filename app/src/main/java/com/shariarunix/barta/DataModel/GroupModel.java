package com.shariarunix.barta.DataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupModel implements Serializable {
    String groupId, groupName, groupLastMsg, groupCreator, grpCreatingTime, lastMsgTime;

    public GroupModel() {
        // Default Empty Constructor
    }

    public GroupModel(String groupId, String groupName, String groupLastMsg, String groupCreator, String grpCreatingTime, String lastMsgTime) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupLastMsg = groupLastMsg;
        this.groupCreator = groupCreator;
        this.grpCreatingTime = grpCreatingTime;
        this.lastMsgTime = lastMsgTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLastMsg() {
        return groupLastMsg;
    }

    public void setGroupLastMsg(String groupLastMsg) {
        this.groupLastMsg = groupLastMsg;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public String getGrpCreatingTime() {
        return grpCreatingTime;
    }

    public void setGrpCreatingTime(String grpCreatingTime) {
        this.grpCreatingTime = grpCreatingTime;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }
}
