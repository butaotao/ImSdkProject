package com.dachen.imsdk.db.po;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "ChatGroup")
public class ChatGroupPo implements Serializable {

    public static final String FROM_SYSTEM="system";

    public static final int TYPE_NOTICE = 0;//通知
    public static final int TYPE_DOUBLE = 1;//二人
    public static final int TYPE_MULTI = 2;//多人
    public static final int TYPE_PUB = 3;//公共号
    public static final int TYPE_GUIDE = 5;//导医
    public static final int TYPE_SERVICE = 6;//客服类型

    private static final long serialVersionUID = -6905214713788821766L;

    @DatabaseField(id = true)
    public String groupId;
    public static final String _groupId = "groupId";
    @DatabaseField
    public int type;
    public static final String _type = "type";
    @DatabaseField
    public String name;//组名
    @DatabaseField
    public int unreadCount;
    public static final String _unreadCount = "unreadCount";
    @DatabaseField
    public long updateStamp;
    public static final String _updateStamp = "updateStamp";
    @DatabaseField
    public boolean isDelete;
    @DatabaseField
    public String gpic;
    @DatabaseField
    public String lastMsgContent;
    public static final String _lastMsgContent = "lastMsgContent";
    @DatabaseField
    public String draft;
    public static final String _draft = "draft";
    @DatabaseField
    public int status;
    public static final String _status = "status";
    @DatabaseField
    public String groupUsers;
    @DatabaseField
    public String bizType;
    public static final String _bizType = "bizType";
    @DatabaseField
    public int bizStatus;
    public static final String _bizStatus = "bizStatus";
    @DatabaseField
    public String param;
    public static final String _param = "param";
    public String lastMsgUid;
    public String fromClient;
    @DatabaseField
    public String notifyParam;
    public static final String _notifyParam = "notifyParam";
    @DatabaseField
    public int top;
    public static final String _top = "top";

    public String doctorGroupName;  //医生集团名称
    public String departmentsName;  //科室名称
    public String title;   //医生职称

    //	public static class ChatGroupUser{
//		public String id;
//		public String name;
//		public String pic;
//		public int role;
//		public int userType;
//	}
    public static class ChatGroupParam {
        public String orderId;
        public int timeout;
        public int orderType;
        public int packType;
        public long appointTime;
        public Integer cancelFrom;
        public long price;
        public int waitCount;
        public int duraTime;
        public int timeLong;//分钟
        public long serviceBeginTime;
        public long treatBeginTime;
        public int recordStatus;
        public String remarks;
        public String userName;

        public String patientName;
        public String patientAge;
        public String patientSex;
        public String patientArea;
        public String orderCreatorId;
        public String hospitalId;
        public String hospitalName;
        public String departments;
        public String groupName;
        public String title;
        public String cancelReason;
        public String expectAppointmentInfo;
    }
    public static class ChatGroupNotifyParam{
        //@功能
        public int notify_type;
        public int notify_count;
    }

    public int getNotityState() {
        return getStateOnPos(status, 0);
    }

    public int getFavState() {
        return getStateOnPos(status, 1);
    }

    public static int getStateOnPos(int status, int bitPos) {
        int a = status >> bitPos;
        return a & 1;
    }
}
