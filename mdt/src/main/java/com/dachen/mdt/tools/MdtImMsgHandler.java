package com.dachen.mdt.tools;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.async.SimpleResultListenerV2;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.adapter.ChatAdapterV2;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.imsdk.service.ImRequestManager;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.entity.DoctorInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/20.
 */
public class MdtImMsgHandler extends ImMsgHandler {

    public MdtImMsgHandler(ChatActivityV2 mContext) {
        super(mContext);
    }

    @Override
    public void onClickOtherUser(ChatMessagePo chatMessage, ChatAdapterV2 adapter) {
        fetchDocInfo(chatMessage.fromUserId);
    }
    public void fetchDocInfo(String userId){
        mContext.mDialog.show();
        SimpleResultListenerV2 listener=new SimpleResultListenerV2() {
            @Override
            public void onSuccess(String data) {
                mContext.mDialog.dismiss();
                DoctorInfo info= JSON.parseObject(data,DoctorInfo.class);
                showDocDialog(info);
            }

            @Override
            public void onError(String msg) {
                mContext.mDialog.dismiss();
                ToastUtil.showToast(mContext,msg);
            }
        };
        String url = UrlConstants.getUrl(UrlConstants.GET_DOCTOR_INFO);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("userId", userId);
        ImCommonRequest request = new ImCommonRequest(url, reqMap, ImRequestManager.makeSucListener( listener), ImRequestManager.makeErrListener(listener));
        VolleyUtil.getQueue(mContext).add(request);
    }

    private void showDocDialog(DoctorInfo info){
        Dialog dialog=new Dialog(mContext, R.style.MsgMenuDialog);
        View v= LayoutInflater.from(mContext).inflate(R.layout.dialog_doc_info,null);
        ViewHolder holder=ViewHolder.get(mContext,v);
        ImageView iv= holder.getView(R.id.iv_avatar);
        ImageLoader.getInstance().displayImage(info.avatar,iv);
        holder.setText(R.id.tv_name,info.name+"-"+info.department);
        holder.setText(R.id.tv_hospital,info.hospital);
        holder.setText(R.id.tv_title,info.title);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
