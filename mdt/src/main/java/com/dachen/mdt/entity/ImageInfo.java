package com.dachen.mdt.entity;

import com.dachen.mdt.adapter.UpImgGridAdapter.UpImgGridItem;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/8/24.
 */
public class ImageInfo implements Serializable{
    public String userId;
    public String userName;
    public String path;
    public long time;

    public static ImageInfo fromUpImg(UpImgGridItem item){
        ImageInfo info=new ImageInfo();
        info.path=item.url;
        info.userId=item.userId;
        info.userName=item.userName;
        info.time=item.time;
        return info;
    }
}
