package com.dachen.imsdk.db.dao;

import android.content.Context;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.ImDbHelper;
import com.dachen.imsdk.db.po.GroupUserPo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class GroupUserInfoDao {

	private static final String TAG = GroupUserInfoDao.class.getSimpleName();
	private Context context;
	private Dao<GroupUserPo, Integer> mDao;
	public static GroupUserInfoDao instance;
	public static synchronized GroupUserInfoDao getInstance(){
		if (instance==null) {
			instance=new GroupUserInfoDao();
		}
		return instance;
	};
	public GroupUserInfoDao() {
		context= ImSdk.getInstance().context;
//		OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, ImDbHelper.class);
		try {
//			mDao = DaoManager.createDao(helper.getConnectionSource(), GroupUserPo.class);
			mDao =ImDbHelper.getInstance(context, ImSdk.getInstance().userId).getDao(GroupUserPo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Dao<GroupUserPo, Integer> getDao(){
		return getInstance().mDao;
	}
	public static GroupUserPo query(String groupId,String userId){
		try {
			QueryBuilder<GroupUserPo, Integer> b=getDao().queryBuilder();
			b.where().eq(GroupUserPo._groupId, groupId).and().eq(GroupUserPo._userId,userId);
			return b.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveUser(GroupUserPo info){
		try {
			GroupUserPo po=query(info.groupId,info.userId);
			if(po!=null){
				info.fid=po.fid;
			}
			getDao().createOrUpdate(info);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
