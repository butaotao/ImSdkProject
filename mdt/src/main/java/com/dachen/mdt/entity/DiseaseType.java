package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mcp on 2016/8/16.
 */
public class DiseaseType implements Serializable{
    public String id;
    public String name;
    public String topDiseaseId;
    public String parentId;
    public int level;
    public List<DiseaseType> children;
}
