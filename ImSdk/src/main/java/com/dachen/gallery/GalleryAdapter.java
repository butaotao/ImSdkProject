package com.dachen.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dachen.common.utils.CommonUtils;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.ToastUtil;
import com.dachen.imsdk.R;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;

public class GalleryAdapter extends BaseAdapter {

    private static final String TAG="GalleryAdapter";
	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
//	ImageLoader imageLoader;
	private boolean isActionMultiplePick;
	private HashSet<String> pathsSet;
    private int preItemSize=1;
    private int maxSelCount;
	private GalleryItemClick mClick=new GalleryItemClick();
//	private int itemHeight;

	public GalleryAdapter(CustomGalleryActivity act, ImageLoader imageLoader) {
		infalter = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = act;
//		this.imageLoader = imageLoader;
		pathsSet=act.selectedPathSet;
	}
    public void setMaxSelCount(int count){
        maxSelCount=count;
    }

	public void setClick(GalleryItemClick click){
		if(click==null)
			click=new GalleryItemClick();
		this.mClick=click;
	}
	private GalleryItemClick getEmptyClick(){
		return new GalleryItemClick() {
			@Override
			public void onClickPic(ViewHolder holder) {}
			@Override
			public void onClickCheck(ViewHolder holder) {}
			@Override
			public void onClickCamera() {

			}
		};
	}

	@Override
	public int getCount() {
		return data.size()+preItemSize;
	}

	@Override
	public CustomGallery getItem(int position) {
        int index=position-preItemSize;
        if(index>=0){
            return data.get(index);
        }
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

//	public void selectAll(boolean selection) {
//		notifyDataSetChanged();
//	}
//	public boolean isAllSelected() {
//		boolean isAllSelected = true;
//		return isAllSelected;
//	}
//	public boolean isAnySelected() {
//		boolean isAnySelected = false;
//		for (int i = 0; i < data.size(); i++) {
//			if (data.get(i).isSeleted) {
//				isAnySelected = true;
//				break;
//			}
//		}
//		return isAnySelected;
//	}
    public int getSelectCount(){
        return pathsSet.size();
    }

	public String[] getSelectedPaths() {
        return pathsSet.toArray(new String[pathsSet.size()]);
	}

	public void addAll(ArrayList<CustomGallery> files) {
		try {
			this.data.clear();
			this.data.addAll(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}

	public void changeSelection(View v,int position) {
        if(position<preItemSize)
            return;
        CustomGallery item=data.get(position-preItemSize);
        changeSelection(item.sdcardPath);
//		notifyDataSetChanged();
        ViewHolder holder= (ViewHolder) v.getTag();
        holder.imgQueueMultiSelected.setSelected(pathsSet.contains(item.sdcardPath));
	}
	public void changeSelection(String path) {
		if (pathsSet.contains(path)) {
			pathsSet.remove(path);
		} else {
            if(maxSelCount>0 && pathsSet.size()==maxSelCount ){
                ToastUtil.showToast(mContext,"最多可选择"+maxSelCount+"张图片");
                return;
            }
			pathsSet.add(path);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
            Logger.d(TAG,"get view new pos:"+position);
			convertView = infalter.inflate(R.layout.gallery_item, parent,false);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
			holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);
			convertView.setTag(holder);
		} else {
            Logger.d(TAG,"get view holder pos:"+position);
			holder = (ViewHolder) convertView.getTag();
		}
        int index=position-preItemSize;
        holder.index=index;
//        ImageLoader.getInstance().cancelDisplayTask(holder.imgQueue);
        if(index==-1){
            holder.imgQueueMultiSelected.setVisibility(View.GONE);
            holder.imgQueue.setImageResource(R.drawable.pickphotos_to_camera_normal);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mClick.onClickCamera();
				}
			});
        }else {
            if (isActionMultiplePick) {
                holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imgQueueMultiSelected.setVisibility(View.GONE);
            }
            CustomGallery item=data.get(index);
            holder.imgQueue.setImageResource(R.drawable.image_download_fail_icon);
//            ImageLoader.getInstance().displayImage("file://" + item.sdcardPath, holder.imgQueue, ImUtils.getGalleyImageOptions());
//			Picasso.with(mContext).load("file://" + item.sdcardPath).centerCrop().resize(300,300).placeholder(R.drawable.defaultpic)
//					.error(R.drawable.image_download_fail_icon).into(holder.imgQueue);
//            Ion.with(mContext).load(CommonUtils.getFileUriStr(item.sdcardPath)).withBitmap().centerCrop().resize(300,300).placeholder(R.drawable.defaultpic)
//                    .error(R.drawable.image_download_fail_icon).intoImageView(holder.imgQueue);
            Ion.with(holder.imgQueue).resize(300,300).placeholder(R.drawable.defaultpic)
                    .error(R.drawable.image_download_fail_icon).load(CommonUtils.getFileUriStr(item.sdcardPath));
            if (isActionMultiplePick) {
                holder.imgQueueMultiSelected.setSelected(pathsSet.contains(item.sdcardPath));
				final View finalConvertView = convertView;
				holder.imgQueueMultiSelected.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeSelection(finalConvertView,position);
						mClick.onClickCheck(holder);
					}
				});
            }
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mClick.onClickPic(holder);
				}
			});
        }
		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
        int index;
	}

//	public void clearCache() {
//		imageLoader.clearDiscCache();
//		imageLoader.clearMemoryCache();
//	}
	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static class GalleryItemClick{
		public void onClickPic(ViewHolder holder){};
		public void onClickCheck(ViewHolder holder){};
		public void onClickCamera(){};
	}
}
