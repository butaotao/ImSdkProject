package com.dachen.mdt.entity;

/**
 * Created by Mcp on 2016/8/10.
 */
public class OrderStatusResult {

//    (0,"未确认"),
//    (1,"已接受，未填写小结"),
//    (2,"已接受并且小结已经填写"),
//    (3,"拒绝/自动拒绝");
    public int status;
    public OrderDetailVO orderDetail;
}
