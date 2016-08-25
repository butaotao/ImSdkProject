package com.dachen.imsdk.db.dao;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.ImDbHelper;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data;
import com.dachen.imsdk.utils.ImUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatGroupDao {

    private Dao<ChatGroupPo, String> mDao;

    public ChatGroupDao(Context context, String userId) {
        mDao = makeDao(context, userId);
    }

    public ChatGroupDao() {
        mDao = makeDao(ImSdk.getInstance().context, ImUtils.getLoginUserId());
    }

    private synchronized Dao<ChatGroupPo, String> getDao() {
        if (mDao == null) {
            mDao = makeDao(ImSdk.getInstance().context, ImUtils.getLoginUserId());
        }
        return mDao;
    }

    private Dao<ChatGroupPo, String> makeDao(Context context, String userId) {
        try {
            return ImDbHelper.getInstance(context, userId).getDao(ChatGroupPo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveGroup(ChatGroupPo po) {
        try {
            ChatGroupPo old = mDao.queryForId(po.groupId);
            if (old != null) {
                po.draft = old.draft;
                if (po.bizStatus == 0|| po.param==null) {
                    po.bizStatus = old.bizStatus;
                    po.param = old.param;
                }
                mDao.update(po);
            } else {
                mDao.create(po);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatGroupPo> queryInType(Object[] bizTypes, Object[] chatTypes) {
        try {
            QueryBuilder<ChatGroupPo, String> b = mDao.queryBuilder();
            Where<ChatGroupPo, String> where = b.where();
            int andNum = 0;
            if (bizTypes != null && bizTypes.length > 0) {
                where.in(ChatGroupPo._bizType, bizTypes);
                ++andNum;
            }
            if (chatTypes != null && chatTypes.length > 0) {
                where.in(ChatGroupPo._type, chatTypes);
                ++andNum;
            }
            if (andNum > 0)
                where.and(andNum);
            b.orderBy(ChatGroupPo._top,false);
            b.orderBy(ChatGroupPo._updateStamp, false);
            return b.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<ChatGroupPo>();
    }

    public List<ChatGroupPo> queryInBizType(Object[] bizTypes) {
        return queryInType(bizTypes,null);
    }

    /**
     * @param bizTypes
     * @param exceptIds 要剔除的组ID
     * @return
     */
    public List<ChatGroupPo> queryInBizTypeExcept(Object[] bizTypes, Object[] exceptIds) {
        return queryInBizTypeExcept(bizTypes, exceptIds, null);
    }

    public List<ChatGroupPo> queryInBizTypeExcept(Object[] bizTypes, Object[] exceptIds, Integer exStatus) {
        try {
            QueryBuilder<ChatGroupPo, String> b = mDao.queryBuilder();
            Where<ChatGroupPo, String> where = b.where();
            int andNum = 0;
            if (bizTypes != null ) {
                where.in(ChatGroupPo._bizType, bizTypes);
                ++andNum;
            }
            if (exceptIds != null ) {
                ++andNum;
                where.notIn(ChatGroupPo._groupId, exceptIds);
            }
            if (exStatus != null) {
                ++andNum;
//				where.eq(ChatGroupPo._status+"&"+exStatus, exStatus);
                where.raw(ChatGroupPo._status + "&" + exStatus + "=" + exStatus);
            }
            if (andNum > 0)
                where.and(andNum);
            b.orderBy(ChatGroupPo._top,false);
            b.orderBy(ChatGroupPo._updateStamp, false);
            return b.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<ChatGroupPo>();
    }


    public List<ChatGroupPo> query(String bizType, Object[] orderStatus) {
        return query(bizType, orderStatus,false);
    }

    public List<ChatGroupPo> query(String bizType, Object[] orderStatus, boolean ascending) {
//        try {
//            QueryBuilder<ChatGroupPo, String> b = mDao.queryBuilder();
//            b.where().eq(ChatGroupPo._bizType, bizType).and().in(ChatGroupPo._bizStatus, orderStatus);
//            b.orderBy(ChatGroupPo._updateStamp, ascending);
//            return b.query();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<ChatGroupPo>();
        return query(new String[]{bizType},orderStatus,ascending,null);
    }

    public List<ChatGroupPo> query(Object[] bizTypes, Object[] orderStatus, boolean ascending, Object[] exceptIds) {
        try {
            QueryBuilder<ChatGroupPo, String> b = mDao.queryBuilder();
            Where<ChatGroupPo, String> where = b.where();
            int andNum = 0;
            if (bizTypes != null ) {
                where.in(ChatGroupPo._bizType, bizTypes);
                ++andNum;
            }
            if (orderStatus != null ) {
                where.in(ChatGroupPo._bizStatus, orderStatus);
                ++andNum;
            }
            if (exceptIds != null ) {
                ++andNum;
                where.notIn(ChatGroupPo._groupId, exceptIds);
            }
            if (andNum > 0)
                where.and(andNum);
            else{
                b.setWhere(null);
            }
            b.orderBy(ChatGroupPo._top,false);
            b.orderBy(ChatGroupPo._updateStamp, ascending);
            return b.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<ChatGroupPo>();
    }

    public void deleteById(String id) {
        try {
            mDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getMaxTs() {
        try {
            return mDao.queryRawValue("select max(updateStamp) from ChatGroup");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long getMinTs() {
        try {
            return mDao.queryRawValue("select min(updateStamp) from ChatGroup");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setUnreadZero(String groupId) {
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._unreadCount, 0).update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void clearNotifyInfo(String groupId) {
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._unreadCount, 0).updateColumnValue(ChatGroupPo._notifyParam, null).update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ChatGroupPo queryForId(String id) {
        try {
            return mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getUnreadCount(Object[] bizTypes) {
        return getUnreadCount(bizTypes,null,null);
    }

    public int getUnreadCountForId(Object[] ids) {
        return getUnreadCount(null,null,ids);
    }

    public int getUnreadCount(String bizType, Object[] orderStatus) {
        return getUnreadCount(new Object[]{bizType},orderStatus,null);
    }

    public int getUnreadCount( Object[] bizTypes, Object[] orderStatus,Object[] ids) {
        int sum = 0;
        try {
            QueryBuilder<ChatGroupPo, String> b = getDao().queryBuilder();
            b.selectRaw("sum(unreadCount)");
            Where<ChatGroupPo,String> where=b.where();
            where.gt(ChatGroupPo._unreadCount, 0);
            if(bizTypes!=null){
                where.and().in(ChatGroupPo._bizType, bizTypes);
            }
            if(orderStatus!=null){
                where.and().in(ChatGroupPo._bizStatus, orderStatus);
            }
            if(ids!=null){
                where.and().in(ChatGroupPo._groupId, ids);
            }
            String[] result= b.queryRawFirst();
            if(result!=null&&result.length==1){
                try {
                    sum=Integer.parseInt(result[0]);
                } catch (NumberFormatException e) {
//                    e.printStackTrace();
                }
            }

//            b.where().in(ChatGroupPo._bizType, bizTypes).and().in(ChatGroupPo._bizStatus, orderStatus).and().gt(ChatGroupPo._unreadCount, 0);
//            b.selectColumns(ChatGroupPo._unreadCount, ChatGroupPo._param, ChatGroupPo._bizStatus);
//            List<ChatGroupPo> tList = b.query();
//            for (ChatGroupPo po : tList) {
//                ChatGroupPo.ChatGroupParam p = JSON.parseObject(po.param, ChatGroupPo.ChatGroupParam.class);
//                //过滤掉未支付的图文订单
//                if(p != null && p.packType == 2 && po.bizStatus == 2){
//                    continue;
//                }
//                sum += po.unreadCount;
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public int groupCount(String bizType, Object[] orderStatus) {
        try {
            QueryBuilder<ChatGroupPo, String> b = getDao().queryBuilder();
            b.where().eq(ChatGroupPo._bizType, bizType).and().in(ChatGroupPo._bizStatus, orderStatus);
            return (int) b.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ChatGroupPo queryLatest(String bizType, Object[] orderStatus) {
        try {
            QueryBuilder<ChatGroupPo, String> b = getDao().queryBuilder();
            b.orderBy(ChatGroupPo._updateStamp, false);
            b.limit(1L);
            b.where().eq(ChatGroupPo._bizType, bizType).and().in(ChatGroupPo._bizStatus, orderStatus);
            return b.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveDraft(String groupId, String text) {
        if (TextUtils.isEmpty(groupId))
            return;
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._draft, text);
            b.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGroupParam(String groupId, int bizStatus, String paramStr) {
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._bizStatus, bizStatus);
            b.updateColumnValue(ChatGroupPo._param, paramStr);
            b.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGroupStatus(String groupId, int status) {
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._status, status);
            b.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveOldGroupInfo(Data groupInfo) {
        try {
            ChatGroupPo po = mDao.queryForId(groupInfo.gid);
            if (po != null) return;
            po = new ChatGroupPo();
            po.bizType = groupInfo.rtype;
            po.groupId = groupInfo.gid;
            po.name = groupInfo.gname;
            po.gpic = groupInfo.gpic;
            po.groupUsers = JSON.toJSONString(groupInfo.userList);
            po.type = groupInfo.type;
            mDao.create(po);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void changeContent(String groupId,String content){
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._lastMsgContent, content);
            b.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setTopFlag(String groupId,int flag){
        UpdateBuilder<ChatGroupPo, String> b = mDao.updateBuilder();
        try {
            b.where().eq(ChatGroupPo._groupId, groupId);
            b.updateColumnValue(ChatGroupPo._top, flag);
            b.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
