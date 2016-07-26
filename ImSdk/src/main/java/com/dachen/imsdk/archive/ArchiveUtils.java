package com.dachen.imsdk.archive;

import android.text.TextUtils;

import com.dachen.common.utils.StringUtils;
import com.dachen.imsdk.R;

/**
 * Created by Mcp on 2016/1/12.
 */
public class ArchiveUtils {
    public static final String CATE_ALL = "";
    public static final String CATE_DOCUMENT = "document";
    public static final String CATE_PIC = "picture";
    public static final String CATE_VIDEO = "video";
    public static final String CATE_MUSIC = "music";
    public static final String CATE_OTHER = "other";

    public static final String MODE_UPLOAD = "upload";
    public static final String MODE_RECEIVE = "receive";

    public static final String INTENT_KEY_ARCHIVE_ITEM = "archiveItem";


    public static int getFileIcon(String suffix) {
        if (!TextUtils.isEmpty(suffix)) {
            if (suffix.equalsIgnoreCase("pdf")) {
                return R.drawable.file_pdf;
            } else if (suffix.equalsIgnoreCase("doc") || suffix.equalsIgnoreCase("docx")) {
                return R.drawable.file_word;
            } else if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
                return R.drawable.file_xml;
            } else if (suffix.equalsIgnoreCase("rtf")) {
                return R.drawable.file_rtf;
            } else if (suffix.equalsIgnoreCase("ppt") || suffix.equalsIgnoreCase("pptx")) {
                return R.drawable.file_ppt;
            } else if (suffix.equalsIgnoreCase("xml")) {
                return R.drawable.file_xml;
            }else if (suffix.equalsIgnoreCase("txt")) {
                return R.drawable.file_txt;
            }
        }
        String mimeType = StringUtils.getMimeType(suffix);
        if (!TextUtils.isEmpty(mimeType)) {
            if (mimeType.startsWith("image")) {
                return R.drawable.file_image;
            } else if (mimeType.startsWith("audio")) {
                return R.drawable.file_audio;
            } else if (mimeType.startsWith("video")) {
                return R.drawable.file_video;
            } else if (mimeType.startsWith("text")) {
                return R.drawable.file_doc;
            }
        }
        return R.drawable.file_unknow;
    }


//    public static void goArchiveDetailActivity(Activity context, int requestCode, ArchiveItem item, String from){
        /*if (context == null || item == null) {
            return;
        }

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.suffix);
        if (TextUtils.isEmpty(mimeType)) {
            Intent intent = new Intent(context, DocDetailActivity.class);
            intent.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
            intent.putExtra("from", from);
            context.startActivityForResult(intent, requestCode);
            return;
        }

        if (mimeType.startsWith("image")) {
            ArrayList<ArchiveItem> imageItems = null;
            int position = -1;
            if (item.items == null) {
                imageItems = new ArrayList<ArchiveItem>();
                imageItems.add(item);
                position = 0;
            } else {
                imageItems = (ArrayList<ArchiveItem>) getImageArchiveItemList(item.items);
                position = getImagePosition(item, item.items);
            }

            Intent intent = new Intent(context, ImageDetailActivity.class);
            intent.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
            intent.putExtra("imageItems", (ArrayList<ArchiveItem>) imageItems);
            intent.putExtra("position", position);
            intent.putExtra("from", from);
            context.startActivityForResult(intent, requestCode);

        } else if (mimeType.startsWith("audio")) {
            Intent intent = new Intent(context, AudioDetailActivity.class);
            intent.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
            intent.putExtra("from", from);
            context.startActivityForResult(intent, requestCode);

        } else if (mimeType.startsWith("video")) {
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
            intent.putExtra("from", from);
            context.startActivityForResult(intent, requestCode);

        } else {
            Intent intent = new Intent(context, DocDetailActivity.class);
            intent.putExtra(ArchiveUtils.INTENT_KEY_ARCHIVE_ITEM, item);
            intent.putExtra("from", from);
            context.startActivityForResult(intent, requestCode);
        }*/
//    }
}
