package com.dachen.mdt.exception;

import android.widget.TextView;

/**
 * Created by Mcp on 2016/8/8.
 */
public class TextEmptyException extends Exception{
    public TextView tv;

    public TextEmptyException(TextView tv) {
        this.tv = tv;
    }
}
