package com.dachen.imsdk.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.adapter.ChatAdapterV2;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.GroupInfo2Bean;
import com.dachen.imsdk.out.ImMsgHandler;

import java.util.List;
import java.util.Map;

//import com.sk.weichat.ui.circle.BaiduMapActivity;
//import com.sk.weichat.ui.circle.SendBaiDuLocate;
//import com.sk.weichat.ui.tool.SingleImagePreviewActivity;
//import com.sk.weichat.ui.tool.VideoPlayActivity;

public class ChatContentViewV2 extends PullDownListView {

	private Context mContext;
	/**
	 * 根据mLoginUserId和mToUserId 确定一张表
	 */
	private String mLoginUserId;
	private String mToUserId;
	/**
	 * 消息记录
	 */
	private List<ChatMessagePo> mChatMessages;

	/**
	 * 消息事件监听者
	 */
	// private MessageEventListener mMessageEventListener;
	private IMsgEventListenerV2 mMessageEventListener;
	/**
	 * 延迟发送时间
	 */
	private int mDelayTime = 0;
	/**
	 * View适配者
	 */
	private LayoutInflater mInflater;
	/**
	 * 处理消息显示的Handler对象
	 */
	private Handler mHandler = new Handler();
	/**
	 * 消息内容适配器
	 */
	// private ChatContentAdapter mChatContentAdapter;
	// private ChatContentAdapterPL mChatContentAdapter;
	public ChatAdapterV2 mChatContentAdapter;

	/**
	 * 图片消息的最大宽度
	 */
	private int mMaxWidth = 100;
	/**
	 * 图片消息的最大高度
	 */
	private int mMaxHeight = 200;

	/**
	 * 用户"我"的昵称
	 */
	private String mLoginNickName;
	/**
	 * 聊天室的名称
	 */
	// private String mRoomNickName;

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			scrollToBottom();
		}
	};

	/**
	 * 构造函数
	 *
	 * @param context
	 */
	public ChatContentViewV2(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造函数
	 *
	 * @param context
	 */
	public ChatContentViewV2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 设置房间号
	 *
	 * @param roomNickName
	 */
	public void setRoomNickName(String roomNickName) {
		// mRoomNickName = roomNickName;
		// mChatContentAdapter.setRoomNickName(roomNickName);
	}

	/**
	 * 重写ListView的方法
	 */

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		/**
		 * 如果之前的宽度大于现在的高度 将此消息延迟显示
		 */
		if (oldw > h) {
			mHandler.removeCallbacks(runnable);
			mHandler.postDelayed(runnable, mDelayTime);
		}
	}

	private void init(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDelayTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

		setCacheColorHint(0x00000000);
		if(isInEditMode()){
			return;
		}
		mLoginUserId = ImSdk.getInstance().userId;
		mLoginNickName = ImSdk.getInstance().userNick;
	}

	/**
	 * 设置数据
	 *
	 * @param chatMessages
	 */
	public void setData(List<ChatMessagePo> chatMessages) {
		this.mChatMessages = chatMessages;
		mChatContentAdapter = new ChatAdapterV2((ChatActivityV2) getContext(), mChatMessages);
		mChatContentAdapter.setMsgEventListener(mMessageEventListener);
		setAdapter(mChatContentAdapter);
	}

	public void setData(List<ChatMessagePo> chatMessages, int chatType) {
		this.mChatMessages = chatMessages;
		mChatContentAdapter = new ChatAdapterV2((ChatActivityV2) getContext(), mChatMessages);
		mChatContentAdapter.setChatType(chatType);
		mChatContentAdapter.setMsgEventListener(mMessageEventListener);
		setAdapter(mChatContentAdapter);
	}
	public void setChatType(int chatType){
		mChatContentAdapter.setChatType(chatType);
	}


	public void setUserInfo(Map<String, GroupInfo2Bean.Data.UserInfo> userInfo) {
		mChatContentAdapter.setUserInfo(userInfo);
	}
	public void setShowByRole(boolean showByRole){
		mChatContentAdapter.setShowByRole(showByRole);
	}

	/**
	 * 这个方法必须调用
	 *
	 * @param toUserId
	 */
	public void setToUserId(String toUserId) {
		mToUserId = toUserId;
	}

	/**
	 * 用于刷新
	 *
	 * @param scrollToBottom
	 */
	// public void notifyDataSetInvalidated(boolean scrollToBottom) {
	//
	// if (mChatContentAdapter == null) {
	// return;
	// }
	// mChatContentAdapter.notifyDataSetInvalidated();
	// if (scrollToBottom)
	// scrollToBottom();
	// }

	public void notifyDataSetChanged(boolean scrollToBottom) {

		if (mChatContentAdapter == null) {
			return;
		}
		mChatContentAdapter.notifyDataSetChanged();
		if (scrollToBottom) {
			scrollToBottom();
		}
	}

	/**
	 * 用于刷新
	 */
	public void notifyDataSetChanged() {
		if (mChatContentAdapter == null) {
			return;
		}
		mChatContentAdapter.notifyDataSetChanged();
		scrollToBottom();
	}

	/*
	 * private String getLengthDesc(int seconds) { if (seconds < 60) { seconds =
	 * 1000 * seconds; } int s = seconds / 1000; int m = (seconds % 1000) / 100;
	 * return (s + "." + m + "''"); }
	 */
	public void setImageMaxWidth(int maxWidth) {
		this.mMaxWidth = maxWidth;
	}

	public void setImageMaxHeight(int maxHeight) {
		this.mMaxHeight = maxHeight;
	}

	/**
	 * 设置消息监听者，提供给外部调用
	 *
	 * @param listener
	 */
	public void setMessageEventListener(IMsgEventListenerV2 listener) {
		mMessageEventListener = listener;
	}

	public void scrollToBottom() {
		if (mChatMessages == null||mChatMessages.size()==0) {
			return;
		}
		setSelection(mChatMessages.size());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mMessageEventListener != null) {
				mMessageEventListener.onEmptyTouch();
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void reset() {
		mChatContentAdapter.reset();
	}
	public void setGroupInfo(ChatGroupPo info){
		mChatContentAdapter.groupInfo=info;
	}
	public void setMsgHandler(ImMsgHandler msgClickListener){
		mChatContentAdapter.setMsgHandler(msgClickListener);
	}
}
