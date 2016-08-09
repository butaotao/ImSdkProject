package com.dachen.mdt.activity.order;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.activity.main.CommonInputActivity;
import com.dachen.mdt.entity.DiseaseInfo;
import com.dachen.mdt.entity.MdtGroupInfo;
import com.dachen.mdt.entity.OrderParam;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.PatientType;
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.ImObjectRequest;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditOrderCaseActivity extends BaseActivity {

    private static final int REQ_CODE_MDT_GROUP = 1;
    private static final int REQ_CODE_MDT_INFO = 2;
    private static final int REQ_CODE_INPUT = 3;
    private static final int REQ_CODE_PATIENT_TYPE = 4;
    private static Map<Integer, String> TYPE_MAP;

    private LocalViewHolder holder;
    private String mdtGroupId;
    private String mPatientTypeId;
    private long mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_case);
        ButterKnife.bind(this);
        holder = new LocalViewHolder();
        initView();
    }

    private void initView() {
        holder.tvPatientName.setText("张三");
        holder.tvPatientSex.setText("男");
        holder.tvPatientAge.setText("22");
        holder.tvPatientPhone.setText("1234567890123");
        holder.tvPatientId.setText("100123");
    }

    @OnClick(R.id.right_btn)
    public void commit() {
        OrderParam reqParam = new OrderParam();
        DiseaseInfo dInfo = new DiseaseInfo();
        PatientInfo pInfo = new PatientInfo();
        try {
            pInfo.name = ViewUtils.checkTextEmpty(holder.tvPatientName);
            String sexStr = ViewUtils.checkTextEmpty(holder.tvPatientSex);
            if ("男".equals(sexStr)) {
                pInfo.sex = 1;
            } else if ("女".equals(sexStr)) {
                pInfo.sex = 2;
            } else {
                ToastUtil.showToast(mThis, "性别有误");
                return;
            }
            String ageStr = ViewUtils.checkTextEmpty(holder.tvPatientAge);
            try {
                pInfo.age = Integer.parseInt(ageStr);
            } catch (Exception e) {
                ToastUtil.showToast(mThis, "年龄有误");
                return;
            }
            pInfo.telephone = ViewUtils.checkTextEmpty(holder.tvPatientPhone);
            pInfo.number = ViewUtils.checkTextEmpty(holder.tvPatientId);
            pInfo.id = "556536974c5b4c3dbf71befb1f1f74e9";
            ViewUtils.checkTextEmpty(holder.tvMdtGroup);
            reqParam.diseaseTypeId = ViewUtils.checkTextEmpty(holder.tvCategory);
            reqParam.target = ViewUtils.checkTextEmpty(holder.tvPurpose);
            dInfo.firstDiag = ViewUtils.checkTextEmpty(holder.tvFirstDiagnose);
            dInfo.desc = ViewUtils.checkTextEmpty(holder.tvChiefComplaint);
            dInfo.diseaseNow = ViewUtils.checkTextEmpty(holder.tvPresentHistory);
            dInfo.diseaseOld = ViewUtils.checkTextEmpty(holder.tvPreviousHistory);
            dInfo.diseaseFamily = ViewUtils.checkTextEmpty(holder.tvFamilyHistory);
            dInfo.diseaseSelf = ViewUtils.checkTextEmpty(holder.tvPersonalHistory);
            dInfo.symptom = ViewUtils.checkTextEmpty(holder.tvBodySign);
            dInfo.checkProcess = ViewUtils.checkTextEmpty(holder.tvTreatProcess);
            dInfo.result = ViewUtils.checkTextEmpty(holder.tvExamineResult);
        } catch (TextEmptyException e) {
            e.printStackTrace();
            e.tv.requestFocus();
            e.tv.setError(Html.fromHtml("<font color='red'>不能为空!</font>"));
            return;
        }
        reqParam.expectEndTime=mEndTime;
        reqParam.diseaseTypeId=mPatientTypeId;
        reqParam.mdtGroupId=mdtGroupId;
        reqParam.patient=pInfo;
        reqParam.disease=dInfo;

        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                ToastUtil.showToast(mThis,"创建成功");
                finish();
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.CREATE_ORDER);
        ImObjectRequest request=new ImObjectRequest(url,reqParam, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @OnClick(R.id.ll_mdt_group)
    protected void chooseMdtGroup(View v) {
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, ChooseMdtActivity.class).putExtra(AppConstants.INTENT_MDT_GROUP_ID, mdtGroupId);
        startActivityForResult(i, REQ_CODE_MDT_GROUP);
    }
    @OnClick(R.id.ll_category)
    protected void choosePatientType(View v) {
        if (mdtGroupId == null) {
            holder.tvMdtGroup.requestFocus();
            holder.tvMdtGroup.setError(Html.fromHtml("<font color='red'>请先选择MDT小组!</font>"));
            return;
        }
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, ChooseMdtPatientTypeActivity.class).putExtra(AppConstants.INTENT_MDT_GROUP_ID, mdtGroupId)
                .putExtra(ChooseMdtPatientTypeActivity.KEY_TYPE_ID,mPatientTypeId);
        startActivityForResult(i, REQ_CODE_PATIENT_TYPE);
    }

    @OnClick({R.id.ll_purpose, R.id.ll_first_diagnose})
    //, R.id.tv_first_diagnose, R.id.tv_diagnose_opinion, R.id.tv_examine_opinion, R.id.tv_treat_opinion
    protected void chooseMdtInfo(View v) {
        if (mdtGroupId == null) {
            holder.tvMdtGroup.requestFocus();
            holder.tvMdtGroup.setError(Html.fromHtml("<font color='red'>请先选择MDT小组!</font>"));
            return;
        }
        TextView tv = commonClickItem(v);
        String type = getTypeMap().get(v.getId());
        if (type == null) return;
        Intent i = new Intent(mThis, ChooseMdtInfoActivity.class).putExtra(AppConstants.INTENT_MDT_GROUP_ID, mdtGroupId)
                .putExtra(ChooseMdtInfoActivity.KEY_TYPE, type)
                .putExtra(ChooseMdtInfoActivity.KEY_START_TEXT,tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_MDT_INFO);
    }

    @OnClick({R.id.ll_chief_complaint, R.id.ll_present_history, R.id.ll_previous_history, R.id.ll_family_history, R.id.ll_personal_history
    ,R.id.ll_treat_process,R.id.ll_examine_result,R.id.ll_body_sign})
    protected void goInput(View v) {
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, CommonInputActivity.class)
                .putExtra(CommonInputActivity.KEY_TEXT, tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_INPUT);
    }

    public static synchronized Map<Integer, String> getTypeMap() {
        if (TYPE_MAP != null)
            return TYPE_MAP;
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put(R.id.ll_purpose, "target");
        TYPE_MAP.put(R.id.ll_first_diagnose, "firstdiag");
        TYPE_MAP.put(R.id.ll_diagnose_opinion, "diagsuggest");
        TYPE_MAP.put(R.id.ll_examine_opinion, "checksuggest");
        TYPE_MAP.put(R.id.ll_treat_opinion, "treatsuggest");
        return TYPE_MAP;
    }

    @OnClick({R.id.ll_end_time})
    protected void showTimeDialog(View v){
        TextView tv = commonClickItem(v);
        final View dialogView = View.inflate(mThis, R.layout.date_time_picker, null);
        final Dialog dialog=new Dialog(mThis);
//        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                timePicker.setIs24HourView(true);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                mEndTime = calendar.getTimeInMillis();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
                holder.tvEndTime.setText(format.format(calendar.getTime()));
                dialog.dismiss();
            }});
//        dialog.setView(dialogView);
        dialog.setContentView(dialogView);
        dialog.show();
    }
    private TextView commonClickItem(View v){
        TextView tv = (TextView) ((ViewGroup) v).getChildAt(1);
        tv.clearFocus();
        tv.setError(null);
        return tv;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_MDT_GROUP) {
            if (resultCode != RESULT_OK) return;
            MdtGroupInfo info = (MdtGroupInfo) data.getSerializableExtra(ChooseMdtActivity.KEY_RESULT_MDT);
            if (info == null) return;
            holder.tvMdtGroup.setText(info.name);
            holder.tvMdtGroup.setError(null);
            mdtGroupId = info.id;
        } else if (requestCode == REQ_CODE_MDT_INFO || requestCode == REQ_CODE_INPUT) {
            if (resultCode != RESULT_OK) return;
            handleTextResult(data);
        }else if(requestCode == REQ_CODE_PATIENT_TYPE){
            if(resultCode!=RESULT_OK)return;
            PatientType info= (PatientType) data.getSerializableExtra(ChooseMdtPatientTypeActivity.KEY_RESULT);
            if (info == null) return;
            mPatientTypeId=info.id;
            holder.tvCategory.setText(info.name);
        }
    }

    private void handleTextResult(Intent data) {
        String res = data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
        int viewId = data.getIntExtra(AppConstants.INTENT_VIEW_ID, 0);
        TextView tv = ButterKnife.findById(mThis, viewId);
        if (tv != null) {
            tv.setText(res);
        }
    }

    public class LocalViewHolder {
        @BindView(R.id.tv_patient_name)
        TextView tvPatientName;
        @BindView(R.id.tv_patient_age)
        TextView tvPatientAge;
        @BindView(R.id.tv_patient_sex)
        TextView tvPatientSex;
        @BindView(R.id.tv_patient_phone)
        TextView tvPatientPhone;
        @BindView(R.id.tv_patient_id)
        TextView tvPatientId;
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
        @BindView(R.id.tv_family_history)
        TextView tvFamilyHistory;
        @BindView(R.id.tv_personal_history)
        TextView tvPersonalHistory;
        @BindView(R.id.tv_body_sign)
        TextView tvBodySign;
        @BindView(R.id.tv_treat_process)
        TextView tvTreatProcess;
        @BindView(R.id.tv_examine_result)
        TextView tvExamineResult;

        public LocalViewHolder() {
            ButterKnife.bind(this, EditOrderCaseActivity.this);
        }
    }
}
