package com.dachen.mdt.entity.event;

/**
 * Created by Mcp on 2016/9/2.
 */
public class SubmitSummaryEvent {
    public String orderId;

    public SubmitSummaryEvent(String orderId) {
        this.orderId = orderId;
    }
}
