package com.dachen.mdt.tools;

import com.dachen.mdt.db.dao.PatientTypeDao;
import com.dachen.mdt.db.po.PatientTypePo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/11.
 */
public class PatientHelper {
    private static Map<String,String> typeName=new HashMap<>();

    public static String getPatientTypeName(String id){
        String name=typeName.get(id);
        if(name==null){
            PatientTypeDao dao=new PatientTypeDao();
            PatientTypePo po=dao.getType(id);
            name=po==null?"":po.name;
            typeName.put(id,name);
        }else {
            name=typeName.get(id);
        }
        return name;
    }
    public static void updatePatientTypeName(String id ,String name){
        if(getPatientTypeName(id).equals(name))
            return ;
        PatientTypeDao dao=new PatientTypeDao();
        dao.saveType(new PatientTypePo(id,name));
    }
}
