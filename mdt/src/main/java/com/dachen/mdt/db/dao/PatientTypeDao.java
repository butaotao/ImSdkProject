package com.dachen.mdt.db.dao;

import android.content.Context;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.db.UserDbHelper;
import com.dachen.mdt.db.po.PatientTypePo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Mcp on 2016/8/11.
 */
public class PatientTypeDao {
    private Dao<PatientTypePo,String> mDao;

    public PatientTypeDao() {
        mDao = makeDao(ImSdk.getInstance().context, ImUtils.getLoginUserId());
    }
    private Dao<PatientTypePo, String> makeDao(Context context, String userId) {
        try {
            return UserDbHelper.getInstance(context, userId).getDao(PatientTypePo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientTypePo getType(String id){
        try {
            return mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveType(PatientTypePo po){
        try {
            mDao.createOrUpdate(po);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
