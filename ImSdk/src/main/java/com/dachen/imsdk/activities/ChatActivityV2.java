package com.dachen.imsdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.toolbox.DCommonRequest;
import com.dachen.common.utils.BitmapUtils;
import com.dachen.common.utils.FileUtils;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.common.utils.downloader.Downloader;
import com.dachen.gallery.CustomGalleryActivity;
import com.dachen.gallery.GalleryAction;
import com.dachen.imsdk.HttpErrorCode;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.R;
import com.dachen.imsdk.archive.ArchiveUtils;
import com.dachen.imsdk.archive.download.ArchiveLoader;
import com.dachen.imsdk.archive.entity.ArchiveItem;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.dachen.imsdk.entity.ChatMessageV2.AtMsgParam;
import com.dachen.imsdk.entity.ChatMessageV2.ImageMsgParam;
import com.dachen.imsdk.entity.ChatMessageV2.VoiceMsgParam;
import com.dachen.imsdk.entity.GroupInfo2Bean;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.entity.MoreItem;
import com.dachen.imsdk.entity.event.CloseChatEvent;
import com.dachen.imsdk.entity.event.GroupSettingEvent;
import com.dachen.imsdk.entity.event.MsgRetractEvent;
import com.dachen.imsdk.entity.fastreply.ReplyBean;
import com.dachen.imsdk.lisener.UserInfoChangeListener;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.MessagePollingV2;
import com.dachen.imsdk.net.MessagePollingV2.MessageReceivableV2;
import com.dachen.imsdk.net.MessageSenderV2;
import com.dachen.imsdk.net.MessageSenderV2.MessageSendCallbackV2;
import com.dachen.imsdk.net.PollingURLs;
import com.dachen.imsdk.net.SendEventRequest;
import com.dachen.imsdk.net.SessionGroup;
import com.dachen.imsdk.net.SessionGroup.SessionGroupCallback;
import com.dachen.imsdk.net.UploadEngine7Niu;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.imsdk.service.ImGroupUserInfoManager;
import com.dachen.imsdk.utils.CameraUtil;
import com.dachen.imsdk.utils.FileUtil;
import com.dachen.imsdk.utils.ImSpUtils;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.imsdk.views.ChatBottomView2;
import com.dachen.imsdk.views.ChatBottomView2.AtMsgSpan;
import com.dachen.imsdk.views.ChatContentViewV2;
import com.dachen.imsdk.views.IMsgEventListenerV2;
import com.dachen.imsdk.views.PullDownListView.RefreshingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.greenrobot1.event.EventBus;

/**
 * @author gaozhuo
 * @since 2015年10月09日
 */
public abstract class ChatActivityV2 extends ImBaseActivity implements MessageReceivableV2, MessageSendCallbackV2,
        ChatBottomView2.ChatBottomListener, IMsgEventListenerV2, UploadEngine7Niu.FileUploadObserver7Niu, SessionGroupCallback, OnClickListener {

    public static ChatActivityV2 instance;

    private static final String TAG = ChatActivityV2.class.getSimpleName();
    public static final int observer_msg_what_user_info_change = 111;
    // 单聊
    public static final int CHAT_TYPE_SINGLE = 1;
    // 群聊
    public static final int CHAT_TYPE_GROUP = 2;
    // 公众号
    public static final int CHAT_TYPE_PUB = 3;

    // 超时时间
    private static final int TIMEOUT = 20 * 1000;

    public static final String INTENT_EXTRA_GROUP_ID = "intent_extra_group_id";
    public static final String INTENT_EXTRA_GROUP_NAME = "intent_extra_group_name";
    public static final String INTENT_EXTRA_GROUP_PARAM="intent_extra_group_param";
    // 该参数仅用于群聊
    public static final String INTENT_EXTRA_GROUP_USER_LIST = "intent_extra_group_user_list";
    public static final String INTENT_EXTRA_USER_ID = "intent_extra_user_id";
    public static final String INTENT_EXTRA_ORDER_ID = "intent_extra_order_id";
    public static final String INTENT_EXTRA_PATIENT_ID = "intent_extra_patient_id";
    public static final String INTENT_EXTRA_NEED_SEND_MESSAGE = "intent_extra_need_send_message";
    public static final String INTENT_EXTRA_SHARE_PARAM = "intent_extra_share_param";

    private static final int MSG_UPDATE_BUSINESS = 101;
    private static final int MSG_REFRESH_LIST = 102;

    public static final int PAGE_SIZE = 20;

    protected String mGroupId;
    //	protected ChatGroupPo mGroupPo;
    protected List<UserInfo> mUserList;
    protected GroupInfo2Bean.Data mGroupInfo;
    protected String mUserId;
    // 该用户是否被从群聊组中移除
    private boolean mIsRemovedFromGroup = false;

    private AudioManager mAudioManager = null;
    // 存储聊天消息
    protected List<ChatMessagePo> mChatMessages = new ArrayList<ChatMessagePo>();

    // 消息轮询
    private MessagePollingV2 mMessagePolling;
    // 消息发送器
    private MessageSenderV2 mMessageSender;
    // 是否是第一次轮询
    protected boolean mIsFirstPoll = true;

    protected ChatContentViewV2 mChatContentView;
    protected ChatBottomView2 mChatBottomView;
    LinearLayout mHeaderContainer;
    protected LinearLayout mEmptyCotainer;
    protected LinearLayout mContentCotainer;
    protected LinearLayout mBtmExtraCotainer;
    protected LinearLayout mBtmExtraCotainer2;
    public LinearLayout mRootLayout;

    protected TextView mTitle;

    protected ImageButton mRightMenu;

    public ChatMessageDao dao;
    public ChatGroupDao groupDao;

    private String loginUserId;
    protected ChatGroupPo groupPo;
    private long refreshTs;
    private long startMsgId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d("UploadEnginePL", "activity = " + this);

        setContentView(R.layout.activity_base_chat_v2);
        loginUserId = ImSdk.getInstance().userId;
        dao = new ChatMessageDao(mThis, loginUserId);
        groupDao = new ChatGroupDao(mThis, loginUserId);
        initView();
        init();
        loadHeaderView();
        getGroupInfo();

        loadMessageFromDB(0);
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mChatContentView = (ChatContentViewV2) findViewById(R.id.contentView);
        mChatBottomView = (ChatBottomView2) findViewById(R.id.bottomView);
        mHeaderContainer = (LinearLayout) findViewById(R.id.header_container);
        mEmptyCotainer = (LinearLayout) findViewById(R.id.empty_container);
        mContentCotainer = (LinearLayout) findViewById(R.id.content_container);
        mBtmExtraCotainer = (LinearLayout) findViewById(R.id.bottom_extra_container);
        mBtmExtraCotainer2 = (LinearLayout) findViewById(R.id.bottom_extra_container2);
        mRootLayout = (LinearLayout) findViewById(R.id.main);

        mChatContentView.setMessageEventListener(this);
        mChatContentView.setRefreshListener(refreshListener);
        mChatContentView.setData(mChatMessages, chatType());
        mChatContentView.setShowByRole(showByRole());
        mChatContentView.setMsgHandler(makeMsgHandler());

        mChatBottomView.setChatBottomListener(this);
        mChatBottomView.setMorePanelData(makeMorePanelData());
        if(chatType()==CHAT_TYPE_GROUP)
            mChatBottomView.setCanAt(true);
        setListenerToRootView();
    }

    public void onEventMainThread(ArchiveLoader.DownloadChangeEvent event) {
        refreshListDelay(500);
    }

    private void loadHeaderView() {
        View header = onLoadHeaderLayout(mHeaderContainer);
        // 默认header view
        if (header == null) {
            header = LayoutInflater.from(this).inflate(R.layout.common_header, mHeaderContainer, false);
            header.findViewById(R.id.back_btn).setOnClickListener(this);
            mTitle = (TextView) header.findViewById(R.id.title);
            mRightMenu = (ImageButton) header.findViewById(R.id.right_menu);
            mRightMenu.setOnClickListener(this);
            setTitle(getIntent().getStringExtra(INTENT_EXTRA_GROUP_NAME));
        }

        onHeaderLayoutLoaded(header);
        mHeaderContainer.addView(header);
    }

    /**
     * 由子类覆盖，返回header view
     *
     * @param parent header view要加入的父容器
     * @return
     */
    protected View onLoadHeaderLayout(ViewGroup parent) {
        return null;
    }

    /**
     * 由子类覆盖，view为子类加载的header view，可以在此方法中对header view进行操作 比如findViewById()等
     *
     * @param view 子类加载的header view
     */
    protected void onHeaderLayoutLoaded(View view) {

    }

    protected void setBottomPadding(int popupWindowHeight) {
        mChatBottomView.hideAll();
        mRootLayout.setPadding(mRootLayout.getPaddingLeft(), mRootLayout.getPaddingTop(), mRootLayout.getPaddingRight(),
                popupWindowHeight - mChatBottomView.getHeight());
        scrollToBottom();
    }

    protected void setTopPadding(int popupWindowHeight) {
        mChatContentView.setPadding(mChatContentView.getPaddingLeft(), popupWindowHeight, mChatContentView.getPaddingRight(),
                mChatContentView.getPaddingBottom());
    }

    protected void setTopPadding(final PopupWindow popupWindow) {
        if (popupWindow == null || popupWindow.getContentView() == null) {
            return;
        }
        //强制测量，注意：这种方法只对wrap_content的宽高才有效
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        popupWindow.getContentView().measure(widthMeasureSpec, heightMeasureSpec);
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setTopPadding(popupWindow.getContentView().getMeasuredHeight());
            }
        });
    }

    protected void resetTopPadding() {
        mChatContentView.setPadding(mChatContentView.getPaddingLeft(), 0, mChatContentView.getPaddingRight(),
                mChatContentView.getPaddingBottom());
    }

    protected void resetBottomPadding() {
        mRootLayout.setPadding(mRootLayout.getPaddingLeft(), mRootLayout.getPaddingTop(), mRootLayout.getPaddingRight(),
                0);
        scrollToBottom();
    }

    private void scrollToBottom() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mChatContentView.scrollToBottom();
            }
        });
    }

    /**
     * 聊天类型：单聊 CHAT_TYPE_SINGLE 群聊 CHAT_TYPE_GROUP
     *
     * @return
     */
    protected int chatType() {
        return CHAT_TYPE_SINGLE;
    }

    /**
     * 返回底部+号面板的页数，由子类实现，默认1页
     *
     * @return
     */
    protected int getMorePanelCount() {
        return 1;
    }

    /**
     * 返回底部+号面板每一页所需的数据，由子类实现
     *
     * @param page 第几页
     * @return
     */
    protected abstract List<MoreItem> getMorePanelData(int page);

    private SparseArray<List<MoreItem>> makeMorePanelData() {
        int count = getMorePanelCount();
        if (count <= 0) {
            return null;
        }
        SparseArray<List<MoreItem>> data = new SparseArray<List<MoreItem>>();
        for (int i = 0; i < count; i++) {
            List<MoreItem> items = getMorePanelData(i);
            data.put(i, items);
        }
        return data.size() > 0 ? data : null;
    }

    /**
     * 更新底部+号面板数据
     */
    protected void updateMorePanelData() {
        mChatBottomView.setMorePanelData(makeMorePanelData());
        mChatBottomView.updateMorePanel();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getGroupInfo() {
        // 单聊
        if (chatType() == 1) {
            if (mGroupId == null) {// 创建会话
                SessionGroup sessionGroup = new SessionGroup(this);
                sessionGroup.setCallback(this);
                List<String> userIds = new ArrayList<String>();
                userIds.add(mUserId);
                sessionGroup.createGroup(userIds);
            } else {// 已经建立了会话
//				SessionMessageDB sessionMessage = SessionMessageDBDao.getSessionDBFromGroupId(mGroupId);

                if (groupPo != null && !TextUtils.isEmpty(groupPo.gpic)) {// 从会话数据库获取
                    mChatContentView.setUserInfo(makeUserInfoMap(groupPo));
                } else {// 请求web
                    SessionGroup sessionGroup = new SessionGroup(this);
                    sessionGroup.setCallback(this);
                    sessionGroup.getGroupInfo(mGroupId);
                }

            }
        } else {// 群聊
            if (mUserList == null) {
//				SessionGroup sessionGroup = SessionGroup.getInstance(this);
//				sessionGroup.setCallback(this);
//				sessionGroup.getGroupInfo(mGroupId);
            } else {
                mChatContentView.setUserInfo(userList2UserInfoMap(mUserList));
            }
        }
    }

    protected HashMap<String, UserInfo> userList2UserInfoMap(List<UserInfo> userList) {
        if (userList == null) {
            return null;
        }
        HashMap<String, UserInfo> userInfoMap = new HashMap<String, UserInfo>();
        for (UserInfo user : userList) {
            userInfoMap.put(user.id, user);
        }
        return userInfoMap;
    }

    private void init() {
        mGroupId = getIntent().getStringExtra(INTENT_EXTRA_GROUP_ID);
        if (mGroupId != null) {
            groupPo = groupDao.queryForId(mGroupId);
            if (groupPo != null) {
                mChatBottomView.getChatEdit().setText(groupPo.draft);
                fetchBizStatus();
            }
        }
        mUserList = (List<UserInfo>) getIntent().getSerializableExtra(INTENT_EXTRA_GROUP_USER_LIST);
        if (mUserList == null && groupPo != null) {
            mUserList = JSON.parseArray(groupPo.groupUsers, UserInfo.class);
        }
        mUserId = getIntent().getStringExtra(INTENT_EXTRA_USER_ID);
        // 从会话数据库获取

        mChatContentView.setGroupInfo(groupPo);
        if (mUserId == null && mGroupId != null) {
//			SessionMessageDB sessionMessage = SessionMessageDBDao.getSessionDBFromGroupId(mGroupId);
            if (groupPo != null) {
//				mUserId = sessionMessage.userIds;
                mUserId = ImUtils.getSingleTargetId(groupPo);
            }
        }

        initMessagePolling();

        initMessageSender();

        mAudioManager = (AudioManager) getSystemService(android.app.Service.AUDIO_SERVICE);

        Downloader.getInstance()
                .init(ImSdk.getInstance().mAppDir + File.separator
                        + ImSdk.getInstance().userId + File.separator
                        + Environment.DIRECTORY_MUSIC);
    }

    private Map<String, UserInfo> makeUserInfoMap(ChatGroupPo po) {
        if (po == null || mUserId == null) {
            return null;
        }
        List<UserInfo> tList = JSON.parseArray(po.groupUsers, UserInfo.class);
        Map<String, UserInfo> userInfoMap = new HashMap<String, UserInfo>();
        for (UserInfo info : tList) {
            userInfoMap.put(info.id, info);
        }
        return userInfoMap;
    }

    private void initMessageSender() {
        mMessageSender = MessageSenderV2.getInstance(this);
        mMessageSender.setMessageSendCallbackListener(this);
    }

    private void initMessagePolling() {
        mMessagePolling = new MessagePollingV2(mThis, dao);
        mMessagePolling.setMessageReceiverListener(this);
        mMessagePolling.setGroupId(mGroupId);
    }

    private UserInfoChangeListener groupUserChangeListener = new UserInfoChangeListener() {
        @Override
        public void onUserChange() {
            runOnUiThread(new Runnable() {
                public void run() {
                    mChatContentView.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        Map<String, UserInfo> userInfoMap = userList2UserInfoMap(mUserList);
        // 被从群组删除
        if (userInfoMap != null && !userInfoMap.containsKey(ImSdk.getInstance().userId)) {
            mIsRemovedFromGroup = true;
            mChatBottomView.setVisibility(View.GONE);
            removedFromGroup(true);
        }
        pollImmediately();
        updateBusinessNow();
        ImGroupUserInfoManager.getInstance().setListener(groupUserChangeListener);
//		startMessagePolling();
    }

    @Override
    protected void onDestroy() {
        if (this == instance)
            instance = null;
        super.onDestroy();

        mChatBottomView.recordCancel();
        String text = mChatBottomView.getChatEdit().getEditableText().toString();
        groupDao.saveDraft(mGroupId, text);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
//        instance = null;
        mChatContentView.reset();
        baseHandler.removeMessages(MSG_UPDATE_BUSINESS);
        mChatBottomView.hideKeyboard();
        super.onPause();
    }

    @Override
    public void finish() {
        groupDao.clearNotifyInfo(mGroupId);
        super.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /***************
     * ChatContentView的回调
     ***************************************/
    @Override
    public void onMyAvatarClick() {

    }

    @Override
    public void onFriendAvatarClick(String friendUserId) {

    }

    @Override
    public void onMessageClick(ChatMessagePo msg) {
    }

    @Override
    public void onMessageLongClick(ChatMessagePo msg) {
    }

    @Override
    public void onEmptyTouch() {
        if(mChatBottomView.inRecordVoice)return;
        mChatBottomView.reset();
    }

    /**
     * 重发消息
     */
    @Override
    public void onSendAgain(ChatMessagePo chatMessage) {
        if (mChatMessages.remove(chatMessage)) {
            sendMessage(chatMessage);
        }
    }

    /**
     * 发送消息
     *
     * @param chatMessage
     */
    protected boolean sendMessage(ChatMessagePo chatMessage) {

        if (mGroupId == null) {
            return false;
        }

        // 退出群，不能发消息
        if (chatType() == 2 && mIsRemovedFromGroup) {
            ToastUtil.showToast(this, R.string.disable_send_message);
            return false;
        }

        addExtraInfo(chatMessage);
        // 保存到本地数据库(重发消息的时候只需要更新，因为消息已经在数据库里面了)
        dao.saveClientMessage(chatMessage, true);

        mChatMessages.add(chatMessage);
        mChatContentView.notifyDataSetChanged(true);

        // 上传文件
        if (chatMessage.type == MessageType.voice || chatMessage.type == MessageType.image
                || chatMessage.type == MessageType.video) {

            // 先上传服务器，再发送XMPP消息
            if (!chatMessage.isUploaded()) {
//				UploadEngineV2.uploadFile(chatMessage, this);
                UploadEngine7Niu.uploadFile(chatMessage, this,null);
            } else {
                mMessageSender.sendMessage(chatMessage);
            }
        } else {
            mMessageSender.sendMessage(chatMessage);
        }
        return true;
    }

    private void addExtraInfo(ChatMessagePo chatMessage) {
        // 这两个参数是调用发送接口必须的
        chatMessage.fromUserId = ImSdk.getInstance().userId;
        chatMessage.groupId = mGroupId;
        if (TextUtils.isEmpty(chatMessage.clientMsgId)) {
            chatMessage.clientMsgId = makeClientId();
        }
        //
        if (mChatMessages.size() > 0) {
            chatMessage.sendTime = mChatMessages.get(mChatMessages.size() - 1).sendTime + 1;
        } else {
            chatMessage.sendTime = 1;
        }
        chatMessage.status = 0;
//        chatMessage.requestState = MessageSendStatus.ing;
        chatMessage.requestState = ChatMessagePo.REQ_STATES_SENDING;
    }

    @Override
    public void sendText(EditText editText) {
        String text=editText.getEditableText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        AtMsgSpan[] spans= editText.getEditableText().getSpans(0,text.length(), AtMsgSpan.class);
        AtMsgParam p=new AtMsgParam();
        p.notify_to=new ArrayList<>();
        for(AtMsgSpan span:spans){
            p.notify_type=1;
            if(span.isAll){
                p.notify_to_all=true;
                break;
            }
            p.notify_to.add(span.userId);
        }
        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.text;
        chatMessage.content = text;
        chatMessage.param=JSON.toJSONString(p);
        sendMessage(chatMessage);
    }

    @Override
    public void sendGif(String text) {
        // if (TextUtils.isEmpty(text)) {
        // return;
        //
        // ChatMsgEntity entity = new ChatMsgEntity();
        // entity.setMsgType(ChatMsgType.chatMsgTypeGif);
        // entity.setMsgContent(text);
        // entity.setFromUserName(mLoginNickName);
        // entity.setFromUserId(mLoginUserId);
        // entity.setTimeSend(TimeUtils.getCurrentTime());
        //
        // entity.setMsgIsSend(1);
        //
        // mChatMessages.add(entity);
        // mChatContentView.notifyDataSetInvalidated(true);
        // sendMessage(entity);

    }

    @Override
    public void sendVoice(String filePath, int timeLen) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.voice;
        chatMessage.clientMsgId = makeClientId();
        ImSpUtils.setMsgFilePath(chatMessage.clientMsgId, filePath);
//		chatMessage.localFilePath = filePath;
        VoiceMsgParam p = new VoiceMsgParam();
        p.name = FileUtils.getFileName(filePath);
        p.time = timeLen + "";
        chatMessage.param = JSON.toJSONString(p);
        sendMessage(chatMessage);

    }

    protected void sendImage(File file) {
        if (!file.exists()) {
            return;
        }

        String filePath = file.getAbsolutePath();
        long fileSize = file.length();
        Bitmap srcImg = BitmapFactory.decodeFile(filePath);

        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.image;
        chatMessage.clientMsgId = makeClientId();
        ImSpUtils.setMsgFilePath(chatMessage.clientMsgId, filePath);
//		chatMessage.localFilePath = filePath;
        ImageMsgParam p = new ImageMsgParam();
        p.name = FileUtils.getFileName(filePath);
        p.size = fileSize + "";
        p.width = srcImg.getWidth();
        p.height = srcImg.getHeight();
        chatMessage.param = JSON.toJSONString(p);
        sendMessage(chatMessage);
    }

    protected void sendVideo(String filePath, long fileSize, long timeLen) {

        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.video;
        chatMessage.clientMsgId = makeClientId();
        VoiceMsgParam p = new VoiceMsgParam();
        p.name = FileUtils.getFileName(filePath);
        p.time = timeLen + "";
        chatMessage.param = JSON.toJSONString(p);
        sendMessage(chatMessage);

    }

    protected void sendArchive(ArchiveItem item) {
        if (TextUtils.isEmpty(item.fileId)) {
            ToastUtil.showToast(this, "文件ID为空,无法发送");
            return;
        }
        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.file;
        ChatMessageV2.ArchiveMsgParam p = new ChatMessageV2.ArchiveMsgParam();
        p.name = item.name;
        p.key = item.fileId;
        p.uri = item.url;
        p.size = item.size;
        p.format = item.suffix;
        chatMessage.param = JSON.toJSONString(p);
        sendMessage(chatMessage);
    }

    protected void sendFile(File f) {
    }

    protected void sendLocate(double latitude, double longitude, String address) {
        // ChatMessage message = new ChatMessage();
        // message.setType(XmppMessage.TYPE_LOCATION);
        // message.setFromUserName(mLoginNickName);
        // message.setFromUserId(mLoginUserId);
        // message.setTimeSend(TimeUtils.sk_time_current_time());
        // message.setLocation_x(latitude+"");
        // message.setLocation_y(longitude+"");
        // message.setContent(address);
        // mChatMessages.add(message);
        // mChatContentView.notifyDataSetInvalidated(true);
        // sendMessage(message);
    }


    /**
     * 点击发送图片
     */
    protected void clickPhoto() {
//        CameraUtil.pickImageSimple(this, REQUEST_CODE_PICK_PHOTO);
        CustomGalleryActivity.openUi(this, true, REQUEST_CODE_PICK_PHOTO);
        mChatBottomView.reset();

    }

    /**
     * 点击调用相机
     */
    protected void clickCamera() {
        mNewPhotoUri = CameraUtil.getOutputMediaFileUri(this, CameraUtil.MEDIA_TYPE_IMAGE);
        CameraUtil.captureImage(this, mNewPhotoUri, REQUEST_CODE_CAPTURE_PHOTO);
        mChatBottomView.reset();

    }

    /**
     * 点击发送录像
     */
    protected void clickVideo() {
//        Intent intent = new Intent(this, LocalVideoActivity.class);
//        intent.putExtra(AppConstant.EXTRA_ACTION, AppConstant.ACTION_SELECT);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    /**
     * 点击发送文件
     */
    protected void clickFile() {
//        Intent intent = new Intent(this, MemoryFileManagement.class);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 点击发送位置
     */
    protected void clickLocation() {
//        Intent intent = new Intent(this, SendBaiDuLocate.class);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATE);
    }

    /**
     * 点击发送快捷回复
     */
    protected void clickQiuckReply() {
        // 快捷回复
        // FastReplyUI.openUI(context);
        Intent intent = new Intent(this, FastReplyUI.class);
        startActivityForResult(intent, REQUEST_CODE_QIUCK_REPLY);
    }

    /**
     * 点击文件
     */
    protected void clickArchive() {
//        startActivityForResult(new Intent(this, ArchiveMainUi.class), REQUEST_CODE_ARCHIVE);
    }

    @Override
    public void onPanelItemClick(int drawableId) {
        if (drawableId == R.drawable.im_tool_photo_button_bg) {
            clickPhoto();
        } else if (drawableId == R.drawable.im_tool_camera_button_bg) {
            clickCamera();
        } else if (drawableId == R.drawable.im_tool_quickreply_button_bg) {
            clickQiuckReply();
        }
    }

    @Override
    public void goAtSomeone() {
        startActivityForResult(new Intent(this,AtChatMemberActivity.class).putExtra(INTENT_EXTRA_GROUP_ID,mGroupId),REQUEST_CODE_AT_PEOPLE);
    }

    /***********************
     * 拍照和选择照片
     **********************/
    private static final int REQUEST_CODE_CAPTURE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static final int REQUEST_CODE_SELECT_VIDEO = 3;
    private static final int REQUEST_CODE_SELECT_FILE = 4;
    private static final int REQUEST_CODE_SELECT_LOCATE = 5;
    private static final int REQUEST_CODE_QIUCK_REPLY = 8;
    public static final int REQUEST_CODE_ARCHIVE = 13;
    private static final int REQUEST_CODE_SELECT_FORWARD = 14;
    private static final int REQUEST_CODE_AT_PEOPLE = 15;

    protected Uri mNewPhotoUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ARCHIVE) {
            if (resultCode != RESULT_OK) return;
            ArchiveItem item = (ArchiveItem) data.getSerializableExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM);
            if (item == null)
                return;
            sendArchive(item);
        } else if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {// 拍照返回
            if (mNewPhotoUri != null) {
                try {
                    sendImage(FileUtil.compressImageToFile2(mNewPhotoUri.getPath(), 50));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.showToast(this, R.string.c_take_picture_failed);
            }

        } else if (requestCode == REQUEST_CODE_PICK_PHOTO) {// 选择一张图片,
            if (resultCode != Activity.RESULT_OK) return;
            String[] all_path = data.getStringArrayExtra(GalleryAction.INTENT_ALL_PATH);
            if (all_path == null) return;
            boolean isOrigin = data.getBooleanExtra(GalleryAction.INTENT_IS_ORIGIN, false);
            for (String path : all_path) {
                int degree = BitmapUtils.readPictureDegree(path);
                Logger.d(TAG, "degree=" + degree);
                if (isOrigin) {
                    sendImage(new File(path));
                } else {
                    try {
                        sendImage(FileUtil.compressImageToFile2(path, 50));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
//            if (data != null && data.getData() != null) {
//                int degree = BitmapUtils.readPictureDegree(CameraUtil.getImagePathFromUri(this, data.getData()));
//                Logger.d(TAG, "degree=" + degree);
//
//                try {
//                    sendImage(FileUtil.compressImageToFile2(CameraUtil.getImagePathFromUri(this, data.getData()), 50));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                ToastUtil.showToast(this, R.string.c_photo_album_failed);
//            }
        } else if (requestCode == REQUEST_CODE_SELECT_VIDEO && resultCode == RESULT_OK) {// 选择视频的返回
//            if (data == null) {
//                return;
//            }
//            String filePath = data.getStringExtra(AppConstant.EXTRA_FILE_PATH);
//            long timeLen = data.getLongExtra(AppConstant.EXTRA_TIME_LEN, 0);
//            long fileSize = data.getLongExtra(AppConstant.EXTRA_FILE_SIZE, 0);
//            if (TextUtils.isEmpty(filePath)) {
//                ToastUtil.showToast(this, R.string.select_failed);
//                return;
//            }
//            File file = new File(filePath);
//            if (!file.exists()) {
//                ToastUtil.showToast(this, R.string.select_failed);
//                return;
//            }
//            sendVideo(filePath, fileSize, timeLen);
        } else if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == RESULT_OK) {// 选择文件的返回
//            String filePath = data.getStringExtra(AppConstant.FILE_PAT_NAME);
//            if (TextUtils.isEmpty(filePath)) {
//                ToastUtil.showToast(this, R.string.select_failed);
//                return;
//            }
//            File file = new File(filePath);
//            if (!file.exists()) {
//                ToastUtil.showToast(this, R.string.select_failed);
//                return;
//            }
//            sendFile(file);
        } else if (requestCode == REQUEST_CODE_SELECT_LOCATE && resultCode == RESULT_OK) {
//            double latitude = data.getDoubleExtra(AppConstant.EXTRA_LATITUDE, 0);
//            double longitude = data.getDoubleExtra(AppConstant.EXTRA_LONGITUDE, 0);
//            String address = DApplication.getUniqueInstance().getBdLocationHelper().getAddress();
//            if (latitude != 0 && longitude != 0 && !TextUtils.isEmpty(address)) {
//                sendLocate(latitude, longitude, address);
//            } else {
//                ToastUtil.showToast(context, "请把定位开启!");
//            }
        } else if (requestCode == REQUEST_CODE_QIUCK_REPLY && resultCode == RESULT_OK) {// 快捷回复
            if (data == null) {
                return;
            }
            ReplyBean replyBean = (ReplyBean) data.getSerializableExtra("replyBean");
            if (replyBean != null) {
                mChatBottomView.getChatEdit().setText(replyBean.replyContent);
                mChatBottomView.morePanelToKeyboard();
                mChatBottomView.getChatEdit().setFocusableInTouchMode(true);
                mChatBottomView.getChatEdit().requestFocus();
                mChatBottomView.getChatEdit().setSelection(mChatBottomView.getChatEdit().getText().length());
            }
        }else if(requestCode==REQUEST_CODE_AT_PEOPLE){
            if(resultCode!=RESULT_OK)return;
            UserInfo u= (UserInfo) data.getSerializableExtra(AtChatMemberActivity.INTENT_USER_INFO);
            mChatBottomView.addAtPeople(u);
        }
    }
    public void addAtPeople(UserInfo u,boolean fromInput){
        mChatBottomView.addAtPeople(u,fromInput);
    }

    /**
     * 文件上传回调
     */
    @Override
    public void onFileUploadSuccess(ChatMessagePo chatMessage) {
        chatMessage.requestState = ChatMessagePo.REQ_STATES_UP_FILE_OK;
        dao.saveClientMessage(chatMessage);
        mMessageSender.sendMessage(chatMessage);
    }

    @Override
    public void onFileUploadFailure(ChatMessagePo chatMessage) {

        chatMessage.requestState = ChatMessagePo.REQ_STATES_UP_FILE_FAIL;
        // 消息状态变了，更新数据库
        dao.saveClientMessage(chatMessage);
        mChatContentView.notifyDataSetChanged(false);

    }

    /**
     * 从web获取消息
     */
    private void getOldMessageFromWeb() {
        final String reqTag = "getMessageFromWeb";
        RequestQueue mRequestQueue = VolleyUtil.getQueue(this);
        mRequestQueue.cancelAll(reqTag);
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", ImSdk.getInstance().accessToken);
        params.put("userId", ImSdk.getInstance().userId);
        params.put("groupId", mGroupId == null ? "" : mGroupId);
        params.put("type", mChatMessages.size() > 0 ? "1" : "0");
//        params.put("msgId", mChatMessages.size() > 0 ? mChatMessages.get(0).msgId : "");
        params.put("msgId",dao.getFirstMsgId(mGroupId));
        params.put("cnt", PAGE_SIZE + "");
        Logger.d(TAG, "getMessageFromWeb param=" + params);
        StringRequest request = new ImCommonRequest(PollingURLs.getMessageV2(), params, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, "getMessageFromWeb response=" + response);
                mChatContentView.headerRefreshingCompleted();
                handleOldMsgResponse(response);
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mChatContentView.headerRefreshingCompleted();

            }

        });

        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 0, 0));
        request.setTag(reqTag);
        mRequestQueue.add(request);
    }

    /**
     * 处理http请求返回结果
     *
     * @param response
     */
    private void handleOldMsgResponse(String response) {
        ResultTemplate<ChatMessageV2> receivedMessage = JSON.parseObject(response,
                new TypeReference<ResultTemplate<ChatMessageV2>>() {
                });
        if (receivedMessage == null || receivedMessage.resultCode != HttpErrorCode.successed
                || receivedMessage.data == null || receivedMessage.data.msgList == null) {
            return;
        }

        List<ChatMessagePo> list = receivedMessage.data.msgList;
        int len = list.size();
        for (int i = len - 1; i >= 0; i--) {
            ChatMessagePo msg = list.get(i);
//			ChatMessagePo chatMessage = chatMsg2Po(msg);
            msg.requestState = ChatMessagePo.REQ_STATES_SEND_OK;
            saveMessage(msg);
            mChatMessages.add(0, msg);
        }
        if (!receivedMessage.data.more && mChatMessages.size() > 0) {
            mChatContentView.setNeedRefresh(false);
            ImSpUtils.setFirstMsgId(mGroupId, mChatMessages.get(0).msgId);
        }
        mChatContentView.notifyDataSetChanged(false);
        if (len > 0) {
            mChatContentView.setSelection(len);
        }

    }

    /**
     * 从这里接收消息
     */
    @Override
    public void receivedMessage(ChatMessageV2 receivedMessage) {

        if (receivedMessage.msgList != null && receivedMessage.msgList.size() > 0) {
            for (ChatMessagePo msg : receivedMessage.msgList) {
//				boolean needAdd=true;
//				if(mChatMessages.size()>0&&msg.sendTime< mChatMessages.get(mChatMessages.size()-1).sendTime){
//					needAdd=false;
//				}
//				if(needAdd&&msg.isMySend()&&!TextUtils.isEmpty( msg.clientMsgId) ){
                int toRemove = -1;
                if (msg.isMySend() && !TextUtils.isEmpty(msg.clientMsgId)) {
                    for (int i = mChatMessages.size() - 1; i >= 0; i--) {
                        if (msg.clientMsgId.equals(mChatMessages.get(i).clientMsgId)) {
                            mChatMessages.get(i).msgId=msg.msgId;
                            toRemove = i;
                            break;
                        }
                    }
                }
                if (toRemove == -1) {
                    mChatMessages.add(msg);
//                    mChatMessages.remove(toRemove);
                } else {

                }
//				ChatMessagePo chatMessage = chatMsg2Po(messagePL);
                // 保存消息到数据库
                msg.requestState = ChatMessagePo.REQ_STATES_SEND_OK;
                saveMessage(msg);

                receivedMessage(msg);
            }
            mChatContentView.notifyDataSetChanged(true);

        }
        if (receivedMessage.more) {
            pollImmediately();
        }
    }

    /**
     * 收到单条消息时调用，给子类继承
     *
     * @param chatMessage
     */
    protected void receivedMessage(ChatMessagePo chatMessage) {

    }

    private void loadMessageFromDB(long time) {
        if (mGroupId == null) {
            return;
        }
        List<ChatMessagePo> list = dao.query(time, PAGE_SIZE, mGroupId);
        if (list != null) {
            for (ChatMessagePo chatMessage : list) {
                if(chatMessage.deleteFlag==1)continue;
                mChatMessages.add(0, chatMessage);
                Logger.d(TAG, "chatMessage from DB=" + chatMessage);
            }
        }
        mChatContentView.notifyDataSetChanged(true);
//		mChatContentView.setSelection(mChatMessages.size()-1);
    }

    /**
     * 将收到的消息保存到数据库,重复的消息不保存
     *
     * @param po
     */
    private void saveMessage(ChatMessagePo po) {
        if (po == null) {
            return;
        }
        if (ImUtils.getLoginUserId().equals(po.fromUserId) && !TextUtils.isEmpty(po.groupId)) {
            dao.saveClientMessage(po,true);
        } else {
            dao.saveOtherMessage(po);
        }

    }


    @Override
    public void sendSuccessed(ChatMessagePo msg, String groudId, String msgId, long time) {
        Logger.d(TAG, "----------->发送成功");

        msg.requestState = ChatMessagePo.REQ_STATES_SEND_OK;
//        msg.msgId = msgId;
        msg.sendTime = time;
        mChatContentView.notifyDataSetChanged(true);
        dao.saveClientMessage(msg,true);
//        long test=dao.countBetweenTime(groudId,0,0);
//        ToastUtil.showToast(this,"test:"+test);
    }

    @Override
    public void sendFailed(ChatMessagePo msg, int resultCode, String resultMsg) {
        Logger.d(TAG, "----------->发送失败");
        msg.requestState = ChatMessagePo.REQ_STATES_SEND_FAIL;
        dao.saveClientMessage(msg);
        mChatContentView.notifyDataSetChanged(true);
        // 10010表示被踢出了群
//        if (resultCode == 10010) {
//            return;
//        }

    }

    @Override
    public void onGroupInfo(GroupInfo2Bean.Data data, int what) {
        if (data == null) {
            return;
        }
        mGroupInfo = data;
        if (groupPo == null) {
            groupPo = makeGroupPo(data);
            fetchBizStatus();
        }
        // 单聊
        if (chatType() == 1) {
            if (what == SessionGroup.CREATE) {
                mGroupId = data.gid;
                // 从数据库加载消息
                loadMessageFromDB(0);
                mMessagePolling.setGroupId(mGroupId);
                pollImmediately();
            }
        }
        if (chatType() != CHAT_TYPE_PUB) {
            mUserList = Arrays.asList(data.userList);
            mChatContentView.setUserInfo(userList2UserInfoMap(mUserList));
            mChatContentView.notifyDataSetChanged(false);
        }

    }

    private ChatGroupPo makeGroupPo(GroupInfo2Bean.Data data) {
        ChatGroupPo po = new ChatGroupPo();
        po.groupId = data.gid;
        po.groupUsers = JSON.toJSONString(data.userList);
        po.bizType = data.rtype;
        po.gpic = data.gpic;
        po.param = "{}";
        return po;
    }

    @Override
    public void onGroupInfoFailed(String msg) {
    }

    /**
     * 当成员被移除会话组时调用
     *
     * @param isRemoved
     */
    protected void removedFromGroup(boolean isRemoved) {

    }

    /**
     * 业务数据，子类可以复写改方法获取需要的业务数据
     */
    protected void onBusinessData() {

    }

    /**
     * 得到对方的UserList
     *
     * @param userList
     * @return
     */
    private static GroupInfo2Bean.Data.UserInfo getUserList(GroupInfo2Bean.Data.UserInfo[] userList) {
        if (userList == null) {
            return null;
        }
        String ownerUserId = ImUtils.getLoginUserId();
        if (ownerUserId == null) {
            return null;
        }
        for (GroupInfo2Bean.Data.UserInfo _u : userList) {
            if (_u != null && _u.id != null && ownerUserId.equalsIgnoreCase(_u.id) == false) {
                return _u;
            }
        }
        return null;
    }

    /**
     * 不等延迟，立刻轮询
     */
    public void pollImmediately() {
        if(TextUtils.isEmpty(ImSdk.getInstance().accessToken))
            return;
        mMessagePolling.pollImmediately();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back_btn) {
            onBackClick(v);
        } else if (i == R.id.right_menu) {
            onRightMenuClick(v);
        } else {
        }
    }

    /**
     * 返回键点击事件
     *
     * @param v
     */
    protected void onBackClick(View v) {
        finish();
    }

    /**
     * header view 右侧菜单点击事件，由子类实现
     *
     * @param v
     */
    protected void onRightMenuClick(View v) {

    }

    /**
     * 设置header view 右侧菜单的图片资源id，由子类调用
     *
     */
    protected void setRightMenuImageResource(int resId) {
        if (mRightMenu != null) {
            mRightMenu.setImageResource(resId);
        }
    }

    /**
     * 设置右侧菜单的可见性
     *
     */
    protected void setRightMenuVisibility(int visibility) {
        if (mRightMenu != null) {
            mRightMenu.setVisibility(visibility);
        }
    }

    /**
     * 设置header view 的标题，由子类调用
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (mTitle != null && !TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    boolean isKeyboardOpened = false;

    /**
     * 监听键盘弹出事件. 如果activity是adjustPan,不起效.
     */
    private void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) {
                    if (isKeyboardOpened == false) {
                        mChatBottomView.hideChatFaceView();
                    }
                    isKeyboardOpened = true;
                } else if (isKeyboardOpened == true) {
                    isKeyboardOpened = false;
                }
            }
        };
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    protected void setEmptyView(View v) {
        mEmptyCotainer.removeAllViews();
        mEmptyCotainer.addView(v);
        mChatContentView.setEmptyView(mEmptyCotainer);
    }

    private String makeClientId() {
        return UUID.randomUUID().toString();
    }

    private RefreshingListener refreshListener = new RefreshingListener() {
        @Override
        public void onHeaderRefreshing() {
            if (mChatMessages.size() == 0) {
                pollImmediately();
                mChatContentView.headerRefreshingCompleted();
                return;
            }
            String firstId = ImSpUtils.getFirstMsgId(mGroupId);
            if (!TextUtils.isEmpty(firstId) && firstId.equals(mChatMessages.get(0).msgId)) {
                mChatContentView.headerRefreshingCompleted();
                mChatContentView.setNeedRefresh(false);
                return;
            }
            List<ChatMessagePo> tList = dao.query(mChatMessages.get(0).sendTime, 20, mGroupId);
            if (tList.size() > 0) {
                Collections.reverse(tList);
                mChatMessages.addAll(0, tList);
                mChatContentView.notifyDataSetChanged(false);
                mChatContentView.setSelection(tList.size());
//				mChatContentView.smoothScrollToPosition(tList.size());
                mChatContentView.headerRefreshingCompleted();
            } else {
                getOldMessageFromWeb();
            }
        }
    };

    public String getGroupId() {
        return mGroupId;
    }

    public void onGroupUpdate(ChatGroupPo po) {
        this.groupPo = po;
        updateBusinessNow();
    }

    protected void updateBusinessNow() {
        if (!isCurrentShow)
            return;
        baseHandler.removeMessages(MSG_UPDATE_BUSINESS);
        baseHandler.sendEmptyMessage(MSG_UPDATE_BUSINESS);
    }

    protected void updateBusinessDelay() {
        baseHandler.removeMessages(MSG_UPDATE_BUSINESS);
        baseHandler.sendEmptyMessageDelayed(MSG_UPDATE_BUSINESS, 10000);
    }

    private void refreshListDelay(long delay) {
        Message msg = baseHandler.obtainMessage(MSG_REFRESH_LIST);
        msg.obj = System.currentTimeMillis();
        baseHandler.sendMessageDelayed(msg, delay);
    }

    @SuppressLint("HandlerLeak")
    private Handler baseHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_BUSINESS:
                    if (groupPo != null && !TextUtils.isEmpty(groupPo.param))
                        onBusinessData();
                    break;
                case MSG_REFRESH_LIST:
                    long ts = (long) msg.obj;
                    if (ts > refreshTs) {
                        refreshTs = System.currentTimeMillis();
                        mChatContentView.notifyDataSetChanged(false);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    protected boolean showByRole() {
        return false;
    }

    /**
     * 更新业务状态
     */
    protected void fetchBizStatus() {
        if (!needBizParam())
            return;
        RequestQueue queue = VolleyUtil.getQueue(this);
        StringRequest req = new BizStatusRequest();
        queue.add(req);
    }

    private class BizStatusRequest extends DCommonRequest {
        public BizStatusRequest() {
            super(ChatActivityV2.this, Method.POST, PollingURLs.getGroupBiz(), bizStatusSucListener, null);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> m = new HashMap<String, String>();
            m.put("access_token", ImSdk.getInstance().accessToken);
            m.put("groupId", mGroupId);
            Logger.d(TAG, "param request:" + m);
            return m;
        }
    }

    private Listener<String> bizStatusSucListener = new Listener<String>() {
        @Override
        public void onResponse(String arg0) {
            Logger.d(TAG, "param result:" + arg0);
            if (groupPo == null)
                return;
            ResultTemplate<ChatGroupPo> res = JSON.parseObject(arg0, new TypeReference<ResultTemplate<ChatGroupPo>>() {
            });
            if (res.resultCode != 1 || res.data == null)
                return;
            ChatGroupPo biz = res.data;
            groupPo.bizStatus = biz.bizStatus;
            groupPo.param = biz.param;
            groupDao.updateGroupParam(mGroupId, biz.bizStatus, biz.param);
            updateBusinessNow();
        }
    };

    public void goArchiveItem(ArchiveItem item, ChatMessagePo po) {
//        Intent i = new Intent(this, ArchiveItemDetailUi.class);
//        i.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
//        startActivityForResult(i, REQUEST_CODE_ARCHIVE);
    }

    /**
     * 发送指令
     *
     * @param eventType
     * @param toUserId
     * @param param
     */
    protected void sendEvent(int eventType, String toUserId, Map<String, String> param) {
        final String reqTag = "sendEvent";
        RequestQueue queue = VolleyUtil.getQueue(mThis);
        queue.cancelAll(reqTag);
        SendEventRequest request = new SendEventRequest(eventType, toUserId, param, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.d(TAG, response);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ToastUtil.showErrorNet(mThis);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 0));
        request.setTag(reqTag);
        queue.add(request);
    }

    protected ImMsgHandler makeMsgHandler() {
        return new ImMsgHandler(this);
    }

    protected boolean needBizParam() {
        return false;
    }

    public void onEventMainThread(MsgRetractEvent event) {
        if(TextUtils.isEmpty(mGroupId)|| !mGroupId.equals(event.gid) ){
            return;
        }
        for(int i=mChatMessages.size()-1;i>=0;i--){
            ChatMessagePo msg=mChatMessages.get(i);
            if(TextUtils.isEmpty(msg.msgId))continue;
            if(msg.msgId.equals(event.msgId)){
                msg.isRetract=1;
                mChatContentView.notifyDataSetChanged();
                break;
            }
        }
    }
    public void retractMsg(final ChatMessagePo po){
        if(po.msgId==null)return;
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("msgId",po.msgId);
        reqMap.put("gid",po.groupId);
        Listener<String> listener=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result=JSON.parseObject(s,EmptyResult.class);
                if(result.resultCode==1){
                    EventBus.getDefault().post(new MsgRetractEvent(po.groupId,po.msgId));
                }else{
                    ToastUtil.showToast(mThis,result.detailMsg);
                }
            }
        };

        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showToast(mThis,"撤回失败");
            }
        };
        StringRequest req=new ImCommonRequest(PollingURLs.retractMsg(),reqMap,listener,errorListener);
        VolleyUtil.getQueue(this).add(req);
    }
    //本地删除 打标记 不删除数据
    public void deleteMsg(ChatMessagePo po){
        dao.setDeleteFlag(po);
        boolean isLast=false;
        if(mChatMessages.indexOf(po)==mChatMessages.size()-1){
            isLast=true;
        }
        mChatMessages.remove(po);
        if(isLast){
            String text="";
            if(mChatMessages.size()>0){
                ChatMessagePo msg=mChatMessages.get(mChatMessages.size()-1);
                if(msg.isRetract==1){
                    text=makeMsgHandler().getRetractText(msg,mChatContentView.mChatContentAdapter);
                }else
                    text=ImUtils.getMsgDesc(msg);
            }
            groupDao.changeContent(mGroupId,text);
        }
        mChatContentView.notifyDataSetChanged();
    }

    public void onEventMainThread(GroupSettingEvent event) {
        if(!event.groupId.equals(mGroupId))
            return;
        if(event.type==GroupSettingEvent.TYPE_EXIT){
            finish();
        }else if(event.type==GroupSettingEvent.TYPE_NAME){
            setTitle(event.name);
        }else if(event.type==GroupSettingEvent.TYPE_STATUS){
            if(groupPo!=null)
                groupPo.status=event.status;
        }
    }

    public void onEventMainThread(CloseChatEvent event) {
        if(!event.groupId.equals(mGroupId))
            return;
        finish();
    }
}
