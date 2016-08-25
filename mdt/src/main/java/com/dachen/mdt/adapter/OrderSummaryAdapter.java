package com.dachen.mdt.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.SummaryInfo;
import com.dachen.mdt.entity.SummaryResult;
import com.dachen.mdt.util.OrderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class OrderSummaryAdapter extends CommonAdapterV2<SummaryResult> {

    public OrderSummaryAdapter(Context context, List<SummaryResult> list) {
        super(context,list);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent, R.layout.order_report_item,position);
        SummaryResult item=mData.get(position);
        holder.setText(R.id.tv_name,item.name);
        holder.setText(R.id.tv_name_info,item.department);
        ImageLoader.getInstance().displayImage(item.avatar, (ImageView) holder.getView(R.id.iv_pic));
        SummaryInfo info=item.summary;
        if (info == null) {
            holder.setVisibility(R.id.tv_no_fill, View.VISIBLE);
            holder.setVisibility(R.id.ll_report_info, View.GONE);
        }else{
            int lineCount=0;
            lineCount+=handleLine(holder,R.id.ll_diagnose_opinion,R.id.tv_diagnose_opinion, OrderUtils.getMdtOptionResultText(info.diagSuggest));
            lineCount+=handleLine(holder,R.id.ll_examine_opinion,R.id.tv_examine_opinion,OrderUtils.getMdtOptionResultText(info.checkSuggest));
            lineCount+=handleLine(holder,R.id.ll_treat_opinion,R.id.tv_treat_opinion,OrderUtils.getMdtOptionResultText(info.treatSuggest));
            lineCount+=handleLine(holder,R.id.ll_other,R.id.tv_other,info.other);
            int lineVis=lineCount==0? View.GONE:View.VISIBLE;
            int noFillVis=lineCount!=0? View.GONE:View.VISIBLE;
            holder.setVisibility(R.id.ll_report_info,lineVis);
            holder.setVisibility(R.id.tv_no_fill,noFillVis);
        }
        return holder.getConvertView();
    }

    private int handleLine(ViewHolder holder,int lineId,int textId,String text){
        if(TextUtils.isEmpty(text)){
            holder.setVisibility(lineId,View.GONE);
            return 0;
        }
        holder.setVisibility(lineId,View.VISIBLE);
        holder.setText(textId,text);
        return 1;
    }

}
