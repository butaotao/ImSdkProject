package com.dachen.mdt.entity;

import android.text.TextUtils;

import com.dachen.mdt.util.AppCommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
        public String group;
        public ArrayList<DiseaseTag> tagList; //用于会诊报告
    }

    public String makeShowText(){
        HashMap<String,TempOptionGroup> tempMap=new HashMap<>();
        ArrayList<TempOptionGroup> groupList=new ArrayList<>();
        for (MdtOptionItem item: array) {
            if(TextUtils.isEmpty(item.group)){
                groupList.add(new TempOptionGroup(item.name));
            }else{
                TempOptionGroup group=tempMap.get(item.group);
                if(group==null){
                    group=TempOptionGroup.makeGroup(item.group);
                    tempMap.put(item.group,group);
                    groupList.add(group);
                }
                group.itemList.add(item);

            }
        }
        StringBuilder builder = new StringBuilder();
        for(TempOptionGroup group:groupList){
            builder.append(group.makeText()).append("\n");
        }
        if(!TextUtils.isEmpty(text)){
            builder.append(text).append("\n");
        }
        AppCommonUtils.deleteLastChar(builder);
        showText = builder.toString();
        return showText;
    }

    public static class TempOptionGroup{
        public boolean isGroup;
        public String groupName;
        public String text;
        public ArrayList<MdtOptionItem> itemList;

        public TempOptionGroup(String text) {
            this.text = text;
        }
        public String makeText(){
            if(!isGroup)return text;
            StringBuilder builder = new StringBuilder();
            for (MdtOptionItem item: itemList) {
                builder.append(item.name).append(",");
            }
            AppCommonUtils.deleteLastChar(builder);
            return String.format(Locale.CHINA,"%s(%s)",groupName,builder.toString());
        }

        public static TempOptionGroup makeGroup(String groupName){
            TempOptionGroup group=new TempOptionGroup(null);
            group.isGroup=true;
            group.itemList=new ArrayList<>();
            group.groupName=groupName;
            return group;
        }
    }
}
