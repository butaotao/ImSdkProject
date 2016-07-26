package com.dachen.imsdk.out;

import com.dachen.imsdk.entity.EventPL;

/**
 * Created by Mcp on 2016/2/19.
 */
public interface OnEventListener {
    boolean onEvent(EventPL ele);
}
