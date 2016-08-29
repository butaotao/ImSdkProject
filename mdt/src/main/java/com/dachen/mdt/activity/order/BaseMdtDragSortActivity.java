package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.MdtOptionResult;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

import java.util.ArrayList;
import java.util.List;

public class BaseMdtDragSortActivity extends BaseActivity {

    protected static final int REQ_CODE_CHOOSE=1;

    protected DragSortListView mListView;
    protected TextView tvTitle;
    protected DragAdapter mAdapter;
    protected MdtOptionResult currentData;
    private View vFooter;
    private View vEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_list);
        initData();

        tvTitle = (TextView) findViewById(R.id.title);
        mListView = (DragSortListView) findViewById(R.id.list_view);
        vEmpty = findViewById(R.id.layout_empty);
        vFooter=getLayoutInflater().inflate(R.layout.choose_text_footer,null);
        mListView.addFooterView(vFooter);
        mAdapter=new DragAdapter(mThis);
        mListView.setDropListener(onDrop);
        mListView.setAdapter(mAdapter);
        findViewById(R.id.btn_add).setOnClickListener(this);

        currentData = (MdtOptionResult) getIntent().getSerializableExtra(AppConstants.INTENT_START_DATA);
        initStartDataMap();
        if(vEmpty.getVisibility()==View.VISIBLE){
            onRightClick(null);
        }

    }

    protected void initData(){}

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId()==R.id.btn_add){
            onRightClick(v);
        }
    }

    private DropListener onDrop=new DropListener() {
        @Override
        public void drop(int from, int to) {
            MdtOptionItem item=mAdapter.getItem(from);
            mAdapter.getData().remove(item);
            mAdapter.getData().add(to,item);
            mAdapter.notifyDataSetChanged();
        }
    };

    protected void initStartDataMap() {
//        chosenIdList = new HashSet<>();
        if(currentData ==null){
            currentData =new MdtOptionResult();
            currentData.array=new ArrayList<>();
        }
        mAdapter.update(currentData.array);
        refreshEmpty();

    }
    private void refreshEmpty(){
        ViewHolder holder=ViewHolder.get(mThis,vFooter);
        View vMain=holder.getView(R.id.layout_main);
        vEmpty.setVisibility(View.GONE);
        if(TextUtils.isEmpty(currentData.text)){
            vMain.setVisibility(View.GONE);
            if(currentData.array.size()==0)
                vEmpty.setVisibility(View.VISIBLE);
        }else{
            vMain.setVisibility(View.VISIBLE);
            holder.setText(R.id.text_view,currentData.text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)return;
        if(requestCode==REQ_CODE_CHOOSE){
            MdtOptionResult res= (MdtOptionResult) data.getSerializableExtra(AppConstants.INTENT_RESULT);
            currentData=res;
            mAdapter.update(currentData.array);
            refreshEmpty();
        }
    }

    protected Intent makeResultIntent(){
        currentData.array= new ArrayList<>(mAdapter.getData() );
        currentData.makeShowText();
        return new Intent().putExtra(AppConstants.INTENT_VIEW_ID, getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID, 0))
                .putExtra(AppConstants.INTENT_RESULT,currentData);
    }

    protected class DragAdapter extends CommonAdapterV2<MdtOptionItem>{

        public DragAdapter(Context mContext) {
            super(mContext);
        }

        public DragAdapter(Context mContext, List<MdtOptionItem> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mThis,convertView,parent,R.layout.drag_text_item,0);
            holder.setText(R.id.text_view,getItem(position).name);
            return holder.getConvertView();
        }
    }
}
