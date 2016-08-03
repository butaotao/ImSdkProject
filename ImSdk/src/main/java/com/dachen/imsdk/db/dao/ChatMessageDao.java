package com.dachen.imsdk.db.dao;

import android.content.Context;
import android.text.TextUtils;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.ImDbHelper;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class ChatMessageDao {
	
	private Dao<ChatMessagePo, Integer> mDao;
	public ChatMessageDao(Context context,String userId){
		mDao=makeDao(context, userId);
	}
	public ChatMessageDao(){
		mDao=makeDao(ImSdk.getInstance().context, ImSdk.getInstance().userId);
	}
	private Dao<ChatMessagePo, Integer> makeDao(Context context,String userId){
		try {
			return ImDbHelper.getInstance(context, userId).getDao(ChatMessagePo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addMessage(ChatMessagePo data){
		try {
			mDao.create(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void saveMessage(ChatMessagePo data){
		try {
			mDao.createOrUpdate(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void saveOtherMessage(ChatMessagePo data){
		try {
//			List<ChatMessagePo> list=mDao.queryForEq(ChatMessagePo._msgId, data.msgId);
			QueryBuilder<ChatMessagePo, Integer> qBuilder=mDao.queryBuilder();
			qBuilder.where().eq(ChatMessagePo._msgId, data.msgId);
			long size=qBuilder.countOf(ChatMessagePo._id);
			if(size==0){
				mDao.create(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void saveClientMessage(ChatMessagePo data){
		saveClientMessage(data,false);
	}
	
	public void saveClientMessage(ChatMessagePo data,boolean updateTime){
		try {
			if(!TextUtils.isEmpty(data.clientMsgId)){
//				GenericRawResults<String[]> r=mDao.queryRaw("select id from ChatMessage where clientMsgId=?", data.clientMsgId);
//				String[] first=r.getFirstResult();
//				if(first!=null){
//					String idStr=first[0];
//					try {
//						data.id=Integer.parseInt(idStr);
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
//				}
				QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
				builder.where().eq(ChatMessagePo._clientMsgId,data.clientMsgId);
				ChatMessagePo po=builder.queryForFirst();
				if(po!=null){
					data.id=po.id;
					if(!updateTime && po.sendTime>=10000){
						data.sendTime=po.sendTime;
					}
				}
				mDao.createOrUpdate(data);
			}else{
				saveOtherMessage(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getLastMsgId(String groupId){
		String msgId = "";
		try {
			QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
			builder.orderBy(ChatMessagePo._sendTime, false);
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId, groupId).and().eq(ChatMessagePo._requestState, ChatMessagePo.REQ_STATES_SEND_OK);
			where.and().isNotNull(ChatMessagePo._msgId);
			ChatMessagePo msg = builder.queryForFirst();
			if (msg != null) {
				msgId = msg.msgId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msgId;
	}
	public String getFirstMsgId(String groupId){
		String msgId = "";
		try {
			QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
			builder.orderBy(ChatMessagePo._sendTime, true);
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId, groupId).and().eq(ChatMessagePo._requestState, ChatMessagePo.REQ_STATES_SEND_OK);
			builder.selectColumns(ChatMessagePo._msgId);
			ChatMessagePo msg = builder.queryForFirst();
			if (msg != null) {
				msgId = msg.msgId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msgId;
	}

	public List<ChatMessagePo> query(long maxTime, long count, String groupId) {
		QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
		try {
			builder.orderBy(ChatMessagePo._sendTime, false);
			builder.limit(count);
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId, groupId);
			if(maxTime!=0){
				where.and().lt(ChatMessagePo._sendTime, maxTime);
			}
			return builder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<ChatMessagePo> queryAfter(long maxTime, String groupId) {
		QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
		try {
			builder.orderBy(ChatMessagePo._sendTime, false);
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId, groupId);
			if(maxTime!=0){
				where.and().gt(ChatMessagePo._sendTime, maxTime);
			}
			return builder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void clearMsgInGroup(String groupId){
		DeleteBuilder<ChatMessagePo, Integer> b=mDao.deleteBuilder() ;
		try {
			b.where().eq(ChatMessagePo._groupId, groupId);
			b.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<ChatMessagePo> queryForType( String groupId,int type,boolean ascending) {
		QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
		try {
			builder.orderBy(ChatMessagePo._sendTime, ascending);
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId, groupId).and().eq(ChatMessagePo._type, type);
			return builder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public long countBetweenTime(String groupId,long start,long end){
		QueryBuilder<ChatMessagePo, Integer> builder = mDao.queryBuilder();
		try {
			Where<ChatMessagePo, Integer> where = builder.where();
			where.eq(ChatMessagePo._groupId,groupId).and().gt(ChatMessagePo._sendTime, start);
			if(end>0){
				where.and().le(ChatMessagePo._sendTime, end);
			}
			return builder.countOf(ChatMessagePo._id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public void retractMsg(String msgId){
		try {
			QueryBuilder<ChatMessagePo, Integer> qBuilder=mDao.queryBuilder();
			qBuilder.where().eq(ChatMessagePo._msgId, msgId);
			ChatMessagePo msg=qBuilder.queryForFirst();
			if(msg==null)return;
			msg.isRetract=1;
			mDao.update(msg);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void setDeleteFlag(ChatMessagePo msg){
		try {
			msg.deleteFlag=1;
			mDao.update(msg);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
