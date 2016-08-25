package com.dachen.mdt.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/8/5.
 */

@DatabaseTable
public class PatientInfo implements Serializable{
    @DatabaseField(id = true)
    public String id;
    @DatabaseField
    public String name;
    @DatabaseField
    public String number;
    @DatabaseField
    public String telephone;
    @DatabaseField
    public String idNum;
    @DatabaseField
    public int age;
    /*性别:1男，2女*/
    @DatabaseField
    public int sex;

    @DatabaseField
    public String tagName;
    @DatabaseField
    public String tagID;
    @DatabaseField
    public String diseaseTypeID;
    @DatabaseField
    public int isMyApply;
}
