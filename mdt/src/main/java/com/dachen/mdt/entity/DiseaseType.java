package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mcp on 2016/8/16.
 */
public class DiseaseType implements Serializable{
    public String id;
    public String name;
    public String topDiseaseId;
    public String parentId;
    public int level;
    public ArrayList<DiseaseType> children;
    public ArrayList<DiseaseTag> tagList;
}
