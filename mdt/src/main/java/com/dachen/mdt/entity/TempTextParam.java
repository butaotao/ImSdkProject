package com.dachen.mdt.entity;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/8/16.
 */
public class TempTextParam implements Serializable {
    public String text;

    public TempTextParam() {
    }

    public TempTextParam(String text) {
        this.text = text;
    }
}
