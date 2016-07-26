package com.dachen.imsdk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.GJson;
import com.dachen.common.toolbox.DCommonRequest;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.HttpErrorCode;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.R;
import com.dachen.imsdk.entity.fastreply.AddFastandReply2Bean;
import com.dachen.imsdk.net.PollingURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * 快捷回复新增
 * 
 * @author
 *
 */
public class FastReplyAddUI extends ImBaseActivity implements OnClickListener{

	private static final String TAG = FastReplyAddUI.class.getSimpleName();
	public static final String EXKEY_DATA="data";

	EditText fast_reply_add_ui_editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fast_reply_add_ui);
		fast_reply_add_ui_editText=(EditText) findViewById(R.id.fast_reply_add_ui_editText);
		init();
	}

	/**
	 * 初始化
	 */
	protected void init() {
		findViewById(R.id.fast_reply_add_ui_back).setOnClickListener(this);
		findViewById(R.id.fast_reply_add_ui_save).setOnClickListener(this);
	}

	/**
	 * 打开界面
	 */
	public static void openUI(Context context) {
		Intent i = new Intent(context, FastReplyAddUI.class);
//		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		i.putExtra(key_findUserType, findUserType);
		context.startActivity(i);
	}
	
	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.fast_reply_add_ui_back) {
			finish();
		} else if (id == R.id.fast_reply_add_ui_save) {
			String replyText = null;
			replyText = fast_reply_add_ui_editText.getText().toString();
			if (replyText == null || replyText.isEmpty()) {
				ToastUtil.showToast(mThis, "请填写内容");
				return;
			}else if (replyText.length() > 500) {
				ToastUtil.showToast(mThis, "不能超过500字");
				return;
			}
			executeAddTask(PollingURLs.addFastandReply, replyText);
		} else {
		}
	}


	/**
	 * 执行任务(新增)
	 */
	protected void executeAddTask(String url, final String replyContent) {

		Log.w(TAG, "url:"+url);
		if (url == null) {
			return;
		}

		mDialog.show();
		RequestQueue queue = VolleyUtil.getQueue(mThis);
		queue.cancelAll(this);

		StringRequest request = new DCommonRequest(this,Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				mDialog.dismiss();
				// TODO
//				response = "{    \"reply\": {        \"replyContent\": \"eeeee\",        \"replyId\": 4,        \"replyTime\": 4444    },    \"resultCode\": 1}";
				Log.w(TAG, "result:"+response);
				// 将string转成json objecct
				AddFastandReply2Bean bean = GJson.parseObject(response, AddFastandReply2Bean.class);
				if (bean != null) {
					if (bean.resultCode == HttpErrorCode.successed) {
						ToastUtil.showToast(mThis, "新增成功");
						// 把数据发送走
//						BaseActivity.mObserverUtil.sendObserver(FastReplyUI.class, FastReplyUI.observer_add, 0, 0, bean.data);
						Intent i=new Intent();
						i.putExtra(EXKEY_DATA, bean.data);
						setResult(RESULT_OK, i);
						// 关闭自己
						finish();
					} else {
						ToastUtil.showToast(mThis, bean.resultMsg);
					}
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mDialog.dismiss();
				ToastUtil.showToast(mThis, "http failed");
			}

		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("access_token", ImSdk.getInstance().accessToken);
				map.put("replyContent", replyContent);
				map.put("is_system", "1");
				return map;
			}

		};

		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 0));
		queue.add(request);

	}

}
