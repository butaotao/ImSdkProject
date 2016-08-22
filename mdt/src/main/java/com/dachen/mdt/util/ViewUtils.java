package com.dachen.mdt.util;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.TimeUtils;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.main.ViewImgActivity;
import com.dachen.mdt.adapter.ViewImgGridAdapter;
import com.dachen.mdt.entity.OrderDetailVO;
import com.dachen.mdt.entity.TextImgListParam;
import com.dachen.mdt.exception.TextEmptyException;

import java.util.Date;

/**
 * Created by Mcp on 2016/8/8.
 */
public class ViewUtils {

    public static String checkTextEmpty(TextView tv) throws TextEmptyException {
        String res = tv.getText().toString();
        if (TextUtils.isEmpty(res)) {
            throw new TextEmptyException(tv);
        }
        return res;
    }

    public static void setError(TextView tv, String err) {
        tv.requestFocus();
        tv.setError(Html.fromHtml("<font color='red'>" + err + "</font>"));
    }

    public static void initOrderInfo(Activity act, View v, OrderDetailVO vo){
        ViewHolder holder= ViewHolder.get(act,v);
        ((TextView) v.findViewById(R.id.tv_patient_info)).setText(String.format("%s,%s,%s岁",vo.patient.name, StringUtils.getSexStr(vo.patient.sex),vo.patient.age) );
        ((TextView) v.findViewById(R.id.tv_patient_num)).setText(vo.patient.number);
        ((TextView) v.findViewById(R.id.tv_mdt_num)).setText(vo.mdtNum);
        ((TextView) v.findViewById(R.id.tv_purpose)).setText(OrderUtils.getMdtOptionResultText(vo.target) );
        ((TextView) v.findViewById(R.id.tv_base_disease)).setText(OrderUtils.getMdtOptionResultText(vo.basicDisease) );
        ((TextView) v.findViewById(R.id.tv_complication)).setText(OrderUtils.getMdtOptionResultText(vo.concomitant) );
        ((TextView) v.findViewById(R.id.tv_first_diagnose)).setText(vo.firstDiag);
        ((TextView) v.findViewById(R.id.tv_chief_complaint)).setText(OrderUtils.getText(vo.disease.complain));
        ((TextView) v.findViewById(R.id.tv_present_history)).setText(OrderUtils.getText(vo.disease.diseaseNow));
        ((TextView) v.findViewById(R.id.tv_previous_history)).setText(OrderUtils.getText(vo.disease.diseaseOld));
        ((TextView) v.findViewById(R.id.tv_family_history)).setText(OrderUtils.getText(vo.disease.diseaseFamily));
        ((TextView) v.findViewById(R.id.tv_personal_history)).setText(OrderUtils.getText(vo.disease.diseaseSelf));
        ((TextView) v.findViewById(R.id.tv_body_sign)).setText(OrderUtils.getText(vo.disease.symptom));
        ((TextView) v.findViewById(R.id.tv_examine_result)).setText(OrderUtils.getText(vo.disease.result));
        ((TextView) v.findViewById(R.id.tv_treat_process)).setText(OrderUtils.getText(vo.disease.checkProcess));
        ((TextView) v.findViewById(R.id.tv_end_time)).setText(TimeUtils.a_format.format(new Date(vo.expectEndTime)) );
        handleOrderImg(act,vo.disease.imaging,holder,R.id.tv_image_examine,R.id.gv_image_examine);
        handleOrderImg(act,vo.disease.pathology,holder,R.id.tv_pathology_examine,R.id.gv_pathology_examine);
    }
    private static void handleOrderImg(final Activity act, final TextImgListParam param, ViewHolder holder, int tvId, int gvId){
        if(param==null)return;
        if(!TextUtils.isEmpty(param.text)){
            holder.setVisibility(tvId, View.VISIBLE);
            holder.setText(tvId,param.text);
        }
        if(param.pathList!=null&&param.pathList.size()>0){
            holder.setVisibility(gvId,View.VISIBLE);
            GridView gvImg=holder.getView(gvId);
            ViewImgGridAdapter adapter=new ViewImgGridAdapter(act,param.pathList);
            gvImg.setAdapter(adapter);
            gvImg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewImgActivity.OpenUi(act,param.pathList,position);
                }
            });

        }
    }
}
