package com.dachen.imsdk.out;

import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.EventPL;

import java.util.List;

/**
 * Created by Mcp on 2016/2/19.
 */
public interface OnImSdkListener {
    void onGroupList(List<ChatGroupPo> list);
    void onEvent(EventPL ele);
}
