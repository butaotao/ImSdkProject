package com.dachen.mdt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dachen.mdt.db.po.PatientTypePo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/11.
 */
public class UserDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "UserDbHelper";
    private static final int VERSION = 1;
    private static final String DB_NAME_PREFIX = "mdt_";
    public String userId;
    private static UserDbHelper instance;
    private static Map<String, UserDbHelper> helperMap = new HashMap<>();

    public static synchronized UserDbHelper getInstance(Context context, String userId) {
        if(userId==null){
            userId="";
        }
        if (instance != null&&instance.userId.equals(userId)) {
            return instance;
        }
        instance = helperMap.get(userId);
        if(instance==null){
            instance=new UserDbHelper(context, userId);
            helperMap.put(userId, instance);
        }
        return instance;
    }

    public UserDbHelper(Context context, String userId) {
        super(context, DB_NAME_PREFIX + userId + ".db", null, VERSION);
        this.userId = userId;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, PatientTypePo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }
}
