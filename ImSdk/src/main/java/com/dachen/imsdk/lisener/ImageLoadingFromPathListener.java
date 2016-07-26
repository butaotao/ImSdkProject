package com.dachen.imsdk.lisener;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoadingFromPathListener implements ImageLoadingListener {
	private String url;
	private ProgressBar progressBar;

	public ImageLoadingFromPathListener(String url, ProgressBar progressBar) {
		this.url = url;
		this.progressBar = progressBar;
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		if (arg1 != null && arg2 != null && !arg2.isRecycled()) {
			((ImageView) arg1).setImageBitmap(arg2);
		}
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		if (arg1 != null && !TextUtils.isEmpty(url)) {
			ImageLoader.getInstance().displayImage(url, (ImageView) arg1, new ImageLoadingFromUrlListener(progressBar));
		}
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
	}
}
