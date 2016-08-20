package com.dachen.mdt.tools;

import android.text.TextUtils;

import com.dachen.mdt.entity.CheckType;

import java.util.List;

/**
 * Created by Mcp on 2016/8/17.
 */
public class CheckResultHelper {

    public static int getTypeIndex(List<CheckType> typeList,String id){
        int index=-1;
        for(int i=0;i<typeList.size();i++){
            if(TextUtils.equals(typeList.get(i).id,id ))
                return i;
        }
        return index;
    }
}
