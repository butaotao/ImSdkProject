package com.dachen.mdt.activity.order;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditOrderCaseActivity extends BaseActivity {

    private LocalViewHolder holder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_case);
        ButterKnife.bind(this);
        holder=new LocalViewHolder();
        holder.tvMdtGroup.setText(null);
    }

    @OnClick(R.id.btn_save)
    public void commit(){
        try {
            ViewUtils.checkTextEmpty(holder.tvMdtGroup);
            ViewUtils.checkTextEmpty(holder.tvCategory);
            ViewUtils.checkTextEmpty(holder.tvPurpose);
            ViewUtils.checkTextEmpty(holder.tvFirstDiagnose);
            ViewUtils.checkTextEmpty(holder.tvChiefComplaint);
            ViewUtils.checkTextEmpty(holder.tvPresentHistory);
            ViewUtils.checkTextEmpty(holder.tvPreviousHistory);
            ViewUtils.checkTextEmpty(holder.tvPersonalHistory);
            ViewUtils.checkTextEmpty(holder.tvBodySign);
            ViewUtils.checkTextEmpty(holder.tvTreatProcess);
            ViewUtils.checkTextEmpty(holder.tvExamineResult);
        } catch (TextEmptyException e) {
            e.printStackTrace();
            e.tv.requestFocus();
//            e.tv.setError("不能为空");
            e.tv.setError(Html.fromHtml("<font color='red'>不能为空!</font>"));
            return;
        }
    }

    public void onLeftClick(View v){
        finish();
    }


    public class LocalViewHolder{
        @BindView(R.id.tv_mdt_group)
        TextView tvMdtGroup;
        @BindView(R.id.tv_category)
        TextView tvCategory;
        @BindView(R.id.tv_purpose)
        TextView tvPurpose;
        @BindView(R.id.tv_end_time)
        TextView tvEndTime;
        @BindView(R.id.tv_first_diagnose)
        TextView tvFirstDiagnose;
        @BindView(R.id.tv_chief_complaint)
        TextView tvChiefComplaint;
        @BindView(R.id.tv_present_history)
        TextView tvPresentHistory;
        @BindView(R.id.tv_previous_history)
        TextView tvPreviousHistory;
        @BindView(R.id.tv_personal_history)
        TextView tvPersonalHistory;
        @BindView(R.id.tv_body_sign)
        TextView tvBodySign;
        @BindView(R.id.tv_treat_process)
        TextView tvTreatProcess;
        @BindView(R.id.tv_examine_result)
        TextView tvExamineResult;

        public LocalViewHolder() {
            ButterKnife.bind(this,EditOrderCaseActivity.this);
        }
    }
}
