package com.dachen.mdt.view;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.adapter.ImOrderAdapter;
import com.dachen.mdt.entity.OrderChatParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ImOrderListView extends ListView {

    private Context mContext;
    private List<ChatGroupPo> itemList;
    private ImOrderAdapter mAdapter;
    private boolean filterMine;
    private boolean adapterHasSet;

    public ImOrderListView(Context context) {
        super(context);
        init(context);
    }

    public ImOrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImOrderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext=context;
        itemList=new ArrayList<>();
        mAdapter=new ImOrderAdapter(context,itemList);
    }

    public void setFilterMine(boolean filterMine) {
        this.filterMine = filterMine;
    }

    public void updateView() {
        if(!adapterHasSet){
            setAdapter(mAdapter);
            adapterHasSet=true;
        }
        new GetSessionData().execute();
    }

    private class GetSessionData extends AsyncTask<Void, Void, List<ChatGroupPo>> {
        @Override
        protected List<ChatGroupPo> doInBackground(Void... params) {
            ChatGroupDao dao=new ChatGroupDao();
            List<ChatGroupPo> list= dao.queryInBizType(null);
            if(filterMine){
                ArrayList<ChatGroupPo> myList=new ArrayList<>();
                for(ChatGroupPo po:list){
                    OrderChatParam param= JSON.parseObject(po.param,OrderChatParam.class);
                    if(param==null|| TextUtils.isEmpty(param.creator))continue;
                    if(ImUtils.getLoginUserId().equals(param.creator))
                        myList.add(po);
                }
                list=myList;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ChatGroupPo> list) {
            itemList.clear();
            itemList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }
}
