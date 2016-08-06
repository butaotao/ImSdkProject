package com.dachen.mdt.entity;

/**
 * Created by Mcp on 2016/8/5.
 */
public class ExpertInfo {
    public static final int ROLE_NORMAL=0;
    public static final int ROLE_MANAGER=1;
    public static final int ROLE_TEAM_LEADER=2;

    public static final int STATUS_NO_CONFIRM=0;
    public static final int STATUS_NO_REPORT=1;
    public static final int STATUS_DONE=2;
    public static final int STATUS_REJECT=3;

    public String avatar;
    public String department;
    public String hospital;
    public String hospitalId;
    public String name;
    public int role;
    public int status;
    public String telephone;
    public String title;
    public String userId;

}
