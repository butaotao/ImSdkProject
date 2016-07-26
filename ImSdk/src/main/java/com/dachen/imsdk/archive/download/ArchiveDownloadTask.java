package com.dachen.imsdk.archive.download;

import com.dachen.common.utils.downloader.FailReason;
import com.dachen.imsdk.archive.entity.ArchiveItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp   2016/1/14.
 */
public class ArchiveDownloadTask implements Runnable{

    private ArchiveItem mItem;

    public ArchiveDownloadTask(ArchiveItem item) {
        this.mItem = item;
    }

    @Override
    public void run() {
        File file = null;
        try {
            checkTaskNotActual();
            file = ArchiveLoader.getInstance().getDownloadFile(mItem);
            if (!file.exists()) {
                file = tryDownloadFile();
            }

            if (file == null || !file.exists()) {
                fireFailEvent(FailReason.FailType.UNKNOWN,new Exception("下载失败.文件不存在"));
                return;
            }

            checkTaskNotActual();

        } catch (TaskCancelledException e) {
            fireCancelEvent();
            e.printStackTrace();
        }
        fireCompleteEvent(file.getAbsolutePath());
    }

    private File tryDownloadFile() throws TaskCancelledException {
        File tempFile = null;
        boolean success = false;

        HttpURLConnection connection = null;
        FileOutputStream os = null;
        InputStream is = null;

        try {
            tempFile = ArchiveLoader.getInstance().getTempFile(mItem);
            if (!tempFile.getParentFile().exists()) {
                boolean createSuccess = tempFile.getParentFile().mkdirs();
                if (!createSuccess) {
                    fireFailEvent(FailReason.FailType.IO_ERROR, new IOException());
                    return null;
                }
            }

            URL url = new URL(mItem.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Snowdream Mobile");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                fireFailEvent(FailReason.FailType.IO_ERROR, new IOException());
                return null;
            }

            is = url.openStream();
            int total = connection.getContentLength();
            int current = 0;
            os = new FileOutputStream(tempFile);

            checkTaskNotActual();

            if (is != null && os != null) {
                byte[] buffer = new byte[1024 * 5];
                int readLen = 0;
                while ((readLen = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, readLen);
                    current += readLen;
                    fireProgressEvent(current, total);
                }
                success = true;
            }

            if (total != -1) {
                if (tempFile.length() != total) {
                    success = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            fireFailEvent(FailReason.FailType.IO_ERROR, e);
        } catch (TaskCancelledException e) {
            throw e;
        } catch (Throwable e) {
            fireFailEvent(FailReason.FailType.UNKNOWN, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (os != null) {
                    os.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // reName
        if (!success) {
            return null;
        }
        File file = ArchiveLoader.getInstance().getDownloadFile(mItem);
        success = tempFile.renameTo(file);
        if (success) {
            return file;
        } else {
            tempFile.delete();
            return null;
        }
    }

    private void checkTaskNotActual() throws TaskCancelledException {
        ArchiveTaskInfo info=ArchiveLoader.getInstance().getInfo(mItem);
        if(info.canceled){
            throw new TaskCancelledException();
        }
    }

    private class TaskCancelledException extends Exception {
    }

    private boolean fireProgressEvent(final int current, final int total) {
        ArchiveTaskInfo info=ArchiveLoader.getInstance().getInfo(mItem);
        info.downLength=current;
        info.totalLength=total;
        postChangeEvent();
        return true;
    }

    private void fireFailEvent(final FailReason.FailType failType, final Throwable failCause) {
        ArchiveTaskInfo info=ArchiveLoader.getInstance().getInfo(mItem);
        info.failed=true;
        info.failMsg=failCause.getMessage();
        info.state=ArchiveTaskInfo.STATE_NOT_DOWNLOAD;
        postChangeEvent();
    }

    private void fireCancelEvent() {
        ArchiveTaskInfo info=ArchiveLoader.getInstance().getInfo(mItem);
        info.canceled=true;
        info.state=ArchiveTaskInfo.STATE_NOT_DOWNLOAD;
        postChangeEvent();
    }

    private void fireCompleteEvent(final String filePath) {
        ArchiveTaskInfo info=ArchiveLoader.getInstance().getInfo(mItem);
        info.state=ArchiveTaskInfo.STATE_DOWNLOAD_OK;
        postChangeEvent();
    }
    private void postChangeEvent(){
        EventBus.getDefault().post(new ArchiveLoader.DownloadChangeEvent(mItem.url));
    }
}
