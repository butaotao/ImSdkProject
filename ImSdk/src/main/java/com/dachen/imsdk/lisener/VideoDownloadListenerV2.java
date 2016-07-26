package com.dachen.imsdk.lisener;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.widget.ImageView;

import com.dachen.common.utils.downloader.DownloadListener;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.utils.ImSpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VideoDownloadListenerV2 implements DownloadListener {
	private ChatMessagePo chatMessage;
	private ImageView imageView;
	private ChatMessageDao dao;

	public VideoDownloadListenerV2(ChatMessagePo chatMessage, ImageView imageView,ChatMessageDao dao) {
		this.chatMessage = chatMessage;
		this.imageView = imageView;
		this.dao=dao;
	}

	@Override
	public void onStarted(String uri, View view) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onFailed(String uri, com.dachen.common.utils.downloader.FailReason failReason, View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onComplete(String uri, String filePath, View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
		ImSpUtils.setMsgFilePath(chatMessage.msgId, filePath);

		//uri变了，需要更新数据库
		dao.saveMessage(chatMessage);
		
		if (imageView != null && ((Integer) imageView.getTag()) == chatMessage.id) {
			Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(filePath);
			if (bitmap == null || bitmap.isRecycled()) {
				bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
				ImageLoader.getInstance().getMemoryCache().put(filePath, bitmap);
			}
			if (bitmap != null && !bitmap.isRecycled()) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageBitmap(null);
			}
		}
	}

	@Override
	public void onCancelled(String uri, View view) {

	}
}
