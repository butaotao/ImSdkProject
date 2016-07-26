package com.dachen.imsdk.views;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dachen.common.adapter.CommonAdapter;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.DisplayUtil;
import com.dachen.imsdk.R;
import com.dachen.imsdk.entity.MoreItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 点击加号出现的"更多"界面
 * 
 * @author gaozhuo
 *
 */
public class MorePanelView extends RelativeLayout {
	private Context mContext;
	private ViewPager mViewPager;
	private LinearLayout mPointsLayout;// 标志滑动到第几页的点的布局

	private ImageView[] mPoints;
	private PagerListener mPagerListener;

	private SparseArray<List<MoreItem>> mData;
	private boolean mIsPanelShowing;
	private OnPanelItemClickListener mOnPanelItemClickListener;

	public interface OnPanelItemClickListener {
		void onPanelItemClick(int drawableId);
//		void onPanelItemClick(int page, int position);
	}

	public MorePanelView(Context context) {
		this(context, null);
	}

	public MorePanelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MorePanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private int mScreenWidth;

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		mContext = context;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mScreenWidth = d.getWidth();

		LayoutInflater.from(mContext).inflate(R.layout.im_chat_more_panel_view, this);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mPointsLayout = (LinearLayout) findViewById(R.id.chat_face_point_loy);
	}

	public void setOnPanelItemClickListener(OnPanelItemClickListener listener) {
		mOnPanelItemClickListener = listener;
	}

	public void setMorePanelData(SparseArray<List<MoreItem>> data) {
		mData = data;
	}

	/**
	 * 当panel处于显示状态时更新panel
	 * 
	 */
	public void updatePanel() {
		if (mIsPanelShowing == true) {
			mIsPanelShowing = false;
			showPanel();
		}
	}

	public void showPanel() {
		if (mIsPanelShowing == true) {
			return;
		}
		if (mData == null || mData.size() <= 0) {
			return;
		}
		int pageCount = mData.size();

		if (pageCount > 1) {
			showPoints(pageCount);
		}else{
			mPointsLayout.setVisibility(View.GONE);
		}

		List<View> views = new ArrayList<View>();

		for (int i = 0; i < pageCount; i++) {
			View contentView = LayoutInflater.from(mContext).inflate(R.layout.im_more_panel_gridview, null);

			GridView gridView = (GridView) contentView.findViewById(R.id.gridView);
			//gridView.setSelector(R.drawable.chat_face_bg);

			GridViewAdapter adapter = new GridViewAdapter(mContext, mData.get(i), R.layout.im_item_more_panel);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new PanelItemClick(i));
			views.add(contentView);
		}
		mViewPager.setAdapter(new PanelPagerAdapter(views));
		mIsPanelShowing = true;
	}

	private void showPoints(int pageCount) {
		mPoints = new ImageView[pageCount];// 有几页
		mPointsLayout.setVisibility(View.VISIBLE);
		mPointsLayout.removeAllViews();
		for (int i = 0; i < mPoints.length; i++) {
			ImageView point = new ImageView(mContext);
			if (i == 0) {
				point.setBackgroundResource(R.drawable.im_tab_press);
			} else {
				point.setBackgroundResource(R.drawable.im_tab_normal);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 5),
					DisplayUtil.dip2px(mContext, 5));
			params.setMargins(0, 0, DisplayUtil.dip2px(mContext, 5), 2);
			point.setLayoutParams(params);
			mPointsLayout.addView(point);
			mPoints[i] = point;
		}
		mPagerListener = new PagerListener(mPoints);
		mViewPager.setOnPageChangeListener(mPagerListener);
	}

	class PanelItemClick implements OnItemClickListener {
		private int mPage;

		public PanelItemClick(int page) {
			mPage = page;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mOnPanelItemClickListener != null) {
//				mOnPanelItemClickListener.onPanelItemClick(mPage, position);
				int drawableId=0;
				if(mData.get(mPage)!=null&&mData.get(mPage).size()>position){
					drawableId=mData.get(mPage).get(position).resId;
				}
				mOnPanelItemClickListener.onPanelItemClick(drawableId);
			}
		}

	}

	/**
	 * 对ViewPager设置Listener
	 * 
	 * @author xk 2013-3-8
	 */
	class PagerListener implements OnPageChangeListener {
		ImageView[] imgArray;

		public PagerListener(ImageView[] array) {
			imgArray = array;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < imgArray.length; i++) {
				imgArray[i].setBackgroundResource(i == arg0 ? R.drawable.im_tab_press : R.drawable.im_tab_normal);
			}
		}

	}

	class GridViewAdapter extends CommonAdapter<MoreItem> {

		public GridViewAdapter(Context context, List<MoreItem> data, int layoutId) {
			super(context, data, layoutId);
		}

		@Override
		public void bind(ViewHolder holder, MoreItem data) {
			TextView textView = holder.getView(R.id.more_item);
			textView.setText(data.name);
			textView.setCompoundDrawablesWithIntrinsicBounds(0, data.resId, 0, 0);
		}

	}

	class PanelPagerAdapter extends PagerAdapter {

		private List<View> views;

		public PanelPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewGroup) container).addView(views.get(position));
			return views.get(position);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

}
