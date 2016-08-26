package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.main.CommonInputActivity;
import com.dachen.mdt.activity.main.CommonListActivity;
import com.dachen.mdt.entity.MdtOptionResult;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.util.AppCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/22.
 */
public abstract class BaseMdtOptionActivity extends CommonListActivity {

    public static final String KEY_PARENT = "keyParent";

    public static final int REQ_CODE_NEXT = 1;
    public static final int REQ_CODE_INPUT = 2;

    protected View mFooterView;
    protected EditText etFooter;

    protected ChooseAdapter mAdapter;
    protected MdtOptionItem mParent;
    protected boolean isMulti = true;
//    protected HashSet<String> chosenIdList;
    protected HashMap<String,MdtOptionItem> chosenMap;
    protected MdtOptionResult currentData;
    protected Map<String, String> cacheDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        mParent = (MdtOptionItem) getIntent().getSerializableExtra(KEY_PARENT);
        currentData = (MdtOptionResult) getIntent().getSerializableExtra(AppConstants.INTENT_START_DATA);
        initStartDataMap();

        mFooterView=getLayoutInflater().inflate(R.layout.choose_text_edit_footer,null);
        etFooter= (EditText) mFooterView.findViewById(R.id.edit_text);
        mAdapter = makeAdapter();
        if (hasFooterEdit())
            mListView.addFooterView(mFooterView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(itemClick);
        etFooter.setText(currentData.text);

        if (needFetchInfo())
            fetchInfo();
    }

    protected void initData(){}

    protected boolean needFetchInfo(){
        if (mParent != null) {
            mAdapter.update(mParent.children);
            return false;
        }
        return true;
    }

    @Override
    public void onRightClick(View v) {
        clickRight();
    }
    protected void clickRight(){
        MdtOptionResult res =makeResultData();
        Intent i=makeResultIntent();
        setResult(RESULT_OK,i.putExtra(AppConstants.INTENT_RESULT,res));
        finish();
    }

    protected boolean hasFooterEdit(){
        return true;
    }

    protected abstract void fetchInfo();


    protected Intent makeResultIntent(){
        return new Intent().putExtra(AppConstants.INTENT_VIEW_ID, getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID, 0));
    }
    protected MdtOptionResult makeResultData(){
        StringBuilder builder = new StringBuilder();
        for (MdtOptionItem item: currentData.array) {
//        for (int i=0;i<currentData.array.size();i++) {
//            MdtOptionItem item =currentData.array.get(i);
//            if(item.supportText){
//                String value=cacheDataMap.get(item.id);
//                if(TextUtils.isEmpty(value))continue;
//                item.value=value;
//            }
//            res.array.add(item);
            builder.append(item.name).append("\n");
        }

//        for (MdtOptionItem item : mAdapter.getData()) {
//            if (chosenIdList.contains(item.id)) {
//                list.add(item);
//                builder.append(item.name).append(",");
//            }
//        }
        String other=etFooter.getText().toString();
        currentData.text=other;
        if(!TextUtils.isEmpty(other)){
            builder.append(other).append("\n");
        }
        AppCommonUtils.deleteLastChar(builder);
        currentData.showText = builder.toString();
        return currentData;
    }

    protected ChooseAdapter makeAdapter() {
        return new ChooseAdapter(mThis);
    }

    protected OnItemClickListener itemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MdtOptionItem item = mAdapter.getItem(position);
//            if (item.supportText) {
//                Intent i = new Intent(mThis, CommonInputActivity.class);
//                String text=null;
//                if(cacheDataMap.get(item.id)!=null)
//                    text=cacheDataMap.get(item.id);
//                i.putExtra(CommonInputActivity.KEY_DATA_ID, item.id).putExtra(CommonInputActivity.KEY_TEXT, text);
//                startActivityForResult(i, REQ_CODE_INPUT);
//                return;
//            }
            if(item.children!=null && getChildClass()!=null){
                Intent i=new Intent(mThis, getChildClass());
                i.putExtra(AppConstants.INTENT_START_DATA,makeResultData());
                i.putExtra(KEY_PARENT,item);
                startActivityForResult(i,REQ_CODE_NEXT);
            }else{
                toggleSelected(item);
                mAdapter.notifyDataSetChanged();
            }

        }
    };

    protected Class getChildClass(){
        return null;
    }

    private void toggleSelected(MdtOptionItem item) {
        String id=item.id;
        if (chosenMap.containsKey(id)){
            currentData.array.remove(chosenMap.get(id));
            chosenMap.remove(id);
        }
        else
            setSelected(item);
    }

    protected void setSelected(MdtOptionItem item) {
        String id=item.id;
        if (!isMulti) {
            chosenMap.clear();
            currentData.array.clear();
        }
        chosenMap.put(id,item);
        currentData.array.add(item);
    }


    protected void initStartDataMap() {
//        chosenIdList = new HashSet<>();
        chosenMap=new HashMap<>();
        cacheDataMap=new HashMap<>();
        if(currentData ==null){
            currentData =new MdtOptionResult();
            currentData.array=new ArrayList<>();
        }
        for (MdtOptionItem item : currentData.array) {
            chosenMap.put(item.id,item);
//            if(item.supportText)
//                cacheDataMap.put(item.id, item.value);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQ_CODE_NEXT) {
            currentData = (MdtOptionResult) data.getSerializableExtra(AppConstants.INTENT_RESULT);
            etFooter.setText(currentData.text);
            if(!isMulti){
                clickRight();
                return;
            }
            initStartDataMap();
            mAdapter.notifyDataSetChanged();
        }
        else if (requestCode == REQ_CODE_INPUT) {
            String id = data.getStringExtra(CommonInputActivity.KEY_DATA_ID);
            String text = data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
            if (TextUtils.isEmpty(id)) return;
            cacheDataMap.put(id,text);
            for (MdtOptionItem item : mAdapter.getData()) {
                if (id.equals(item.id)) {
                    if (TextUtils.isEmpty(text)) {
                        chosenMap.remove(id);
                        item.value = null;
                    } else {
                        setSelected(item);
                        item.value = text;
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected class ChooseAdapter extends CommonAdapterV2<MdtOptionItem> {

        public ChooseAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
            MdtOptionItem info = mData.get(position);
            String text = info.name;
            int arrowVis = View.GONE;
            if (info.children != null)
                arrowVis = View.VISIBLE;
            holder.setVisibility(R.id.iv_arrow, arrowVis);
            holder.setText(R.id.text_view, text);
            int vis = View.INVISIBLE;
            if (chosenMap.containsKey(info.id)) {
                vis = View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check, vis);
            return holder.getConvertView();
        }

    }

}
