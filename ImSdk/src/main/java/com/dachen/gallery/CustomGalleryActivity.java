package com.dachen.gallery;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.dachen.common.utils.StringUtils;
import com.dachen.gallery.GalleryAdapter.GalleryItemClick;
import com.dachen.gallery.GalleryAdapter.ViewHolder;
import com.dachen.imsdk.R;
import com.dachen.imsdk.activities.ImBaseActivity;
import com.dachen.imsdk.utils.CameraUtil;
import com.dachen.imsdk.utils.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CustomGalleryActivity extends ImBaseActivity implements OnClickListener{

    private static final int REQUEST_CODE_CAMERA=21;
    public HashSet<String> selectedPathSet;

    GridView gridGallery;
    Handler handler;
    public GalleryAdapter adapter;
    View imgNoMedia;
    TextView tvConfirm;
    public CheckBox mCheckOrigin;
    public boolean isMultiPick;
    private ImageLoader imageLoader;
    private int maxPicNum;
    private boolean isFoldersShown;
    private List<CustomGalleryFolder> mFolders;
    private String curFolderId;
    private ListView lvFolder;
    private TextView tvFolder;
//    private String mCameraFilePath;
    public ArrayList<CustomGallery> currentGalleryList;
    private SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery);
        mSp=getPreferences(MODE_PRIVATE);
        selectedPathSet =new HashSet<>();
        isMultiPick=getIntent().getBooleanExtra(GalleryAction.INTENT_MULTI_PICK,false);
        imageLoader = ImageLoader.getInstance();

//        if(savedInstanceState!=null)
//            mCameraFilePath=savedInstanceState.getString("mCameraFilePath");
//        if(TextUtils.isEmpty(mCameraFilePath))
//        mCameraFilePath= FileUtil.getRandomImageFilePath();
//        mCameraFilePath= FileUtil.getCameraTempPath();
        init();
    }

    private String getCameraFilePath(){
        String mCameraFilePath=mSp.getString("mCameraFilePath",null);
        if(mCameraFilePath==null){
            mCameraFilePath=remakeCameraPath();
        }
        return mCameraFilePath;
    }
    private String remakeCameraPath(){
        String path= FileUtil.getRandomImageFilePath();
        mSp.edit().putString("mCameraFilePath",path).apply();
        return path;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init() {

        maxPicNum = getIntent().getIntExtra(GalleryAction.INTENT_MAX_NUM, 0);
        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
//        gridGallery.setFastScrollEnabled(true);
        gridGallery.setCacheColorHint(0);
        adapter = new GalleryAdapter(this, imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true) {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
                if(scrollState!=SCROLL_STATE_IDLE){
                    if (isFoldersShown) {
                        hideFolderList();
                    }
                }
            }
        };
        adapter.setMaxSelCount(maxPicNum);
        gridGallery.setOnScrollListener(listener);
        adapter.setMultiplePick(isMultiPick);
        adapter.setClick(mItemClick);
        gridGallery.setAdapter(adapter);
        imgNoMedia = findViewById(R.id.imgNoMedia);

        tvConfirm = (TextView) findViewById(R.id.btn_confirm);
        tvConfirm.setOnClickListener(mOkClickListener);
        mCheckOrigin = (CheckBox) findViewById(R.id.check_origin);
        adapter.addAll(getGalleryPhotos(null));
        checkImageStatus();

        mFolders = getGalleryFolders();
        GalleryFolderAdapter folderAdapter = new GalleryFolderAdapter(mFolders, this);
        lvFolder = (ListView) findViewById(R.id.lv_folders);
        lvFolder.setAdapter(folderAdapter);
        lvFolder.setOnItemClickListener(folderItemListener);
        tvFolder= (TextView) findViewById(R.id.tv_folder_name);
        tvFolder.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
        refreshSelNum();
    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_folder_name) {
            if (isFoldersShown) {
                hideFolderList();
            } else {
                showFolderList();
            }
        }else if(i==R.id.back_btn){
            finish();
        }
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickOk();
        }
    };
    public void clickOk(){
        String[] allPath = adapter.getSelectedPaths();
        Intent data = new Intent().putExtra(GalleryAction.INTENT_ALL_PATH, allPath)
                .putExtra(GalleryAction.INTENT_IS_ORIGIN, mCheckOrigin.isChecked());
        setResult(RESULT_OK, data);
        finish();
    }
    private GalleryItemClick mItemClick=new GalleryItemClick(){
        @Override
        public void onClickCamera() {
            goCamera();
        }

        @Override
        public void onClickPic(ViewHolder holder) {
            CustomGalleryPreviewFragment frag=new CustomGalleryPreviewFragment();
            frag.setIndex(holder.index);
            getSupportFragmentManager().beginTransaction().add(Window.ID_ANDROID_CONTENT,frag).addToBackStack(null).commit();
            tvFolder.setVisibility(View.GONE);
        }

        @Override
        public void onClickCheck(ViewHolder holder) {
            super.onClickCheck(holder);
            refreshSelNum();
        }
    };
    public void setSingleResult(String path){
        Intent data = new Intent().putExtra(GalleryAction.INTENT_ALL_PATH, new String[]{path});
        setResult(RESULT_OK, data);
        finish();
    }

//    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//            if(position==0){
//                goCamera();
//                return;
//            }
//            CustomGallery item = adapter.getItem(position);
//            Intent data = new Intent().putExtra(GalleryAction.INTENT_ALL_PATH, new String[]{item.sdcardPath});
//            setResult(RESULT_OK, data);
//            finish();
//        }
//    };
    private void refreshSelNum(){
        if(!isMultiPick)return;
        int num=adapter.getSelectCount();
        String txt="确定";
        txt+=String.format( "(%d)",num);
        tvConfirm.setText(txt);
    }

    private void goCamera(){
        Uri photoUri=Uri.fromFile(new File(getCameraFilePath()));
        CameraUtil.captureImage(this, photoUri, REQUEST_CODE_CAMERA);
    }
    AdapterView.OnItemClickListener folderItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGalleryFolder item = mFolders.get(position);
            if (StringUtils.strEquals(item.id, curFolderId))
                return;
            adapter.addAll(getGalleryPhotos(item.id));
            hideFolderList();
            curFolderId=item.id;
            tvFolder.setText(item.name);
        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos(String folderId) {
        ArrayList<CustomGallery> galleryList = new ArrayList<>();
        Cursor imgCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            String selection = null;
            String[] args = null;
            if (folderId != null) {
                selection = Media.BUCKET_ID + "=?";
                args = new String[]{folderId};
            }
            imgCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, args, orderBy);
            if (imgCursor != null && imgCursor.getCount() > 0) {
                while (imgCursor.moveToNext()) {
                    int dataColumnIndex = imgCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String sdcardPath = imgCursor.getString(dataColumnIndex);
                    CustomGallery item = new CustomGallery(sdcardPath);
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imgCursor != null)
                imgCursor.close();
        }
        Collections.reverse(galleryList);
        currentGalleryList=galleryList;
        return galleryList;
    }

    private ArrayList<CustomGalleryFolder> getGalleryFolders() {
        ArrayList<CustomGalleryFolder> galleryList = new ArrayList<CustomGalleryFolder>();
        Cursor imgCursor = null;
        try {
            final String[] columns = {Media.BUCKET_ID,Media.BUCKET_DISPLAY_NAME};
            final String orderBy = MediaStore.Images.Media.BUCKET_ID;
            imgCursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, "1) GROUP BY 1,(2", null, orderBy);
            if (imgCursor != null && imgCursor.getCount() > 0) {
                while (imgCursor.moveToNext()) {
                    CustomGalleryFolder item = new CustomGalleryFolder();
                    int dataColumnIndex = imgCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    item.id = imgCursor.getString(dataColumnIndex);
                    item.name = imgCursor.getString(imgCursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME));
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imgCursor != null)
                imgCursor.close();
        }
        galleryList.add(0, new CustomGalleryFolder(null, "所有图片"));
        return galleryList;
    }

    private void hideFolderList() {
//        if (!isFoldersShown) return;
        isFoldersShown = false;
        ObjectAnimator.ofFloat(lvFolder, "translationY", 0, lvFolder.getHeight()).setDuration(200).start();
    }

    private void showFolderList() {
//        if (isFoldersShown) return;
        lvFolder.setVisibility(View.VISIBLE);
        isFoldersShown = true;
        ObjectAnimator.ofFloat(lvFolder, "translationY", lvFolder.getHeight(), 0).setDuration(200).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_CAMERA){
            if(resultCode!=RESULT_OK)return;
            Intent res = new Intent().putExtra(GalleryAction.INTENT_ALL_PATH, new String[]{getCameraFilePath()});
            setResult(RESULT_OK, res);
//            mCameraFilePath= FileUtil.getRandomImageFilePath();
            remakeCameraPath();
            finish();
        }
    }

    public static void openUi(Activity act,boolean isMulti,int reqCode){
        openUi(act, isMulti, reqCode,0);
    }
    public static void openUi(Activity act,boolean isMulti,int reqCode,int maxPicNum){
        Intent i=new Intent(act,CustomGalleryActivity.class);
        i.putExtra(GalleryAction.INTENT_MULTI_PICK,isMulti);
        i.putExtra(GalleryAction.INTENT_MAX_NUM,maxPicNum);
        act.startActivityForResult(i, reqCode);
    }
    public void onClosePreview(){
        adapter.notifyDataSetChanged();
        refreshSelNum();
        tvFolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
