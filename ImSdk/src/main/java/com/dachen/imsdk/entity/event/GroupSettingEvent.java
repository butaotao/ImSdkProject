package com.dachen.imsdk.entity.event;

/**
 * Created by Mcp on 2016/3/2.
 */
public class GroupSettingEvent {
    public static final int TYPE_NAME=1;
    public static final int TYPE_STATUS=2;
    public static final int TYPE_EXIT=3;
    public static final int TYPE_AVATAR=4;
    public String groupId;
    public String name;
    public int type;
    public int status;
    public String url;

    public GroupSettingEvent(String groupId, int type) {
        if(groupId==null)
            groupId="";
        this.groupId = groupId;
        this.type=type;
    }
}
