package com.dachen.mdt.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class MdtGroupInfo implements Serializable{
    public String id;
    public String name;
    public String diseaseTypeId;
    public String parentId;
    public List<MdtGroupInfo> children;
}
