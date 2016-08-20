package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/8/17.
 */
public class CheckTypeResult implements Serializable {
    public List<String> pathList;
    public ArrayList<CheckType> typeList;
    public String text;
}
