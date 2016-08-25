package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mcp on 2016/8/17.
 */
public class CheckTypeResult implements Serializable {
    public ArrayList<ImageInfo> imageList;
    public ArrayList<CheckType> typeList;
    public String text;
}
