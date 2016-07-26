package com.dachen.imsdk.archive.entity;

import android.text.TextUtils;

import com.dachen.common.bean.FileSizeBean;
import com.dachen.imsdk.archive.download.ArchiveLoader;
import com.dachen.imsdk.archive.download.ArchiveTaskInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mcp  2016/1/12.
 */
public class ArchiveItem implements Serializable {

    public String fileId;
    public String name;
    public String suffix;
    public String mimeType;
    public String size;
    public String sizeStr;
    public String type;
    public String url;
    public int uploader;
    public long uploadDate;
    public String sendUserId;
    public String receiveUserId;
    public long sendDate;
    public String sendUserName;
    //用于图片左右滑动浏览
    public List<ArchiveItem> items;

    public ArchiveItem() {
    }

    public ArchiveItem(String id, String url, String name, String suffix, String size) {
        this.fileId = id;
        this.url = url;
        this.name = name;
        this.suffix = suffix;
        this.size = size;
    }

    public String getSizeStr() {
        if (!TextUtils.isEmpty(sizeStr)) {
            return sizeStr;
        } else {//如果服务端返回的sizeStr为空，则自己计算
            int size = 0;
            try {
                size = Integer.parseInt(this.size);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            FileSizeBean bean = new FileSizeBean(size);
            return bean.getSizeStr();
        }
    }

    /**
     * 文件是否在本地
     *
     * @return
     */
    public boolean isLocal() {
        ArchiveTaskInfo info = ArchiveLoader.getInstance().getInfo(this);
        if (info != null && info.state == ArchiveTaskInfo.STATE_DOWNLOAD_OK) {
            return true;
        }
        return false;
    }
}
