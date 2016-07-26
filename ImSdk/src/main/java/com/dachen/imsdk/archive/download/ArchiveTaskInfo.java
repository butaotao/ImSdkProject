package com.dachen.imsdk.archive.download;

/**
 * Created by Mcp on 2016/1/14.
 */
public class ArchiveTaskInfo {
    public static final int STATE_NOT_DOWNLOAD=1;
    public static final int STATE_IN_DOWNLOADIN=2;
    public static final int STATE_DOWNLOAD_OK =3;
    public int state;
    public String url;
    public int totalLength;
    public int downLength;
    public boolean canceled;
    public boolean failed;
    public String failMsg;

    public ArchiveTaskInfo(int state) {
        this.state = state;
    }
}
