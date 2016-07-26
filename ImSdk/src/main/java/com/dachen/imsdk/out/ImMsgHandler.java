package com.dachen.imsdk.out;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.DisplayUtil;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.TimeUtils;
import com.dachen.imsdk.R;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.adapter.ChatAdapterV2;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.db.po.SimpleUserInfo;
import com.dachen.imsdk.entity.GroupInfo2Bean;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.entity.ImgTextMsgV2;
import com.dachen.imsdk.entity.MultiMpt;
import com.dachen.imsdk.service.ImGroupUserInfoManager;
import com.dachen.imsdk.service.ImSimpleUserInfoManager;
import com.dachen.imsdk.utils.ImUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/1/21.
 */
public class ImMsgHandler {
    protected ChatActivityV2 mContext;

    public ImMsgHandler(ChatActivityV2 mContext) {
        this.mContext = mContext;
    }

    //    public void onMptMsgClick(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
//        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
//        if (mpt == null)
//            return;
//        if (mpt.style == 10) {
//            onClickMpt10(chatMessage, adapter, v);
//        }
//    }
    public void onClickOtherUser(ChatMessagePo chatMessage, ChatAdapterV2 adapter) {
    }

    public void onClickMyself(ChatMessagePo chatMessage, ChatAdapterV2 adapter) {
    }

    public void onClickMpt6(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickMpt7(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickMpt10(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickNewMpt17(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickNewMpt16(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickTextAndUri(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    public void onClickNewMpt18(ImgTextMsgV2 mpt){
    }

    public void onClickMpt8(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
    }

    protected Map<String, String> getParam(ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt != null) {
            return mpt.getBizParam();
        }
        return null;
    }

    public static ImgTextMsgV2 getImgTextMsgV2(ChatMessagePo chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        ImgTextMsgV2 mpt = JSON.parseObject(chatMessage.param, ImgTextMsgV2.class);
        return mpt;
    }

    public static ImgTextMsgV2 getMptInMulti(ChatMessagePo chatMessage) {
        if (chatMessage == null || TextUtils.isEmpty(chatMessage.param))
            return null;
        MultiMpt multi = JSON.parseObject(chatMessage.param, MultiMpt.class);
        ImgTextMsgV2 mpt = null;
        List<ImgTextMsgV2> mptList = multi.list;
        if (mptList != null && mptList.size() > 0) {
            mpt = mptList.get(0);
        }
        return mpt;
    }

    public static MultiMpt getMultiMpt(ChatMessagePo chatMessage) {
        if (chatMessage == null || TextUtils.isEmpty(chatMessage.param))
            return null;
        MultiMpt multi = JSON.parseObject(chatMessage.param, MultiMpt.class);
        return multi;
    }


    protected GroupInfo2Bean.Data.UserInfo getGroupUser(ChatMessagePo chatMessage, ChatAdapterV2 adapter) {
        return getGroupUser(chatMessage, adapter.mChatType, adapter.mUserInfo);
    }

    public GroupInfo2Bean.Data.UserInfo getGroupUser(ChatMessagePo chatMessage, int mChatType, Map<String, GroupInfo2Bean.Data.UserInfo> mUserInfo) {
        GroupInfo2Bean.Data.UserInfo userInfo = mUserInfo.get(chatMessage.fromUserId);
        if (userInfo == null && mChatType != ChatActivityV2.CHAT_TYPE_PUB) {
            userInfo = ImGroupUserInfoManager.getInstance().getUserInfoForId(chatMessage.groupId, chatMessage.fromUserId);
        }
        return userInfo;
    }

    public void handleReceivedHeadPic(ChatMessagePo chatMessage, ViewHolder holder, int mChatType, ChatGroupPo groupInfo, Map<String, GroupInfo2Bean.Data.UserInfo> mUserInfo) {
        if (chatMessage.fromUserId.equals("10000")) {// 好友是系统账号，那么显示系统头像
            holder.setImageResource(R.id.chat_head_iv, R.drawable.im_notice);
        } else if (mChatType == ChatActivityV2.CHAT_TYPE_PUB) {
            if (holder.getView(R.id.chat_head_iv) == null) {
                return;
            }
            if (!chatMessage.fromUserId.startsWith("pub_")) {
                if (groupInfo != null && !TextUtils.isEmpty(groupInfo.gpic)) {
                    ImageLoader.getInstance().displayImage(groupInfo.gpic,
                            (ImageView) holder.getView(R.id.chat_head_iv));
                }
                return;
            }
            SimpleUserInfo info = ImSimpleUserInfoManager.getInstance().getUserInfoForId(chatMessage.fromUserId,
                    SimpleUserInfo.USER_TYPE_PUBLIC);
            if (info == null || TextUtils.isEmpty(info.headUrl)) {
                holder.setImageResource(R.id.chat_head_iv, R.drawable.avatar_normal);
            } else {
                ImageLoader.getInstance().displayImage(info.headUrl, (ImageView) holder.getView(R.id.chat_head_iv));
            }

        } else {// 其他
            holder.setImageResource(R.id.chat_head_iv, R.drawable.avatar_normal);
            if (mUserInfo == null || "0".equals(chatMessage.fromUserId)) {
                return;
            }
            GroupInfo2Bean.Data.UserInfo userInfo = getGroupUser(chatMessage, mChatType, mUserInfo);
            if (userInfo == null) {
                return;
            }
            if (!TextUtils.isEmpty(userInfo.pic)) {
                ImageLoader.getInstance().displayImage(userInfo.pic, (ImageView) holder.getView(R.id.chat_head_iv));
            }
        }
    }

    public void showMpt10Msg(ChatMessagePo chatMessage, ViewHolder holder) {
        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt == null) {
            return;
        }

        holder.setText(R.id.title, StringUtils.handleNull(mpt.title));
        String content = mpt.content;
        //名片现在已经不再发送这个字段
//        if (TextUtils.isEmpty(content) && !TextUtils.isEmpty(mpt.footer)) {
//            content = mpt.footer.substring("线下执业地点：".length());
//        }
        holder.setText(R.id.content, StringUtils.handleNull(content));
        holder.setText(R.id.footer, StringUtils.handleNull(mpt.footer));
        if (!TextUtils.isEmpty(mpt.price)) {
            holder.setVisibility(R.id.price, View.VISIBLE);
            holder.setText(R.id.price, mpt.price);
        } else {
            holder.setVisibility(R.id.price, View.GONE);
        }
        if (!TextUtils.isEmpty(mpt.action)) {
            holder.setVisibility(R.id.action, View.VISIBLE);
            holder.setText(R.id.action, mpt.action);
        } else {
            holder.setVisibility(R.id.action, View.GONE);
        }
        String remark = mpt.remark;
        if (!TextUtils.isEmpty(remark)) {
            //正常情况下如果有两个数据，|分隔符是一定存在的，即使|两边为空值字符split[]也为2
            String[] split = remark.split("\\|", -1);
            if (split != null) {
                if(split.length == 2){
                    holder.setText(R.id.remark1, StringUtils.handleNull(split[0]));
                    holder.setText(R.id.remark2, StringUtils.handleNull(split[1]));
                }else if(split.length == 1){
                    holder.setText(R.id.remark1, StringUtils.handleNull(split[0]));
                }
            }
        }
        if (mpt.pic != null) {
            ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.teletext_icon),
                    ImUtils.getAvatarRoundImageOptions());
        }
        if (TextUtils.isEmpty(mpt.footer) && TextUtils.isEmpty(mpt.action)) {
            holder.setVisibility(R.id.ll_footer, View.GONE);
        }
    }

    public void showNewMpt16Msg(ChatMessagePo chatMessage, ViewHolder holder) {
        ImgTextMsgV2 mpt = getMptInMulti(chatMessage);
        if (mpt == null) return;
        holder.setText(R.id.title, mpt.title);
        if (mpt.time == null) {
            holder.setVisibility(R.id.time, View.GONE);
        } else {
            holder.setText(R.id.time, TimeUtils.sk_time_long_to_true_time_str(mpt.time / 1000));
        }
        holder.setText(R.id.content, mpt.digest);
        ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.image), ImUtils.getNormalImageOptions());
        if (TextUtils.isEmpty(mpt.footer)) {
            holder.setVisibility(R.id.ll_footer, View.GONE);
        } else {
            holder.setVisibility(R.id.ll_footer, View.VISIBLE);
            holder.setText(R.id.tv_footer, mpt.footer);
        }
    }

    public void showNewMpt18Msg(ChatMessagePo chatMessage, ViewHolder holder) {
        MultiMpt multiMpt = getMultiMpt(chatMessage);
        if(multiMpt == null || multiMpt.list == null || multiMpt.list.isEmpty()){
            return;
        }
        LinearLayout container = (LinearLayout) holder.getView(R.id.chat_warp_view);
        container.removeAllViews();
        final int size = multiMpt.list.size();
        for(int i = 0; i < size; i++){
            final ImgTextMsgV2 mpt = multiMpt.list.get(i);
            View itemView = null;
            if(mpt.style == 0){
                itemView = mContext.getLayoutInflater().from(mContext).inflate(R.layout.im_newmpt_item_big, null);
            }else {
                itemView = mContext.getLayoutInflater().from(mContext).inflate(R.layout.im_newmpt_item_small, null);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickNewMpt18(mpt);

                }
            });

            final ImageView pic = (ImageView) itemView.findViewById(R.id.pic);
            TextView title = (TextView) itemView.findViewById(R.id.title);
            ImageLoader.getInstance().displayImage(mpt.pic, pic, ImUtils.getNormalImageOptions());
            title.setText(mpt.title);
            container.addView(itemView);

            if(i != size - 1){//加入分割线
                View lineView = new View(mContext);
                lineView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 0.5F)));
                lineView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_d5d5d5));
                container.addView(lineView);
            }

        }

    }
    public void showRetractMsg(ChatMessagePo chatMessage, ViewHolder holder,ChatAdapterV2 adapter){
        holder.setText(R.id.chat_content_tv,  getRetractText(chatMessage,adapter));
    }
    public String getRetractText(ChatMessagePo chatMessage, ChatAdapterV2 adapter){
        String msg="撤回了一条消息";
        if(chatMessage.isMySend()){
            msg="你"+msg;
        }else{
            UserInfo u=getGroupUser(chatMessage,adapter);
            if(u!=null){
                msg="\""+u.name+"\""+msg;
            }
        }
        return msg;
    }

    public boolean menuHasForward(){
        return false;
    }
    public boolean menuHasRetract(){
        return false;
    }
}
