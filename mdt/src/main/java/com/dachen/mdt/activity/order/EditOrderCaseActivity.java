package com.dachen.mdt.activity.order;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.gallery.CustomGalleryActivity;
import com.dachen.gallery.GalleryAction;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.activity.main.ChooseTextActivity;
import com.dachen.mdt.activity.main.CommonInputActivity;
import com.dachen.mdt.adapter.UpImgGridAdapter;
import com.dachen.mdt.adapter.UpImgGridAdapter.UpImgGridItem;
import com.dachen.mdt.entity.CheckTypeResult;
import com.dachen.mdt.entity.DiseaseInfo;
import com.dachen.mdt.entity.DiseaseType;
import com.dachen.mdt.entity.MdtGroupInfo;
import com.dachen.mdt.entity.OrderParam;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.TempTextParam;
import com.dachen.mdt.entity.TextImgListParam;
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.ImObjectRequest;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.OrderUtils;
import com.dachen.mdt.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class EditOrderCaseActivity extends BaseActivity {

    private static final int REQ_CODE_MDT_GROUP = 1;
    private static final int REQ_CODE_MDT_INFO = 2;
    private static final int REQ_CODE_INPUT = 3;
    private static final int REQ_CODE_PATIENT_TYPE = 4;
    private static final int REQ_CODE_IMG_EXAMINE = 5;
    private static final int REQ_CODE_DIS_TYPE = 6;
    private static final int REQ_CODE_CHECK_RESULT = 7;
    private static final int REQ_CODE_PATHOLOGY = 8;
    private static final int REQ_CODE_ID_CARD =9;
    private static final int REQ_CODE_PATIENT_SEX =10;
    private static Map<Integer, String> TYPE_MAP;
    public static final String KEY_PATIENT="patient";

    private PatientInfo mPatient;
    private LocalViewHolder holder;
    private String mdtGroupId;
    private DiseaseType diseaseType;
    private long mEndTime;
    private UpImgGridAdapter mImageExamineAdapter;
    private UpImgGridAdapter mPathologyAdapter;
    private CheckTypeResult mCheckResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_case);
        ButterKnife.bind(this);
        holder = new LocalViewHolder();
        initView();
    }

    private void initView() {
        mPatient= (PatientInfo) getIntent().getSerializableExtra(KEY_PATIENT);
        if(mPatient==null)
            mPatient=new PatientInfo();
        else{
            holder.tvPatientName.setText(mPatient.name);
            holder.tvPatientSex.setText(OrderUtils.getSexStr(mPatient.sex));
            holder.tvPatientAge.setText(String.valueOf(mPatient.age));
            holder.tvPatientPhone.setText(mPatient.telephone);
            holder.tvPatientId.setText(mPatient.number);
            holder.tvIdCard.setText(mPatient.idNum);
        }
        mImageExamineAdapter =new UpImgGridAdapter(mThis);
        holder.gvImageExamine.setAdapter(mImageExamineAdapter);
        mPathologyAdapter =new UpImgGridAdapter(mThis);
        holder.gvPathologyExamine.setAdapter(mPathologyAdapter);
    }

    @OnClick(R.id.right_btn)
    public void commit() {
        OrderParam reqParam = new OrderParam();
        DiseaseInfo dInfo = new DiseaseInfo();
        PatientInfo pInfo = mPatient;
        try {
            pInfo.name = ViewUtils.checkTextEmpty(holder.tvPatientName);
            pInfo.idNum = ViewUtils.checkTextEmpty(holder.tvIdCard);
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
            ViewUtils.checkTextEmpty(holder.tvMdtGroup);
            ViewUtils.checkTextEmpty(holder.tvDiseaseType);
            reqParam.target = ViewUtils.checkTextEmpty(holder.tvPurpose);
            dInfo.complain = new TempTextParam(ViewUtils.checkTextEmpty(holder.tvChiefComplaint));
            dInfo.diseaseNow =  new TempTextParam( ViewUtils.checkTextEmpty(holder.tvPresentHistory));
            dInfo.diseaseOld =  new TempTextParam(ViewUtils.checkTextEmpty(holder.tvPreviousHistory));
            dInfo.diseaseFamily =  new TempTextParam(ViewUtils.checkTextEmpty(holder.tvFamilyHistory));
            dInfo.diseaseSelf =  new TempTextParam(ViewUtils.checkTextEmpty(holder.tvPersonalHistory));
            dInfo.symptom =  new TempTextParam(ViewUtils.checkTextEmpty(holder.tvBodySign) );
            dInfo.checkProcess =  new TempTextParam(ViewUtils.checkTextEmpty(holder.tvTreatProcess));
        } catch (TextEmptyException e) {
            e.printStackTrace();
            e.tv.requestFocus();
            e.tv.setError(Html.fromHtml("<font color='red'>不能为空!</font>"));
            return;
        }
        dInfo.result=mCheckResult;
        dInfo.imaging=new TextImgListParam();
        dInfo.imaging.text=holder.etImageExamine.getText().toString();
        ArrayList<String> imgList=new ArrayList<>();
        for(UpImgGridItem item:mImageExamineAdapter.getData()){
            String url=item.url;
            if(TextUtils.isEmpty(url)){
                ToastUtil.showToast(mThis,"还有图片未成功上传");
                return;
            }
            imgList.add(url);
        }
        dInfo.imaging.pathList=imgList;

        dInfo.pathology=new TextImgListParam();
        dInfo.pathology.text=holder.etPathologyExamine.getText().toString();
        ArrayList<String> pathologyList=new ArrayList<>();
        for(UpImgGridItem item:mPathologyAdapter.getData()){
            String url=item.url;
            if(TextUtils.isEmpty(url)){
                ToastUtil.showToast(mThis,"还有图片未成功上传");
                return;
            }
            pathologyList.add(url);
        }
        dInfo.pathology.pathList=pathologyList;

        reqParam.expectEndTime=mEndTime;
        reqParam.diseaseTypeId=diseaseType.topDiseaseId;
        reqParam.firstDiag=diseaseType.name;
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

    @OnClick(R.id.ll_disease_type)
    protected void chooseDisType(View v) {
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, ChooseDiseaseTypeActivity.class).putExtra(ChooseDiseaseTypeActivity.KEY_START, diseaseType);
        startActivityForResult(i, REQ_CODE_DIS_TYPE);
    }
    @OnClick(R.id.ll_mdt_group)
    protected void chooseMdtGroup(View v) {
        if (diseaseType == null) {
            holder.tvDiseaseType.requestFocus();
            holder.tvDiseaseType.setError(Html.fromHtml("<font color='red'>请先选择病种!</font>"));
            return;
        }
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, ChooseMdtActivity.class).putExtra(AppConstants.INTENT_MDT_GROUP_ID, mdtGroupId)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID,diseaseType.topDiseaseId);
        startActivityForResult(i, REQ_CODE_MDT_GROUP);
    }

    @OnClick({R.id.ll_purpose}) //, R.id.ll_first_diagnose
    protected void chooseMdtInfo(View v) {
        if (diseaseType == null) {
            holder.tvDiseaseType.requestFocus();
            holder.tvDiseaseType.setError(Html.fromHtml("<font color='red'>请先选择病种!</font>"));
            return;
        }
        TextView tv = commonClickItem(v);
        String type = getTypeMap().get(v.getId());
        if (type == null) return;
        Intent i = new Intent(mThis, ChooseMdtInfoActivity.class).putExtra(AppConstants.INTENT_DISEASE_TOP_ID, diseaseType.topDiseaseId)
                .putExtra(ChooseMdtInfoActivity.KEY_TYPE, type)
                .putExtra(ChooseMdtInfoActivity.KEY_START_TEXT,tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_MDT_INFO);
    }

    @OnClick({R.id.ll_chief_complaint, R.id.ll_present_history, R.id.ll_previous_history, R.id.ll_family_history, R.id.ll_personal_history
    ,R.id.ll_treat_process,R.id.ll_body_sign,R.id.ll_patient_name,R.id.ll_patient_phone,R.id.ll_patient_id})
    protected void goInput(View v) {
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, CommonInputActivity.class)
                .putExtra(CommonInputActivity.KEY_TEXT, tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_INPUT);
    }

    @OnClick(R.id.ll_id_card)
    protected void goIdCard(View v) {
        if(!TextUtils.isEmpty(mPatient.id))
            return;
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, CommonInputActivity.class)
                .putExtra(CommonInputActivity.KEY_TEXT, tv.getText().toString());
        startActivityForResult(i, REQ_CODE_ID_CARD);
    }

    @OnClick(R.id.ll_examine_result)
    protected void goCheckResult(View v) {
        TextView tv = commonClickItem(v);
        Intent i = new Intent(mThis, ChooseCheckResultActivity.class)
                .putExtra(ChooseCheckResultActivity.KEY_START,mCheckResult);
        startActivityForResult(i, REQ_CODE_CHECK_RESULT);
    }
    @OnClick(R.id.ll_patient_sex)
    protected void goPatientSex(View v) {
        TextView tv = commonClickItem(v);
        ArrayList<String> list= new ArrayList<>();
        list.addAll(Arrays.asList("男","女") );
        Intent i = new Intent(mThis, ChooseTextActivity.class).putExtra(ChooseTextActivity.KEY_LIST,list)
                .putExtra(ChooseTextActivity.KEY_SELECTED,tv.getText().toString());
        startActivityForResult(i, REQ_CODE_PATIENT_SEX);
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
        final Dialog dialog=new Dialog(mThis,R.style.Dialog);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
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
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQ_CODE_MDT_GROUP) {
            MdtGroupInfo info = (MdtGroupInfo) data.getSerializableExtra(ChooseMdtActivity.KEY_RESULT_MDT);
            if (info == null) return;
            holder.tvMdtGroup.setText(info.name);
            holder.tvMdtGroup.setError(null);
            mdtGroupId = info.id;
        } else if (requestCode == REQ_CODE_MDT_INFO || requestCode == REQ_CODE_INPUT) {
            handleTextResult(data);
//        }else if(requestCode == REQ_CODE_PATIENT_TYPE){
//            if(resultCode!=RESULT_OK)return;
//            PatientType info= (PatientType) data.getSerializableExtra(ChooseMdtPatientTypeActivity.KEY_RESULT);
//            if (info == null) return;
//            mPatientTypeId=info.id;
//            holder.tvCategory.setText(info.name);
        }else if(requestCode==REQ_CODE_DIS_TYPE){
            diseaseType= (DiseaseType) data.getSerializableExtra(ChooseDiseaseTypeActivity.KEY_RESULT);
            holder.tvDiseaseType.setText(diseaseType.name);
        }else if(requestCode==REQ_CODE_CHECK_RESULT){
            mCheckResult= (CheckTypeResult) data.getSerializableExtra(ChooseCheckResultActivity.KEY_RESULT);
            holder.tvExamineResult.setText(OrderUtils.getText(mCheckResult));
        }else if(requestCode==REQ_CODE_IMG_EXAMINE||requestCode==REQ_CODE_PATHOLOGY){
            String[] all_path = data.getStringArrayExtra(GalleryAction.INTENT_ALL_PATH);
            if (all_path == null) return;
            UpImgGridAdapter adapter;
//            EditText editText;
            if(requestCode==REQ_CODE_IMG_EXAMINE){
                adapter=mImageExamineAdapter;
//                editText=holder.etImageExamine;
            }
            else{
                adapter=mPathologyAdapter;
//                editText=holder.etPathologyExamine;
            }

            for (String path : all_path) {
                adapter.addLocalPic(path,true);
            }
            adapter.notifyDataSetChanged();
//            refreshPicInfoEdit(adapter,editText);
        }else if(requestCode==REQ_CODE_ID_CARD){
            String idCard=data.getStringExtra(AppConstants.INTENT_TEXT_RESULT).trim();
            holder.tvIdCard.setText(idCard);
            if(!OrderUtils.validIdCard(idCard)){
                ViewUtils.setError(holder.tvIdCard,"身份证号有误!");
                return;
            }
            Date birth=OrderUtils.getBirthFromId(idCard);
            if(birth!=null){
                int age=OrderUtils.getAge(birth);
                holder.tvPatientAge.setText(String.valueOf(age));
            }
            int sex=OrderUtils.getSexFromIdCard(idCard);
            holder.tvPatientSex.setText(OrderUtils.getSexStr(sex));
        }else if(requestCode==REQ_CODE_PATIENT_SEX){
            holder.tvPatientSex.setText(data.getStringExtra(AppConstants.INTENT_RESULT));
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

    @OnItemClick(R.id.gv_image_examine)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        handleImgItemClick(mImageExamineAdapter,position,REQ_CODE_IMG_EXAMINE);
    }
    @OnItemClick(R.id.gv_pathology_examine)
    public void onPathologyItemClick(AdapterView<?> parent, View view, int position, long id) {
        handleImgItemClick(mPathologyAdapter,position,REQ_CODE_PATHOLOGY);
    }
    private void handleImgItemClick(UpImgGridAdapter adapter,int position,int reqCode){
        UpImgGridItem item=adapter.getItem(position);
        if(item.isAdd){
            CustomGalleryActivity.openUi(mThis,true,reqCode,8-adapter.getData().size());
        }
    }

    public class LocalViewHolder {
        @BindView(R.id.tv_patient_name)
        TextView tvPatientName;
        @BindView(R.id.tv_id_card)
        TextView tvIdCard;
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
//        @BindView(R.id.tv_category)
//        TextView tvCategory;
        @BindView(R.id.tv_purpose)
        TextView tvPurpose;
        @BindView(R.id.tv_end_time)
        TextView tvEndTime;
//        @BindView(R.id.tv_first_diagnose)
//        TextView tvFirstDiagnose;
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
        @BindView(R.id.gv_image_examine)
        GridView gvImageExamine;
        @BindView(R.id.gv_pathology_examine)
        GridView gvPathologyExamine;
        @BindView(R.id.tv_disease_type)
        TextView tvDiseaseType;
        @BindView(R.id.et_image_examine)
        EditText etImageExamine;
        @BindView(R.id.et_pathology_examine)
        EditText etPathologyExamine;

        public LocalViewHolder() {
            ButterKnife.bind(this, EditOrderCaseActivity.this);
        }
    }
}
