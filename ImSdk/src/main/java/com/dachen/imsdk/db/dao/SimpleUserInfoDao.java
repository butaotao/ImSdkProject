package com.dachen.imsdk.db.dao;

import android.content.Context;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.ImDbHelper;
import com.dachen.imsdk.db.po.SimpleUserInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleUserInfoDao {
	
	private static final String TAG = SimpleUserInfoDao.class.getSimpleName();
	private Context context;
	private Dao<SimpleUserInfo, String> mDao;
	public static SimpleUserInfoDao instance;
	public static synchronized SimpleUserInfoDao getInstance(){
		if (instance==null) {
			instance=new SimpleUserInfoDao();
		}
		return instance;
	};
	public SimpleUserInfoDao() {
		context= ImSdk.getInstance().context;
//		OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, ImDbHelper.class);
		try {
//			mDao = DaoManager.createDao(helper.getConnectionSource(), SimpleUserInfo.class);
			mDao=ImDbHelper.getInstance(context, ImSdk.getInstance().userId).getDao(SimpleUserInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Dao<SimpleUserInfo, String> getDao(){
		return getInstance().mDao;
	}
	public static SimpleUserInfo queryForId(String id){
		try {
			return getDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<SimpleUserInfo> queryAll(){
		try {
			return getDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<SimpleUserInfo>();
	}
	public void saveUser(SimpleUserInfo info){
		try {
			getDao().createOrUpdate(info);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
