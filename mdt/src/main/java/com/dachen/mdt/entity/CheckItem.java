package com.dachen.mdt.entity;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/8/16.
 */
public class CheckItem implements Serializable{
    public String id;
    public String name;
    public String alias;
    public String unit;
    public String dataType;
    public String value;
    public Region region;

//    "id":"检查项Id",
//            "name":"中文名"，
//            "alias ":"缩写"，
//            "unit":"单位"，
//            "dataType":"数据类型（number/string），如果为number，则为数字型。对应显示数子键盘；string为文本",
//            "region": [0,100]------参考范围（数字类型才有此项）

    public static class Region implements Serializable{
        public Integer min;
        public Integer max;
    }
}
