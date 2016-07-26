package com.dachen.imsdk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.dachen.common.utils.StringUtils;
import com.dachen.imsdk.R;
import com.dachen.imsdk.utils.HtmlUtils;

/**
 * 双击放大查看IM聊天中的文本消息
 * 
 * *.xml android:scrollbars="vertical" android:singleLine="false"
 * 
 * // android 让 TextView 自带滚动条
 * full_view_ui_text.setMovementMethod(ScrollingMovementMethod.getInstance());
 * 
 * @author
 *
 */
public class FullViewUI extends ImBaseActivity {

	private static final String TAG = FullViewUI.class.getSimpleName();

	private static final String Key_text = "Key_text";

	private String viewText = "";

	private boolean isFinish = true;

	TextView full_view_ui_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_view_ui);
		full_view_ui_text= (TextView) findViewById(R.id.full_view_ui_text);
		full_view_ui_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClick_full_view_ui_text();
			}
		});
		init();
	}

	/**
	 * 初始化
	 */
	protected void init() {

		Intent i = this.getIntent();
		if (i != null) {
			Bundle b = i.getExtras();
			if (b != null) {
				this.viewText = b.getString(Key_text);
			}
		}

		Log.w(TAG, "viewText:" + viewText);

		setViewText(viewText);

		// android 让 TextView 自带滚动条
		full_view_ui_text.setMovementMethod(ScrollingMovementMethod.getInstance());

		// 先经过onTouch，再到onClick
		full_view_ui_text.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.w(TAG, "onTouch():event:" + event.toString());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					isFinish = true;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					isFinish = false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {

				}
				Log.w(TAG, "onTouch():isFinish:" + isFinish);
				return false;
			}

		});

	}

	/**
	 * 显示文本
	 * 
	 * @param text
	 */
	private void setViewText(String text) {
		// 显示表情 --- start
		String s = StringUtils.replaceSpecialChar(text);
		CharSequence charSequence = HtmlUtils.transform200SpanString(s.replaceAll("\n", "\r\n"), true);
		// 显示表情 --- end
		full_view_ui_text.setText(charSequence);
	}

	/**
	 * 打开界面
	 * 
	 * @param context
	 * @param text
	 */
	public static void openUI(Context context, String text) {
		Intent i = new Intent(context, FullViewUI.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(Key_text, text);
		context.startActivity(i);
		((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	/**
	 */
	void onClick_full_view_ui_text() {
		Log.w(TAG, "onClick_full_view_ui_text():isFinish:" + isFinish);
		if (isFinish) {
			finish();
			overridePendingTransition(0, 0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, 0);
		}
		return super.onKeyDown(keyCode, event);
	}

}
