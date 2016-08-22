package com.dachen.imsdk.adapter;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.MultiItemCommonAdapter;
import com.dachen.common.adapter.MultiItemTypeSupport;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.bean.FileSizeBean;
import com.dachen.common.utils.BitmapUtils;
import com.dachen.common.utils.DisplayUtil;
import com.dachen.common.utils.QiNiuUtils;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.TimeUtils;
import com.dachen.common.utils.downloader.Downloader;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.R;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.activities.ChatImgActivity;
import com.dachen.imsdk.activities.FullViewUI;
import com.dachen.imsdk.archive.ArchiveUtils;
import com.dachen.imsdk.archive.download.ArchiveLoader;
import com.dachen.imsdk.archive.download.ArchiveTaskInfo;
import com.dachen.imsdk.archive.entity.ArchiveItem;
import com.dachen.imsdk.consts.ImConsts;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.db.po.SimpleUserInfo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.dachen.imsdk.entity.ChatMessageV2.ImageMsgParam;
import com.dachen.imsdk.entity.ChatMessageV2.VoiceMsgParam;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.entity.ImgTextMsgV2;
import com.dachen.imsdk.lisener.ImageLoadingFromUrlListener;
import com.dachen.imsdk.lisener.VideoDownloadListenerV2;
import com.dachen.imsdk.lisener.VoiceDownloadListenerV2;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.imsdk.service.ImSimpleUserInfoManager;
import com.dachen.imsdk.utils.HtmlUtils;
import com.dachen.imsdk.utils.ImSpUtils;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.imsdk.utils.audio.VoicePlayer;
import com.dachen.imsdk.views.IMsgEventListenerV2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatAdapterV2 extends MultiItemCommonAdapter<ChatMessagePo> implements VoicePlayer.OnMediaStateChange {
    public static final String TAG = ChatAdapterV2.class.getSimpleName();

    // item 类型的个数，必须为下面定义的item类型个数之和
    private static final int ITEM_TYPE_COUNT = 31;

    // item 类型
    private static final int VIEW_SYSTEM = 0;
    private static final int VIEW_FROM_ME_TEXT = 1;
    private static final int VIEW_TO_ME_TEXT = 2;
    private static final int VIEW_FROM_ME_IMAGE = 3;
    private static final int VIEW_TO_ME_IMAGE = 4;
    private static final int VIEW_FROM_ME_VOICE = 5;
    private static final int VIEW_TO_ME_VOICE = 6;
    private static final int VIEW_FROM_ME_LOCATION = 7;
    private static final int VIEW_TO_ME_LOCATION = 8;
    private static final int VIEW_FROM_ME_GIF = 9;
    private static final int VIEW_TO_ME_GIF = 10;
    private static final int VIEW_FROM_ME_VIDEO = 11;
    private static final int VIEW_TO_ME_VIDEO = 12;
    private static final int VIEW_FROM_ME_FILE = 13;
    private static final int VIEW_TO_ME_FILE = 14;
    private static final int VIEW_FROM_ME_MPT_STYLE6 = 15;
    private static final int VIEW_TO_ME_MPT_STYLE6 = 16;
    private static final int VIEW_FROM_ME_MPT_STYLE10 = 17;
    private static final int VIEW_TO_ME_MPT_STYLE10 = 18;
    private static final int VIEW_TO_ME_MPT_STYLE7 = 19;
    private static final int VIEW_FROM_ME_TEXT_AND_URI = 20;
    private static final int VIEW_TO_ME_TEXT_AND_URI = 21;
    private static final int VIEW_WHOLE_NEWMPT_16 = 22;
    private static final int VIEW_FROM_ME_MPT_STYLE16 = 23;
    private static final int VIEW_TO_ME_MPT_STYLE16 = 24;
    private static final int VIEW_TO_ME_NEWMPT_17 = 25;
    private static final int VIEW_WHOLE_MPT_STYLE6 = 26;
    private static final int VIEW_WHOLE_NEWMPT_18 = 27;
    private static final int VIEW_FROM_ME_MPT_STYLE8 = 28;
    private static final int VIEW_TO_ME_MPT_STYLE8 = 29;
    private static final int VIEW_FROM_ME_NEWMPT_17 = 30;

    /// 聊天类型，1-单聊 2-群聊
    public int mChatType = 1;
    private ChatActivityV2 mContext;
    private String mLoginUserId = ImSdk.getInstance().userId;

    private IMsgEventListenerV2 mMessageEventListener;
    private VoicePlayer mVoicePlayer;

    // 当前正在播放的声音消息的id
    private long mPlayVoiceId = -1;
    // 双击计算
    private long mLastTime = 0L;
    private long mCurTime = 0L;
    // 保存对方的信息，key-对方id，value-对方信息
    public Map<String, UserInfo> mUserInfo;
    private boolean showByRole;
    public ChatGroupPo groupInfo;
    private PopupWindow mFilePop;
    private int mFilePopHeight;
    //当前要处理的文件消息
//    private ArchiveItem mArchiveItem;
    private ChatMessagePo mArchiveMsg;
    public ImMsgHandler msgHandler;
    private ClipboardManager mClipboardManager;

    public ChatAdapterV2(ChatActivityV2 context, List<ChatMessagePo> data) {
        // super(context, data,new ChatItemType());
        super(context, data, null);
        mMultiItemTypeSupport = new ChatItemType();
        mContext = context;
        mVoicePlayer = new VoicePlayer();
        mVoicePlayer.setOnMediaStateChangeListener(this);
        mClipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        initPop();
    }
    private void initPop(){
//        final View v= LayoutInflater.from(mContext).inflate(R.layout.popwindow_im_down_file, null);
//        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        mFilePopHeight = v.getMeasuredHeight();
//
//        v.findViewById(R.id.im_archive_download).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ChatMessageV2.ArchiveMsgParam p = JSON.parseObject(mArchiveMsg.param, ChatMessageV2.ArchiveMsgParam.class);
//                final ArchiveItem item = new ArchiveItem(p.key, p.uri, p.name, p.format, p.size);
//                ArchiveLoader.getInstance().startDownload(item);
//                mFilePop.dismiss();
//                notifyDataSetChanged();
//            }
//        });
//        v.findViewById(R.id.im_archive_forward).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.startActivity(new Intent(mContext,ChatGroupActivity.class).putExtra(MsgMenuAdapter.INTENT_EXTRA_MSG_ID,mArchiveMsg.msgId));
//                mFilePop.dismiss();
//                notifyDataSetChanged();
//            }
//        });
//        mFilePop = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        mFilePop.setOutsideTouchable(true);
//        mFilePop.setBackgroundDrawable(new BitmapDrawable());
    }

    public void setUserInfo(Map<String, UserInfo> userInfo) {
        mUserInfo = userInfo;
    }

    public void setMsgHandler(ImMsgHandler msgHandler){
        if(msgHandler==null){
            msgHandler=new ImMsgHandler(mContext);
        }
        this.msgHandler=msgHandler;
    }
    @Override
    public void bind(ViewHolder holder, ChatMessagePo data) {
        handleCommonPart(holder, data);

        if(data.isRetract==1){
            handleRetractMessage(holder,data);
            return;
        }
        if (getItemViewType(holder.getPosition()) == VIEW_SYSTEM) {
            handleSystemMessage(holder, data);
            return;
        }
        handleLongClick(holder,data);

        if (getItemViewType(holder.getPosition()) == VIEW_WHOLE_NEWMPT_16) {
            showWholeMptStyle16Message(holder, data);
        }

        if (getItemViewType(holder.getPosition()) == VIEW_WHOLE_NEWMPT_18) {
            showWholeMptStyle18Message(holder, data);
        }

        if (data.isMySend()) {
            handleSentMessage(holder, data);
        } else {
            handleReceivedMessage(holder, data);
        }

        switch (getItemViewType(holder.getPosition())) {

            case VIEW_FROM_ME_TEXT:
            case VIEW_TO_ME_TEXT:
                showTextMessage(holder, data);
                addTextMessageListener(holder, data);

                break;
            case VIEW_FROM_ME_TEXT_AND_URI:
            case VIEW_TO_ME_TEXT_AND_URI:
                showTextAndUriMessage(holder, data);
                addTextAndUriMessageListener(holder, data);

                break;
            case VIEW_FROM_ME_IMAGE:
            case VIEW_TO_ME_IMAGE:
                showImageMessage(holder, data);
                addImageMessageListener(holder, data);

                break;
            case VIEW_FROM_ME_VOICE:
            case VIEW_TO_ME_VOICE:
                showVoiceMessage(holder, data);
                addVoiceMessageListener(holder, data);

                break;
            case VIEW_FROM_ME_LOCATION:
            case VIEW_TO_ME_LOCATION:
                showLocationMessage(holder, data);

                break;
            case VIEW_FROM_ME_GIF:
            case VIEW_TO_ME_GIF:
                showGifMessage(holder, data);

                break;
            case VIEW_FROM_ME_VIDEO:
            case VIEW_TO_ME_VIDEO:
                showVideoMessage(holder, data);
                addVideoMessageListener(holder, data);

                break;
            case VIEW_FROM_ME_FILE:
            case VIEW_TO_ME_FILE:
                showFileMessage(holder, data);

                break;
            case VIEW_FROM_ME_MPT_STYLE6:
            case VIEW_TO_ME_MPT_STYLE6:
                showMptStyle6Message(holder, data);
                addMptStyle6MessageListener(holder, data);
                break;
            case VIEW_WHOLE_MPT_STYLE6:
                showWholeMptStyle6Message(holder,data);
                addMptStyle6MessageListener(holder, data);
                break;
            case VIEW_FROM_ME_MPT_STYLE10:
            case VIEW_TO_ME_MPT_STYLE10:
                showMptStyle10Message(holder, data);
                addMptStyle10MessageListener(holder, data);
                break;

            case VIEW_TO_ME_MPT_STYLE7:
                showMptStyle7Message(holder, data);
                addMptStyle7MessageListener(holder, data);
                break;

            case VIEW_FROM_ME_MPT_STYLE16:
            case VIEW_TO_ME_MPT_STYLE16:
                showMptStyle16Message(holder, data);
            case VIEW_TO_ME_NEWMPT_17:
            case VIEW_FROM_ME_NEWMPT_17:
                showNewmpt17Message(holder, data);
                break;
            case VIEW_FROM_ME_MPT_STYLE8:
            case VIEW_TO_ME_MPT_STYLE8:
                showMptStyle8Message(holder,data);
                //showMptStyle6Message(holder, data);
                //addMptStyle6MessageListener(holder, data);
                break;
        }

    }

    private void showNewmpt17Message(ViewHolder holder, final ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt == null) {
            return;
        }
        holder.setText(R.id.title, mpt.title);
        holder.setText(R.id.content, mpt.digest);
        if (!TextUtils.isEmpty(mpt.pic)) {
            ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.teletext_icon),
                    new DisplayImageOptions.Builder().build());
        } else {
            // 患教资料设置默认图片
            holder.setImageResource(R.id.teletext_icon, R.drawable.set_list_up_press);
        }
        final String url = mpt.url;
        if (!TextUtils.isEmpty(url)) {
            holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgHandler.onClickNewMpt17(chatMessage, ChatAdapterV2.this, v);
                }
            });
        }
    }

    private void showMptStyle8Message(ViewHolder holder, final ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt == null) {
            return;
        }
        holder.setText(R.id.name, mpt.title);
        holder.setText(R.id.content, mpt.content);
        holder.setText(R.id.footer, mpt.footer);
        Map<String,String> param=getParam(chatMessage);
        if(param==null){
            holder.setVisibility(R.id.sex, View.GONE);
            holder.setVisibility(R.id.age, View.GONE);
        }
        String sex=param.get("sex");
        holder.setVisibility(R.id.sex, View.VISIBLE);
//        holder.setTextColor(R.id.age, Color.BLACK);
        holder.setText(R.id.age,param.get("age"));
        if("1".equals(sex)){
//            holder.setImageResource(R.id.sex,R.drawable.boy);
//            holder.setTextColor(R.id.age,0xff3889F5);
            holder.setText(R.id.sex,"男");
        }else if("2".equals(sex)){
            holder.setText(R.id.sex,"女");
//            holder.setImageResource(R.id.sex,R.drawable.girl);
//            holder.setTextColor(R.id.age,0xffFF7777);
        }else{
            holder.setVisibility(R.id.sex, View.GONE);
        }

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickMpt8(chatMessage, ChatAdapterV2.this, v);
            }
        });
    }

    /**
     * 显示style = 16的图文消息，
     */
    private void showWholeMptStyle16Message(ViewHolder holder, final ChatMessagePo chatMessage) {
//        MultiMpt multi = JSON.parseObject(chatMessage.param, MultiMpt.class);
//        ImgTextMsgV2 mpt = null;
//        List<ImgTextMsgV2> mptList = multi.list;
//        if (mptList != null && mptList.size() > 0) {
//            mpt = mptList.get(0);
//        }
//        if (mpt == null) {
//            return;
//        }
//        holder.setText(R.id.title, mpt.title);
//        if (mpt.time == null) {
//            holder.setVisibility(R.id.time, View.GONE);
//        } else {
//            holder.setText(R.id.time, TimeUtils.sk_time_long_to_true_time_str(mpt.time / 1000));
//        }
//        holder.setText(R.id.content, mpt.digest);
//        ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.image));
//        if (TextUtils.isEmpty(mpt.footer)) {
//            holder.setVisibility(R.id.ll_footer, View.GONE);
//        } else {
//            holder.setVisibility(R.id.ll_footer, View.VISIBLE);
//            holder.setText(R.id.tv_footer, mpt.footer);
//        }
//        final String url = mpt.url;
//        if (!TextUtils.isEmpty(url)) {
//            holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    msgHandler.onClickNewMpt16(chatMessage, ChatAdapterV2.this, v);
////                    Intent intent = new Intent(mContext, WebActivity.class);
////                    intent.putExtra("url", url);
////                    mContext.startActivity(intent);
//                }
//            });
//        } else {
//            holder.setOnClickListener(R.id.chat_warp_view, null);
//        }
        msgHandler.showNewMpt16Msg(chatMessage,holder);

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickNewMpt16(chatMessage, ChatAdapterV2.this, v);
            }
        });
    }

    private void showWholeMptStyle18Message(ViewHolder holder, final ChatMessagePo chatMessage) {

        msgHandler.showNewMpt18Msg(chatMessage,holder);

//        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                msgHandler.onClickNewMpt16(chatMessage, ChatAdapterV2.this, v);
//            }
//        });
    }


    /**
     * 显示style = 7的图文消息，这里为通知消息
     */
    private void showMptStyle7Message(ViewHolder holder, ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = JSON.parseObject(chatMessage.param, ImgTextMsgV2.class);
        if (mpt == null) {
            return;
        }

        holder.setText(R.id.title, mpt.title);
        if (mpt.time == null) {
            holder.setVisibility(R.id.time, View.GONE);
        } else {
            holder.setText(R.id.time, TimeUtils.sk_time_long_to_true_time_str(mpt.time / 1000));
        }
        holder.setText(R.id.content, mpt.content);
        holder.setVisibility(R.id.content_layout, View.VISIBLE);
        holder.setVisibility(R.id.info_layout, View.GONE);
        if (TextUtils.isEmpty(mpt.footer)) {
            holder.setVisibility(R.id.detail_layout, View.GONE);
        } else {
            holder.setVisibility(R.id.detail_layout, View.VISIBLE);
            holder.setText(R.id.detail, mpt.footer);
        }
        holder.setVisibility(R.id.time_txt, View.GONE);
    }

    /**
     * 添加文本消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addTextMessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {

//        holder.setOnClickListener(R.id.chat_text, new OnClickListener() {
        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 双击放大
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 400) {
//                    TextView textView = (TextView) v;
//                    if (textView != null) {
//                        FullViewUI.openUI(mContext, textView.getText().toString());
//                    }
                        FullViewUI.openUI(mContext, chatMessage.content);
                }
            }

        });
//        holder.setOnLongClickListener(R.id.chat_text, new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mClipboardManager.setPrimaryClip(ClipData.newPlainText("text",chatMessage.content));
//                ToastUtil.showToast(mContext,"文字已复制");
//                return true;
//            }
//        });

    }

    /**
     * 添加text-uri消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addTextAndUriMessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {
        holder.setOnClickListener(R.id.click_text, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickTextAndUri(chatMessage, ChatAdapterV2.this, v);
//                ChatMessageV2.TextAndUriMsgParam p = JSON.parseObject(chatMessage.param, ChatMessageV2.TextAndUriMsgParam.class);
//                String url = p.uri + "&access_token=" + UserSp.getInstance(mContext).getAccessToken("") + "&userType="
//                        + UserType.Doctor;
//                Intent intent = new Intent(mContext, WebActivity.class);
//                intent.putExtra("url", url);
//                mContext.startActivity(intent);
            }

        });

    }
    /**
     * 添加style=10的图文消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addMptStyle10MessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickMpt10(chatMessage, ChatAdapterV2.this, v);
            }
        });

        holder.setOnClickListener(R.id.action, new OnClickListener() {

            @Override
            public void onClick(View v) {
                msgHandler.onClickMpt10(chatMessage, ChatAdapterV2.this, v);
//				Map<String, String> param = getParam(chatMessage);
//				if (param == null) {
//					return;
//				}
//				String bizType = param.get("bizType");
//
//				// 进入支付页
//				if ("6".equals(bizType)) {
//					final String orderId = param.get("bizId");
//					Intent intent = new Intent(mContext, TelPackagePayActivity.class);
//					intent.putExtra("orderId", orderId);
//					ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
//					if (mpt != null) {
//						intent.putExtra("appointTime", mpt.getFooter());
//					}
//					mContext.startActivity(intent);
//					return;
//				}
            }
        });
    }

    /**
     * 添加style=6的图文消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addMptStyle6MessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {

            @Override
            public void onClick(View v) {
                msgHandler.onClickMpt6(chatMessage, ChatAdapterV2.this, v);
            }
        });
    }

    /**
     * 添加style=7的通知消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addMptStyle7MessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {

        holder.setOnClickListener(R.id.root_layout, new OnClickListener() {

            @Override
            public void onClick(View v) {
                msgHandler.onClickMpt7(chatMessage, ChatAdapterV2.this, v);
            }
        });
    }

    private void handleSystemMessage(ViewHolder holder, ChatMessagePo chatMessage) {
//        holder.setText(R.id.time_tv, TimeUtils.getDateTime(chatMessage.sendTime));
        holder.setText(R.id.chat_content_tv, chatMessage.content);
    }
    private void handleRetractMessage(ViewHolder holder, ChatMessagePo chatMessage) {
        msgHandler.showRetractMsg(chatMessage,holder,this);
    }

    private void showMptStyle10Message(ViewHolder holder, ChatMessagePo chatMessage) {
        msgHandler.showMpt10Msg(chatMessage,holder);
    }

    private void showWholeMptStyle6Message(ViewHolder holder, ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = JSON.parseObject(chatMessage.param, ImgTextMsgV2.class);
        if (mpt == null) {
            return;
        }

        holder.setText(R.id.title, mpt.title);
        if (mpt.time == null) {
            holder.setVisibility(R.id.time, View.GONE);
        } else {
            holder.setText(R.id.time, TimeUtils.sk_time_long_to_true_time_str(mpt.time / 1000));
        }
        holder.setText(R.id.content, mpt.content);
//        if (TextUtils.isEmpty(mpt.footer)) {
//            holder.setVisibility(R.id.detail_layout, View.GONE);
//        } else {
//            holder.setVisibility(R.id.detail_layout, View.VISIBLE);
//            holder.setText(R.id.detail, mpt.footer);
//        }
        if (!TextUtils.isEmpty(mpt.pic)) {
            ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.teletext_icon),
                    ImUtils.getNormalImageOptions());
        }
    }
    private void showMptStyle6Message(ViewHolder holder, ChatMessagePo chatMessage) {

        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt == null) {
            return;
        }

        holder.setText(R.id.title, mpt.title);
        holder.setText(R.id.content, mpt.content);
        if (!TextUtils.isEmpty(mpt.remark)) {
            holder.setText(R.id.remark, mpt.remark);
            holder.setVisibility(R.id.remark, View.VISIBLE);
            // 健康关怀需要价格转换
            Map<String, String> param = mpt.bizParam;
            if (param != null) {
                if ("10".equals(param.get("bizType"))) {
                    holder.setText(R.id.remark, "¥" + StringUtils.convertPrice(mpt.remark));
                }
            }

        } else {
            holder.setVisibility(R.id.remark, View.GONE);
        }

        if (!TextUtils.isEmpty(mpt.pic)) {
            ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.teletext_icon),
                    ImUtils.getNormalImageOptions());
        } else {
            // 患教资料设置默认图片
            Map<String, String> param = mpt.bizParam;
            if (param != null && "9".equals(param.get("bizType"))) {
                holder.setImageResource(R.id.teletext_icon, R.drawable.im_tool_patient_education_material_button_bg);
            }
        }

    }

    private void showMptStyle16Message(ViewHolder holder, ChatMessagePo chatMessage) {

        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt == null) {
            return;
        }

        holder.setText(R.id.title, mpt.title);
        holder.setText(R.id.content, mpt.content);
        holder.setText(R.id.remark, mpt.remark);

        if (!TextUtils.isEmpty(mpt.pic)) {
            ImageLoader.getInstance().displayImage(mpt.pic, (ImageView) holder.getView(R.id.teletext_icon),
                    new DisplayImageOptions.Builder().build());
        }

    }

    private void showFileMessage(ViewHolder holder, final ChatMessagePo data) {
        ChatMessageV2.ArchiveMsgParam p = JSON.parseObject(data.param, ChatMessageV2.ArchiveMsgParam.class);
        if (p == null)
            return;
        final ArchiveItem item = new ArchiveItem(p.key, p.uri, p.name, p.format, p.size);
        final ArchiveTaskInfo info = ArchiveLoader.getInstance().getInfo(item);
//        holder.setImageResource(R.id.teletext_icon, ArchiveUtils.getFileIcon(p.format));
        ImageView iv=holder.getView(R.id.teletext_icon);
        String mimeType = StringUtils.getMimeType(item.suffix);
        if (mimeType != null && mimeType.startsWith("image")) {
            ImageLoader.getInstance().displayImage(item.url + "?imageView2/3/h/100/w/100",iv, ImUtils.getNormalImageOptions());
        } else {
           iv.setImageResource(ArchiveUtils.getFileIcon(item.suffix));
        }
        int size = 0;
        try {
            size = Integer.parseInt(item.size);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        FileSizeBean bean = new FileSizeBean(size);
        if (info.state == ArchiveTaskInfo.STATE_IN_DOWNLOADIN) {
            holder.setVisibility(R.id.ll_downloading, View.VISIBLE);
            holder.setVisibility(R.id.ll_info, View.INVISIBLE);
            holder.setText(R.id.tv_size, bean.getSizeStr());
            holder.setText(R.id.tv_down_info, info.downLength / bean.unitByteLength + "/" + bean.getSizeStr());
            ProgressBar pb = holder.getView(R.id.progress_bar);
            pb.setMax(info.totalLength);
            pb.setProgress(info.downLength);
        } else {
            if(data.isMySend()){
                holder.setText(R.id.tv_has_down, "已发送");
            }else
            if (info.state == ArchiveTaskInfo.STATE_DOWNLOAD_OK) {
                holder.setText(R.id.tv_has_down, "已下载");
            } else {
                holder.setText(R.id.tv_has_down, "未下载");
            }
            holder.setText(R.id.tv_size, bean.getSizeStr());
            holder.setVisibility(R.id.ll_downloading, View.INVISIBLE);
            holder.setVisibility(R.id.ll_info, View.VISIBLE);
        }
        holder.setText(R.id.name, p.name);
        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.goArchiveItem(item,data);
            }
        });
        holder.setOnLongClickListener(R.id.chat_warp_view, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (info.state == ArchiveTaskInfo.STATE_DOWNLOAD_OK) {
//                    return true;
//                }
//                int[] pos = new int[2];
//                v.getLocationInWindow(pos);
////                mArchiveItem = item;
//                mArchiveMsg=data;
//                mFilePop.showAtLocation(v, Gravity.TOP, 0, pos[1] - mFilePopHeight);
                showMenu(data,info.state==ArchiveTaskInfo.STATE_NOT_DOWNLOAD);
                return true;
            }
        });
    }


    /**
     * 显示视频消息
     *
     * @param holder
     * @param chatMessage
     */
    private void showVideoMessage(ViewHolder holder, ChatMessagePo chatMessage) {
        if (chatMessage.isMySend() == false) {
            holder.setVisibility(R.id.video_progress, View.GONE);
            if (chatMessage.isRead() == false) {
                holder.setVisibility(R.id.unread_img_view, View.VISIBLE);
            } else {
                holder.setVisibility(R.id.unread_img_view, View.GONE);
            }
        }

        // 是否要去下载
        boolean isNeedDownLoad = true;
        String localFilePath = "";
        if (chatMessage.isMySend()) {
            localFilePath = ImSpUtils.getMsgFilePath(chatMessage.clientMsgId);
        }
        if (!TextUtils.isEmpty(localFilePath)) {
            File file = new File(localFilePath);
            if (file != null && file.exists()) {
                isNeedDownLoad = false;
            }
        }

        holder.setImageResource(R.id.chat_thumb, R.drawable.defaultpic);
        if (isNeedDownLoad) {
            // 去下载
            holder.getView(R.id.chat_thumb).setTag(chatMessage.id);// 设置Tag，防止在下载完成设置图片的时候，Item被其他视图回收使用了，覆盖其他的视图了
            VoiceMsgParam p = JSON.parseObject(chatMessage.param, VoiceMsgParam.class);
            Downloader.getInstance().addDownload(p.uri, (ProgressBar) holder.getView(R.id.video_progress),
                    new VideoDownloadListenerV2(chatMessage, (ImageView) holder.getView(R.id.chat_thumb),
                            mContext.dao));
        } else {
            // 不需要加载，直接拿本地的
            Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(localFilePath);
            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = ThumbnailUtils.createVideoThumbnail(localFilePath, Thumbnails.MINI_KIND);
                ImageLoader.getInstance().getMemoryCache().put(localFilePath, bitmap);
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                holder.setImageBitmap(R.id.chat_thumb, bitmap);
            } else {
                holder.setImageBitmap(R.id.chat_thumb, null);
            }
        }

    }

    private void showGifMessage(ViewHolder holder, ChatMessagePo data) {
        // TODO Auto-generated method stub

    }

    private void showLocationMessage(ViewHolder holder, ChatMessagePo data) {
        // TODO Auto-generated method stub

    }

    /**
     * 显示语音消息
     *
     * @param holder
     * @param chatMessage
     */
    private void showVoiceMessage(ViewHolder holder, ChatMessagePo chatMessage) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.getView(R.id.chat_voice)
                .getLayoutParams();
        VoiceMsgParam msgParam = JSON.parseObject(chatMessage.param, VoiceMsgParam.class);
        params.width = DisplayUtil.getVoiceViewWidth(mContext, StringUtils.getIntVal(msgParam.time));
        holder.getView(R.id.chat_voice).requestLayout();
        holder.setText(R.id.chat_voice_length, msgParam.time + "s");

        if (mPlayVoiceId == -1 || mPlayVoiceId != chatMessage.id) {
            holder.setVisibility(R.id.chat_voice_anim, View.GONE);
            holder.setVisibility(R.id.chat_voice_icon, View.VISIBLE);
        } else {
            holder.setVisibility(R.id.chat_voice_anim, View.VISIBLE);
            holder.setVisibility(R.id.chat_voice_icon, View.GONE);
            ImageView iv = holder.getView(R.id.chat_voice_anim);
            AnimationDrawable d = (AnimationDrawable) iv.getDrawable();
            d.start();
        }

        if (chatMessage.isMySend() == false) {
            holder.setVisibility(R.id.voice_progress, View.GONE);
            if (chatMessage.isRead() == false) {
                holder.setVisibility(R.id.unread_img_view, View.VISIBLE);
            } else {
                holder.setVisibility(R.id.unread_img_view, View.GONE);
            }

        }

        // 是否要去下载
        boolean voicefromDisk = false;
        String localFilePath = ImSpUtils.getMsgFilePath(chatMessage);
        if (!TextUtils.isEmpty(localFilePath)) {
            File file = new File(localFilePath);
            if (file != null && file.exists()) {
                voicefromDisk = true;
            }
        }
        if (!voicefromDisk) {
            FileInfo info = getFileUrl(msgParam);
            Downloader.getInstance().addDownload(info.oriUrl, (ProgressBar) holder.getView(R.id.voice_progress),
                    new VoiceDownloadListenerV2(chatMessage, mContext.dao));
        }

    }

    /**
     * 添加语音消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addVoiceMessageListener(final ViewHolder holder, final ChatMessagePo chatMessage) {

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDownload = false;
                String localFilePath = ImSpUtils.getMsgFilePath(chatMessage);
                if (!TextUtils.isEmpty(localFilePath)) {
                    File file = new File(localFilePath);
                    if (file != null && file.exists()) {
                        isDownload = true;
                    }
                }

                if (isDownload) {
                    if (chatMessage.isMySend() == false && chatMessage.isRead() == false) {
                        chatMessage.status = 1;
                        // isRead状态改变了，需要更新数据库
                        mContext.dao.saveMessage(chatMessage);

                        if (holder.getView(R.id.unread_img_view) != null) {
                            holder.setVisibility(R.id.unread_img_view, View.GONE);
                        }
                    }

                    play(holder, chatMessage, localFilePath);

                }
            }
        });

    }

    /**
     * 添加图片消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addImageMessageListener(ViewHolder holder, final ChatMessagePo chatMessage) {
        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, PhotoViewerActivity.class);
//                ImageMsgParam p = JSON.parseObject(chatMessage.param, ImageMsgParam.class);
//                FileInfo info = getFileUrl(p);
//                intent.putExtra(PhotoViewerActivity.INTENT_EXTRA_IMAGE_URL, info.oriUrl);
                Intent intent = new Intent(mContext, ChatImgActivity.class);
                if(groupInfo!=null){
                    intent.putExtra(ChatImgActivity.INTENT_CHAT_GROUP_ID, groupInfo.groupId);
                }
                intent.putExtra(ChatImgActivity.INTENT_TARGET_MSG, chatMessage);
                mContext.startActivity(intent);

            }
        });
    }

    /**
     * 添加视频消息监听器
     *
     * @param holder
     * @param chatMessage
     */
    private void addVideoMessageListener(final ViewHolder holder, final ChatMessagePo chatMessage) {

        holder.setOnClickListener(R.id.chat_warp_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (chatMessage.isMySend() == false && chatMessage.isRead() == false) {
//                    chatMessage.status = 1;
//                    // isRead状态改变了，需要更新数据库
//                    mContext.dao.saveMessage(chatMessage);
//
//                    if (holder.getView(R.id.unread_img_view) != null) {
//                        holder.setVisibility(R.id.unread_img_view, View.GONE);
//                    }
//                }
//
//                Intent intent = new Intent(mContext, VideoPlayActivity.class);
//                // 是否要去下载
//                boolean isNeedDownLoad = true;
//                String localFilePath = ImSpUtils.getMsgFilePath(chatMessage);
//                if (!TextUtils.isEmpty(localFilePath)) {
//                    File file = new File(localFilePath);
//                    if (file != null && file.exists()) {
//                        isNeedDownLoad = false;
//                    }
//                }
//                VoiceMsgParam p = JSON.parseObject(chatMessage.param, VoiceMsgParam.class);
//                if (isNeedDownLoad) {
//                    if (p.uri == null) {
//                        return;
//                    }
//                    intent.putExtra(AppConstant.EXTRA_FILE_PATH, p.uri);
//                } else {
//                    intent.putExtra(AppConstant.EXTRA_FILE_PATH, localFilePath);
//                }
//                mContext.startActivity(intent);
            }
        });

    }

    /**
     * 显示图片消息
     *
     * @param holder
     * @param chatMessage
     */
    private void showImageMessage(ViewHolder holder, ChatMessagePo chatMessage) {

        boolean imageFromDisk = false;
        String localFilePath = null;
        File file = null;
        if (chatMessage.isMySend() == true) {
            localFilePath = ImSpUtils.getMsgFilePath(chatMessage.clientMsgId);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file != null && file.exists()) {
                    imageFromDisk = true;
                } else {
                    ImSpUtils.setMsgFilePath(chatMessage.clientMsgId, null);
                }
            }
        }
        ImageMsgParam p = JSON.parseObject(chatMessage.param, ImageMsgParam.class);

        if (imageFromDisk) {
            ImageView chat_image = holder.getView(R.id.chat_image);
            int[] imageWH = BitmapUtils.getImageWH(localFilePath);
            if (imageWH != null && imageWH.length == 2) {
                imageWH[0] = DisplayUtil.dip2px(mContext, imageWH[0]);
                imageWH[1] = DisplayUtil.dip2px(mContext, imageWH[1]);
                setWidthAndHeight(chat_image, imageWH[0], imageWH[1]);
            }
            ImageLoader.getInstance().displayImage("file://" + localFilePath, chat_image,
                    new DisplayImageOptions.Builder().build());

        } else {
            FileInfo fileInfo = getFileUrl(p);
            if (!TextUtils.isEmpty(fileInfo.oriUrl)) {
                // 从服务器获取
                ImageView chat_image = holder.getView(R.id.chat_image);

                int[] imageWH = BitmapUtils.getImageWH(p.width, p.height);
                if (imageWH != null && imageWH.length == 2) {
                    imageWH[0] = DisplayUtil.dip2px(mContext, imageWH[0]);
                    imageWH[1] = DisplayUtil.dip2px(mContext, imageWH[1]);
                    setWidthAndHeight(chat_image, imageWH[0], imageWH[1]);
                }
                ImageLoader.getInstance().displayImage(fileInfo.smalUrl, chat_image,
                        new ImageLoadingFromUrlListener((ProgressBar) holder.getView(R.id.progress)));
            }
        }

    }

    /**
     * 显示文本消息
     *
     * @param holder
     * @param chatMessage
     */
    private void showTextMessage(ViewHolder holder, ChatMessagePo chatMessage) {
        String s = StringUtils.replaceSpecialChar(chatMessage.content);
        CharSequence charSequence = HtmlUtils.transform200SpanString(s.replaceAll("\n", "\r\n"), true);
        holder.setText(R.id.chat_text, charSequence);
    }

    /**
     * 显示text-uri消息
     *
     * @param holder
     * @param chatMessage
     */
    private void showTextAndUriMessage(ViewHolder holder, ChatMessagePo chatMessage) {
        String s = StringUtils.replaceSpecialChar(chatMessage.content);
        CharSequence charSequence = HtmlUtils.transform200SpanString(s.replaceAll("\n", "\r\n"), true);
        TextView textView = holder.getView(R.id.chat_text);
        textView.setText(charSequence);

        TextView clickText = holder.getView(R.id.click_text);
        clickText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        clickText.getPaint().setAntiAlias(true);// 抗锯齿

    }

    /**
     * 处理我接收的消息
     */
    private void handleReceivedMessage(ViewHolder holder, final ChatMessagePo chatMessage) {
        // 处理显示在右边的特殊情况
        holder.setVisibility(R.id.progress, View.GONE);
        holder.setVisibility(R.id.failed_img_view, View.GONE);
        msgHandler.handleReceivedHeadPic(chatMessage,holder,mChatType,groupInfo,mUserInfo);

        // 点击对方的头像
        holder.setOnClickListener(R.id.chat_head_iv, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickOtherUser(chatMessage, ChatAdapterV2.this);

            }
        });
        holder.setOnLongClickListener(R.id.chat_head_iv, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UserInfo u=msgHandler.getGroupUser(chatMessage,mChatType,mUserInfo);
                if(u!=null)
                    mContext.addAtPeople(u,false);
                return true;
            }
        });
    }

    /**
     * 处理我发给别人的消息
     */
    private void handleSentMessage(ViewHolder holder, final ChatMessagePo chatMessage) {

        // 我就是系统账号，那么显示系统头像
        if (mLoginUserId != null && mLoginUserId.equals(ImConsts.ID_SYSTEM_MESSAGE)) {
            holder.setImageResource(R.id.chat_head_iv, R.drawable.im_notice);
        } else {// 其他
            ImageView ivHead=holder.getView(R.id.chat_head_iv);
            String avatarUrl =ImSdk.getInstance().userAvatar;
            if (ivHead!=null&&!TextUtils.isEmpty(avatarUrl)) {
                ImageLoader.getInstance().displayImage(avatarUrl,  ivHead);
            }
        }
        // 点击自己的头像
        holder.setOnClickListener(R.id.chat_head_iv, new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.onClickMyself(chatMessage,ChatAdapterV2.this);
            }
        });

        switch (chatMessage.requestState) {
            case ChatMessagePo.REQ_STATES_SENDING:
            case ChatMessagePo.REQ_STATES_UP_FILE:
            case ChatMessagePo.REQ_STATES_UP_FILE_OK:
                holder.setVisibility(R.id.progress, View.VISIBLE);
                holder.setVisibility(R.id.failed_img_view, View.GONE);
                break;
            case ChatMessagePo.REQ_STATES_SEND_OK:
                holder.setVisibility(R.id.progress, View.GONE);
                holder.setVisibility(R.id.failed_img_view, View.GONE);
                break;
            case ChatMessagePo.REQ_STATES_SEND_FAIL:
            case ChatMessagePo.REQ_STATES_UP_FILE_FAIL:
                holder.setVisibility(R.id.progress, View.GONE);
                holder.setVisibility(R.id.failed_img_view, View.VISIBLE);
                break;
        }

        holder.setOnClickListener(R.id.failed_img_view, new OnClickListener() {
            @Override
            public void onClick(View v) {
                int rs=chatMessage.requestState;
                if ( (rs== ChatMessagePo.REQ_STATES_SEND_FAIL||rs==ChatMessagePo.REQ_STATES_UP_FILE_FAIL) && mMessageEventListener != null) {
                    mMessageEventListener.onSendAgain(chatMessage);
                }
            }
        });

    }

    /**
     * 处理所以消息中的共同部分
     */
    private void handleCommonPart(ViewHolder holder, ChatMessagePo chatMessage) {
        // 是否显示日期
        boolean showTime = true;
        if(chatMessage.sendTime<10000){
            showTime=false;
        }else
        if (holder.getPosition() >= 1) {
            // 上一条消息
            ChatMessagePo prevMessage = mData.get(holder.getPosition() - 1);
            long prevTime = prevMessage.sendTime / 1000;
            long nowTime = chatMessage.sendTime / 1000;
            // 小于15分钟，不显示 . IOS是2分钟.先改一样的.
            if (nowTime - prevTime < 2 * 60) {
                showTime = false;
            }
        }
        if (showTime) {
            holder.setVisibility(R.id.time_tv, View.VISIBLE);
//            holder.setText(R.id.time_tv, TimeUtils.getDateTime(chatMessage.sendTime));
            holder.setText(R.id.time_tv, TimeUtils.getMsgTimeStr(chatMessage.sendTime));
        } else {
            holder.setVisibility(R.id.time_tv, View.GONE);
        }

        // 显示昵称
        if (holder.getView(R.id.nick_name) == null) {
            // 图文通知消息没有昵称，直接返回
            return;
        }
        if(holder.getView(R.id.nick_name)==null)return;

        if (mChatType == 1) {// 单聊
            holder.setVisibility(R.id.nick_name, View.GONE);
        } else if (mChatType == ChatActivityV2.CHAT_TYPE_PUB) {
            if( chatMessage.fromUserId==null){
                chatMessage.fromUserId="";
            }
            if (chatMessage.fromUserId.equals(mLoginUserId)) {// 不显示自己昵称
                holder.setVisibility(R.id.nick_name, View.GONE);
                return;
            }
            if (!chatMessage.fromUserId.startsWith("pub_")) {
                if (groupInfo != null) {
                    holder.setText(R.id.nick_name, groupInfo.name);
                }
                return;
            }
            SimpleUserInfo info = ImSimpleUserInfoManager.getInstance().getUserInfoForId(chatMessage.fromUserId,
                    SimpleUserInfo.USER_TYPE_PUBLIC);
            if (info == null || TextUtils.isEmpty(info.userNick)) {
                holder.setText(R.id.nick_name, "");
            } else {
                holder.setText(R.id.nick_name, info.userNick);
            }
        } else {// 群聊
            if (chatMessage.fromUserId.equals(mLoginUserId)) {// 不显示自己昵称
                holder.setVisibility(R.id.nick_name, View.GONE);
            } else {// 显示对方昵称
                if (mUserInfo != null) {
                    UserInfo userInfo = mUserInfo.get(chatMessage.fromUserId);
                    if (userInfo != null && !TextUtils.isEmpty(userInfo.name)) {
                        holder.setVisibility(R.id.nick_name, View.VISIBLE);
                        holder.setText(R.id.nick_name, userInfo.name);
                    } else {
                        holder.setVisibility(R.id.nick_name, View.GONE);
                    }

                }
            }
        }

    }
    private void handleLongClick(ViewHolder holder, final ChatMessagePo chatMessage) {
        if(chatMessage.type==MessageType.file)return;
        holder.setOnLongClickListener(R.id.chat_warp_view, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenu(chatMessage);
                return true;
            }
        });
    }


    public void play(ViewHolder viewHolder, ChatMessagePo chatMessage, String path) {

        if (mPlayVoiceId == -1) {// 没有在播放
            String voicePath = path;// 内容即为路径
            try {
                mVoicePlayer.play(voicePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayVoiceId = chatMessage.id;
            // viewHolder.setVisibility(R.id.chat_voice_anim, View.VISIBLE);
            // viewHolder.setVisibility(R.id.chat_voice_icon, View.GONE);
        } else {
            if (mPlayVoiceId == chatMessage.id) {
                mVoicePlayer.stop();
                mPlayVoiceId = -1;
                // viewHolder.setVisibility(R.id.chat_voice_anim, View.GONE);
                // viewHolder.setVisibility(R.id.chat_voice_icon, View.VISIBLE);
            } else {// 正在播放别的， 在播放这个
                mVoicePlayer.keepStop();
                mPlayVoiceId = -1;

                String voicePath = path;// 内容即为路径
                try {
                    mVoicePlayer.play(voicePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPlayVoiceId = chatMessage.id;
                // viewHolder.setVisibility(R.id.chat_voice_anim, View.VISIBLE);
                // viewHolder.setVisibility(R.id.chat_voice_icon, View.GONE);
            }
        }
        notifyDataSetChanged();

    }

    /**
     * 播放声音
     */
    @Override
    public void onFinishPlay(MediaPlayer player) {
        mPlayVoiceId = -1;
        // if (mPlayVoiceViewHolder != null) {
        // mPlayVoiceViewHolder.setVisibility(R.id.chat_voice_anim, View.GONE);
        // mPlayVoiceViewHolder.setVisibility(R.id.chat_voice_icon,
        // View.VISIBLE);
        // }
        // mPlayVoiceViewHolder = null;
        notifyDataSetChanged();
    }

    @Override
    public void onErrorPlay() {
        mPlayVoiceId = -1;
        // if (mPlayVoiceViewHolder != null) {
        // mPlayVoiceViewHolder.setVisibility(R.id.chat_voice_anim, View.GONE);
        // mPlayVoiceViewHolder.setVisibility(R.id.chat_voice_icon,
        // View.VISIBLE);
        // }
        // mPlayVoiceViewHolder = null;
        notifyDataSetChanged();
    }

    @Override
    public void onSecondsChange(int seconds) {

    }

    /**
     * 设置聊天类型 1-单聊 2-群聊
     *
     * @param chatType
     */
    public void setChatType(int chatType) {
        mChatType = chatType;
        notifyDataSetChanged();
    }

    public void setMsgEventListener(IMsgEventListenerV2 listener) {
        mMessageEventListener = listener;
    }

    private void setWidthAndHeight(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * 重置声音播放器
     */
    public void reset() {
        if (mVoicePlayer != null) {
            mVoicePlayer.stop();
        }
    }

    /**
     * 获取ImgTextMsgV2中的param字段
     *
     * @param chatMessage
     * @return
     */
    private Map<String, String> getParam(ChatMessagePo chatMessage) {
        ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
        if (mpt != null) {
            return mpt.getBizParam();
        }
        return null;
    }

    /**
     * 获取ImgTextMsgV2
     *
     * @param chatMessage
     * @return
     */
    private ImgTextMsgV2 getImgTextMsgV2(ChatMessagePo chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        ImgTextMsgV2 mpt = JSON.parseObject(chatMessage.param, ImgTextMsgV2.class);
        return mpt;
    }

    /**
     * 提供多Item支持
     *
     * @author gaozhuo
     */
    public class ChatItemType implements MultiItemTypeSupport<ChatMessagePo> {
        @Override
        public int getLayoutId(int position, ChatMessagePo data) {
            switch (getItemViewType(position, data)) {
                case VIEW_FROM_ME_TEXT:
                    return R.layout.im_chat_from_item_text;

                case VIEW_TO_ME_TEXT:
                    return R.layout.im_chat_to_item_text;

                case VIEW_FROM_ME_TEXT_AND_URI:
                    return R.layout.im_chat_from_item_text_and_uri;

                case VIEW_TO_ME_TEXT_AND_URI:
                    return R.layout.im_chat_to_item_text_and_uri;

                case VIEW_FROM_ME_IMAGE:
                    return R.layout.im_chat_from_item_image;

                case VIEW_TO_ME_IMAGE:
                    return R.layout.im_chat_to_item_image;

                case VIEW_FROM_ME_VOICE:
                    return R.layout.im_chat_from_item_voice;

                case VIEW_TO_ME_VOICE:
                    return R.layout.im_chat_to_item_voice;

                case VIEW_FROM_ME_LOCATION:
                    return R.layout.im_chat_from_item_location;

                case VIEW_TO_ME_LOCATION:
                    return R.layout.im_chat_to_item_location;

                case VIEW_FROM_ME_GIF:
                    return R.layout.im_chat_from_item_gif;

                case VIEW_TO_ME_GIF:
                    return R.layout.im_chat_to_item_gif;

                case VIEW_FROM_ME_VIDEO:
                    return R.layout.im_chat_from_item_video;

                case VIEW_TO_ME_VIDEO:
                    return R.layout.im_chat_to_item_video;

                case VIEW_FROM_ME_FILE:
                    return R.layout.im_chat_from_item_file;

                case VIEW_TO_ME_FILE:
                    return R.layout.im_chat_to_item_file;

                case VIEW_FROM_ME_MPT_STYLE6:
                    return R.layout.im_chat_from_item_mpt_style6;

                case VIEW_TO_ME_MPT_STYLE6:
                    return R.layout.im_chat_to_item_mpt_style6;

                case VIEW_FROM_ME_MPT_STYLE10:
                    return R.layout.im_chat_from_item_mpt_style10;

                case VIEW_TO_ME_MPT_STYLE10:
                    return R.layout.im_chat_to_item_mpt_style10;

                case VIEW_TO_ME_MPT_STYLE7:// 图文通知
                    return R.layout.im_chat_to_item_mpt_style7;

                case VIEW_WHOLE_NEWMPT_16:
                    return R.layout.im_chat_whole_newmpt_16;

                case VIEW_FROM_ME_MPT_STYLE16:
                    return R.layout.im_chat_from_item_mpt_style16;

                case VIEW_TO_ME_MPT_STYLE16:
                    return R.layout.im_chat_to_item_mpt_style16;

                case VIEW_TO_ME_NEWMPT_17:
                    return R.layout.im_chat_to_item_newmpt_17;

                case VIEW_FROM_ME_NEWMPT_17:
                    return R.layout.im_chat_from_item_newmpt_17;

                case VIEW_WHOLE_MPT_STYLE6:
                    return R.layout.im_chat_whole_item_mpt_style6;

                case VIEW_WHOLE_NEWMPT_18:
                    return R.layout.im_chat_whole_newmpt_18;

                case VIEW_FROM_ME_MPT_STYLE8:
                    return R.layout.im_chat_from_item_mpt_style8;

                case VIEW_TO_ME_MPT_STYLE8:
                    return R.layout.im_chat_to_item_mpt_style8;
                default:
                    return R.layout.im_chat_item_system;
            }

        }

        @Override
        public int getViewTypeCount() {
            return ITEM_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position, ChatMessagePo data) {
            if(data.isRetract==1)
                return VIEW_SYSTEM;
            switch (data.type) {
                case MessageType.text:
                    return showOnRight(data) ? VIEW_FROM_ME_TEXT : VIEW_TO_ME_TEXT;

                case MessageType.text_and_uri:
                    return showOnRight(data) ? VIEW_FROM_ME_TEXT_AND_URI : VIEW_TO_ME_TEXT_AND_URI;

                case MessageType.image:
                    return showOnRight(data) ? VIEW_FROM_ME_IMAGE : VIEW_TO_ME_IMAGE;

                case MessageType.voice:
                    return showOnRight(data) ? VIEW_FROM_ME_VOICE : VIEW_TO_ME_VOICE;

                case MessageType.location:
                    return showOnRight(data) ? VIEW_FROM_ME_LOCATION : VIEW_TO_ME_LOCATION;

                case MessageType.gif:
                    return showOnRight(data) ? VIEW_FROM_ME_GIF : VIEW_TO_ME_GIF;

                case MessageType.video:
                    return showOnRight(data) ? VIEW_FROM_ME_VIDEO : VIEW_TO_ME_VIDEO;

                case MessageType.file:
                    return showOnRight(data) ? VIEW_FROM_ME_FILE : VIEW_TO_ME_FILE;

                case MessageType.image_and_text:

                    ImgTextMsgV2 mpt = JSON.parseObject(data.param, ImgTextMsgV2.class);
                    if (mpt == null) {
                        return showOnRight(data) ? VIEW_FROM_ME_TEXT : VIEW_TO_ME_TEXT;
                    }
                    // TODO
                    // List<ImgTextMsgV2> mptList = JSON.parseArray(data.param,
                    // ImgTextMsgV2.class);
                    // if (mptList != null && mptList.size() > 0) {
                    // mpt = mptList.get(0);
                    // }
                    if (mpt.style == 6) {
                        if ("0".equals(data.fromUserId)) {
                            return VIEW_WHOLE_MPT_STYLE6;
                        }
                        return showOnRight(data) ? VIEW_FROM_ME_MPT_STYLE6 : VIEW_TO_ME_MPT_STYLE6;
                    } else if (mpt.style == 10) {
                        return showOnRight(data) ? VIEW_FROM_ME_MPT_STYLE10 : VIEW_TO_ME_MPT_STYLE10;
                    } else if (mpt.style == 7) {
                        return VIEW_TO_ME_MPT_STYLE7;
                    } else if (mpt.style == 16) {
                        return showOnRight(data) ? VIEW_FROM_ME_MPT_STYLE16 : VIEW_TO_ME_MPT_STYLE16;
                    }else if (mpt.style == 8) {
                        return showOnRight(data) ? VIEW_FROM_ME_MPT_STYLE8 : VIEW_TO_ME_MPT_STYLE8;
                    }
                    break;
                case MessageType.newmpt16:
                    return VIEW_WHOLE_NEWMPT_16;
                case MessageType.newmpt17:
                    return showOnRight(data) ?VIEW_FROM_ME_NEWMPT_17: VIEW_TO_ME_NEWMPT_17;
                case MessageType.newmpt18:
                    return VIEW_WHOLE_NEWMPT_18;
                case MessageType.notification:
                    return VIEW_SYSTEM;
            }
            if("0".equals(data.fromUserId)){
                return VIEW_SYSTEM;
            }else{
                return showOnRight(data) ? VIEW_FROM_ME_TEXT : VIEW_TO_ME_TEXT;
            }
        }
    }

    public void setShowByRole(boolean showByRole) {
        this.showByRole = showByRole;
        notifyDataSetChanged();
    }

    private boolean showOnRight(ChatMessagePo po) {
        if (showByRole) {
            if (po.isMySend()) {
                return true;
            }
            if (mUserInfo == null) {
                return po.isMySend();
            }
            UserInfo myInfo = mUserInfo.get(ImUtils.getLoginUserId());
            UserInfo targetInfo = mUserInfo.get(po.fromUserId);
            if (myInfo == null || targetInfo == null || myInfo.role == 0) {
                return false;
            }
            return myInfo.role == targetInfo.role;
        } else {
            return po.isMySend();
        }
    }

    private class FileInfo {
        public String oriUrl;
        public String smalUrl;

        public FileInfo(String oriUrl, String smalUrl) {
            this.oriUrl = oriUrl;
            this.smalUrl = smalUrl;
        }
    }

    private FileInfo getFileUrl(ChatMessageV2.FileMsgBaseParam param) {
        if(TextUtils.isEmpty(param.uri)){
            String url = QiNiuUtils.getFileUrl(QiNiuUtils.BUCKET_MSG, param.key);
            return new FileInfo(url, url + QiNiuUtils.SURFIX_SMALL);
        }else{
            if (param.isPic()) {
                return new FileInfo(StringUtils.thumbnailUrl2originalUrl(param.uri), param.uri);
            } else {
                return new FileInfo(param.uri, param.uri);
            }
        }
    }
    private void showMenu(ChatMessagePo data){
        showMenu(data,false);
    }

    private void showMenu(ChatMessagePo data,boolean hasDownload){
        Dialog dialog=new Dialog(mContext,R.style.MsgMenuDialog);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        View v=mInflater.inflate(R.layout.msg_menu,mContext.mRootLayout,false);
        List<String> items=new ArrayList<>();
        if(data.type==MessageType.text||data.type==MessageType.text_and_uri)
            items.add(MsgMenuAdapter.ITEM_COPY);
        if(data.type==MessageType.file&&hasDownload){
            items.add(MsgMenuAdapter.ITEM_DOWNLOAD);
        }
        if(msgHandler.menuHasRetract()&&data.isMySend() &&data.type!=MessageType.voice)
            items.add(MsgMenuAdapter.ITEM_RETRACT);
        items.add(MsgMenuAdapter.ITEM_DELETE);
        if(msgHandler.menuHasForward()&& data.type!=MessageType.newmpt18 && data.msgId!=null)
            items.add(MsgMenuAdapter.ITEM_FORWARD);
        ListView lv = (ListView) v.findViewById(R.id.list_view);
        lv.setAdapter(new MsgMenuAdapter(data,mContext,items,this,dialog));
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
