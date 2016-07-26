package com.dachen.imsdk.entity.event;

/**
 * Created by Mcp on 2016/2/23.
 */
public class NewMsgEvent {
    public Object from;
    public NewMsgEvent(){
    }

    public NewMsgEvent(Object from) {
        this.from = from;
    }
}
