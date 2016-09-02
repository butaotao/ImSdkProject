package com.dachen.mdt.activity;

import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.mdt.tools.MdtImMsgHandler;

/**
 * Created by Mcp on 2016/8/9.
 */
public abstract class AppBaseChatActivity extends ChatActivityV2 {

    @Override
    protected ImMsgHandler makeMsgHandler() {
        return new MdtImMsgHandler(this);
    }
}
