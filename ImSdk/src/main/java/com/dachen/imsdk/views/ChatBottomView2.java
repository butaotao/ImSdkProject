package com.dachen.imsdk.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dachen.imsdk.R;
import com.dachen.imsdk.activities.AtChatMemberActivity;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.entity.MoreItem;
import com.dachen.imsdk.utils.audio.IMRecordController;
import com.dachen.imsdk.utils.audio.RecordListener;

import java.util.List;

public class ChatBottomView2 extends LinearLayout implements View.OnClickListener, MorePanelView.OnPanelItemClickListener {
	private Context mContext;
	private ImageButton mEmotionBtn;
	private ImageButton mMoreBtn;
	private EditText mChatEdit;
	private Button mRecordBtn;
	private Button mSendBtn;
	private ImageButton mVoiceImgBtn;

	private ChatFaceView mChatFaceView;
	/* Tool */
	private MorePanelView mMorePanelView;

	private InputMethodManager mInputManager;
	private Handler mHandler = new Handler();
	private int mDelayTime = 0;
	private IMRecordController mRecordController;
	private ChatBottomListener mBottomListener;
    private boolean canAt;
    private int deleteStart=-1;
    private int deleteEnd;
	public boolean inRecordVoice;

	public ChatBottomView2(Context context) {
		this(context, null);
	}

	public ChatBottomView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ChatBottomView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private static final int RIGHT_VIEW_RECORD = 0;
	private static final int RIGHT_VIEW_SNED = 1;
	private int mRightView = RIGHT_VIEW_RECORD;// 当前右边的模式，用int变量保存，效率更高点

	private void init(final Context context) {
		mContext = context;
		mInputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		mDelayTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

		LayoutInflater.from(mContext).inflate(R.layout.im_chat_bottom2, this);

		mEmotionBtn = (ImageButton) findViewById(R.id.emotion_btn);
		mMoreBtn = (ImageButton) findViewById(R.id.more_btn);
		mChatEdit = (EditText) findViewById(R.id.chat_edit);
		mRecordBtn = (Button) findViewById(R.id.record_btn);
		mSendBtn = (Button) findViewById(R.id.send_btn);
		mVoiceImgBtn = (ImageButton) findViewById(R.id.voice_img_btn);

		mChatFaceView = (ChatFaceView) findViewById(R.id.chat_face_view);
		mMorePanelView = (MorePanelView) findViewById(R.id.more_panel_view);

		mEmotionBtn.setOnClickListener(this);
		mMoreBtn.setOnClickListener(this);
		mVoiceImgBtn.setOnClickListener(this);
		mSendBtn.setOnClickListener(this);
		mChatEdit.setOnClickListener(this);
		mMorePanelView.setOnPanelItemClickListener(this);

		mChatEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mChatEdit.requestFocus();
				return false;
			}
		});


		mChatEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int currentView = 0;
				if (s.length() <= 0) {
					currentView = RIGHT_VIEW_RECORD;
				} else {
					currentView = RIGHT_VIEW_SNED;
				}
				if(canAt){
                    if(before==0&&count==1&&s.charAt(start)=='@'){
                        if(mBottomListener!=null)
                            mBottomListener.goAtSomeone();
                    }
                }

				if (currentView == mRightView) {
					return;
				}
				mRightView = currentView;
				if (mRightView == 0) {
					mMoreBtn.setVisibility(View.VISIBLE);
					mSendBtn.setVisibility(View.GONE);
				} else {
					mMoreBtn.setVisibility(View.GONE);
					mSendBtn.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(canAt){
                    Editable eText=mChatEdit.getEditableText();
                    AtMsgSpan[] spans=eText.getSpans(start,start,AtMsgSpan.class);
                    if(spans.length>0){
                        for(AtMsgSpan span:spans){
                            int spanStart=eText.getSpanStart(span);
                            int spanEnd=eText.getSpanEnd(span);
                            if( spanStart==start &&count==0 ){
                                continue;
                            }
                            if( spanEnd==start  ){
                                continue;
                            }
                            if(spanEnd==(start+1) &&after==0){
//                                eText.replace(spanStart,spanEnd-1,"");
                                deleteStart=spanStart;
                                deleteEnd=spanEnd-1;
                            }
                            mChatEdit.getEditableText().removeSpan(span);
                        }
                    }
                }
			}

			@Override
			public void afterTextChanged(Editable s) {
                if(deleteStart>=0){
                    int temp=deleteStart;
                    deleteStart=-1;
                    s.replace(temp,deleteEnd,"");
                }
			}
		});

		mRecordController = new IMRecordController(mContext);
		mRecordController.setRecordListener(new RecordListener() {

			@Override
			public void onRecordSuccess(String filePath, int timeLen) {
				// 录音成功，返回录音文件的路径
				inRecordVoice=false;
				mRecordBtn.setText(R.string.motalk_voice_chat_tip_1);
				mRecordBtn.setBackgroundResource(R.drawable.im_voice_button_normal);
				if (mBottomListener != null) {
					mBottomListener.sendVoice(filePath, timeLen);
				}
			}

			@Override
			public void onRecordStart() {
				// 录音开始
				inRecordVoice=true;
				mRecordBtn.setText(R.string.motalk_voice_chat_tip_2);
				mRecordBtn.setBackgroundResource(R.drawable.im_voice_button_pressed);
			}

			@Override
			public void onRecordCancel() {
				// 录音取消
				inRecordVoice=false;
				mRecordBtn.setText(R.string.motalk_voice_chat_tip_1);
				mRecordBtn.setBackgroundResource(R.drawable.im_voice_button_normal);

			}
		});

		mRecordBtn.setOnTouchListener(mRecordController);

		mChatFaceView.setEmotionClickListener(new ChatFaceView.EmotionClickListener() {
			@Override
			public void onNormalFaceClick(SpannableString ss) {
				if (mChatEdit.hasFocus()) {
					mChatEdit.getText().insert(mChatEdit.getSelectionStart(), ss);
				} else {
					mChatEdit.getText().insert(mChatEdit.getText().toString().length(), ss);
				}
			}

			@Override
			public void onGifFaceClick(String resName) {
				// 发送GIF图片的回调
				if (mBottomListener != null) {
					mBottomListener.sendGif(resName);
				}
			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		mChatEdit.setFocusable(hasWindowFocus);
		mChatEdit.setFocusableInTouchMode(hasWindowFocus);
		super.onWindowFocusChanged(hasWindowFocus);
	}

	/**
	 * 改变录音按钮的状态<br/>
	 * 1、当处于非录音状态，显示录音按钮<br/>
	 * true的状态 2、当处于录音状态，显示键盘按钮<br/>
	 * false的状态
	 */
	private void changeRecordBtn(boolean show) {
		boolean isShowing = mRecordBtn.getVisibility() != View.GONE;
		if (isShowing == show) {
			return;
		}
		if (show) {
			mChatEdit.setVisibility(View.GONE);
			mRecordBtn.setVisibility(View.VISIBLE);
			// mVoiceImgBtn.setImageResource(R.drawable.im_keyboard_nor);
			mVoiceImgBtn.setBackgroundResource(R.drawable.im_btn_keyboard_bg);
		} else {
			mChatEdit.setVisibility(View.VISIBLE);
			mChatEdit.requestFocus();
			mRecordBtn.setVisibility(View.GONE);
			// mVoiceImgBtn.setImageResource(R.drawable.im_voice_nor);
			mVoiceImgBtn.setBackgroundResource(R.drawable.im_voice_nor);
		}
	}

	/**
	 * 改变更多按钮的状态<br/>
	 * 1、当更多布局显示时，显示隐藏按钮<br/>
	 * false的状态 2、当更多布局隐藏时，显示更多按钮<br/>
	 * true的状态
	 */
	private void changeMorePanelView(boolean show) {
		boolean isShowing = mMorePanelView.getVisibility() != View.GONE;
		if (isShowing == show) {
			return;
		}
		if (show) {
			mMorePanelView.showPanel();
			mMorePanelView.setVisibility(View.VISIBLE);
			mMoreBtn.setBackgroundResource(R.drawable.im_btn_collapse_bg);
		} else {
			mMorePanelView.setVisibility(View.GONE);
			mMoreBtn.setBackgroundResource(R.drawable.im_btn_more_bg);
		}
	}

	public void morePanelToKeyboard() {
		mInputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		changeMorePanelView(false);
	}

	public EditText getChatEdit() {
		return mChatEdit;
	}

	/**
	 * 更新更多面板
	 */
	public void updateMorePanel() {
		boolean isShowing = mMorePanelView.getVisibility() != View.GONE;
		if (isShowing == true) {
			mMorePanelView.updatePanel();
		}
	}

	/**
	 * 显示或隐藏表情布局
	 *
	 * @param show
	 */
	private void changeChatFaceView(boolean show) {
		boolean isShowing = mChatFaceView.getVisibility() != View.GONE;
		if (isShowing == show) {
			return;
		}
		if (show) {
			mChatEdit.requestFocus();
			mChatFaceView.setVisibility(View.VISIBLE);
			mEmotionBtn.setBackgroundResource(R.drawable.im_btn_keyboard_bg);
		} else {
			mChatFaceView.setVisibility(View.GONE);
			mEmotionBtn.setBackgroundResource(R.drawable.im_btn_emotion_bg);
		}
	}

	public void hideChatFaceView() {
		changeChatFaceView(false);
	}

	public void hideAll() {
		mInputManager.hideSoftInputFromWindow(mChatEdit.getApplicationWindowToken(), 0);
		changeChatFaceView(false);
		changeMorePanelView(false);
	}

	@Override
	public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emotion_btn) {
            if (mChatFaceView.getVisibility() != View.GONE) {// 表情布局在显示,那么点击则是隐藏表情，显示键盘
                mInputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                changeChatFaceView(false);
            } else {// 表情布局没有显示,那么点击则是显示表情，隐藏键盘、录音、更多布局
                mInputManager.hideSoftInputFromWindow(mChatEdit.getApplicationWindowToken(), 0);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeChatFaceView(true);
                        changeMorePanelView(false);
                        changeRecordBtn(false);
                    }
                }, mDelayTime);
            }

        } else if (i == R.id.more_btn) {
            if (mMorePanelView.getVisibility() != View.GONE) {// 表情布局在显示,那么点击则是隐藏表情，显示键盘
                mInputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                changeMorePanelView(false);
            } else {// 更多布局没有显示,那么点击则是显示更多，隐藏表情、录音、键盘布局
                mInputManager.hideSoftInputFromWindow(mChatEdit.getApplicationWindowToken(), 0);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeChatFaceView(false);
                        changeMorePanelView(true);
                        changeRecordBtn(false);
                    }
                }, mDelayTime);
            }

        } else if (i == R.id.chat_edit) {
            changeChatFaceView(false);
            changeMorePanelView(false);
            changeRecordBtn(false);

        } else if (i == R.id.voice_img_btn) {
            if (mRecordBtn.getVisibility() != View.GONE) {// 录音布局在显示,那么点击则是隐藏录音，显示键盘
                mInputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                changeRecordBtn(false);
            } else {
                // 录音布局没有显示,那么点击则是显示录音，隐藏表情、更多、键盘布局
                mInputManager.hideSoftInputFromWindow(mChatEdit.getApplicationWindowToken(), 0);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeChatFaceView(false);
                        changeMorePanelView(false);
                        changeRecordBtn(true);
                    }
                }, mDelayTime);
            }


            /*************************** 底部栏事件回调 **************************/
        } else if (i == R.id.send_btn) {
            if (mBottomListener != null) {
//                String msg = mChatEdit.getText().toString().trim();
//                if (TextUtils.isEmpty(msg)) {
//                    return;
//                }
                mBottomListener.sendText(mChatEdit);
                mChatEdit.setText("");
            }

        } else {
            Toast.makeText(mContext, "开发中", Toast.LENGTH_LONG).show();
        }

	}

	public void reset() {
		changeChatFaceView(false);
		changeMorePanelView(false);
		changeRecordBtn(false);
		mInputManager.hideSoftInputFromWindow(mChatEdit.getApplicationWindowToken(), 0);
	}

	public void setChatBottomListener(ChatBottomListener listener) {
		mBottomListener = listener;
	}

	public void setMorePanelData(SparseArray<List<MoreItem>> data) {
		mMorePanelView.setMorePanelData(data);
	}

	/**
	 *
	 * 发送消息的统一接口
	 *
	 */
	public interface ChatBottomListener {
		void sendText(EditText editText);

		void sendGif(String text);

		void sendVoice(String filePath, int timeLen);

		/**
		 * 点击+号后展开的面板的item的点击事件
		 */
//		void onPanelItemClick(int page, int position);
		void onPanelItemClick(int drawableId);

        void goAtSomeone();
	}

	public void recordCancel() {
		if (mRecordController != null) {
			mRecordController.cancel();
		}
	}

//	@Override
//	public void onPanelItemClick(int page, int position) {
//		if (mBottomListener != null) {
//			mBottomListener.onPanelItemClick(page, position);
//		}
//	}
	@Override
	public void onPanelItemClick(int drawableId) {
		if (mBottomListener != null) {
			mBottomListener.onPanelItemClick(drawableId);
		}
	}

    public static class AtMsgSpan extends StyleSpan {
        public boolean isAll;
        public String userId;
        public String originText;

        public AtMsgSpan( boolean isAll, String userId,String text) {
            super(Typeface.BOLD);
            this.isAll = isAll;
            this.userId = userId;
            this.originText=text;
        }
    }

    public void setCanAt(boolean canAt){
        this.canAt=canAt;
    }

	public void addAtPeople( UserInfo u){
		addAtPeople(u,true);
	}

    public void addAtPeople( UserInfo u,boolean fromInput){
        int index=mChatEdit.getSelectionStart();
		int targetIndex=index;
		if(fromInput){
			if(index<=0)return;
			targetIndex=index-1;
			char c=mChatEdit.getText().charAt(targetIndex);
			if(c!='@')return;
		}
        boolean isAll=  AtChatMemberActivity.ID_ALL.equals(u.id);
        String text= isAll?"@所有人 ":"@"+u.name+" ";
        SpannableString ss=new SpannableString(text);
        ss.setSpan(new AtMsgSpan(isAll,u.id,text),0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mChatEdit.getText().replace(targetIndex,index,ss);
    }

}
