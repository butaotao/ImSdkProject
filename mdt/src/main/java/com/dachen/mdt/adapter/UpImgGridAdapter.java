package com.dachen.mdt.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.toolbox.QiniuUploadTask;
import com.dachen.common.utils.CommonUtils;
import com.dachen.common.utils.QiNiuUtils;
import com.dachen.imsdk.net.UploadEngine7Niu;
import com.dachen.imsdk.net.UploadEngine7Niu.UploadObserver7Niu;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.R;
import com.dachen.mdt.adapter.UpImgGridAdapter.UpImgGridItem;
import com.dachen.mdt.entity.ImageInfo;
import com.dachen.mdt.util.AppCommonUtils;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/11.
 */
public class UpImgGridAdapter extends CommonAdapterV2<UpImgGridItem> {
    private String smallSuffix;
    private int maxNum = Integer.MAX_VALUE;
    private UpImgGridItem mAddItem;
    private boolean showAdd=true;
    protected Map<UpImgGridItem,QiniuUploadTask> mTaskMap=new HashMap<>();
    private UpImgGridCallback callback=new UpImgGridCallback();
    private String bucket= QiNiuUtils.BUCKET_MDT_CASE;

    public UpImgGridAdapter(Context mContext) {
        super(mContext);
        mAddItem=new UpImgGridItem();
        mAddItem.isAdd=true;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setSmallSuffix(String smallSuffix) {
        this.smallSuffix = smallSuffix;
    }

    @Override
    public int getCount() {
        if(!showAdd)
            return mData.size();
        return mData.size() >= maxNum ? maxNum : mData.size()+1;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
        notifyDataSetChanged();
    }

    public UpImgGridItem addLocalPic(String path,boolean uploadNow){
        UpImgGridItem item=new UpImgGridItem();
        item.localPath=path;
        item.userId= ImUtils.getLoginUserId();
        item.userName= AppCommonUtils.getLoginUser().name;
        mData.add(item);
        if(uploadNow)
            startUpload(item);
//        notifyDataSetChanged();
        return item;
    }

    public UpImgGridItem addPicUrl(ImageInfo img){
        UpImgGridItem item=new UpImgGridItem();
        item.url=img.path;
        item.userId=img.userId;
        item.userName=img.userName;
        item.time= img.time;
        mData.add(item);
//        notifyDataSetChanged();
        return item;
    }
    public void addPicUrlList(List<ImageInfo> imgList){
        if(imgList==null)return;
        for(ImageInfo info:imgList){
            addPicUrl(info);
        }
    }

    @Override
    public UpImgGridItem getItem(int position) {
        if(position<mData.size())
            return mData.get(position);
        return mAddItem;
    }
    public ArrayList<ImageInfo> getImageInfoList(){
        ArrayList<ImageInfo> list = new ArrayList<>();
        for(UpImgGridItem item:mData){
            ImageInfo info=ImageInfo.fromUpImg(item);
            if(TextUtils.isEmpty(item.url)){
                info.path=Uri.fromFile(new File(item.localPath)).toString();
            }
            list.add(info);
        }
        return list;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected String getBucket(){
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setCallback(UpImgGridCallback callback) {
        this.callback = callback;
    }

    public void startUpload(final UpImgGridItem item){
        if(!TextUtils.isEmpty(item.url))return;
        if(mTaskMap.get(item)!=null)return;
        UploadObserver7Niu callBack=new UploadObserver7Niu() {

            @Override
            public void onUploadSuccess(String url) {
                item.percent=100;
                item.url=url;
                item.time=System.currentTimeMillis();
                mTaskMap.remove(item);
                notifyDataSetChanged();
            }

            @Override
            public void onUploadFailure(String msg) {
                item.percent=-1;
                mTaskMap.remove(item);
                notifyDataSetChanged();
            }
        };
        item.percent=0;
        QiniuUploadTask task=UploadEngine7Niu.uploadFileCommon(item.localPath,callBack,getBucket(),null);
        mTaskMap.put(item,task);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext,convertView,parent, R.layout.up_img_grid_item,position);
        final UpImgGridItem item=getItem(position);
        holder.setVisibility(R.id.tv_state,View.INVISIBLE);
        if(item.isAdd){
            ImageLoader.getInstance().displayImage( "drawable://"+R.drawable.add_photo , (ImageView) holder.getView(R.id.iv_pic));
            holder.setVisibility(R.id.btn_delete,View.INVISIBLE);
        }else{
            holder.setVisibility(R.id.btn_delete,View.VISIBLE);
            if(!TextUtils.isEmpty(item.localPath)){
                holder.setVisibility(R.id.tv_state,View.VISIBLE);
                if(!TextUtils.isEmpty(item.url)){
                    holder.setText(R.id.tv_state,"已上传");
                }else if(item.percent==-1){
                    holder.setText(R.id.tv_state,"上传失败");
                }else{
                    holder.setText(R.id.tv_state,"上传中");
                }
            }
            if(!TextUtils.isEmpty(item.localPath)){
                Ion.with((ImageView) holder.getView(R.id.iv_pic)).load(CommonUtils.getFileUriStr(item.localPath) );
//                ImageLoader.getInstance().displayImage( Uri.fromFile(new File(item.localPath)).toString(), (ImageView) holder.getView(R.id.iv_pic));
            }else{
                String url=item.url;
                if(smallSuffix!=null)
                    url+=smallSuffix;
                Ion.with((ImageView) holder.getView(R.id.iv_pic)).load(url);
//                ImageLoader.getInstance().displayImage(url, (ImageView) holder.getView(R.id.iv_pic));

            }
            holder.setOnClickListener(R.id.btn_delete, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    QiniuUploadTask task=mTaskMap.get(item);
                    if(task!=null)
                        task.cancel();
                    mTaskMap.remove(item);
                    mData.remove(item);
                    notifyDataSetChanged();
                    callback.onDeleteItem(item);
                }
            });
        }
        return holder.getConvertView();
    }

    public static class UpImgGridItem {
        public boolean isAdd;
        public String localPath;
        public String url;
        public String userId;
        public String userName;
        public String key;
        public float percent;
        public long time;
    }
    public static class UpImgGridCallback{
        public void onDeleteItem(UpImgGridItem item){
        }
    }
}
