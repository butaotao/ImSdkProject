package com.dachen.mdt.activity.order;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.CheckItem;
import com.dachen.mdt.entity.CheckType;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.tools.CheckResultHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCheckValueActivity extends BaseActivity implements OnClickListener{

    public static final String KEY_DATA="data";
    public static final String KEY_TYPE="type";
    public static final String KEY_RESULT="result";
    private ArrayList<CheckType> mData;
    private CheckType mType;
    private Map<String,String> mValueMap=new HashMap<>();

    protected ListView mListView;
    protected TextView tvTitle;
    private EditValueAdapter mAdapter;
    private Dialog mDialog;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_check_value);

        mType= (CheckType) getIntent().getSerializableExtra(KEY_TYPE);
        mData= (ArrayList<CheckType>) getIntent().getSerializableExtra(KEY_DATA);

        mAdapter=new EditValueAdapter(mThis);
        mListView= (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(mItemClick);
        mListView.setAdapter(mAdapter);
        tvTitle= (TextView) findViewById(R.id.title);
        tvTitle.setText(mType.name);
        findViewById(R.id.right_btn).setOnClickListener(this);
        initValues();
        fetchInfo();
    }

    private void initValues(){
        int index=CheckResultHelper.getTypeIndex(mData,mType.id);
        if(index<0)
            return;
        CheckType typeData=mData.get(index);
        if(typeData.itemList==null||typeData.itemList.size()==0)
            return;
        for(CheckItem item:typeData.itemList){
            mValueMap.put(item.id,item.value);
        }
    }

    private OnItemClickListener mItemClick=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckItem item=mAdapter.getItem(position);
            showEditDialog(item);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_btn:
                mValueMap.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<CheckItem> list= JSON.parseArray(dataStr,CheckItem.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_CHECK_LIST);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("typeId",mType.id);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @Override
    public void onLeftClick(View v) {
        ArrayList<CheckItem> resList=new ArrayList<>();
        for(CheckItem item:mAdapter.getData()){
            String value=mValueMap.get(item.id);
            if(TextUtils.isEmpty(value))
                continue;
            item.value=value;
            resList.add(item);
        }
        int index=CheckResultHelper.getTypeIndex(mData,mType.id);
        if(resList.size()==0){
            if(index>=0)
                mData.remove(index);
        }else{
            mType.itemList=resList;
            if(index>=0){
                mData.set(index,mType);
            }else
                mData.add(0,mType);
        }
        setResult(RESULT_OK,new Intent().putExtra(KEY_RESULT,mData));
        finish();
    }

    private void showEditDialog(final CheckItem item){
        if(mDialog==null){
            dialogView=getLayoutInflater().inflate(R.layout.dialog_edit_check_value,null);
            dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog=new Dialog(mThis,R.style.Dialog);
            mDialog.setContentView(dialogView,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            WindowManager man= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display d = man.getDefaultDisplay();
            WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();
            Point point = new Point();
            d.getSize(point);
            p.width = (int)(point.x * 0.82f);
            p.height= LayoutParams.WRAP_CONTENT;
            mDialog.getWindow().setAttributes(p);
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
        }
        final ViewHolder holder=ViewHolder.get(mThis,dialogView);
        holder.setText(R.id.title,item.alias);
        final EditText tvValue=holder.getView(R.id.et_value);
        TextView tvUnit=holder.getView(R.id.tv_unit);
        tvValue.setText(mValueMap.get(item.id));
        tvUnit.setText(item.unit);
        if("string".equals(item.dataType)){
            tvUnit.setInputType(EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        }else{
            tvUnit.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        }
        holder.getView(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=tvValue.getText().toString();
                mValueMap.put(item.id,text);
                mAdapter.notifyDataSetChanged();
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private class EditValueAdapter extends CommonAdapterV2<CheckItem> {

        public EditValueAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder= ViewHolder.get(mContext,convertView,parent,R.layout.edit_check_value_item,position);
            CheckItem info=mData.get(position);
            holder.setVisibility(R.id.iv_arrow, View.VISIBLE);
            holder.setText(R.id.tv_name, info.alias);
            holder.setText(R.id.tv_unit, info.unit);
            holder.setText(R.id.tv_value, mValueMap.get(info.id) );
            return holder.getConvertView();
        }

    }
}
