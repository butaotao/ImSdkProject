package com.dachen.mdt.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ListView;

import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.mdt.adapter.ImOrderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ImOrderListView extends ListView {

    private Context mContext;
    private List<ChatGroupPo> itemList;
    private ImOrderAdapter mAdapter;

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
        setAdapter(mAdapter);
    }

    public void updateView() {
        new GetSessionData().execute();
    }

    private class GetSessionData extends AsyncTask<Void, Void, List<ChatGroupPo>> {
        @Override
        protected List<ChatGroupPo> doInBackground(Void... params) {
            ChatGroupDao dao=new ChatGroupDao();
//            return dao.queryInBizType(AppImUtils.getBizTypeListOrder());
            return dao.queryInBizType(null);
        }

        @Override
        protected void onPostExecute(List<ChatGroupPo> list) {
            itemList.clear();
            itemList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }
}
