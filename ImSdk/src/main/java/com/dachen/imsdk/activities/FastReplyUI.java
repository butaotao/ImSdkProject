package com.dachen.imsdk.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import com.dachen.imsdk.entity.fastreply.DeleteFastandReply2Bean;
import com.dachen.imsdk.entity.fastreply.GetFastandReply2Bean;
import com.dachen.imsdk.entity.fastreply.ReplyBean;
import com.dachen.imsdk.net.PollingURLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快捷回复
 * 
 * @author
 *
 */
public class FastReplyUI extends ImBaseActivity implements OnClickListener{

	private static final String TAG = FastReplyUI.class.getSimpleName();

	public static final int observer_add = 111;

	ListView fast_reply_ui_listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fast_reply_ui);
		fast_reply_ui_listView=(ListView) findViewById(R.id.fast_reply_ui_listView);
		init();
	}

	/**
	 * 初始化
	 */
	protected void init() {
		findViewById(R.id.fast_reply_ui_add).setOnClickListener(this);
		findViewById(R.id.fast_reply_ui_back).setOnClickListener(this);

		// ListView单击
		fast_reply_ui_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.w(TAG, "onItemClick():position:" + position + ",id:" + id);
				ReplyBean bean = (ReplyBean) parent.getItemAtPosition(position);
				// 把数据发送走
				// BaseActivity.mObserverUtil.sendObserver(Doctor2DoctorChatActivity.class,
				// BaseChatActivity.observer_reply, 0, 0, bean);
				// BaseActivity.mObserverUtil.sendObserver(Doctor2PatientChatActivity.class,
				// BaseChatActivity.observer_reply, 0, 0, bean);
				// BaseActivity.mObserverUtil.sendObserver(DoctorGroupChatActivity.class,
				// BaseChatActivity.observer_reply, 0, 0, bean);

				Intent intent = getIntent();
				intent.putExtra("replyBean", bean);
				setResult(RESULT_OK, intent);
				// 关闭自己
				finish();
			}

		});

		// ListView长按
		fast_reply_ui_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.w(TAG, "onItemLongClick():position:" + position + ",id:" + id);
				ReplyBean bean = (ReplyBean) parent.getItemAtPosition(position);
				// 删除
				showDeleteDialog(bean);
				return true;
			}

		});

		// 执行任务
		executeGetTask(PollingURLs.getFastandReply);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==observer_add){
			if(resultCode==RESULT_OK){
				ReplyBean bean = (ReplyBean) data.getSerializableExtra(FastReplyAddUI.EXKEY_DATA);
				addAdapter(bean);
			}
		}
	}

	/**
	 * 打开界面
	 */
	public static void openUI(Context context) {
		Intent i = new Intent(context, FastReplyUI.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// i.putExtra(key_findUserType, findUserType);
		context.startActivity(i);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.fast_reply_ui_back) {
			finish();
		} else if (id == R.id.fast_reply_ui_add) {
			//			FastReplyAddUI.openUI(context);
			startActivityForResult(new Intent(this, FastReplyAddUI.class), observer_add);
		} else {
		}
		
	}

	/**
	 * 设置一组新的数据
	 * 
	 * @param reply
	 */
	protected void setAdapter(ReplyBean[] reply) {
		// 设置适配器
		BaseListAdapter adapter = new BaseListAdapter(mThis, GJson.asList(reply));
		fast_reply_ui_listView.setAdapter(adapter);
	}

	/**
	 * 新增一条数据
	 * 
	 * @param reply
	 */
	protected void addAdapter(ReplyBean reply) {
		if (reply == null) {
			return;
		}

		Log.w(TAG, "addAdapter():reply:" + reply.replyId);
		BaseListAdapter adapter = (BaseListAdapter) fast_reply_ui_listView.getAdapter();
		if (adapter != null) {
			adapter.getItems().add(reply);
			// 刷新适配器
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 删除一条数据
	 * 
	 * @param reply
	 */
	protected void deleteAdapter(ReplyBean reply) {
		if (reply == null) {
			return;
		}

		Log.w(TAG, "deleteAdapter():reply:" + reply.replyId);
		BaseListAdapter adapter = (BaseListAdapter) fast_reply_ui_listView.getAdapter();
		if (adapter != null) {
			adapter.getItems().remove(reply);
			// 刷新适配器
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 删除一项
	 */
	protected void showDeleteDialog(final ReplyBean reply) {
		if (reply == null||reply.is_system==0) {
			return;
		}

		Log.w(TAG, "showDeleteDialog()");

		new AlertDialog.Builder(mThis).setMessage("删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 执行删除任务
				executeDeleteTask(PollingURLs.deleteFastandReply, reply);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}

		}).show();
	}

	/**
	 * 执行任务(获取)
	 */
	protected void executeGetTask(String url) {

		Log.w(TAG, "url:" + url);
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
				Log.w(TAG, "result:" + response);
				// TODO
				// response = "{ \"reply\": [ { \"replyContent\": \"aaaa\",
				// \"replyId\": 1, \"replyTime\": 11111 }, { \"replyContent\":
				// \"bbbb\", \"replyId\": 2, \"replyTime\": 22222 }, {
				// \"replyContent\": \"ccc\", \"replyId\": 3, \"replyTime\":
				// 333333 } ], \"resultCode\": 1}";
				// 将string转成json objecct
				GetFastandReply2Bean bean = GJson.parseObject(response, GetFastandReply2Bean.class);
				if (bean != null) {
					if (bean.resultCode == HttpErrorCode.successed) {
						ReplyBean[] data = bean.data;
						// 设置适配器
						setAdapter(data);
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

		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("access_token", ImSdk.getInstance().accessToken);
				return map;
			}

		};

		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 0));
		queue.add(request);

	}

	/**
	 * 执行任务(删除)
	 */
	protected void executeDeleteTask(String url, final ReplyBean reply) {

		Log.w(TAG, "url:" + url);
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
				Log.w(TAG, "result:" + response);
				// 将string转成json objecct
				DeleteFastandReply2Bean bean = GJson.parseObject(response, DeleteFastandReply2Bean.class);
				if (bean != null) {
					if (bean.resultCode == HttpErrorCode.successed) {
						ToastUtil.showToast(mThis, "删除成功");
						deleteAdapter(reply);
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

		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("access_token", ImSdk.getInstance().accessToken);
				map.put("replyId", String.valueOf(reply.replyId));
				return map;
			}

		};

		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 0));
		queue.add(request);

	}

	/**
	 * 适合器
	 * 
	 * @author lmc
	 *
	 */
	private class BaseListAdapter extends BaseAdapter {

		private Context context = null;
		private LayoutInflater inflater;

		private List<ReplyBean> items;

		public BaseListAdapter(Context context, List<ReplyBean> list) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
			this.items = list;
			if (this.items == null) {
				this.items = new ArrayList<ReplyBean>();
			}
		}

		public List<ReplyBean> getItems() {
			return items;
		}

		@Override
		public int getCount() {
			if (items == null) {
				return 0;
			}
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			if (items == null) {
				return null;
			}
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			if (items == null) {
				return 0;
			}
			// return items.get(position).replyId;
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.fast_reply_listview, null);
				viewHolder.content = (TextView) convertView.findViewById(R.id.fast_reply_listview_content);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.content.setText(items.get(position).replyContent);

			return convertView;
		}

		class ViewHolder {
			TextView content;
		}

	}

	
//TODO
//	@Override
//	public void onEvent(Object observer, int what, int arg1, int arg2, Object dataObject) {
//		Log.e(TAG, "onEvent():observer:" + observer.getClass().getSimpleName() + ",what:" + what + ",arg1:" + arg1
//				+ ",arg2:" + arg2);
//		if (what == observer_add) {
//			ReplyBean bean = (ReplyBean) dataObject;
//			addAdapter(bean);
//		}
//	}

}
