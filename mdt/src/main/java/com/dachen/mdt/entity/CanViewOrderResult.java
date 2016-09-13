package com.dachen.mdt.entity;

import java.io.Serializable;

/**
 * [一句话简单描述]
 *
 * @author huxinwu
 * @version 1.0
 * @date 2016/9/13
 */
public class CanViewOrderResult implements Serializable {

    public static class OrderItem implements Serializable{
        public String patientName;
        public int patientSex;
        public String creator;
        public String orderId;
        public String mdtGroupName;
        public String groupId;
        public String patientAge;
        public long endTime;
        public String userName;
        public String hospital;
        public String firstDiag;
    }
}
