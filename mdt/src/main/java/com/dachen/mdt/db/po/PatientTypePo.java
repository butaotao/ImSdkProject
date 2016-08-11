package com.dachen.mdt.db.po;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Mcp on 2016/8/11.
 */
@DatabaseTable(tableName = "PatientType")
public class PatientTypePo {

    @DatabaseField(id = true)
    public String id;
    @DatabaseField
    public String name;

    public PatientTypePo(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
