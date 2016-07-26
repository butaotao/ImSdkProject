package com.dachen.imsdk.lisener;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dachen.imsdk.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoadingFromUrlListener implements ImageLoadingListener {
	private ProgressBar progressBar;

	public ImageLoadingFromUrlListener(ProgressBar progressBar) {
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
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
		if (arg1 != null) {
			((ImageView) arg1).setImageResource(R.drawable.image_download_fail_icon);
		}
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}
}
