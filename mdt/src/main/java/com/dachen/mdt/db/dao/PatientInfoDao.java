package com.dachen.mdt.db.dao;

import android.content.Context;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.db.UserDbHelper;
import com.dachen.mdt.entity.PatientInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/8/11.
 */
public class PatientInfoDao {
    private Dao<PatientInfo,String> mDao;

    public PatientInfoDao() {
        mDao = makeDao(ImSdk.getInstance().context, ImUtils.getLoginUserId());
    }
    private Dao<PatientInfo, String> makeDao(Context context, String userId) {
        try {
            return UserDbHelper.getInstance(context, userId).getDao(PatientInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientInfo getPatient(String id){
        try {
            return mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePatient(PatientInfo po){
        try {
            mDao.createOrUpdate(po);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<PatientInfo> queryAll(){
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
