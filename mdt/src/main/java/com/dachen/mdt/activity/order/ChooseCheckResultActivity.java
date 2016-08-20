package com.dachen.mdt.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.ToastUtil;
import com.dachen.gallery.CustomGalleryActivity;
import com.dachen.gallery.GalleryAction;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.activity.main.CommonInputActivity;
import com.dachen.mdt.adapter.CheckResultItemLineAdapter;
import com.dachen.mdt.adapter.UpImgGridAdapter;
import com.dachen.mdt.adapter.UpImgGridAdapter.UpImgGridItem;
import com.dachen.mdt.entity.CheckType;
import com.dachen.mdt.entity.CheckTypeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseCheckResultActivity extends BaseActivity implements OnClickListener{

    public static final String KEY_START="start";
    public static final String KEY_RESULT="result";
    private static final int REQ_CODE_ADD=1;
    private static final int REQ_CODE_IMG=2;
    private static final int REQ_CODE_OTHER=3;

    private Map<String ,View> mResultViewMap=new HashMap<>();
    protected LinearLayout llPic,llResult,llOther,llEmpty;
    protected TextView tvOther;
    protected GridView mImgGrid;
    private UpImgGridAdapter mImgAdapter;
    private CheckTypeResult mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_check_result);
        setTheme(R.style.ActionSheetStyleiOS7);
        initView();
    }

    private void initView(){
        llPic= (LinearLayout) findViewById(R.id.ll_pic);
        llResult= (LinearLayout) findViewById(R.id.ll_result);
        llOther= (LinearLayout) findViewById(R.id.ll_other);
        llEmpty= (LinearLayout) findViewById(R.id.ll_empty);
        tvOther= (TextView) findViewById(R.id.tv_other);

        mImgGrid= (GridView) findViewById(R.id.gv_img);
        mImgAdapter=new UpImgGridAdapter(this);
        mImgAdapter.setShowAdd(false);
        mImgGrid.setAdapter(mImgAdapter);
        findViewById(R.id.right_btn).setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);

        mResult= (CheckTypeResult) getIntent().getSerializableExtra(KEY_START);
        if(mResult==null)
            mResult=new CheckTypeResult();
        if(mResult.typeList==null)
            mResult.typeList=new ArrayList<>();
        if(mResult.pathList==null)
            mResult.pathList=new ArrayList<>();
        for(String url:mResult.pathList)
            mImgAdapter.addPicUrl(url);
        mImgAdapter.notifyDataSetChanged();
        refreshImg();
        refreshResult(mResult);
        checkEmpty();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.right_btn||v.getId()==R.id.iv_add){
            ActionSheet.createBuilder(this,  getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("添加图片", "添加检查结果","其他")
                    .setCancelableOnTouchOutside(true).setListener(sheetListener).show();
        }
    }

    private ActionSheetListener sheetListener=new ActionSheetListener() {
        @Override
        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        }
        @Override
        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            if(index==1){
                Intent i=new Intent(mThis,ChooseCheckTypeActivity.class).putExtra(ChooseCheckTypeActivity.KEY_START,mResult);
                startActivityForResult(i,REQ_CODE_ADD);
            }else if(index==0){
                CustomGalleryActivity.openUi(mThis,true,REQ_CODE_IMG);
            }else if(index==2){
                Intent i = new Intent(mThis, CommonInputActivity.class).putExtra(CommonInputActivity.KEY_TEXT, mResult.text);
                startActivityForResult(i,REQ_CODE_OTHER);
            }
        }
    };

    private View getResultView(String typeId){
        View v=mResultViewMap.get(typeId);
        if(v!=null)
            return v;
        v=getLayoutInflater().inflate(R.layout.check_result_item,llResult,false);
        return v;
    }

    private void refreshOther(String otherText){
        if(TextUtils.isEmpty(otherText)){
            llOther.setVisibility(View.GONE);
            return;
        }
        tvOther.setText(otherText);
        llOther.setVisibility(View.VISIBLE);
    }

    private void refreshResult(CheckTypeResult res){
        refreshOther(res.text);
        if(res.typeList==null||res.typeList.size()==0){
            llResult.setVisibility(View.GONE);
            return;
        }
        llResult.setVisibility(View.VISIBLE);
        llResult.removeAllViews();
        for(CheckType type:res.typeList){
            if(type.itemList==null||type.itemList.size()==0)
                continue;
            View v=getResultView(type.id);
            ViewHolder holder=ViewHolder.get(mThis,v);
            holder.setText(R.id.tv_name,type.name);
            ListView lv=holder.getView(R.id.list_view);
            lv.setAdapter(new CheckResultItemLineAdapter(mThis,type.itemList));
            llResult.addView(v);
        }
    }

    private void refreshImg(){
        if(mImgAdapter.getData().size()==0){
            llPic.setVisibility(View.GONE);
        }else{
            llPic.setVisibility(View.VISIBLE);
        }
    }
    private void checkEmpty(){
        int vis= View.GONE;
        boolean picEmpty= mImgAdapter.getData().size()==0;
        boolean resEmpty= mResult.typeList.size()==0;
        boolean otherEmpty=  TextUtils.isEmpty(mResult.text);
//        if(!llPic.isShown()&& !llResult.isShown()&& !llOther.isShown()){
        if(picEmpty&& resEmpty&& otherEmpty){
            vis= View.VISIBLE;
        }
        llEmpty.setVisibility(vis);
    }

    @Override
    public void onLeftClick(View v) {
        mResult.pathList.clear();
        for(UpImgGridItem item:mImgAdapter.getData()){
            String url=item.url;
            if(TextUtils.isEmpty(url)){
                ToastUtil.showToast(mThis,"还有图片未成功上传");
                return;
            }
            mResult.pathList.add(url);
        }
        Intent i=new Intent().putExtra(KEY_RESULT,mResult);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQ_CODE_ADD){
            if(resultCode!=RESULT_OK)return;
//            ArrayList<CheckType> typeList= (ArrayList<CheckType>) data.getSerializableExtra(ChooseCheckTypeActivity.KEY_RESULT);
            mResult= (CheckTypeResult) data.getSerializableExtra(ChooseCheckTypeActivity.KEY_RESULT);
            refreshResult(mResult);
            checkEmpty();
        }else if(requestCode==REQ_CODE_IMG){
            if (resultCode != Activity.RESULT_OK) return;
            String[] all_path = data.getStringArrayExtra(GalleryAction.INTENT_ALL_PATH);
            if (all_path == null) return;
            for (String path : all_path) {
                mImgAdapter.addLocalPic(path,true);
            }
            mImgAdapter.notifyDataSetChanged();
            refreshImg();
            checkEmpty();
        }else if(requestCode==REQ_CODE_OTHER ){
            if(resultCode!=RESULT_OK)return;
            mResult.text=data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
            refreshOther(mResult.text);
        }
    }

}
