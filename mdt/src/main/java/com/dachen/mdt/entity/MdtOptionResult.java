package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mcp on 2016/8/22.
 */
public class MdtOptionResult implements Serializable{
    public ArrayList<MdtOptionItem> array;
    public String showText;

    public static class MdtOptionItem implements Serializable{
        public String id;
        public String name;
        public boolean supportText;
        public String value;
        public String displayName;
        public String parentId;
        public ArrayList<MdtOptionItem> children;
        public int level;
    }
}
