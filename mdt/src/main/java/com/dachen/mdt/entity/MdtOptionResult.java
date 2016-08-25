package com.dachen.mdt.entity;

import android.text.TextUtils;

import com.dachen.mdt.util.AppCommonUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mcp on 2016/8/22.
 */
public class MdtOptionResult implements Serializable{
    public ArrayList<MdtOptionItem> array;
    public String showText;
    public String text;

    public static class MdtOptionItem implements Serializable{
        public String id;
        public String name;
        public String value;
        public String displayName;
        public String parentId;
        public ArrayList<MdtOptionItem> children;
        public int level;
        public String topDiseaseId;
        public ArrayList<DiseaseTag> tagList; //用于会诊报告
    }

    public String makeShowText(){
        StringBuilder builder = new StringBuilder();
        for (MdtOptionItem item: array) {
//        for (int i=0;i<currentData.array.size();i++) {
//            MdtOptionItem item =currentData.array.get(i);
//            if(item.supportText){
//                String value=cacheDataMap.get(item.id);
//                if(TextUtils.isEmpty(value))continue;
//                item.value=value;
//            }
//            res.array.add(item);
            builder.append(item.name).append("\n");
        }
        if(!TextUtils.isEmpty(text)){
            builder.append(text).append("\n");
        }
        AppCommonUtils.deleteLastChar(builder);
        showText = builder.toString();
        return showText;
    }
}
