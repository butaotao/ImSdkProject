package com.dachen.imsdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.R;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.entity.ImgTextMsgV2;
import com.dachen.imsdk.out.ImMsgHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * IM工具类
 *
 * @author mcp
 * @since 2015年12月14日
 */
public class ImUtils {

    private static DisplayImageOptions mNormalImageOptions;
    private static DisplayImageOptions mGalleyImageOptions;
    private static DisplayImageOptions mAvatarRoundImageOptions;
    private static DisplayImageOptions mAvatarNormalImageOptions;
    private static DisplayImageOptions mAvatarCircleImageOptions;

    /**
     * 清空聊天消息
     *
     * @param context
     * @param groupId
     */
    public static void clearMessage(Context context, String groupId) {
        ChatMessageDao dao = new ChatMessageDao(context, ImUtils.getLoginUserId());
        dao.clearMsgInGroup(groupId);
    }

    public static String getLoginUserId() {
        if (ImSdk.getInstance().userId == null) {
            return "";
        } else {
            return ImSdk.getInstance().userId;
        }
    }

    public static DisplayImageOptions getAvatarRoundImageOptions() {
        if (mAvatarRoundImageOptions == null) {
            mAvatarRoundImageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .resetViewBeforeLoading(true)
                    .displayer(new RoundedBitmapDisplayer(10))
                    .showImageForEmptyUri(R.drawable.ic_default_head)
                    .showImageOnFail(R.drawable.ic_default_head)
                    .showImageOnLoading(R.drawable.ic_default_head)
                    .build();
        }
        return mAvatarRoundImageOptions;
    }

    public static DisplayImageOptions getNormalImageOptions() {
        if (mNormalImageOptions == null) {
            mNormalImageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
//					.resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.image_download_fail_icon)
                    .showImageOnFail(R.drawable.image_download_fail_icon)
					.showImageOnLoading(R.drawable.image_download_fail_icon)
                    .build();
        }
        return mNormalImageOptions;
    }
    public static DisplayImageOptions getGalleyImageOptions() {
        if (mGalleyImageOptions == null) {
            mGalleyImageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
//					.resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.image_download_fail_icon)
                    .showImageOnFail(R.drawable.image_download_fail_icon)
//					.showImageOnLoading(R.drawable.image_download_fail_icon)
                    .build();
        }
        return mGalleyImageOptions;
    }

    public static DisplayImageOptions getAvatarNormalImageOptions() {
        if (mAvatarNormalImageOptions == null) {
            mAvatarNormalImageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Config.RGB_565)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.ic_default_head)
                    .showImageOnFail(R.drawable.ic_default_head)
                    .showImageOnLoading(R.drawable.ic_default_head)
                    .build();
        }
        return mAvatarNormalImageOptions;
    }

    public static String getSingleTargetId(ChatGroupPo po) {
        String targetId = "";
        if (TextUtils.isEmpty(po.groupUsers)) {
            return targetId;
        }
        List<UserInfo> list = JSON.parseArray(po.groupUsers, UserInfo.class);
        for (UserInfo chatGroupUser : list) {
            if (!chatGroupUser.id.equals(ImUtils.getLoginUserId())) {
                targetId = chatGroupUser.id;
                break;
            }
        }
        return targetId;
    }

    public static UserInfo getSingleTarget(ChatGroupPo po) {
        UserInfo info = null;
        if (TextUtils.isEmpty(po.groupUsers)) {
            return null;
        }
        List<UserInfo> list = JSON.parseArray(po.groupUsers, UserInfo.class);
        for (UserInfo chatGroupUser : list) {
            if (!chatGroupUser.id.equals(ImUtils.getLoginUserId())) {
                info = chatGroupUser;
                break;
            }
        }
        return info;
    }

    public static String getMsgDesc(ChatMessagePo msg){

        if(!TextUtils.isEmpty(msg.content))
            return msg.content;
        if(msg.type== MessageType.image)
            return "[图片]";
        if(msg.type== MessageType.audio)
            return "[语音]";
        if(msg.type== MessageType.file)
            return "[文件]";
        if(msg.type== MessageType.video)
            return "[视频]";
        String text="";
        if(msg.type==MessageType.image_and_text || msg.type==MessageType.newmpt17){
            ImgTextMsgV2 mpt= ImMsgHandler.getImgTextMsgV2(msg);
            if(mpt!=null)
                text=mpt.title;
        }
        if(msg.type==MessageType.newmpt16 || msg.type==MessageType.newmpt18){
            ImgTextMsgV2 mpt=ImMsgHandler.getMptInMulti(msg);
            if(mpt!=null)
                text=mpt.title;
        }
        return text;
    }
}
