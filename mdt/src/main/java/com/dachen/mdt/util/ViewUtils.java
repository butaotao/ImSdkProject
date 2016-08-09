package com.dachen.mdt.util;

import android.text.TextUtils;
import android.widget.TextView;

import com.dachen.mdt.exception.TextEmptyException;

/**
 * Created by Mcp on 2016/8/8.
 */
public class ViewUtils {

    public static String checkTextEmpty(TextView tv) throws TextEmptyException {
        String res=tv.getText().toString();
        if (TextUtils.isEmpty(res)) {
            throw new TextEmptyException(tv);
        }
        return res;
    }
}