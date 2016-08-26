package com.dachen.mdt.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.dachen.common.adapter.CommonExpandAdapter;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.patient.PatientInfoActivity;
import com.dachen.mdt.db.dao.PatientInfoDao;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.PatientTagGroup;
import com.dachen.mdt.entity.event.PatientUpdateEvent;
import com.dachen.mdt.net.PatientUpdater;
import com.dachen.mdt.util.OrderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/6.
 */
public class PatientFragment extends BaseFragment {

    private ExpandableListView mListView;
    private PatientAdapter mAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_patient;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=super.onCreateView(inflater, container, savedInstanceState);
        mListView= (ExpandableListView) v.findViewById(R.id.list_view);
        mAdapter=new PatientAdapter(mParent);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(mItemClick);
        new UpdateDataTask().execute();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        PatientUpdater.getInstance().execute();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }
    private OnChildClickListener mItemClick=new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            PatientInfo info= (PatientInfo) mAdapter.getChild(groupPosition,childPosition);
            startActivity(new Intent(mParent, PatientInfoActivity.class).putExtra(PatientInfoActivity.KEY_PATIENT,info));
            return true;
        }
    };

    private class UpdateDataTask extends AsyncTask<Void,Void,List<PatientInfo>>{
        @Override
        protected List<PatientInfo> doInBackground(Void... params) {
            PatientInfoDao dao=new PatientInfoDao();
            return dao.queryAll();
        }

        @Override
        protected void onPostExecute(List<PatientInfo> list) {
            Map<String,PatientTagGroup> map=new HashMap<>();
            List<PatientTagGroup> dataList=new ArrayList<>();
            PatientTagGroup myGroup=new PatientTagGroup();
            myGroup.tagName="我发起的病例";
            dataList.add(myGroup);
            for(PatientInfo info:list){
                if(info.isMyApply==1){
                    myGroup.patientList.add(info);
                }
                PatientTagGroup group=map.get(info.tagName);
                if(group==null){
                    group=new PatientTagGroup();
                    group.tagName=info.tagName;
                    dataList.add(group);
                    map.put(info.tagName,group);
                }
                group.patientList.add(info);
            }
            mAdapter.update(dataList);
        }
    }

    public void onEventMainThread(PatientUpdateEvent event){
        new UpdateDataTask().execute();
    }

    private class PatientAdapter extends CommonExpandAdapter<PatientTagGroup> {
        public PatientAdapter(Context mContext) {
            super(mContext);
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            PatientTagGroup group=getGroup(groupPosition);
            if(group.patientList==null)return 0;
            return group.patientList.size();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            PatientTagGroup group=getGroup(groupPosition);
            return group.patientList.get(childPosition);
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.patient_frag_group_item,0);
            holder.setText(R.id.tv_name,getGroup(groupPosition).tagName);
            int src=isExpanded?R.drawable.triangle_down:R.drawable.triangle_right;
            holder.setImageResource(R.id.iv_arrow,src);
            return holder.getConvertView();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.patient_frag_child_item,0);
            PatientInfo p= (PatientInfo) getChild(groupPosition,childPosition);
            holder.setText(R.id.tv_name,p.name);
            holder.setText(R.id.tv_info, OrderUtils.getPatientInfoStr(p));
            return holder.getConvertView();
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
