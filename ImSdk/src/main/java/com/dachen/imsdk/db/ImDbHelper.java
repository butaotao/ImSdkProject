package com.dachen.imsdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.db.po.GroupUserPo;
import com.dachen.imsdk.db.po.SimpleUserInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ImDbHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG="ImDbHelper";
	private static final int VERSION = 7;
	private static final String DB_NAME_PREFIX = "im_";
	public String userId;
	private static ImDbHelper instance;
	private static Map<String, ImDbHelper> helperMap=new HashMap<String, ImDbHelper>();

	public static synchronized ImDbHelper getInstance(Context context, String userId) {
//		Logger.e(TAG, "getInstnace userId:"+userId);
		if(userId==null){
			userId="";
		}
		if (instance != null&&instance.userId.equals(userId)) {
			return instance;
		}
		instance = helperMap.get(userId);
		if(instance==null){
//			try {
//				throw new Exception();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Logger.e(TAG, "getInstnace null. create new"+userId);
			instance=new ImDbHelper(context, userId);
			helperMap.put(userId, instance);
		}
//		else{
//			Logger.e(TAG, "getInstnace null. create new"+userId);
//		}
		return instance;
	}

	public static synchronized void closeHelper() {
		if (instance != null) {
			instance = null;
		}
	}
	public static synchronized void clearMap(){
		helperMap=new HashMap<String, ImDbHelper>();
	}

	public ImDbHelper(Context context, String userId) {
		super(context, DB_NAME_PREFIX + userId + ".db", null, VERSION);
		this.userId = userId;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, ChatGroupPo.class);
			TableUtils.createTableIfNotExists(connectionSource, ChatMessagePo.class);
			TableUtils.createTableIfNotExists(connectionSource, SimpleUserInfo.class);
			TableUtils.createTableIfNotExists(connectionSource, GroupUserPo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connSource, int oldVersion, int newVersion) {
		try {
			if (oldVersion < 2) {
				TableUtils.dropTable(connSource, ChatGroupPo.class, true);
				TableUtils.createTable(connSource, ChatGroupPo.class);
			}
			if (oldVersion < 4) {
				TableUtils.createTableIfNotExists(connectionSource, SimpleUserInfo.class);
				TableUtils.createTableIfNotExists(connectionSource, GroupUserPo.class);
			}
			if(oldVersion<5){
                db.execSQL("alter table ChatMessage add column isRetract integer");
                db.execSQL("alter table ChatMessage add column deleteFlag integer");
			}
			if(oldVersion<6){
                db.execSQL("alter table ChatGroup add column notifyParam varchar");
			}
			if(oldVersion<7){
                db.execSQL("alter table ChatGroup add column top integer");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
